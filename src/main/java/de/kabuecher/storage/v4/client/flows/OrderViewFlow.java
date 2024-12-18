package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ChangeAddressBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ScanBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.SummarizingBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.ComponentType;
import de.kabuecher.storage.v4.client.utils.DeliveryLabelPrinter;
import de.kabuecher.storage.v4.client.utils.Translateables;
import de.kabuecher.storage.v4.sevdesk.SevDesk;
import de.kabuecher.storage.v4.sevdesk.impl.invoice.Invoice;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import de.kabuecher.storage.v4.sevdesk.impl.offer.OfferPos;
import de.kabuecher.storage.v4.storage.UnitManagement;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

public class OrderViewFlow {

    private ScanBody eanScanBody;
    private ScanBody boxScanBody;
    private SummarizingBody mainBody;
    private Offer currentOffer;

    public OrderViewFlow() {
        units.put("store1", new JSONObject().put("stacks", new JSONObject().put("A", new JSONObject())));
        SevDesk sevDesk = new SevDesk();
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

        mainBody = summarizingBody;
        Main.bodyHandler.setContentBody(mainBody);
        summarizingBody.remove(summarizingBody.getButton("confirm_button"));
        summarizingBody.remove(summarizingBody.getButton("cancel_button"));
        summarizingBody.repaint();
    }
    int positionIndex = 0;
    int positionIndexSub = 0;
    private HashMap<String, HashMap<String, Integer>> findingAmounts = new HashMap<>();

