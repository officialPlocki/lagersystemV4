package de.kabuecher.storage.v4.client.sevdesk;

import java.time.ZonedDateTime;

public interface Part {

    String getId();

    String getObjectName();

    ZonedDateTime getCreate();

    ZonedDateTime getUpdate();

    String getName();

    String getPartNumber();

    String getText();

    Category getCategory();

    int getStock();

    boolean isStockEnabled();

    Unity getUnity();

    double getPrice();

    double getPriceNet();

    double getPriceGross();

    SevClient getSevClient();

    double getPricePurchase();

    double getTaxRate();

    int getStatus();

    String getInternalComment();

    interface Category {
        String getId();

        String getObjectName();
    }

    interface Unity {
        String getId();

        String getObjectName();
    }

}
