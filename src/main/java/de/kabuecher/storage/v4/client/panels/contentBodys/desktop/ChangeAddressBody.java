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
public class ChangeAddressBody extends BodyType {

    /**
     * Creates new form LoginBody
     */
    public ChangeAddressBody() {
        initComponents();

        addLabel("action_label", action_label);
        addTextField("recipient_field", recipient_field);
        addTextField("first_adr_field", first_adr_field);
        addTextField("second_adr_field", second_adr_field);
        addTextField("city_field", city_field);
        addTextField("zip_field", zip_field);
        addTextField("country_field", country_field);
        addButton("continue_button", continue_button);
        recipient_field.setEnabled(false);
        first_adr_field.setEnabled(false);
        second_adr_field.setEnabled(false);
        city_field.setEnabled(false);
        zip_field.setEnabled(false);
        country_field.setEnabled(false);
    }

    @Override
    public void init() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {

            if(event instanceof KeyEvent keyEvent) {

                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = keyEvent.getKeyCode();

                    if (keyCode == KeyEvent.VK_BACK_SPACE) {
                        SwingUtilities.invokeLater(() -> {
                        });
                        keyEvent.consume();
                    } else if (keyCode == KeyEvent.VK_ENTER) {
                        SwingUtilities.invokeLater(() -> {

                        });
                        keyEvent.consume();
                    } else if (Character.isDefined(keyEvent.getKeyChar())) {
                        char c = keyEvent.getKeyChar();
                        SwingUtilities.invokeLater(() -> {
                        });
                        keyEvent.consume();
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

        action_label = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        recipient_field = new javax.swing.JTextField();
        first_adr_field = new javax.swing.JTextField();
        second_adr_field = new javax.swing.JTextField();
        city_field = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        zip_field = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        country_field = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        continue_button = new javax.swing.JButton();

        setBackground(new java.awt.Color(17, 21, 28));
        setForeground(new java.awt.Color(231, 231, 231));
        setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        setMinimumSize(new java.awt.Dimension(1218, 1012));
        setSize(new java.awt.Dimension(1218, 1012));

        action_label.setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        action_label.setForeground(new java.awt.Color(231, 231, 231));
        action_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Oswald", 0, 72)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(231, 231, 231));
        jLabel1.setText("Lieferaddresse");

        recipient_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        recipient_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        first_adr_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        first_adr_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        second_adr_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        second_adr_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        city_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        city_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel2.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(231, 231, 231));
        jLabel2.setText("2. Addresszeile");

        jLabel3.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(231, 231, 231));
        jLabel3.setText("Empfänger");

        jLabel4.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(231, 231, 231));
        jLabel4.setText("1. Addresszeile (c/o, z.Hd., ...)");

        jLabel5.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(231, 231, 231));
        jLabel5.setText("Ort");

        zip_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        zip_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel6.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(231, 231, 231));
        jLabel6.setText("Land");

        country_field.setEditable(false);
        country_field.setFont(new java.awt.Font("Oswald", 0, 32)); // NOI18N
        country_field.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        country_field.setText("Deutschland");
        country_field.setEnabled(false);

        jLabel7.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(231, 231, 231));
        jLabel7.setText("Postleitzahl");

        jCheckBox1.setBackground(new java.awt.Color(17, 21, 28));
        jCheckBox1.setFont(new java.awt.Font("Oswald", 0, 36)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(231, 231, 231));
        jCheckBox1.setText("Lieferaddresse ist nicht Rechnungsadresse");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        continue_button.setBackground(new java.awt.Color(30, 150, 252));
        continue_button.setFont(new java.awt.Font("Oswald", 0, 36)); // NOI18N
        continue_button.setForeground(new java.awt.Color(231, 231, 231));
        continue_button.setText("Weiter");
        continue_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continue_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 125, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(first_adr_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(second_adr_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(363, 363, 363))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(zip_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(country_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel7))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(recipient_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(city_field, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addGap(304, 304, 304))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(415, 415, 415)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(451, 451, 451)
                        .addComponent(continue_button, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(recipient_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(city_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addComponent(first_adr_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(zip_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(second_adr_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(country_field, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addGap(18, 18, 18)
                .addComponent(continue_button, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(243, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
        if(jCheckBox1.isSelected()) {
            recipient_field.setEnabled(true);
            first_adr_field.setEnabled(true);
            second_adr_field.setEnabled(true);
            city_field.setEnabled(true);
            zip_field.setEnabled(true);
        } else {
            recipient_field.setEnabled(false);
            first_adr_field.setEnabled(false);
            second_adr_field.setEnabled(false);
            city_field.setEnabled(false);
            zip_field.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void continue_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continue_buttonActionPerformed
        handleActionEvent("continue_button", evt);
    }//GEN-LAST:event_continue_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel action_label;
    private javax.swing.JTextField city_field;
    private javax.swing.JButton continue_button;
    private javax.swing.JTextField country_field;
    private javax.swing.JTextField first_adr_field;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField recipient_field;
    private javax.swing.JTextField second_adr_field;
    private javax.swing.JTextField zip_field;
    // End of variables declaration//GEN-END:variables
}
