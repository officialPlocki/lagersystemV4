package de.kabuecher.storage.v4.sevdesk.impl.invoice;

import de.kabuecher.storage.v4.sevdesk.impl.Contact;
import de.kabuecher.storage.v4.sevdesk.impl.SevClient;

import java.time.ZonedDateTime;

public interface Invoice {

    String getId();

    String getObjectName();

    String getInvoiceNumber();

    Contact getContact();

    ZonedDateTime getCreate();

    ZonedDateTime getUpdate();

    SevClient getSevClient();

    ZonedDateTime getInvoiceDate();

    String getHeader();

    String getHeadText();

    String getFootText();

    int getTimeToPay();

    int getDiscountTime();

    double getDiscount();

    Country getAddressCountry();

    ZonedDateTime getPayDate();

    ZonedDateTime getDeliveryDate();

    ZonedDateTime getDeliveryDateUntil();

    int getStatus();

    double getSmallSettlement();

    User getContactPerson();

    double getTaxRate();

    TaxRule getTaxRule();

    String getTaxText();

    int getDunningLevel();

    String getTaxType();

    PaymentMethod getPaymentMethod();

    CostCentre getCostCentre();

    Origin getOrigin();

    String getInvoiceType();

    String getAccountIntervall();

    String getAccountNextInvoice();

    double getReminderTotal();

    double getReminderDebit();

    String getReminderDeadline();

    double getReminderCharge();

    TaxSet getTaxSet();

    String getAddress();

    String getCurrency();

    double getSumNet();

    double getSumTax();

    double getSumGross();

    double getSumDiscounts();

    double getSumNetForeignCurrency();

    double getSumTaxForeignCurrency();

    double getSumGrossForeignCurrency();

    double getSumDiscountsForeignCurrency();

    double getSumNetAccounting();

    double getSumTaxAccounting();

    double getSumGrossAccounting();

    double getPaidAmount();

    String getCustomerInternalNote();

    boolean isShowNet();

    ZonedDateTime getEnshrined();

    String getSendType();

    String getSendPaymentReceivedNotificationDate();

    interface Country {
        String getId();
        String getObjectName();
    }

    interface User {
        String getId();
        String getObjectName();
    }

    interface TaxRule {
        String getId();
        String getObjectName();
    }

    interface PaymentMethod {
        String getId();
        String getObjectName();
    }

    interface CostCentre {
        String getId();
        String getObjectName();
    }

    interface Origin {
        String getId();
        String getObjectName();
    }

    interface TaxSet {
        String getId();
        String getObjectName();
    }
}
