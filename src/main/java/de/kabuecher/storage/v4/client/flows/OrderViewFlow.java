package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ChangeAddressBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ScanBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.SummarizingBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.WaitingBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.ComponentType;
import de.kabuecher.storage.v4.client.utils.DeliveryLabelPrinter;
import de.kabuecher.storage.v4.client.utils.Translateables;
import de.kabuecher.storage.v4.client.sevdesk.SevDeskClient;
import de.kabuecher.storage.v4.client.sevdesk.invoice.Invoice;
import de.kabuecher.storage.v4.client.sevdesk.offer.Offer;
import de.kabuecher.storage.v4.client.sevdesk.offer.OfferPos;
import de.kabuecher.storage.v4.client.utils.storage.UnitManagementClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderViewFlow {

    private ScanBody eanScanBody;
    private ScanBody boxScanBody;
    private SummarizingBody mainBody;
    private Offer currentOffer;

    private int index = 0;
    private final List<String> keys = new ArrayList<>();
    private int subIndex = 0;
    private int positionIndex = 0;
    private final HashMap<String, JSONArray> subs = new HashMap<>();

    private String ean = "";
    private final JSONObject units = new JSONObject();
    private boolean end = false;

    public OrderViewFlow() {

        if(Main.timeout) {
            return;
        }

        Main.addToLog("Starting order view flow");
        units.put(Main.username, new JSONObject().put("stacks", new JSONObject().put("A", new JSONObject())));
        SevDeskClient sevDesk = new SevDeskClient();
        List<Offer> offers = sevDesk.getOpenOffers();
        SummarizingBody summarizingBody = new SummarizingBody(offers);
        for (String component : summarizingBody.getTypeComponents().keySet()) {
            summarizingBody.getTypeComponents().get(component).addAction("submit_button", new ComponentType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    submitOrder(offers.stream().filter(offer -> offer.getId().equals(component)).findFirst().get());
                }

                @Override
                public void run() {

                }
            });
        }

        Main.addToLog("Displaying open offers");
        mainBody = summarizingBody;
        Main.bodyHandler.setContentBody(mainBody);
        summarizingBody.remove(summarizingBody.getButton("confirm_button"));
        summarizingBody.remove(summarizingBody.getButton("cancel_button"));
        summarizingBody.repaint();
    }

    private void submitOrder(Offer offer) {

        WaitingBody waitingBody = new WaitingBody();
        Main.bodyHandler.setContentBody(waitingBody);
        waitingBody.getProgressBar().setValue(1);

        Main.addToLog("Submitting order: " + offer.getId());
        SevDeskClient sevDesk = new SevDeskClient();
        currentOffer = offer;

        List<OfferPos> positions = sevDesk.getOrderPositions(offer.getId());
        for(OfferPos pos : positions) {
            JSONObject object = new UnitManagementClient().searchForItemInStore(Main.username, new Translateables().getEANByPartID(pos.getPart().getId()), pos.getQuantity());
            if(object == null) {
                mainBody.getActionLabel().setText("Nicht genügend Einheiten vorhanden");
                return;
            }
            for(String key : object.keySet()) {
                if(!keys.contains(pos.getPart().getId())) {
                    keys.add(pos.getPart().getId());
                }
                subs.put(pos.getPart().getId(), subs.getOrDefault(pos.getPart().getId(), new JSONArray()).put(new JSONObject().put(key, object.getInt(key))));
                Main.addToLog("Added " + object.getInt(key) + " units of " + pos.getPart().getId() + " to order");
            }
        }

        waitingBody.getProgressBar().setValue(5);

        boxScanBody = new ScanBody();
        boxScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");
        boxScanBody.getLabel("arg_label").setText(subs.get(keys.get(index)).getJSONObject(subIndex).keySet().toArray()[positionIndex].toString());
        boxScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {}

            @Override
            public void handleKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();

                if(keyCode == KeyEvent.VK_ENTER) {
                    analyzeBOXEnterEvent(boxScanBody.getTextField("scanTextfield"), boxScanBody.getActionLabel(), boxScanBody.getLabel("arg_label"));
                }
            }

            @Override
            public void run() {}
        });

        waitingBody.getProgressBar().setValue(10);

        Main.bodyHandler.setContentBody(boxScanBody);
    }

    private void analyzeBOXEnterEvent(JTextField scanTextfield, JLabel actionLabel, JLabel arg_label) {
        Main.addToLog("Analyzing BOX enter event");
        String enteredBox = scanTextfield.getText();
        String expectedBox = arg_label.getText();

        if (enteredBox.equalsIgnoreCase(expectedBox)) {
            actionLabel.setText("BOX recognized: " + enteredBox);
            eanScanBody = new ScanBody();

            eanScanBody.getLabel("arg_label").setText(new Translateables().getNameByPartID(keys.get(index)));
            eanScanBody.getLabel("amount_label").setText("Anzahl: " + subs.get(keys.get(index)).getJSONObject(subIndex).getInt(subs.get(keys.get(index)).getJSONObject(subIndex).keySet().toArray()[positionIndex].toString()));
            eanScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {}

                @Override
                public void handleKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        analyzeEANEnterEvent(eanScanBody.getTextField("scanTextfield"), eanScanBody.getActionLabel(), eanScanBody.getLabel("arg_label"));
                    }
                }

                @Override
                public void run() {}
            });

            Main.bodyHandler.setContentBody(eanScanBody);
        } else {
            actionLabel.setText("BOX not recognized");
            scanTextfield.setText("");
        }
    }

    private void analyzeEANEnterEvent(JTextField scanTextfield, JLabel actionLabel, JLabel arg_label) {
        Main.addToLog("Analyzing EAN enter event");
        String enteredEAN = scanTextfield.getText();
        String expectedEAN = new Translateables().getEANByName(arg_label.getText());

        if (enteredEAN.equalsIgnoreCase(expectedEAN)) {
            ean = enteredEAN;
            actionLabel.setText("EAN recognized: " + enteredEAN);

            JSONObject stack = units.getJSONObject(Main.username).getJSONObject("stacks").getJSONObject("A");
            String boxKey = subs.get(keys.get(index)).getJSONObject(subIndex).keySet().toArray()[positionIndex].toString();

            if (!stack.has(boxKey)) {
                stack.put(boxKey, new JSONObject());
            }

            JSONObject box = stack.getJSONObject(boxKey);

            int additionalAmount = subs.get(keys.get(index)).getJSONObject(subIndex).getInt(boxKey);
            int currentAmount = box.optInt(ean, 0);
            box.put(ean, currentAmount + additionalAmount);

            // Update indices
            updateIndices();

            if (!end) {
                boxScanBody = new ScanBody();
                boxScanBody.getLabel("arg_label").setText(subs.get(keys.get(index)).getJSONObject(subIndex).keySet().toArray()[positionIndex].toString());
                boxScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {}

                    @Override
                    public void handleKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                            analyzeBOXEnterEvent(boxScanBody.getTextField("scanTextfield"), boxScanBody.getActionLabel(), boxScanBody.getLabel("arg_label"));
                        }
                    }

                    @Override
                    public void run() {}
                });

                Main.bodyHandler.setContentBody(boxScanBody);
            } else {
                displaySummary();
            }
        } else {
            actionLabel.setText("EAN not recognized: " + enteredEAN);
            scanTextfield.setText("");
        }
    }

    private void updateIndices() {
        Main.addToLog("Updating indices");
        if (subIndex < subs.get(keys.get(index)).length() - 1) {
            subIndex++;
        } else {
            if (positionIndex < subs.get(keys.get(index)).getJSONObject(subIndex).keySet().size() - 1) {
                positionIndex++;
            } else {
                if (index < keys.size() - 1) {
                    index++;
                    subIndex = 0;
                    positionIndex = 0;
                } else {
                    end = true;
                }
            }
        }
    }

    private void displaySummary() {
        Main.addToLog("Displaying summary");
        SummarizingBody summarizingBody = new SummarizingBody(units, true, true);

        summarizingBody.getTypeComponents().forEach((key, value) -> {
            value.getButton("change_button").setEnabled(false);
            value.getButton("delete_button").setEnabled(false);
        });

        summarizingBody.addAction("confirm_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                confirmOrder();
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {}

            @Override
            public void run() {}
        });

        summarizingBody.addAction("cancel_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                Main.bodyHandler.setContentBody(null);
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {}

            @Override
            public void run() {}
        });

        Main.bodyHandler.setContentBody(summarizingBody);
        Main.addToLog("Summary displayed");
    }

    private void confirmOrder() {
        Main.addToLog("Confirming order");
        UnitManagementClient management = new UnitManagementClient();

        for (String key : units.keySet()) {
            JSONObject store = units.getJSONObject(key);
            for (String stackKey : store.getJSONObject("stacks").keySet()) {
                JSONObject stack = store.getJSONObject("stacks").getJSONObject(stackKey);
                for (String boxKey : stack.keySet()) {
                    JSONObject box = stack.getJSONObject(boxKey);
                    for (String eanKey : box.keySet()) {
                        int amount = box.getInt(eanKey);
                        management.removeItem(key, stackKey, boxKey, eanKey, amount);
                    }
                }
            }
        }

        ChangeAddressBody changeAddressBody = new ChangeAddressBody();
        String address = "";
        address = currentOffer.getAddress();
        if(address.split("\n").length == 3) {
            changeAddressBody.getTextField("recipient_field").setText(address.split("\n")[0]);
            changeAddressBody.getTextField("second_adr_field").setText(address.split("\n")[1]);
            changeAddressBody.getTextField("city_field").setText(address.split("\n")[2].split(" ")[1]);
            changeAddressBody.getTextField("zip_field").setText(address.split("\n")[2].split(" ")[0]);
        } else {
            changeAddressBody.getTextField("recipient_field").setText(address.split("\n")[0]);
            changeAddressBody.getTextField("first_adr_field").setText(address.split("\n")[1]);
            changeAddressBody.getTextField("second_adr_field").setText(address.split("\n")[2]);
            changeAddressBody.getTextField("city_field").setText(address.split("\n")[3].split(" ")[1]);
            changeAddressBody.getTextField("zip_field").setText(address.split("\n")[3].split(" ")[0]);
        }

        changeAddressBody.addAction("continue_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                finalizeOrder(currentOffer, changeAddressBody);
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {}

            @Override
            public void run() {}
        });
        Main.bodyHandler.setContentBody(changeAddressBody);
        Main.addToLog("Order confirmed");
    }

    private void finalizeOrder(Offer offer, BodyType bodyType) {
        Main.addToLog("Finalizing order");

        WaitingBody waitingBody = new WaitingBody();

        Main.bodyHandler.setContentBody(waitingBody);

        waitingBody.getProgressBar().setValue(1);
        SevDeskClient sevDesk = new SevDeskClient();
        Offer deliveryNote = sevDesk.createDeliveryNote(offer.getId());
        waitingBody.getProgressBar().setValue(2);
        Invoice invoice = sevDesk.createInvoice(offer.getId());
        waitingBody.getProgressBar().setValue(3);
        sevDesk.setOfferStatus(offer.getId(), 1000);
        waitingBody.getProgressBar().setValue(4);
        offer = sevDesk.transformOfferToConfirSmation(offer.getId());
        waitingBody.getProgressBar().setValue(5);

        sevDesk.setAddressOfDeliveryNote(deliveryNote.getId(), bodyType.getTextField("recipient_field").getText() + "\n" + bodyType.getTextField("first_adr_field").getText() + "\n" + (!bodyType.getTextField("second_adr_field").getText().isEmpty() ? bodyType.getTextField("second_adr_field").getText() + "\n" : "") + bodyType.getTextField("zip_field").getText() + " " + bodyType.getTextField("city_field").getText());

        waitingBody.getProgressBar().setValue(6);
        sevDesk.printOrderID(deliveryNote.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));

        waitingBody.getProgressBar().setValue(7);
        sevDesk.printInvoiceID(invoice.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));

        waitingBody.getProgressBar().setValue(8);
        sevDesk.printOrderID(deliveryNote.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));

        waitingBody.getProgressBar().setValue(9);

        JSONObject object = new JSONObject();
        object.put("recipient", bodyType.getTextField("recipient_field").getText());
        if(!bodyType.getTextField("first_adr_field").getText().isEmpty()) {
            object.put("addr1", bodyType.getTextField("first_adr_field").getText());
        }
        if(!bodyType.getTextField("second_adr_field").getText().isEmpty()) {
            object.put("addr2", bodyType.getTextField("second_adr_field").getText());
        }
        object.put("zip", bodyType.getTextField("zip_field").getText());
        object.put("city", bodyType.getTextField("city_field").getText());

        try {
            new DeliveryLabelPrinter().generateLabel(object, deliveryNote.getOrderNumber(), "LEF" + deliveryNote.getOrderNumber());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        waitingBody.getProgressBar().setValue(10);
        Main.bodyHandler.setContentBody(null);
        Main.addToLog("Order finalized");
    }
}