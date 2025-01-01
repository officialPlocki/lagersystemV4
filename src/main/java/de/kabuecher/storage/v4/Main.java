package de.kabuecher.storage.v4;

import co.plocki.MySQLRunner;
import co.plocki.driver.MySQLDriver;
import co.plocki.json.JSONFile;
import co.plocki.json.JSONValue;
import de.kabuecher.storage.v4.client.desktop.DesktopContentBodyHandler;
import de.kabuecher.storage.v4.client.desktop.DisplayDriver;
import de.kabuecher.storage.v4.client.flows.LoginFlow;
import de.kabuecher.storage.v4.client.panels.DesktopSetupFrame;
import de.kabuecher.storage.v4.server.UndertowServer;
import de.kabuecher.storage.v4.server.console.ConsoleInputHandler;
import de.kabuecher.storage.v4.server.console.command.UserCommand;
import org.bouncycastle.crypto.generators.SCrypt;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    private static File logFile;
    private static JSONFile jsonFile;
    public static String sevdesk_api_token;
    public static DisplayDriver displayDriver;
    public static DesktopContentBodyHandler bodyHandler;
    public static MySQLDriver mysqlDriver;
    public static String passwordHash;
    public static String username;
    public static long lastAction = System.currentTimeMillis();
    public static boolean timeout = true;
    protected static boolean isClient;

    public static JSONFile getJsonFile() {
        return jsonFile;
    }

    public static void addToLog(String action) {

        if(isClient) return;

        FileWriter writer = null;
        try {
            writer = new FileWriter(logFile, true);
            writer.write(ZonedDateTime.now() + ": " + action + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        logFile = new File("./.kabuecher/log/" + System.currentTimeMillis() + ".log");
        if(!logFile.exists()) {
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
        }
        addToLog("Starting application");

        jsonFile = new JSONFile("./.kabuecher/config.json",
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        return new JSONObject("{\"printer\":\"\", \"label_printer\":\"\"}");
                    }

                    @Override
                    public String objectName() {
                        return "printerConfig";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        return new JSONObject("{\"mode\":\"client\"}");
                    }

                    @Override
                    public String objectName() {
                        return "config";
                    }
                }
        );

        if(jsonFile.isNew()) {
            jsonFile.save();
            new DesktopSetupFrame();
        }

        sevdesk_api_token = jsonFile.get("sevdesk_api_token").getString("value");

        String mode = jsonFile.get("config").getString("mode");

        if(mode.equalsIgnoreCase("client")) {
            isClient = true;

            boolean f = false;

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (Font font : Arrays.stream(ge.getAllFonts()).toList()) {
                if(font.getName().toLowerCase().contains("oswald")) {
                    f = true;
                    break;
                }
            }

            if(!f) {
                JFrame frame = new JFrame("Install Font");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(300, 150);
                frame.setLayout(new BorderLayout());
                frame.setLocationRelativeTo(null);

                JLabel messageLabel = new JLabel("Eine benötigte Font ist nicht installiert: OSWALD", SwingConstants.CENTER);
                frame.add(messageLabel, BorderLayout.CENTER);

                JButton installButton = new JButton("Font herunterladen");
                frame.add(installButton, BorderLayout.SOUTH);

                String fontLink = "https://fonts.google.com/share?selection.family=Oswald";

                installButton.addActionListener(_ -> {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new URI(fontLink));
                        } else {
                            JOptionPane.showMessageDialog(frame, "Unable to open the link.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error opening link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                return;
            }

            displayDriver = new DisplayDriver();
            bodyHandler = displayDriver.getBodyHandler();

            displayDriver.show(true);

            Thread timeoutThread = new Thread(() -> {
                while(true) {
                    if(timeout) {
                        if(System.currentTimeMillis() > (lastAction + (1000 * 60 * 30))) {
                            System.exit(0);
                        }
                    } else if(System.currentTimeMillis() - lastAction > (1000 * 60 * 5)) {
                        Main.username = null;
                        Main.passwordHash = null;

                        new LoginFlow();
                        JLabel time = displayDriver.getPanel().getTimeLabel();
                        time.setText("00:00");
                        timeout = true;
                        Main.displayDriver.getPanel().getLogoutButton().setEnabled(false);
                        JOptionPane.showMessageDialog(null, "Sie wurden aus Sicherheitsgründen ausgeloggt.", "ABMELDUNG", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JLabel time = displayDriver.getPanel().getTimeLabel();
                        long timeLeft = (1000 * 60 * 5) - (System.currentTimeMillis() - lastAction);
                        time.setText(String.format("%02d:%02d", timeLeft / (1000 * 60), (timeLeft / 1000) % 60));
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            timeoutThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread(timeoutThread::interrupt));

            new LoginFlow();
            displayDriver.getPanel().getLogoutButton().addActionListener(_ -> {
                Main.username = null;
                Main.passwordHash = null;

                new LoginFlow();
                JLabel time = displayDriver.getPanel().getTimeLabel();
                time.setText("00:00");
                timeout = true;
                Main.displayDriver.getPanel().getLogoutButton().setEnabled(false);
            });
        } else if(mode.equalsIgnoreCase("server")) {
            MySQLRunner mySQLRunner = new MySQLRunner();
            mySQLRunner.start();

            mysqlDriver = new MySQLDriver();

            ConsoleInputHandler consoleInputHandler = new ConsoleInputHandler();
            consoleInputHandler.addCommand("user", new UserCommand());

            consoleInputHandler.start();

            UndertowServer server = new UndertowServer();
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consoleInputHandler.stop();
                mySQLRunner.stop();
                server.stop();
            }));
        }
    }
}
