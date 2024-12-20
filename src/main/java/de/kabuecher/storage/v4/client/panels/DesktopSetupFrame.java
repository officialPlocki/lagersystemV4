package de.kabuecher.storage.v4.client.panels;

import de.kabuecher.storage.v4.Main;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DesktopSetupFrame extends JFrame {

    private JComboBox<String> labelPrinterCombo;
    private JComboBox<String> normalPrinterCombo;
    private JButton saveButton;
    private JFrame frame;

    public DesktopSetupFrame() {

        Main.addToLog("Starting desktop setup frame");

        frame = this;
        setTitle("Printer Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Fetch available printers
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }

        // Label Printer Dropdown
        add(new JLabel("Select Label Printer:"));
        labelPrinterCombo = new JComboBox<>(printerNames);
        add(labelPrinterCombo);

        // Normal Printer Dropdown
        add(new JLabel("Select Normal Printer:"));
        normalPrinterCombo = new JComboBox<>(printerNames);
        add(normalPrinterCombo);

        // Save Button
        saveButton = new JButton("Save Printers");
        saveButton.addActionListener(new SaveButtonListener());
        add(saveButton);

        setVisible(true);

        Main.addToLog("Desktop setup frame started");
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String labelPrinter = (String) labelPrinterCombo.getSelectedItem();
            String normalPrinter = (String) normalPrinterCombo.getSelectedItem();

            // Call save method (implement this as needed)
            try {
                savePrinterSettings(labelPrinter, normalPrinter);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            frame.dispose();
            Main.displayDriver.show(true);

            JOptionPane.showMessageDialog(DesktopSetupFrame.this,
                    "Printers saved successfully!\nLabel Printer: " + labelPrinter + "\nNormal Printer: " + normalPrinter,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Empty method for saving printer settings to a file
    private void savePrinterSettings(String labelPrinter, String normalPrinter) throws IOException {
        JSONObject object = Main.getJsonFile().get("printerConfig");
        object.put("label_printer", labelPrinter);
        object.put("printer", normalPrinter);
        Main.getJsonFile().save();
    }
}