    private void submitOrder(Offer offer) {
        SevDesk sevDesk = new SevDesk();
        currentOffer = offer;

        if(offer != null) {
            List<OfferPos> positions = sevDesk.getOrderPositions(offer.getId());
            for(OfferPos pos : positions) {
                JSONObject object = new UnitManagement().searchForItemInStore("store1", new Translateables().getEANByPartID(pos.getPart().getId()), pos.getQuantity());
                if(object == null) {
                    mainBody.getActionLabel().setText("Nicht genügend Einheiten vorhanden");
                    return;
                }
                for(String key : object.keySet()) {
                    findingAmounts.put(new Translateables().getEANByPartID(pos.getPart().getId()), new HashMap<>());
                    findingAmounts.get(new Translateables().getEANByPartID(pos.getPart().getId())).put(key, object.getInt(key));
                }
            }
        }


        eanScanBody = new ScanBody();
        eanScanBody.getLabel("arg_label").setText(new Translateables().getEANByPartID(String.valueOf(findingAmounts.keySet().toArray()[positionIndex])));
        eanScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {}

            @Override
            public void handleKeyEvent(KeyEvent event) {
                int keyCode = event.getKeyCode();
                System.out.println("triggered");

                if(keyCode == KeyEvent.VK_ENTER) {
                    analyzeEANEnterEvent(eanScanBody.getTextField("scanTextfield"), eanScanBody.getActionLabel(), eanScanBody.getLabel("arg_label"));
                }
            }

            @Override
            public void run() {}
        });
        eanScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");

        Main.bodyHandler.setContentBody(eanScanBody);
        if(findingAmounts.get(findingAmounts.keySet().toArray()[positionIndex]).size() > (positionIndexSub+1)) {
            positionIndexSub = 1;
        } else {
            positionIndex++;
            positionIndexSub = 0;
        }


        boxScanBody = new ScanBody();
        boxScanBody.getActionLabel().setText("Masseneinbuchung in versch. Kisten = Kein Edit");
        boxScanBody.getLabel("arg_label").setText(findingAmounts.get(findingAmounts.keySet().toArray()[positionIndex]).keySet().toArray()[positionIndexSub].toString());
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
    }

    private String ean = "";

    private void analyzeEANEnterEvent(JTextField scanTextfield, JLabel actionLabel, JLabel arg_label) {

        if(scanTextfield.getText().equalsIgnoreCase(arg_label.getText())) {
            this.ean = scanTextfield.getText();

            Main.bodyHandler.setContentBody(boxScanBody);

            if(findingAmounts.get(findingAmounts.keySet().toArray()[positionIndex]).size() > (positionIndexSub+1)) {
                positionIndexSub = 1;
            } else {
                positionIndex++;
                positionIndexSub = 0;
            }

            eanScanBody = new ScanBody();
            eanScanBody.getLabel("arg_label").setText(findingAmounts.get(findingAmounts.keySet().toArray()[positionIndex]).keySet().toArray()[positionIndexSub].toString());
            eanScanBody.addAction("enter", new BodyType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {}

                @Override
                public void handleKeyEvent(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    System.out.println("triggered");

                    if(keyCode == KeyEvent.VK_ENTER) {
                        analyzeEANEnterEvent(eanScanBody.getTextField("scanTextfield"), eanScanBody.getActionLabel(), eanScanBody.getLabel("arg_label"));
                    }
                }

                @Override
                public void run() {}
            });
        } else if(scanTextfield.getText().equalsIgnoreCase("00")) {
            SummarizingBody summarizingBody = new SummarizingBody(units);

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
                public void handleKeyEvent(KeyEvent event) {

                }

                @Override
                public void run() {

                }
            });

            summarizingBody.addAction("cancel_button", new BodyType.ActionEventRunnable() {
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

            Main.bodyHandler.setContentBody(summarizingBody);
        } else {
            actionLabel.setText("EAN nicht erkannt");
            scanTextfield.setText("");
        }


    }

    private void confirmOrder() {

        UnitManagement management = new UnitManagement();

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

        management.saveChanges();

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
                try {
                    finalizeOrder(currentOffer, changeAddressBody);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {

            }

            @Override
            public void run() {

            }
        });
        Main.bodyHandler.setContentBody(changeAddressBody);
    }

    private void finalizeOrder(Offer offer, BodyType bodyType) throws Exception {
        SevDesk sevDesk = new SevDesk();
        Offer deliveryNote = sevDesk.createDeliveryNote(offer.getId());
        Invoice invoice = sevDesk.createInvoice(deliveryNote.getId());
        sevDesk.setOfferStatus(offer.getId(), 1000);
        sevDesk.transformOfferToConfirmation(offer.getId());

        sevDesk.setAddressOfDeliveryNote(deliveryNote.getId(), bodyType.getTextField("recipient_field").getText() + "\n" + bodyType.getTextField("first_adr_field").getText() + "\n" + (!bodyType.getTextField("second_adr_field").getText().isEmpty() ? bodyType.getTextField("second_adr_field").getText() + "\n" : "") + bodyType.getTextField("zip_field").getText() + " " + bodyType.getTextField("city_field").getText());

        sevDesk.printOrderID(deliveryNote.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));
        sevDesk.printInvoiceID(invoice.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));
        sevDesk.printOrderID(offer.getId(), Main.getJsonFile().get("printerConfig").getString("printer"));

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

        new DeliveryLabelPrinter().generateLabel(object, deliveryNote.getOrderNumber(), "LEF" + deliveryNote.getOrderNumber());

        Main.bodyHandler.setContentBody(null);
    }

    private final JSONObject units = new JSONObject();

    private void analyzeBOXEnterEvent(JTextField scanTextfield, JLabel actionLabel, JLabel arg_label) {

        if(scanTextfield.getText().equalsIgnoreCase(arg_label.getText())) {

            JSONObject stack = units.getJSONObject("store1").getJSONObject("stacks").getJSONObject("A");
            if(stack.has(arg_label.getText())) {
                JSONObject box = stack.getJSONObject(arg_label.getText());
                if(box.has(ean)) {
                    box.put(ean, box.getInt(ean)+1);
                } else {
                    box.put(ean, 1);
                }
            }

            Main.bodyHandler.setContentBody(eanScanBody);

            boxScanBody = new ScanBody();
            boxScanBody.getLabel("arg_label").setText(findingAmounts.get(findingAmounts.keySet().toArray()[positionIndex]).keySet().toArray()[positionIndexSub].toString());
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
        } else {
            actionLabel.setText("BOX nicht erkannt");
            scanTextfield.setText("");
        }

    }

}
