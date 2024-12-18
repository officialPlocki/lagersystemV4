package de.kabuecher.storage.v4;

import co.plocki.json.JSONFile;
import co.plocki.json.JSONValue;
import de.kabuecher.storage.v4.client.desktop.DesktopContentBodyHandler;
import de.kabuecher.storage.v4.client.desktop.DisplayDriver;
import de.kabuecher.storage.v4.client.panels.DesktopSetupFrame;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Arrays;

public class Main {

    private static JSONFile jsonFile;
    public static String sevdesk_api_token;
    public static DisplayDriver displayDriver;
    public static DesktopContentBodyHandler bodyHandler;


    public static JSONFile getJsonFile() {
        return jsonFile;
    }

    public static void main(String[] args) throws Exception {

        boolean f = false;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font font : Arrays.stream(ge.getAllFonts()).toList()) {
            if(font.getName().toLowerCase().contains("oswald")) {
                f = true;
                break;
            }
        }

        if(!f) {
            // Create a JFrame for the popup
            JFrame frame = new JFrame("Install Font");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null); // Center the frame on screen

            // Add a message label
            JLabel messageLabel = new JLabel("Eine benötigte Font ist nicht installiert: OSWALD", SwingConstants.CENTER);
            frame.add(messageLabel, BorderLayout.CENTER);

            // Add a button to open the link
            JButton installButton = new JButton("Font herunterladen");
            frame.add(installButton, BorderLayout.SOUTH);

            // Define the link to the font
            String fontLink = "https://fonts.google.com/share?selection.family=Oswald";

            // Add an action listener to the button
            installButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
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
                }
            });

            // Make the frame visible
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            return;
        }

        jsonFile = new JSONFile("./.kabuecher/config.json",
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        return new JSONObject("{\"value\":\"your_api_key_here\"}");
                    }

                    @Override
                    public String objectName() {
                        return "sevdesk_api_token";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        //label printer & normal printer
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
                        //label printer & normal printer
                        return new JSONObject("{\"api_key\":\"\"}");
                    }

                    @Override
                    public String objectName() {
                        return "sendcloud";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        //label printer & normal printer
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
            displayDriver = new DisplayDriver();
            bodyHandler = displayDriver.getBodyHandler();

            displayDriver.show(true);
        } else if(mode.equalsIgnoreCase("server")) {

        }
    }
}
