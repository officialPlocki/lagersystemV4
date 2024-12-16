package de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public abstract class ComponentType extends JPanel {

    private final HashMap<String, Runnable> actionMap = new HashMap<>();
    private final HashMap<String, JTextField> textFieldMap = new HashMap<>();
    private final HashMap<String, JLabel> labelMap = new HashMap<>();
    private final HashMap<String, JButton> buttonMap = new HashMap<>();

    public JSONObject getUnitInfo() {
        return null;
    }

    protected void addButton(String key, JButton button) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (button == null) {
            throw new IllegalArgumentException("Button must not be null.");
        }

        // Add to the HashMap
        buttonMap.put(key, button);
    }

    protected JButton getButton(String key) {
        return buttonMap.get(key);
    }

    protected void addTextField(String key, JTextField textField) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (textField == null) {
            throw new IllegalArgumentException("TextField must not be null.");
        }

        // Add to the HashMap
        textFieldMap.put(key, textField);

    }

    protected JTextField getTextField(String key) {
        return textFieldMap.get(key);
    }

    protected JLabel getLabel(String key) {
        return labelMap.get(key);
    }

    protected void addLabel(String key, JLabel label) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (label == null) {
            throw new IllegalArgumentException("Label must not be null.");
        }

        // Add to the HashMap
        labelMap.put(key, label);
    }

    public JLabel getActionLabel() {
        return null;
    }

    protected void handleActionEvent(String key, ActionEvent event) {
        Runnable runnable = actionMap.get(key);
        if (runnable != null) {
            ((ActionEventRunnable) runnable).handleActionEvent(event);
        }
    }

    public void addAction(String key, Runnable runnable) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable must not be null.");
        }
        if (!(runnable instanceof ComponentType.ActionEventRunnable)) {
            throw new IllegalArgumentException("Runnable must be an instance of ActionEventRunnable.");
        }

        // Add to the HashMap

        actionMap.put(key, runnable);
    }

    public interface ActionEventRunnable extends Runnable {
        void handleActionEvent(ActionEvent event);
    }

}
