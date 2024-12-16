package de.kabuecher.storage.v4.sevdesk.impl.offer;

import de.kabuecher.storage.v4.sevdesk.impl.Part;

public interface OfferPos {

    String getId();
    Offer getOffer();
    Part getPart();
    int getQuantity();
    double getPrice();
    double getPriceTax();
    double getPriceGross();
    String getName();
    Unity getUnity();
    int getPositionNumber();
    String getText();
    double getDiscount();
    boolean isOptional();
    double getTaxRate();

    interface Unity {
        String getId();
        String getObjectName();
    }
}
