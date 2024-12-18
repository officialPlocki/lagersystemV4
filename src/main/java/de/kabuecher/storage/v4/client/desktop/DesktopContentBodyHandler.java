package de.kabuecher.storage.v4.client.desktop;

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
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.util.Objects;

public class DesktopContentBodyHandler {

    private final JPanel contentBody;
    private final JFrame frame;

    public DesktopContentBodyHandler(JFrame frame, JPanel contentBody) {
        this.frame = frame;
        this.contentBody = contentBody;
    }

    public void setContentBody(BodyType bodyType) {
        Dimension size = (Dimension) frame.getSize().clone();
        boolean fullscreen = frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;

        contentBody.removeAll();

        if(bodyType != null && contentBody.getComponentCount() > 0) {
            Dimension oldPanelSize = (Dimension) contentBody.getComponent(0).getSize().clone();
            bodyType.setSize(oldPanelSize);
        }

        for (AWTEventListener awtEventListener : Toolkit.getDefaultToolkit().getAWTEventListeners()) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                if(event instanceof KeyEvent keyEvent) {
                    if(keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode() == KeyEvent.VK_F9) {


                        String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                        SecureRandom RANDOM = new SecureRandom();

                        StringBuilder sb = new StringBuilder(12);
                        for (int i = 0; i < 12; i++) {
                            int index = RANDOM.nextInt(CHARSET.length());
                            sb.append(CHARSET.charAt(index));
                        }

                        String data = sb.toString();

                        // Generate QR code
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        BitMatrix bitMatrix;
                        try {
                            bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
                        } catch (WriterException e) {
                            throw new RuntimeException(e);
                        }
                        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

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

                        float widthInches = 4f;
                        float heightInches = 6f;
                        float widthPoints = widthInches * 72;
                        float heightPoints = heightInches * 72;

                        File tmp;
                        try {
                            tmp = File.createTempFile("label", ".pdf");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        PdfWriter writer;
                        try {
                            writer = new PdfWriter(new FileOutputStream(tmp));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        PdfDocument pdfDoc = new PdfDocument(writer);
                        Document document = new Document(pdfDoc, new com.itextpdf.kernel.geom.PageSize(widthPoints, heightPoints));

                        document.add(new Paragraph(data).setFontSize(14).setTextAlignment(TextAlignment.CENTER));

                        Image qrImage;
                        try {
                            qrImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(qrCodeFile.getAbsolutePath()));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        qrImage.setAutoScale(true).setWidth(150).setHeight(150).setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(qrImage);

                        document.close();
                        qrCodeFile.delete();

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
                    }
                }
            }, AWTEvent.KEY_EVENT_MASK);
        }

        contentBody.add(Objects.requireNonNullElseGet(bodyType, JPanel::new));
        contentBody.repaint();

        if(bodyType != null) {
            bodyType.init();
        }

        frame.pack();

        frame.setSize(size);
        if(fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

}
