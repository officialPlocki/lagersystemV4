package de.kabuecher.storage.v4.client.desktop;

import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.util.Objects;

public class DesktopContentBodyHandler {

    private final JPanel contentBody;
    private JFrame frame;

    public DesktopContentBodyHandler(JFrame frame, JPanel contentBody) {
        this.frame = frame;
        this.contentBody = contentBody;
    }

    public void setContentBody(BodyType bodyType) {
        contentBody.removeAll();

        for (AWTEventListener awtEventListener : Toolkit.getDefaultToolkit().getAWTEventListeners()) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
        }

        contentBody.add(Objects.requireNonNullElseGet(bodyType, JPanel::new));
        contentBody.repaint();

        if(bodyType != null) {
            bodyType.init();
        }

        frame.pack();
    }

}
