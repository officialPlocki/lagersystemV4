package de.kabuecher.storage.v4.client.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.kabuecher.storage.v4.Main;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class LabelGenerator {

    public void generateLabel(String title, String subtitle, List<String> text, String ean) throws Exception {
        // Create a new document
        try (PDDocument document = new PDDocument()) {
            // Create a new page with dimensions 4x6 inches (in points: 288x432)
            PDPage page = new PDPage();
            document.addPage(page);

            // Start drawing content on the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(40, 390); // Position near the top
                contentStream.showText(title);
                contentStream.endText();

                // Subtitle
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(40, 360);
                contentStream.showText(subtitle);
                contentStream.endText();

                // Text
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(40, 320);

                if(!text.isEmpty()) {
                    contentStream.showText(text.getFirst());
                }

                if(text.size() > 1) {
                    for (String line : text.subList(1, text.size())) {
                        contentStream.newLineAtOffset(0, -20);
                        contentStream.showText(line);
                    }
                }

                contentStream.endText();

                // URL
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(40, 150);
                contentStream.showText("Nur auf: https://store.kabuecher.de");
                contentStream.endText();

                // Generate QR Code
                BufferedImage qrImage = generateQRCodeImage(ean);
                PDImageXObject qrCode = LosslessFactory.createFromImage(document, qrImage);
                contentStream.drawImage(qrCode, 40, 200, 100, 100); // Position and scale of QR Code
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);


            try {
                PDDocument pdf = PDDocument.load(out.toByteArray());
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
        }
    }

    private BufferedImage generateQRCodeImage(String data) throws WriterException {

        Main.addToLog("Generating QR code for data: " + data);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

}
