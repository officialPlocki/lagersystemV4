package de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public abstract class BodyType extends JPanel {

    private final HashMap<String, ComponentType> components = new HashMap<>();
    private final HashMap<String, ActionEventRunnable> actionMap = new HashMap<>();
    private final HashMap<String, JTextField> textFieldMap = new HashMap<>();
    private final HashMap<String, JLabel> labelMap = new HashMap<>();
    private final HashMap<String, JButton> buttonMap = new HashMap<>();

    public void init() {

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

    public JProgressBar getProgressBar() {
        return null;
    }

    public void addComponent(String key, ComponentType component) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (component == null) {
            throw new IllegalArgumentException("Component must not be null.");
        }

        // Add to the HashMap
        components.put(key, component);
    }

    public HashMap<String, ComponentType> getTypeComponents() {
        return components;
    }

    public JButton getButton(String key) {
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

    public JTextField getTextField(String key) {
        return textFieldMap.get(key);
    }

    public JLabel getLabel(String key) {
        return labelMap.get(key);
    }

    public void addLabel(String key, JLabel label) {
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

    protected void handleKeyEvent(String key, KeyEvent event) {
        ActionEventRunnable runnable = actionMap.get(key);
        if (runnable != null) {
            runnable.handleKeyEvent(event);
        }
    }

    protected void handleActionEvent(String key, ActionEvent event) {
        ActionEventRunnable runnable = actionMap.get(key);
        if (runnable != null) {
            runnable.handleActionEvent(event);
        }
    }

    public void addAction(String key, ActionEventRunnable actionRunnable) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (actionRunnable == null) {
            throw new IllegalArgumentException("ActionEventRunnable must not be null.");
        }

        // Add to the HashMap
        actionMap.put(key, actionRunnable);
    }


    public interface ActionEventRunnable extends Runnable {
        void handleActionEvent(ActionEvent event);
        void handleKeyEvent(KeyEvent event);
    }

}
