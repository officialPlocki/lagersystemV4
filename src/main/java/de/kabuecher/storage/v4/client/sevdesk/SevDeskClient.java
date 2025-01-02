package de.kabuecher.storage.v4.client.sevdesk;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.sevdesk.builder.ContactBuilder;
import de.kabuecher.storage.v4.client.sevdesk.builder.InvoiceBuilder;
import de.kabuecher.storage.v4.client.sevdesk.builder.OfferBuilder;
import de.kabuecher.storage.v4.client.sevdesk.builder.PartBuilder;
import de.kabuecher.storage.v4.client.sevdesk.invoice.Invoice;
import de.kabuecher.storage.v4.client.sevdesk.offer.Offer;
import de.kabuecher.storage.v4.client.sevdesk.offer.OfferPos;
import de.kabuecher.storage.v4.client.utils.Translateables;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SevDeskClient {

    public Offer getOffer(String offerID) {
        Main.addToLog("Getting offer with ID " + offerID);

        JSONObject body = new JSONObject();
        body.put("method", "getOffer");
        return getOffer(offerID, body);
    }

    @Nullable
    private Offer getOffer(String offerID, JSONObject body) {
        body.put("data", new JSONObject().put("offerID", offerID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JSONObject offerJson = new JSONObject(response.body());
                System.out.println(offerJson);
                return new OfferBuilder().buildOffer(offerJson.getJSONArray("objects").getJSONObject(0));
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Offer> getOpenOffers() {
        Main.addToLog("Getting open offers");

        JSONObject body = new JSONObject();
        body.put("method", "getOpenOffers");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Offer> offers = new ArrayList<>();

            if(response.statusCode() == 200) {
                JSONObject offersJson = new JSONObject(response.body());

                JSONArray objects = offersJson.getJSONArray("offers");

                for (int i = 0; i < objects.length(); i++) {
                    offers.add(getOffer(objects.getString(i)));
                }
            }

            return offers;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAddressOfDeliveryNote(String offerID, String address) {
        Main.addToLog("Setting address of delivery note with ID " + offerID + " to " + address);

        JSONObject body = new JSONObject();
        body.put("method", "setAddressOfDeliveryNote");
        body.put("data", new JSONObject().put("offerID", offerID).put("address", address));

        requestWeb(body);

    }

    private void requestWeb(JSONObject body) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOfferStatus(String offerID, int status) {
        Main.addToLog("Setting status of offer with ID " + offerID + " to " + status);

        JSONObject body = new JSONObject();
        body.put("method", "setOfferStatus");
        body.put("data", new JSONObject().put("offerID", offerID).put("status", status));

        requestWeb(body);
    }

    public Offer transformOfferToConfirSmation(String offerID) {
        Main.addToLog("Transforming offer with ID " + offerID + " to confirmation");

        JSONObject body = new JSONObject();
        body.put("method", "transformOfferToConfirmation");
        return getOffer(offerID, body);
    }

    public Offer createDeliveryNote(String offerID) {
        Main.addToLog("Creating delivery note from offer with ID " + offerID);

        JSONObject body = new JSONObject();
        body.put("method", "createDeliveryNote");
        return getOffer(offerID, body);
    }

    public Invoice createInvoice(String offerID) {
        Main.addToLog("Creating invoice from offer with ID " + offerID);

        JSONObject body = new JSONObject();
        body.put("method", "createInvoice");
        body.put("data", new JSONObject().put("offerID", offerID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                JSONObject invoiceJson = new JSONObject(response.body());
                return new InvoiceBuilder().buildInvoice(invoiceJson.getJSONObject("objects"));
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Part getPart(String partID) {
        Main.addToLog("Getting part with ID " + partID);

        JSONObject body = new JSONObject();
        body.put("method", "getPart");
        body.put("data", new JSONObject().put("partID", partID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                JSONObject partJson = new JSONObject(response.body());
                System.out.println(partJson);
                return new PartBuilder().buildPart(partJson);
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OfferPos> getOrderPositions(String orderID) {
        Main.addToLog("Getting positions of order with ID " + orderID);

        JSONObject body = new JSONObject();
        body.put("method", "getOrderPositions");
        body.put("data", new JSONObject().put("orderID", orderID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<OfferPos> positions = new ArrayList<>();

            if(response.statusCode() == 200) {
                JSONObject orderJson = new JSONObject(response.body());

                for (int i = 0; i < orderJson.getJSONArray("objects").length(); i++) {
                    OfferPos pos = new OfferBuilder().buildOfferPos(orderJson.getJSONArray("objects").getJSONObject(i));
                    if(!new Translateables().isIgnored(pos.getPart().getId())) {
                        positions.add(pos);
                    }
                }
            }

            return positions;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Contact getContact(String contactID) {
        Main.addToLog("Getting contact with ID " + contactID);

        JSONObject body = new JSONObject();
        body.put("method", "getContact");
        body.put("data", new JSONObject().put("contactID", contactID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                JSONObject contactJson = new JSONObject(response.body());
                return new ContactBuilder().buildContact(contactJson.getJSONObject("objects"));
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContactID(String contactName) {
        Main.addToLog("Getting contact ID for contact name " + contactName);

        JSONObject body = new JSONObject();
        body.put("method", "getContactID");
        body.put("data", new JSONObject().put("contactName", contactName));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                JSONObject contactJson = new JSONObject(response.body());
                return contactJson.getJSONObject("objects").getString("contactID");
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void printOrderID(String orderID, String printer) {
        Main.addToLog("Printing order with ID " + orderID);

        JSONObject body = new JSONObject();
        body.put("method", "printOrderID");
        body.put("data", new JSONObject().put("orderID", orderID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject orderJson = new JSONObject(response.body());
            orderJson = orderJson.getJSONObject("objects");

            String base64Content = orderJson.getString("content");

            print(base64Content, printer);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void printInvoiceID(String invoiceID, String printer) {
        Main.addToLog("Printing invoice with ID " + invoiceID);

        JSONObject body = new JSONObject();
        body.put("method", "printInvoiceID");
        body.put("data", new JSONObject().put("invoiceID", invoiceID));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject orderJson = new JSONObject(response.body());
            orderJson = orderJson.getJSONObject("objects");

            String base64Content = orderJson.getString("content");

            print(base64Content, printer);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
