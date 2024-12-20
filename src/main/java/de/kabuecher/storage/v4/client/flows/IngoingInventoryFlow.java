package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.EditAmountBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ScanBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.SummarizingBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.ComponentType;
import de.kabuecher.storage.v4.storage.UnitManagement;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class IngoingInventoryFlow {

    private final JSONObject units = new JSONObject();
    private SummarizingBody summaryBody;
    private String ean = "";
    private ScanBody eanScanBody;
    private ScanBody boxScanBody;

    public IngoingInventoryFlow() {

        Main.addToLog("Starting IngoingInventoryFlow");

        units.put("store1", new JSONObject().put("stacks", new JSONObject().put("A", new JSONObject())));

        eanScanBody = new ScanBody();
        eanScanBody.getLabel("arg_label").setText("EAN");
        eanScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {}

            @Override
            public void handleKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();
                System.out.println("triggered");

                if(keyCode == KeyEvent.VK_ENTER) {
                    analyzeEANEnterEvent(eanScanBody.getTextField("scanTextfield"), eanScanBody.getActionLabel());
                }
            }

            @Override
            public void run() {}
        });

        Main.addToLog("Setting content body to eanScanBody");
        eanScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");

        Main.bodyHandler.setContentBody(eanScanBody);

        Main.addToLog("Setting boxScanBody");
        boxScanBody = new ScanBody();
        boxScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");
        boxScanBody.getLabel("arg_label").setText("BOX");
        boxScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {}

            @Override
            public void handleKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();

                if(keyCode == KeyEvent.VK_ENTER) {
                    analyzeBOXEnterEvent(boxScanBody.getTextField("scanTextfield"), boxScanBody.getActionLabel());
                }
            }

            @Override
            public void run() {}
        });
    }

    private void analyzeEANEnterEvent(JTextField field, JLabel actionLabel) {
        Main.addToLog("Analyzing EAN enter event");

        String text = field.getText().toUpperCase();
        if(text.startsWith("BX")) {
            Main.addToLog("Text starts with BX");

            actionLabel.setText("Boxcode hier nicht erlaubt!");
            field.setText("");

            new Thread(() -> {

                try {
                    Thread.sleep(1000*15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                actionLabel.setText("Masseneinbuchung in versch. Kisten = Kein Edit");
            }).start();
        } else if(text.length() == 13 || text.length() == 12) {
            Main.addToLog("Text length is 13 or 12");
            ean = text;

            Main.bodyHandler.setContentBody(boxScanBody);
        } else if(text.equalsIgnoreCase("00")) {
            Main.addToLog("Text is 00");

            boolean diff = false;

            //check if units contains multiple times the same item from different boxes (not multiple times from the same box)
            JSONObject cache = new JSONObject();
            for(String unit : units.keySet()) {
                for(String stack : units.getJSONObject(unit).getJSONObject("stacks").keySet()) {
                    for(String box : units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).keySet()) {
                        JSONObject boxContent = units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).getJSONObject(box);
                        for(String ean : boxContent.keySet()) {
                            if(cache.has(ean)) {
                                diff = true;
                            } else {
                                cache.put(ean, 1);
                            }
                        }
                    }
                }
            }


            summaryBody = new SummarizingBody(units, diff, diff);

            for(String id : summaryBody.getTypeComponents().keySet()) {
                ComponentType component = summaryBody.getTypeComponents().get(id);
                component.addAction("change_button", new ComponentType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        changeButtonActionEvent(component.getUnitInfo(), id);
                    }

                    @Override
                    public void run() {

                    }
                });
                component.addAction("delete_button", new ComponentType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        deleteButtonActionEvent(component.getUnitInfo(), id);
                    }

                    @Override
                    public void run() {

                    }
                });
            }

            summaryBody.addAction("confirm_button", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    handleConfirm();
                }

                @Override
                public void handleKeyEvent(KeyEvent event) {

                }

                @Override
                public void run() {

                }
            });

            summaryBody.addAction("cancel_button", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    Main.bodyHandler.setContentBody(null);
                }

                @Override
                public void handleKeyEvent(KeyEvent event) {

                }

                @Override
                public void run() {

                }
            });

            Main.bodyHandler.setContentBody(summaryBody);
        }

        Main.addToLog("Finished analyzing EAN enter event");
    }

    private void handleConfirm() {
        Main.addToLog("Handling confirm");

        UnitManagement management = new UnitManagement();

        for(String unit : units.keySet()) {
            for(String stack : units.getJSONObject(unit).getJSONObject("stacks").keySet()) {
                for(String box : units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).keySet()) {
                    JSONObject boxContent = units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).getJSONObject(box);
                    for(String ean : boxContent.keySet()) {
                        int amount = boxContent.getInt(ean);
                        management.addItemToBox(unit, stack, box, ean, amount);
                    }
                }
            }
        }

        management.saveChanges();

        Main.bodyHandler.setContentBody(null);
    }

    private void deleteButtonActionEvent(JSONObject unitInfo, String eanI) {
        Main.addToLog("Handling delete button action event");

        if(units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").getJSONObject(unitInfo.getString("box")).keySet().size() == 1) {
            summaryBody.getActionLabel().setText("Artikel nicht löschbar, Buchung sonst leer!");
            return;
        }

        units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").getJSONObject(unitInfo.getString("box")).remove(eanI);


        boolean diff = false;

        //check if units contains multiple times the same item from different boxes (not multiple times from the same box)
        JSONObject cache = new JSONObject();
        for(String unit : units.keySet()) {
            for(String stack : units.getJSONObject(unit).getJSONObject("stacks").keySet()) {
                for(String box : units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).keySet()) {
                    JSONObject boxContent = units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).getJSONObject(box);
                    for(String ean : boxContent.keySet()) {
                        if(cache.has(ean)) {
                            diff = true;
                        } else {
                            cache.put(ean, 1);
                        }
                    }
                }
            }
        }

        summaryBody = new SummarizingBody(units, diff, diff);

        Main.addToLog("Setting actions for summary body");

        for(String id : summaryBody.getTypeComponents().keySet()) {
            ComponentType component = summaryBody.getTypeComponents().get(id);
            component.addAction("change_button", new ComponentType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    changeButtonActionEvent(component.getUnitInfo(), id);
                }

                @Override
                public void run() {

                }
            });
            component.addAction("delete_button", new ComponentType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    deleteButtonActionEvent(component.getUnitInfo(), id);
                }

                @Override
                public void run() {

                }
            });
        }

        summaryBody.addAction("confirm_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                handleConfirm();
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {

            }

            @Override
            public void run() {

            }
        });

        summaryBody.addAction("cancel_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                Main.bodyHandler.setContentBody(null);
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {

            }

            @Override
            public void run() {

            }
        });

        Main.bodyHandler.setContentBody(summaryBody);
        Main.addToLog("Finished handling delete button action event");
    }

    private void changeButtonActionEvent(JSONObject unitInfo, String ean) {
        Main.addToLog("Handling change button action event");
        EditAmountBody editAmountBody = new EditAmountBody();

        editAmountBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {}

            @Override
            public void handleKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();

                if(keyCode == KeyEvent.VK_ENTER) {
                    analyzeAmountEnterEvent(unitInfo, editAmountBody.getTextField("amountField"), editAmountBody.getActionLabel(), ean);
                }
            }

            @Override
            public void run() {}
        });

        Main.bodyHandler.setContentBody(editAmountBody);
        Main.addToLog("Finished handling change button action event");
    }

    private void analyzeAmountEnterEvent(JSONObject unitInfo, JTextField field, JLabel actionLabel, String ean) {
        Main.addToLog("Analyzing amount enter event");
        String text = field.getText();
        if(text.isEmpty()) {
            Main.addToLog("Text is empty");
            actionLabel.setText("Bitte geben Sie eine Zahl ein!");
            field.setText("");

            new Thread(() -> {

                try {
                    Thread.sleep(1000*15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                actionLabel.setText("Masseneinbuchung in versch. Kisten = Kein Edit");
            }).start();
        } else {
            Main.addToLog("Text is not empty");
            int amount = Integer.parseInt(text);
            units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").getJSONObject(unitInfo.getString("box")).put(ean, amount);


            boolean diff = false;

            //check if units contains multiple times the same item from different boxes (not multiple times from the same box)
            JSONObject cache = new JSONObject();
            for(String unit : units.keySet()) {
                for(String stack : units.getJSONObject(unit).getJSONObject("stacks").keySet()) {
                    for(String box : units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).keySet()) {
                        JSONObject boxContent = units.getJSONObject(unit).getJSONObject("stacks").getJSONObject(stack).getJSONObject(box);
                        for(String eanA : boxContent.keySet()) {
                            if(cache.has(eanA)) {
                                diff = true;
                            } else {
                                cache.put(eanA, 1);
                            }
                        }
                    }
                }
            }

            summaryBody = new SummarizingBody(units, diff, diff);

            for(String id : summaryBody.getTypeComponents().keySet()) {
                ComponentType component = summaryBody.getTypeComponents().get(id);
                component.addAction("change_button", new ComponentType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        changeButtonActionEvent(component.getUnitInfo(), id);
                    }

                    @Override
                    public void run() {

                    }
                });
                component.addAction("delete_button", new ComponentType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        deleteButtonActionEvent(component.getUnitInfo(), id);
                    }

                    @Override
                    public void run() {

                    }
                });
            }

            summaryBody.addAction("confirm_button", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    handleConfirm();
                }

                @Override
                public void handleKeyEvent(KeyEvent event) {

                }

                @Override
                public void run() {

                }
            });

            summaryBody.addAction("cancel_button", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    Main.bodyHandler.setContentBody(null);
                }

                @Override
                public void handleKeyEvent(KeyEvent event) {

                }

                @Override
                public void run() {

                }
            });

            Main.bodyHandler.setContentBody(summaryBody);
            Main.addToLog("Finished analyzing amount enter event");
        }
    }

    private void analyzeBOXEnterEvent(JTextField field, JLabel actionLabel) {
        String text = field.getText();
        System.out.println(text);
        if(text.startsWith("BX")) {
            Main.addToLog("Text starts with BX");

            if(units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").has(text)) {
                JSONObject content = units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").getJSONObject(text);
                if(content.has(ean)) {
                    int amount = content.getInt(ean);
                    content.put(ean, amount + 1);
                } else {
                    content.put(ean, 1);
                }

                units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").put(text, content);
            } else {
                JSONObject content = new JSONObject();
                content.put(ean, 1);
                units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A").put(text, content);
            }

            System.out.println(units);

            eanScanBody = new ScanBody();
            eanScanBody.getLabel("arg_label").setText("EAN");
            eanScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {}

                @Override
                public void handleKeyEvent(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    System.out.println("triggered");

                    if(keyCode == KeyEvent.VK_ENTER) {
                        analyzeEANEnterEvent(eanScanBody.getTextField("scanTextfield"), eanScanBody.getActionLabel());
                    }
                }

                @Override
                public void run() {}
            });

            Main.bodyHandler.setContentBody(eanScanBody);

            boxScanBody = new ScanBody();
            boxScanBody.getLabel("arg_label").setText("BOX");
            boxScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {}

                @Override
                public void handleKeyEvent(KeyEvent event) {
                    int keyCode = event.getKeyCode();

                    if(keyCode == KeyEvent.VK_ENTER) {
                        analyzeBOXEnterEvent(boxScanBody.getTextField("scanTextfield"), boxScanBody.getActionLabel());
                    }
                }

                @Override
                public void run() {}
            });
            eanScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");
        } else if(text.length() == 13 || text.length() == 12) {
            Main.addToLog("Text length is 13 or 12");

            actionLabel.setText("EAN hier nicht erlaubt!");
            field.setText("");

            new Thread(() -> {

                try {
                    Thread.sleep(1000*15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                actionLabel.setText("Masseneinbuchung in versch. Kisten = Kein Edit");
            }).start();
        }

    }

}
