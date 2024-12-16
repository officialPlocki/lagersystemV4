package de.kabuecher.storage.v4.sevdesk.impl.offer;

import de.kabuecher.storage.v4.sevdesk.impl.Contact;
import de.kabuecher.storage.v4.sevdesk.impl.SevClient;

import java.time.ZonedDateTime;

public interface Offer {

    String getId();
    String getObjectName();
    ZonedDateTime getCreate();
    ZonedDateTime getUpdate();
    String getOrderNumber();
    Contact getContact();
    String getOrderDate();
    String getStatus();
    String getHeader();
    String getHeadText();
    String getFootText();
    Country getAddressCountry();
    SevUser getCreateUser();
    SevClient getSevClient();
    String getDeliveryTerms();
    String getPaymentTerms();
    Origin getOrigin();
    String getVersion();
    String getSmallSettlement();
    SevUser getContactPerson();
    String getTaxRate();
    TaxRule getTaxRule();
    TaxSet getTaxSet();
    String getTaxText();
    String getTaxType();
    String getOrderType();
    ZonedDateTime getSendDate();
    String getAddress();
    String getCurrency();
    String getSumNet();
    String getSumTax();
    String getSumGross();
    String getSumDiscounts();
    String getSumNetForeignCurrency();
    String getSumTaxForeignCurrency();
    String getSumGrossForeignCurrency();
    String getSumDiscountsForeignCurrency();
    String getCustomerInternalNote();
    boolean isShowNet();
    String getSendType();

    interface Country {
        String getId();
        String getObjectName();
    }

    interface SevUser {
        String getId();
        String getObjectName();
    }

    interface Origin {
        String getId();
        String getObjectName();
    }

    interface TaxRule {
        String getId();
        String getObjectName();
    }

    interface TaxSet {
        String getId();
        String getObjectName();
    }
}
