package de.kabuecher.storage.v4.client.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import de.kabuecher.storage.v4.Main;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class DeliveryLabelPrinter {

    public void generateLabel(JSONObject address, String orderNumber, String qrData) {

        Main.addToLog("Generating label for order " + orderNumber);
        // Generate QR code
        BufferedImage qrCodeImage;
        try {
            qrCodeImage = generateQRCodeImage(qrData);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        // Save QR code to a temporary file
        File qrCodeFile;
        try {
            qrCodeFile = File.createTempFile("qrCode", ".png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ImageIO.write(qrCodeImage, "PNG", qrCodeFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Custom 4x6 inch label size (4x6 inches = 288x432 points at 72 dpi)
        float widthInches = 4f;
        float heightInches = 6f;
        float widthPoints = widthInches * 72; // 72 points per inch
        float heightPoints = heightInches * 72;

        // Create PDF with custom page size
        File tmp = null;
        try {
            tmp = File.createTempFile("label", ".pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PdfWriter writer = null;
        try {
            writer = new PdfWriter(new FileOutputStream(tmp));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, new com.itextpdf.kernel.geom.PageSize(widthPoints, heightPoints));

        String addressString = address.getString("recipient") + "\n" +
                (address.has("addr1") ? address.getString("addr1") + "\n" : "") +
                (address.has("addr2") ? address.getString("addr2") + "\n" : "") +
                (address.has("zip") ? address.getString("zip") : "") + " " +
                (address.has("city") ? address.getString("city") : "");

        // Add name, address, and order number
        document.add(new Paragraph(addressString).setFontSize(35).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\nLieferschein: " + orderNumber).setFontSize(35).setTextAlignment(TextAlignment.CENTER));

        // Add QR code to the PDF
        Image qrImage = null;
        try {
            qrImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(qrCodeFile.getAbsolutePath()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        qrImage.setAutoScale(true).setWidth(150).setHeight(150).setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(qrImage);

        document.close();
        qrCodeFile.delete(); // Cleanup temp file

        try {
            PDDocument pdf = PDDocument.load(tmp);
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
            book.append(new PDFPrintable(pdf, Scaling.SHRINK_TO_FIT), format, pdf.getNumberOfPages());
            job.setPageable(book);

            job.print();
        } catch (IOException | PrinterException e) {
            throw new RuntimeException(e);
        }

        tmp.delete();

        Main.addToLog("Label printed");
    }

    private static BufferedImage generateQRCodeImage(String data) throws WriterException {

        Main.addToLog("Generating QR code for data: " + data);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
