package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ScanBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.ShipBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import de.kabuecher.storage.v4.sendcloud.SendCloud;
import de.kabuecher.storage.v4.sevdesk.SevDesk;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;

public class ShipFlow {

    private final ScanBody scanBody;

    public ShipFlow() {

        Main.addToLog("Starting ship flow");

        scanBody = new ScanBody();
        scanBody.getLabel("arg_label").setText("Liefercode");

        scanBody.addAction("enter", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {

            }

            @Override
            public void handleKeyEvent(KeyEvent event) {
                analyzeScan(scanBody.getTextField("scanTextfield").getText());
            }

            @Override
            public void run() {

            }
        });

        Main.bodyHandler.setContentBody(scanBody);
    }

    public void analyzeScan(String text) {

        Main.addToLog("Analyzing scan: " + text);

        if(text.startsWith("LEF")) {

            Main.addToLog("Scan is a delivery code");

            String offerNo = text.replaceFirst("LEF", "");
            Offer offer = new SevDesk().getOffer(offerNo);
            if(offer != null) {

                ShipBody shipBody = new ShipBody();
                shipBody.addAction("more_button", new BodyType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        moreButtonClicked(offer, shipBody.getTextField("weightField").getText().replaceAll("g", "").replaceAll("kg", ""));
                    }

                    @Override
                    public void handleKeyEvent(KeyEvent event) {

                    }

                    @Override
                    public void run() {

                    }
                });

                shipBody.addAction("less_button", new BodyType.ActionEventRunnable() {
                    @Override
                    public void handleActionEvent(ActionEvent event) {
                        lessButtonClicked(offer, shipBody.getTextField("weightField").getText().replaceAll("g", "").replaceAll("kg", ""));
                    }

                    @Override
                    public void handleKeyEvent(KeyEvent event) {

                    }

                    @Override
                    public void run() {

                    }
                });

                Main.bodyHandler.setContentBody(shipBody);

                Main.addToLog("Offer found: " + offerNo);
            } else {
                Main.addToLog("Offer not found: " + offerNo);

                scanBody.getActionLabel().setText("Liefercode nicht gefunden");
                scanBody.getTextField("scanTextfield").setText("");
            }
        } else {
            Main.addToLog("Scan is not a delivery code");

            scanBody.getActionLabel().setText("Gescannter Code ist kein Liefercode");
            scanBody.getTextField("scanTextfield").setText("");
        }
    }

    private void moreButtonClicked(Offer offer, String weight) {
        Main.addToLog("More button clicked");
        SendCloud sendCloud = new SendCloud();
        sendCloud.getParcelLabel(offer, Double.parseDouble(weight), 15);
        Main.bodyHandler.setContentBody(null);
        Main.addToLog("Label printed");
    }

    private void lessButtonClicked(Offer offer, String weight) {
        Main.addToLog("Less button clicked");
        SendCloud sendCloud = new SendCloud();
        sendCloud.getParcelLabel(offer, Double.parseDouble(weight), 5);
        Main.bodyHandler.setContentBody(null);
        Main.addToLog("Label printed");
    }

    private void printLabel(File file) {
        Main.addToLog("Printing label");
        try {
            PDDocument document = PDDocument.load(file);
            PrinterJob job = PrinterJob.getPrinterJob();

            for (PrintService printService : PrintServiceLookup.lookupPrintServices(null, null)) {
                if(printService.getName().equals(Main.getJsonFile().get("printerConfig").getString("label_printer"))) {
                    job.setPrintService(printService);
                    break;
                }
            }

            Paper paper = job.defaultPage().getPaper();

            PageFormat format = new PageFormat();
            format.setPaper(paper);

            Book book = new Book();
            book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), format, document.getNumberOfPages());
            job.setPageable(book);

            job.print();
        } catch (IOException | PrinterException e) {
            throw new RuntimeException(e);
        }

        Main.addToLog("Label printed");
    }

}
