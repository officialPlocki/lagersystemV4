/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package de.kabuecher.storage.v4.client.panels;

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
import de.kabuecher.storage.v4.client.desktop.DesktopContentBodyHandler;
import de.kabuecher.storage.v4.client.flows.IngoingInventoryFlow;
import de.kabuecher.storage.v4.client.flows.OrderViewFlow;
import de.kabuecher.storage.v4.client.flows.OutgoingInventoryFlow;
import de.kabuecher.storage.v4.client.flows.ShipFlow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.SecureRandom;

/**
 *
 * @author plocki
 */
public class DesktopMainPanel extends javax.swing.JPanel {

    private final DesktopContentBodyHandler bodyHandler;
    private final JFrame frame;

    /**
     * Creates new form MainPanel
     */
    public DesktopMainPanel(JFrame frame) {

        Main.addToLog("Starting main panel");

        initComponents();
        this.frame = frame;

        bodyHandler = new DesktopContentBodyHandler(frame, contentBody);
        login_logout_button.setEnabled(false);

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if(event instanceof KeyEvent keyEvent) {
                if(keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode() == KeyEvent.VK_F9) {

                    Main.addToLog("F9 pressed");


                    String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                    SecureRandom RANDOM = new SecureRandom();

                    StringBuilder sb = new StringBuilder(12);
                    for (int i = 0; i < 12; i++) {
                        int index = RANDOM.nextInt(CHARSET.length());
                        sb.append(CHARSET.charAt(index));
                    }

                    String data = "BX"+ sb.substring(3, 12);

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

                    Main.addToLog("Label printed");
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }

    public DesktopContentBodyHandler getBodyHandler() {
        return bodyHandler;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        sideBar = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        hub_outgoing_button = new javax.swing.JButton();
        orders_button = new javax.swing.JButton();
        hub_ingoing_button = new javax.swing.JButton();
        login_logout_button = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        scanField = new javax.swing.JTextField();
        ship_button = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();
        fullscreenToggle = new javax.swing.JToggleButton();
        contentBody = new javax.swing.JPanel();

        setBackground(new java.awt.Color(17, 21, 28));
        setMinimumSize(new java.awt.Dimension(1538, 1024));
        setPreferredSize(new java.awt.Dimension(1538, 1024));
        setSize(new java.awt.Dimension(1538, 1024));

        sideBar.setBackground(new java.awt.Color(33, 35, 64));
        sideBar.setSize(new java.awt.Dimension(264, 1024));

        title.setFont(new java.awt.Font("Oswald", 0, 48)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Lagersystem");
        title.setToolTipText("");

        hub_outgoing_button.setBackground(new java.awt.Color(231, 231, 231));
        hub_outgoing_button.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        hub_outgoing_button.setText("Lagerausgang");
        hub_outgoing_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hub_outgoing_buttonActionPerformed(evt);
            }
        });

        orders_button.setBackground(new java.awt.Color(231, 231, 231));
        orders_button.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        orders_button.setText("Bestellungen");
        orders_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orders_buttonActionPerformed(evt);
            }
        });

        hub_ingoing_button.setBackground(new java.awt.Color(231, 231, 231));
        hub_ingoing_button.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        hub_ingoing_button.setText("Lagereingang");
        hub_ingoing_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hub_ingoing_buttonActionPerformed(evt);
            }
        });

        login_logout_button.setBackground(new java.awt.Color(231, 231, 231));
        login_logout_button.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        login_logout_button.setText("Anmelden / Abmelden");

        jLabel2.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(231, 231, 231));
        jLabel2.setText("- EANs (bald)");

        jLabel3.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(231, 231, 231));
        jLabel3.setText("Folgendes ist scannbar:");

        jLabel5.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(231, 231, 231));
        jLabel5.setText("- Boxcodes (bald)");

        jLabel6.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(231, 231, 231));
        jLabel6.setText("- Versandcodes");

        scanField.setFont(new java.awt.Font("Oswald", 0, 36)); // NOI18N
        scanField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        scanField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanFieldActionPerformed(evt);
            }
        });

        ship_button.setBackground(new java.awt.Color(231, 231, 231));
        ship_button.setFont(new java.awt.Font("Oswald", 0, 24)); // NOI18N
        ship_button.setText("Versand");
        ship_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ship_buttonActionPerformed(evt);
            }
        });

        versionLabel.setFont(new java.awt.Font("Oswald", 0, 12)); // NOI18N
        versionLabel.setForeground(new java.awt.Color(255, 255, 255));
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        versionLabel.setText("V4.1");
        versionLabel.setToolTipText("");

        fullscreenToggle.setText("Vollbild");
        fullscreenToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullscreenToggleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sideBarLayout = new javax.swing.GroupLayout(sideBar);
        sideBar.setLayout(sideBarLayout);
        sideBarLayout.setHorizontalGroup(
            sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarLayout.createSequentialGroup()
                .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sideBarLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(title)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(versionLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(sideBarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scanField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hub_outgoing_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hub_ingoing_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(orders_button, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                            .addComponent(login_logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ship_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(sideBarLayout.createSequentialGroup()
                                .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(sideBarLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6))))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(sideBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fullscreenToggle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sideBarLayout.setVerticalGroup(
            sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sideBarLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(versionLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(orders_button, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hub_ingoing_button, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hub_outgoing_button, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ship_button, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fullscreenToggle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(37, 37, 37)
                .addComponent(scanField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(login_logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentBody.setBackground(new java.awt.Color(17, 21, 28));
        contentBody.setSize(new java.awt.Dimension(1218, 1012));

        javax.swing.GroupLayout contentBodyLayout = new javax.swing.GroupLayout(contentBody);
        contentBody.setLayout(contentBodyLayout);
        contentBodyLayout.setHorizontalGroup(
            contentBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1218, Short.MAX_VALUE)
        );
        contentBodyLayout.setVerticalGroup(
            contentBodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1012, Short.MAX_VALUE)
        );

        jLayeredPane1.setLayer(sideBar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(contentBody, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(sideBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sideBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentBody, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void hub_outgoing_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hub_outgoing_buttonActionPerformed
        new OutgoingInventoryFlow();
    }//GEN-LAST:event_hub_outgoing_buttonActionPerformed

    private void hub_ingoing_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hub_ingoing_buttonActionPerformed
        new IngoingInventoryFlow();
    }//GEN-LAST:event_hub_ingoing_buttonActionPerformed

    private void ship_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ship_buttonActionPerformed
        new ShipFlow();
    }//GEN-LAST:event_ship_buttonActionPerformed

    private void orders_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orders_buttonActionPerformed
        new OrderViewFlow();
    }//GEN-LAST:event_orders_buttonActionPerformed

    private void scanFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanFieldActionPerformed
        if(evt.getID() == KeyEvent.KEY_PRESSED) {
            if(scanField.getText().startsWith("LEF")) {
                new ShipFlow().analyzeScan(scanField.getText());
                Main.addToLog("Analyzing scan: " + scanField.getText());
            }
        }
    }//GEN-LAST:event_scanFieldActionPerformed

    private void fullscreenToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullscreenToggleActionPerformed
        if(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            Main.addToLog("Fullscreen disabled");
        } else {
            frame.setExtendedState(JFrame.NORMAL);
            Main.addToLog("Fullscreen enabled");
        }
    }//GEN-LAST:event_fullscreenToggleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentBody;
    private javax.swing.JToggleButton fullscreenToggle;
    private javax.swing.JButton hub_ingoing_button;
    private javax.swing.JButton hub_outgoing_button;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton login_logout_button;
    private javax.swing.JButton orders_button;
    private javax.swing.JTextField scanField;
    private javax.swing.JButton ship_button;
    private javax.swing.JPanel sideBar;
    private javax.swing.JLabel title;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
