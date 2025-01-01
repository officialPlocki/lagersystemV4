/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package de.kabuecher.storage.v4.client.panels.contentBodys.desktop;

import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.component.SummarizingBodyCartComponent;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.component.SummarizingBodyOrderComponent;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.ItemObject;
import de.kabuecher.storage.v4.client.utils.Translateables;
import de.kabuecher.storage.v4.client.sevdesk.offer.Offer;
import org.json.JSONObject;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author plocki
 */
public class SummarizingBody extends BodyType {

    /**
     * Creates new form LoginBody
     */
    public SummarizingBody(JSONObject units, boolean compress, boolean disableComponentButtons) {
        initComponents();

        addLabel("action_label", action_label);
        addButton("cancel_button", cancel_button);
        addButton("confirm_button", confirm_button);

        // Create a container for the components
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Arrange components vertically

        //if compress true, show only a single component for each ean (add up all amounts)
        if(compress) {
            HashMap<String, ItemObject> items = new HashMap<>();
            for (String unit : units.keySet()) {
                JSONObject unitObject = units.getJSONObject(unit);
                for (String stack : unitObject.getJSONObject("stacks").keySet()) {
                    JSONObject stackObject = unitObject.getJSONObject("stacks").getJSONObject(stack);
                    for (String box : stackObject.keySet()) {
                        JSONObject boxObject = stackObject.getJSONObject(box);
                        for (String ean : boxObject.keySet()) {
                            if(items.containsKey(ean)) {
                                items.put(ean, new ItemObject() {
                                    @Override
                                    public String getName() {
                                        return new Translateables().getNameByEAN(ean);
                                    }

                                    @Override
                                    public String getEAN() {
                                        return ean;
                                    }

                                    @Override
                                    public String getPartID() {
                                        return new Translateables().getPartIDByEAN(ean);
                                    }

                                    @Override
                                    public int amount() {
                                        return items.get(ean).amount() + boxObject.getInt(ean);
                                    }

                                    @Override
                                    public JSONObject storageUnit() {
                                        return items.get(ean).storageUnit();
                                    }
                                });
                            } else {
                                items.put(ean, new ItemObject() {
                                    @Override
                                    public String getName() {
                                        return new Translateables().getNameByEAN(ean);
                                    }

                                    @Override
                                    public String getEAN() {
                                        return ean;
                                    }

                                    @Override
                                    public String getPartID() {
                                        return new Translateables().getPartIDByEAN(ean);
                                    }

                                    @Override
                                    public int amount() {
                                        return boxObject.getInt(ean);
                                    }

                                    @Override
                                    public JSONObject storageUnit() {
                                        return new JSONObject();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            for (ItemObject item : items.values()) {
                SummarizingBodyCartComponent component = new SummarizingBodyCartComponent(null, item.getName(), item.amount());

                if(disableComponentButtons) {
                    component.getButton("delete_button").setEnabled(false);
                    component.getButton("change_button").setEnabled(false);
                }

                panel.add(component);
                addComponent(item.getEAN(), component);
            }
        } else {
            for (String unit : units.keySet()) {
                JSONObject unitObject = units.getJSONObject(unit);
                for (String stack : unitObject.getJSONObject("stacks").keySet()) {
                    JSONObject stackObject = unitObject.getJSONObject("stacks").getJSONObject(stack);
                    for (String box : stackObject.keySet()) {
                        JSONObject boxObject = stackObject.getJSONObject(box);
                        for (String ean : boxObject.keySet()) {
                            JSONObject informational = new JSONObject();
                            informational.put("unit", unit);
                            informational.put("stack", stack);
                            informational.put("box", box);

                            SummarizingBodyCartComponent component = new SummarizingBodyCartComponent(informational, ean, boxObject.getInt(ean));

                            panel.add(component);
                            addComponent(ean, component);
                        }
                    }
                }
            }
        }

        jScrollPane1.setViewportView(panel);
    }

    public SummarizingBody(List<Offer> offers) {
        initComponents();

        addLabel("action_label", action_label);
        addButton("cancel_button", cancel_button);
        addButton("confirm_button", confirm_button);

        // Create a container for the components
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Arrange components vertically

        for (Offer offer : offers) {
            SummarizingBodyOrderComponent component = new SummarizingBodyOrderComponent(offer);

            panel.add(component);
            addComponent(offer.getId(), component);
        }

        jScrollPane1.setViewportView(panel);
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cancel_button = new javax.swing.JButton();
        confirm_button = new javax.swing.JButton();
        action_label = new javax.swing.JLabel();

        setBackground(new java.awt.Color(17, 21, 28));
        setForeground(new java.awt.Color(231, 231, 231));
        setFont(new java.awt.Font("Oswald", 0, 18)); // NOI18N
        setMinimumSize(new java.awt.Dimension(1218, 1012));
        setPreferredSize(new java.awt.Dimension(1218, 1012));
        setSize(new java.awt.Dimension(1218, 1012));

        jLabel1.setFont(new java.awt.Font("Oswald", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(231, 231, 231));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Zusammenfassung");

        jScrollPane1.setMaximumSize(new java.awt.Dimension(715, 800000));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(715, 834));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(715, 834));
        jScrollPane1.setSize(new java.awt.Dimension(715, 834));

        cancel_button.setBackground(new java.awt.Color(244, 68, 46));
        cancel_button.setFont(new java.awt.Font("Oswald", 0, 36)); // NOI18N
        cancel_button.setForeground(new java.awt.Color(231, 231, 231));
        cancel_button.setText("Abbrechen");
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_buttonActionPerformed(evt);
            }
        });

        confirm_button.setBackground(new java.awt.Color(30, 150, 252));
        confirm_button.setFont(new java.awt.Font("Oswald", 0, 36)); // NOI18N
        confirm_button.setForeground(new java.awt.Color(231, 231, 231));
        confirm_button.setText("Bestätigen");
        confirm_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirm_buttonActionPerformed(evt);
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
                        .addContainerGap()
                        .addComponent(cancel_button, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(confirm_button, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(444, 444, 444)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(243, 243, 243)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 254, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 822, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancel_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(confirm_button, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(action_label, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancel_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_buttonActionPerformed
        handleActionEvent("cancel_button", evt);
    }//GEN-LAST:event_cancel_buttonActionPerformed

    private void confirm_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirm_buttonActionPerformed
        handleActionEvent("confirm_button", evt);
    }//GEN-LAST:event_confirm_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel action_label;
    private javax.swing.JButton cancel_button;
    private javax.swing.JButton confirm_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
