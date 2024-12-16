package de.kabuecher.storage.v4.client.desktop;

import de.kabuecher.storage.v4.client.panels.DesktopMainPanel;

import javax.swing.*;
import java.awt.*;

public class DisplayDriver {

    private final JFrame frame;
    private DesktopContentBodyHandler bodyHandler;

    public DesktopContentBodyHandler getBodyHandler() {
        return bodyHandler;
    }

    public DisplayDriver() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1538, 1060));

        DesktopMainPanel panel = new DesktopMainPanel(frame);
        bodyHandler = panel.getBodyHandler();
        frame.add(panel);

        frame.pack();
    }

    public void show(boolean show) {
        frame.setVisible(show);
    }

    public void close() {
        frame.dispose();
    }

}
