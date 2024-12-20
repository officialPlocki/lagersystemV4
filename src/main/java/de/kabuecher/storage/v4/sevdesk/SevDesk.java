package de.kabuecher.storage.v4.sevdesk;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.utils.Translateables;
import de.kabuecher.storage.v4.sevdesk.impl.Contact;
import de.kabuecher.storage.v4.sevdesk.impl.Part;
import de.kabuecher.storage.v4.sevdesk.impl.builder.ContactBuilder;
import de.kabuecher.storage.v4.sevdesk.impl.builder.InvoiceBuilder;
import de.kabuecher.storage.v4.sevdesk.impl.builder.OfferBuilder;
import de.kabuecher.storage.v4.sevdesk.impl.builder.PartBuilder;
import de.kabuecher.storage.v4.sevdesk.impl.invoice.Invoice;
import de.kabuecher.storage.v4.sevdesk.impl.invoice.InvoicePos;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import de.kabuecher.storage.v4.sevdesk.impl.offer.OfferPos;
import de.kabuecher.storage.v4.sevdesk.query.QueryMethod;
import de.kabuecher.storage.v4.sevdesk.query.SevDeskQuery;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SevDesk {

    public Invoice getInvoice(String invoiceID) {
        Main.addToLog("Getting invoice with ID " + invoiceID);
        JSONObject invoiceJson = SevDeskQuery.query("/Invoice/" + invoiceID, null, QueryMethod.GET);
        invoiceJson = invoiceJson.getJSONArray("objects").getJSONObject(0);

        return new InvoiceBuilder().buildInvoice(invoiceJson);
    }

    public InvoicePos getInvoicePos(String posID) {
        Main.addToLog("Getting invoice position with ID " + posID);
        JSONObject invoicePosJson = SevDeskQuery.query("/InvoicePos/" + posID, null, QueryMethod.GET);
        invoicePosJson = invoicePosJson.getJSONArray("objects").getJSONObject(0);

        return new InvoiceBuilder().buildInvoicePos(invoicePosJson);
    }

    public String getInvoiceID(String invoiceNumber) {
        Main.addToLog("Getting invoice ID for invoice number " + invoiceNumber);
        JSONObject invoiceJson = SevDeskQuery.query("/Invoice?invoiceNumber=" + invoiceNumber, null, QueryMethod.GET);
        invoiceJson = invoiceJson.getJSONArray("objects").getJSONObject(0);

        return invoiceJson.getString("id");
    }

    public Offer getOffer(String offerID) {
        Main.addToLog("Getting offer with ID " + offerID);
        JSONObject offerJson = SevDeskQuery.query("/Order/" + offerID, null, QueryMethod.GET);
        offerJson = offerJson.getJSONArray("objects").getJSONObject(0);

        return new OfferBuilder().buildOffer(offerJson);
    }

    public List<Offer> getOpenOffers() {
        Main.addToLog("Getting open offers");
        JSONObject offersJson = SevDeskQuery.query("/Order?status=500&orderType=AN&createAfter=" + ZonedDateTime.now().minusDays(14), null, QueryMethod.GET);

        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < offersJson.getJSONArray("objects").length(); i++) {
            offers.add(new OfferBuilder().buildOffer(offersJson.getJSONArray("objects").getJSONObject(i)));
        }

        return offers;
    }

    public String getOfferID(String offerNumber) {
        Main.addToLog("Getting offer ID for offer number " + offerNumber);
        JSONObject offerJson = SevDeskQuery.query("/Order?search=" + offerNumber, null, QueryMethod.GET);
        offerJson = offerJson.getJSONArray("objects").getJSONObject(0);

        return offerJson.getString("id");
    }

    public void setAddressOfDeliveryNote(String offerID, String address) {
        Main.addToLog("Setting address of delivery note with ID " + offerID + " to " + address);
        SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("address", address), QueryMethod.PUT);
    }

    public void setOfferStatus(String offerID, int status) {
        Main.addToLog("Setting status of offer with ID " + offerID + " to " + status);
        SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("status", status), QueryMethod.PUT);
    }

    public void transformOfferToConfirmation(String offerID) {
        Main.addToLog("Transforming offer with ID " + offerID + " to confirmation");
        SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("orderType", "AB"), QueryMethod.PUT);
    }

    public Offer createDeliveryNote(String offerID) {
        Main.addToLog("Creating delivery note from offer with ID " + offerID);
        JSONObject deliveryNoteJson = SevDeskQuery.query("/Order/Factory/createPackingListFromOrder", new JSONObject().put("id", offerID).put("objectName", "Order"), QueryMethod.POST);

        return new OfferBuilder().buildOffer(deliveryNoteJson);
    }

    public Offer createDeliveryNoteWithDifferentAddress(String offerID, String address) {
        Main.addToLog("Creating delivery note from offer with ID " + offerID + " with address " + address);
        JSONObject deliveryNoteJson = SevDeskQuery.query("/Order/Factory/createPackingListFromOrder", new JSONObject().put("id", offerID).put("objectName", "Order"), QueryMethod.POST);

        deliveryNoteJson = SevDeskQuery.query("/Order/" + deliveryNoteJson.getJSONArray("objects").getJSONObject(0).getString("id"), new JSONObject().put("address", address), QueryMethod.PUT);
        return new OfferBuilder().buildOffer(deliveryNoteJson);
    }

    public Invoice createInvoice(String offerID) {
        Main.addToLog("Creating invoice from offer with ID " + offerID);
        JSONObject invoiceJson = SevDeskQuery.query("/Invoice/Factory/createInvoiceFromOrder", new JSONObject().put("id", offerID).put("objectName", "Order"), QueryMethod.POST);

        return new InvoiceBuilder().buildInvoice(invoiceJson);
    }

    public Part getPart(String partID) {
        Main.addToLog("Getting part with ID " + partID);
        JSONObject partJson = SevDeskQuery.query("/Part/" + partID, null, QueryMethod.GET);
        partJson = partJson.getJSONArray("objects").getJSONObject(0);

        return new PartBuilder().buildPart(partJson);
    }

    public List<OfferPos> getOrderPositions(String orderID) {
        Main.addToLog("Getting positions of order with ID " + orderID);
        JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPositions", null, QueryMethod.GET);

        List<OfferPos> positions = new ArrayList<>();
        for (int i = 0; i < orderJson.getJSONArray("objects").length(); i++) {
            OfferPos pos = new OfferBuilder().buildOfferPos(orderJson.getJSONArray("objects").getJSONObject(i));
            if(!new Translateables().isIgnored(pos.getPart().getId())) {
                positions.add(pos);
            }
        }

        return positions;
    }

    public List<Part> getParts() {
        Main.addToLog("Getting all parts");
        JSONObject partsJson = SevDeskQuery.query("/Part", null, QueryMethod.GET);

        List<Part> parts = new ArrayList<>();
        for (int i = 0; i < partsJson.getJSONArray("objects").length(); i++) {
            parts.add(new PartBuilder().buildPart(partsJson.getJSONArray("objects").getJSONObject(i)));
        }

        return parts;
    }

    public String getPartID(String partNumber) {
        Main.addToLog("Getting part ID for part number " + partNumber);
        JSONObject partJson = SevDeskQuery.query("/Part?search=" + partNumber, null, QueryMethod.GET);
        partJson = partJson.getJSONArray("objects").getJSONObject(0);

        return partJson.getString("id");
    }

    public Contact getContact(String contactID) {
        Main.addToLog("Getting contact with ID " + contactID);
        JSONObject contactJson = SevDeskQuery.query("/Contact/" + contactID, null, QueryMethod.GET);
        contactJson = contactJson.getJSONArray("objects").getJSONObject(0);

        return new ContactBuilder().buildContact(contactJson);
    }

    public String getContactID(String contactName, String printer) {
        Main.addToLog("Getting contact ID for contact name " + contactName);
        JSONObject contactJson = SevDeskQuery.query("/Contact?search=" + contactName, null, QueryMethod.GET);
        contactJson = contactJson.getJSONArray("objects").getJSONObject(0);

        return contactJson.getString("id");
    }

    public void printOrderID(String orderID, String printer) {
        Main.addToLog("Printing order with ID " + orderID);
        JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPdf", null, QueryMethod.GET);
        orderJson = orderJson.getJSONObject("objects");

        String base64Content = orderJson.getString("content");
        print(base64Content, printer);
    }

    public void printInvoiceID(String invoiceID, String printer) {
        Main.addToLog("Printing invoice with ID " + invoiceID);
        JSONObject orderJson = SevDeskQuery.query("/Invoice/" + invoiceID + "/getPdf", null, QueryMethod.GET);
        orderJson = orderJson.getJSONObject("objects");

        String base64Content = orderJson.getString("content");

        print(base64Content, printer);
    }

    private void print(String base64Content, String printer) {
        Main.addToLog("Printing document with printer " + printer);
        byte[] pdfBytes = Base64.getDecoder().decode(base64Content);

        try {
            PDDocument document = PDDocument.load(pdfBytes);
            PrinterJob job = PrinterJob.getPrinterJob();

            for (PrintService printService : PrintServiceLookup.lookupPrintServices(null, null)) {
                if(printService.getName().equals(printer)) {
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
            Main.addToLog("Document printed");
        } catch (IOException | PrinterException e) {
            throw new RuntimeException(e);
        }
    }
}
