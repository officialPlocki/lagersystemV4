/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package de.kabuecher.storage.v4.client.panels.contentBodys.desktop;

import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 *
 * @author plocki
 */
public class EditAmountBody extends BodyType {

    /**
     * Creates new form LoginBody
     */
    public EditAmountBody() {
        initComponents();

        addTextField("amountField", amountField);
        addLabel("enter_amount", enter_amount);
        addLabel("action_label", action_label);
    }

    @Override
    public void init() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {

            if(event instanceof KeyEvent keyEvent) {

                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = keyEvent.getKeyCode();

                    if (keyCode == KeyEvent.VK_BACK_SPACE) {
                        if(!(amountField.isFocusOwner())) {
                            SwingUtilities.invokeLater(() -> {
                                String text = amountField.getText();
                                if (!text.isEmpty()) {
                                    amountField.setText(text.substring(0, text.length() - 1));
                                }
                            });
                            keyEvent.consume();
                        }
                    } else if (keyCode == KeyEvent.VK_ENTER) {
                        SwingUtilities.invokeLater(() -> {
                            handleKeyEvent("enter", keyEvent);
                        });
                        keyEvent.consume();
                    } else if (Character.isDefined(keyEvent.getKeyChar())) {
                        char c = keyEvent.getKeyChar();
                        if(!(amountField.isFocusOwner())) {
                            SwingUtilities.invokeLater(() -> {
                                amountField.setText(amountField.getText() + c);
                            });
                            keyEvent.consume();
                        }
                    }
                }
            }

        }, AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public JLabel getActionLabel() {
        return action_label;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enter_amount = new javax.swing.JLabel();
        amountField = new javax.swing.JTextField();
        action_label = new javax.swing.JLabel();

        setBackground(new java.awt.Color(17, 21, 28));
        setForeground(new java.awt.Color(231, 231, 231));
        setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        setMinimumSize(new java.awt.Dimension(1218, 1012));
        setSize(new java.awt.Dimension(1218, 1012));

        enter_amount.setFont(new java.awt.Font("Oswald", 1, 72)); // NOI18N
        enter_amount.setForeground(new java.awt.Color(231, 231, 231));
        enter_amount.setText("Anzahl Eingeben");

        amountField.setFont(new java.awt.Font("Oswald", 0, 62)); // NOI18N
        amountField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amountField.setToolTipText("");
        amountField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountFieldActionPerformed(evt);
            }
        });

        action_label.setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        action_label.setForeground(new java.awt.Color(231, 231, 231));
        action_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(amountField, javax.swing.GroupLayout.PREFERRED_SIZE, 795, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(364, 364, 364)
                        .addComponent(enter_amount))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(226, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(236, 236, 236)
                .addComponent(enter_amount)
                .addGap(35, 35, 35)
                .addComponent(amountField, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void amountFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountFieldActionPerformed
        handleActionEvent("amountField", evt);
    }//GEN-LAST:event_amountFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel action_label;
    private javax.swing.JTextField amountField;
    private javax.swing.JLabel enter_amount;
    // End of variables declaration//GEN-END:variables
}