package de.kabuecher.storage.v4.sevdesk.impl.invoice;

import de.kabuecher.storage.v4.sevdesk.impl.Part;
import de.kabuecher.storage.v4.sevdesk.impl.SevClient;

import java.time.ZonedDateTime;

public interface InvoicePos {

    String getId();
    String getObjectName();
    ZonedDateTime getCreate();
    ZonedDateTime getUpdate();
    Invoice getInvoice();
    Part getPart();
    boolean isQuantity();
    String getPrice();
    String getName();
    Unity getUnity();
    SevClient getSevClient();
    String getPositionNumber();
    String getText();
    String getDiscount();
    String getTaxRate();
    String getSumDiscount();
    String getSumNetAccounting();
    String getSumTaxAccounting();
    String getSumGrossAccounting();
    String getPriceNet();
    String getPriceGross();
    String getPriceTax();

    interface Unity {
        String getId();
        String getObjectName();
    }

}
