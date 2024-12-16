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
public class ScanBody extends BodyType {

    /**
     * Creates new form LoginBody
     */
    public ScanBody() {
        initComponents();

        addTextField("scanTextfield", scanTextfield);
        addLabel("arg_label", arg_label);
        addLabel("action_label", action_label);
    }

    @Override
    public void init() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {

            if(event instanceof KeyEvent keyEvent) {

                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = keyEvent.getKeyCode();

                    if (keyCode == KeyEvent.VK_BACK_SPACE) {
                        if(!(scanTextfield.isFocusOwner())) {
                            SwingUtilities.invokeLater(() -> {
                                String text = scanTextfield.getText();
                                if (!text.isEmpty()) {
                                    scanTextfield.setText(text.substring(0, text.length() - 1));
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

                        if(!(scanTextfield.isFocusOwner())) {
                            SwingUtilities.invokeLater(() -> {
                                scanTextfield.setText(scanTextfield.getText() + c);
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

        arg_label = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        scanTextfield = new javax.swing.JTextField();
        action_label = new javax.swing.JLabel();

        setBackground(new java.awt.Color(17, 21, 28));
        setForeground(new java.awt.Color(231, 231, 231));
        setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        setMinimumSize(new java.awt.Dimension(1218, 1012));
        setSize(new java.awt.Dimension(1218, 1012));

        arg_label.setFont(new java.awt.Font("Oswald", 1, 72)); // NOI18N
        arg_label.setForeground(new java.awt.Color(231, 231, 231));
        arg_label.setText("ARG1: arg2");

        jLabel2.setFont(new java.awt.Font("Oswald", 1, 72)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(231, 231, 231));
        jLabel2.setText("Bitte Scannen:");

        scanTextfield.setFont(new java.awt.Font("Oswald", 0, 62)); // NOI18N
        scanTextfield.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        scanTextfield.setToolTipText("");
        scanTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanTextfieldActionPerformed(evt);
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
                        .addGap(427, 427, 427)
                        .addComponent(arg_label))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(scanTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 795, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(226, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(393, 393, 393)
                    .addComponent(jLabel2)
                    .addContainerGap(393, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(arg_label)
                .addGap(129, 129, 129)
                .addComponent(scanTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jLabel2)
                    .addContainerGap(879, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void scanTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanTextfieldActionPerformed
        handleActionEvent("scanTextfield", evt);
    }//GEN-LAST:event_scanTextfieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel action_label;
    private javax.swing.JLabel arg_label;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField scanTextfield;
    // End of variables declaration//GEN-END:variables
}