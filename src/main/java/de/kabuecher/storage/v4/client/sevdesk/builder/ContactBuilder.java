package de.kabuecher.storage.v4.client.sevdesk.builder;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.sevdesk.SevDeskClient;
import de.kabuecher.storage.v4.client.sevdesk.Contact;
import de.kabuecher.storage.v4.client.sevdesk.SevClient;
import org.json.JSONObject;

import java.time.ZonedDateTime;

public class ContactBuilder {

    public Contact buildContact(JSONObject contact) {

        Main.addToLog("Building contact from JSON object: " + contact.toString());

        return new Contact() {
            @Override
            public String getId() {
                return contact.has("id") ? String.valueOf(contact.get("id")) : null;
            }

            @Override
            public String getObjectName() {
                return contact.optString("objectName", null);
            }

            @Override
            public ZonedDateTime getCreate() {
                return contact.has("create") ? ZonedDateTime.parse(contact.getString("create")) : null;
            }

            @Override
            public ZonedDateTime getUpdate() {
                return contact.has("update") ? ZonedDateTime.parse(contact.getString("update")) : null;
            }

            @Override
            public String getName() {
                return contact.optString("name", null);
            }

            @Override
            public String getStatus() {
                return contact.optString("status", null);
            }

            @Override
            public String getCustomerNumber() {
                return contact.optString("customerNumber", null);
            }

            @Override
            public Contact getParent() {
                if (!contact.has("parent") || contact.isNull("parent")) {
                    return null;
                }

                return new SevDeskClient().getContact(contact.getJSONObject("parent").getString("id"));
            }

            @Override
            public String getSurename() {
                return contact.optString("surename", null);
            }

            @Override
            public String getFamilyname() {
                return contact.optString("familyname", null);
            }

            @Override
            public String getTitel() {
                return contact.optString("titel", null);
            }

            @Override
            public Category getCategory() {
                if (!contact.has("category") || contact.isNull("category")) {
                    return null;
                }
                JSONObject categoryJson = contact.getJSONObject("category");
                return new Category() {
                    @Override
                    public String getId() {
                        return categoryJson.has("id") ? String.valueOf(categoryJson.get("id")) : null;
                    }

                    @Override
                    public String getObjectName() {
                        return categoryJson.optString("objectName", null);
                    }
                };
            }

            @Override
            public String getDescription() {
                return contact.optString("description", null);
            }

            @Override
            public String getAcademicTitle() {
                return contact.optString("academicTitle", null);
            }

            @Override
            public String getGender() {
                return contact.optString("gender", null);
            }

            @Override
            public SevClient getSevClient() {
                if (!contact.has("sevClient") || contact.isNull("sevClient")) {
                    return null;
                }
                JSONObject sevClientJson = contact.getJSONObject("sevClient");
                return new SevClient() {
                    @Override
                    public String getId() {
                        return sevClientJson.has("id") ? String.valueOf(sevClientJson.get("id")) : null;
                    }

                    @Override
                    public String getObjectName() {
                        return sevClientJson.optString("objectName", null);
                    }
                };
            }

            @Override
            public String getName2() {
                return contact.optString("name2", null);
            }

            @Override
            public String getBirthday() {
                return contact.optString("birthday", null);
            }

            @Override
            public String getVatNumber() {
                return contact.optString("vatNumber", null);
            }

            @Override
            public String getBankAccount() {
                return contact.optString("bankAccount", null);
            }

            @Override
            public String getBankNumber() {
                return contact.optString("bankNumber", null);
            }

            @Override
            public String getDefaultCashbackTime() {
                return contact.optString("defaultCashbackTime", null);
            }

            @Override
            public String getDefaultCashbackPercent() {
                return contact.optString("defaultCashbackPercent", null);
            }

            @Override
            public String getDefaultTimeToPay() {
                return contact.optString("defaultTimeToPay", null);
            }

            @Override
            public String getTaxNumber() {
                return contact.optString("taxNumber", null);
            }

            @Override
            public String getTaxOffice() {
                return contact.optString("taxOffice", null);
            }

            @Override
            public boolean isExemptVat() {
                return contact.optBoolean("exemptVat", false);
            }

            @Override
            public String getDefaultDiscountAmount() {
                return contact.optString("defaultDiscountAmount", null);
            }

            @Override
            public boolean isDefaultDiscountPercentage() {
                return contact.optBoolean("defaultDiscountPercentage", false);
            }

            @Override
            public String getBuyerReference() {
                return contact.optString("buyerReference", null);
            }

            @Override
            public boolean isGovernmentAgency() {
                return contact.optBoolean("governmentAgency", false);
            }

            @Override
            public String getAdditionalInformation() {
                return contact.optString("additionalInformation", null);
            }
        };
    }
}
