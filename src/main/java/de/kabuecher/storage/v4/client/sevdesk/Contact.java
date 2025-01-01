package de.kabuecher.storage.v4.client.sevdesk;

import java.time.ZonedDateTime;

public interface Contact {

    String getId();

    String getObjectName();

    ZonedDateTime getCreate();

    ZonedDateTime getUpdate();

    String getName();

    String getStatus();

    String getCustomerNumber();

    Contact getParent();

    String getSurename();

    String getFamilyname();

    String getTitel();

    Category getCategory();

    String getDescription();

    String getAcademicTitle();

    String getGender();

    SevClient getSevClient();

    String getName2();

    String getBirthday();

    String getVatNumber();

    String getBankAccount();

    String getBankNumber();

    String getDefaultCashbackTime();

    String getDefaultCashbackPercent();

    String getDefaultTimeToPay();

    String getTaxNumber();

    String getTaxOffice();

    boolean isExemptVat();

    String getDefaultDiscountAmount();

    boolean isDefaultDiscountPercentage();

    String getBuyerReference();

    boolean isGovernmentAgency();

    String getAdditionalInformation();

    // Nested interfaces
    interface Category {
        String getId();

        String getObjectName();
    }
}
