package de.kabuecher.storage.v4.sevdesk.impl.builder;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.sevdesk.SevDesk;
import de.kabuecher.storage.v4.sevdesk.impl.Contact;
import de.kabuecher.storage.v4.sevdesk.impl.Part;
import de.kabuecher.storage.v4.sevdesk.impl.SevClient;
import de.kabuecher.storage.v4.sevdesk.impl.invoice.Invoice;
import de.kabuecher.storage.v4.sevdesk.impl.invoice.InvoicePos;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.Date;

public class InvoiceBuilder {

    public Invoice buildInvoice(JSONObject invoice) {

        Main.addToLog("Building invoice from JSON object: " + invoice.toString());

        return new Invoice() {
            @Override
            public String getId() {
                return invoice.has("id") ? String.valueOf(invoice.get("id")) : null;
            }

            @Override
            public String getObjectName() {
                return invoice.has("objectName") ? invoice.getString("objectName") : null;
            }

            @Override
            public String getInvoiceNumber() {
                return invoice.has("invoiceNumber") ? invoice.getString("invoiceNumber") : null;
            }

            @Override
            public Contact getContact() {
                return (invoice.has("contact") && !invoice.isNull("contact")) ? new SevDesk().getContact(invoice.getJSONObject("contact").getString("id")) : null;
            }

            @Override
            public ZonedDateTime getCreate() {
                return invoice.has("create") ? ZonedDateTime.parse(invoice.getString("create")) : null;
            }

            @Override
            public ZonedDateTime getUpdate() {
                return invoice.has("update") ? ZonedDateTime.parse(invoice.getString("update")) : null;
            }

            @Override
            public SevClient getSevClient() {
                return new SevClient() {
                    @Override
                    public String getId() {
                        return invoice.has("sevClient") && invoice.getJSONObject("sevClient").has("id")
                                ? String.valueOf(invoice.getJSONObject("sevClient").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("sevClient") && invoice.getJSONObject("sevClient").has("objectName")
                                ? invoice.getJSONObject("sevClient").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public ZonedDateTime getInvoiceDate() {
                return invoice.has("invoiceDate") ? ZonedDateTime.parse(invoice.getString("invoiceDate")) : null;
            }

            @Override
            public String getHeader() {
                return invoice.has("header") ? invoice.getString("header") : null;
            }

            @Override
            public String getHeadText() {
                return invoice.has("headText") ? invoice.getString("headText") : null;
            }

            @Override
            public String getFootText() {
                return invoice.has("footText") ? invoice.getString("footText") : null;
            }

            @Override
            public int getTimeToPay() {
                return invoice.has("timeToPay") ? invoice.getInt("timeToPay") : 0;
            }

            @Override
            public int getDiscountTime() {
                return invoice.has("discountTime") ? invoice.getInt("discountTime") : 0;
            }

            @Override
            public double getDiscount() {
                return invoice.has("discount") ? invoice.getDouble("discount") : 0.0;
            }

            @Override
            public Country getAddressCountry() {
                return new Country() {
                    @Override
                    public String getId() {
                        return invoice.has("addressCountry") && invoice.getJSONObject("addressCountry").has("id")
                                ? String.valueOf(invoice.getJSONObject("addressCountry").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("addressCountry") && invoice.getJSONObject("addressCountry").has("objectName")
                                ? invoice.getJSONObject("addressCountry").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public ZonedDateTime getPayDate() {
                return invoice.has("payDate") ? ZonedDateTime.parse(invoice.getString("payDate")) : null;
            }

            @Override
            public ZonedDateTime getDeliveryDate() {
                return invoice.has("deliveryDate") ? ZonedDateTime.parse(invoice.getString("deliveryDate")) : null;
            }

            @Override
            public ZonedDateTime getDeliveryDateUntil() {
                return invoice.has("deliveryDateUntil") ? ZonedDateTime.parse(invoice.getString("deliveryDateUntil")) : null;
            }

            @Override
            public int getStatus() {
                return invoice.has("status") ? invoice.getInt("status") : 0;
            }

            @Override
            public double getSmallSettlement() {
                return invoice.has("smallSettlement") ? invoice.getDouble("smallSettlement") : 0.0;
            }

            @Override
            public User getContactPerson() {
                return new User() {
                    @Override
                    public String getId() {
                        return invoice.has("contactPerson") && invoice.getJSONObject("contactPerson").has("id")
                                ? String.valueOf(invoice.getJSONObject("contactPerson").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("contactPerson") && invoice.getJSONObject("contactPerson").has("objectName")
                                ? invoice.getJSONObject("contactPerson").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public double getTaxRate() {
                return invoice.has("taxRate") ? invoice.getDouble("taxRate") : 0.0;
            }

            @Override
            public TaxRule getTaxRule() {
                return new TaxRule() {
                    @Override
                    public String getId() {
                        return invoice.has("taxRule") && invoice.getJSONObject("taxRule").has("id")
                                ? String.valueOf(invoice.getJSONObject("taxRule").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("taxRule") && invoice.getJSONObject("taxRule").has("objectName")
                                ? invoice.getJSONObject("taxRule").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public String getTaxText() {
                return invoice.has("taxText") ? invoice.getString("taxText") : null;
            }

            @Override
            public int getDunningLevel() {
                return invoice.has("dunningLevel") ? invoice.getInt("dunningLevel") : 0;
            }

            @Override
            public String getTaxType() {
                return invoice.has("taxType") ? invoice.getString("taxType") : null;
            }

            @Override
            public PaymentMethod getPaymentMethod() {
                return new PaymentMethod() {
                    @Override
                    public String getId() {
                        return invoice.has("paymentMethod") && invoice.getJSONObject("paymentMethod").has("id")
                                ? String.valueOf(invoice.getJSONObject("paymentMethod").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("paymentMethod") && invoice.getJSONObject("paymentMethod").has("objectName")
                                ? invoice.getJSONObject("paymentMethod").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public CostCentre getCostCentre() {
                return new CostCentre() {
                    @Override
                    public String getId() {
                        return invoice.has("costCentre") && invoice.getJSONObject("costCentre").has("id")
                                ? String.valueOf(invoice.getJSONObject("costCentre").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("costCentre") && invoice.getJSONObject("costCentre").has("objectName")
                                ? invoice.getJSONObject("costCentre").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public Origin getOrigin() {
                return new Origin() {
                    @Override
                    public String getId() {
                        return invoice.has("origin") && invoice.getJSONObject("origin").has("id")
                                ? String.valueOf(invoice.getJSONObject("origin").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("origin") && invoice.getJSONObject("origin").has("objectName")
                                ? invoice.getJSONObject("origin").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public String getInvoiceType() {
                return invoice.has("invoiceType") ? invoice.getString("invoiceType") : null;
            }

            @Override
            public String getAccountIntervall() {
                return invoice.has("accountIntervall") ? invoice.getString("accountIntervall") : null;
            }

            @Override
            public String getAccountNextInvoice() {
                return invoice.has("accountNextInvoice") ? invoice.getString("accountNextInvoice") : null;
            }

            @Override
            public double getReminderTotal() {
                return invoice.has("reminderTotal") ? invoice.getDouble("reminderTotal") : 0.0;
            }

            @Override
            public double getReminderDebit() {
                return invoice.has("reminderDebit") ? invoice.getDouble("reminderDebit") : 0.0;
            }

            @Override
            public String getReminderDeadline() {
                return invoice.has("reminderDeadline") ? invoice.getString("reminderDeadline") : null;
            }

            @Override
            public double getReminderCharge() {
                return invoice.has("reminderCharge") ? invoice.getDouble("reminderCharge") : 0.0;
            }

            @Override
            public TaxSet getTaxSet() {
                return new TaxSet() {
                    @Override
                    public String getId() {
                        return invoice.has("taxSet") && invoice.getJSONObject("taxSet").has("id")
                                ? String.valueOf(invoice.getJSONObject("taxSet").get("id"))
                                : null;
                    }

                    @Override
                    public String getObjectName() {
                        return invoice.has("taxSet") && invoice.getJSONObject("taxSet").has("objectName")
                                ? invoice.getJSONObject("taxSet").getString("objectName")
                                : null;
                    }
                };
            }

            @Override
            public String getAddress() {
                return invoice.has("address") ? invoice.getString("address") : null;
            }

            @Override
            public String getCurrency() {
                return invoice.has("currency") ? invoice.getString("currency") : null;
            }

            @Override
            public double getSumNet() {
                return invoice.has("sumNet") ? invoice.getDouble("sumNet") : 0.0;
            }

            @Override
            public double getSumTax() {
                return invoice.has("sumTax") ? invoice.getDouble("sumTax") : 0.0;
            }

            @Override
            public double getSumGross() {
                return invoice.has("sumGross") ? invoice.getDouble("sumGross") : 0.0;
            }

            @Override
            public double getSumDiscounts() {
                return invoice.has("sumDiscounts") ? invoice.getDouble("sumDiscounts") : 0.0;
            }

            @Override
            public double getSumNetForeignCurrency() {
                return invoice.has("sumNetForeignCurrency") ? invoice.getDouble("sumNetForeignCurrency") : 0.0;
            }

            @Override
            public double getSumTaxForeignCurrency() {
                return invoice.has("sumTaxForeignCurrency") ? invoice.getDouble("sumTaxForeignCurrency") : 0.0;
            }

            @Override
            public double getSumGrossForeignCurrency() {
                return invoice.has("sumGrossForeignCurrency") ? invoice.getDouble("sumGrossForeignCurrency") : 0.0;
            }

            @Override
            public double getSumDiscountsForeignCurrency() {
                return invoice.has("sumDiscountsForeignCurrency") ? invoice.getDouble("sumDiscountsForeignCurrency") : 0.0;
            }

            @Override
            public double getSumNetAccounting() {
                return invoice.has("sumNetAccounting") ? invoice.getDouble("sumNetAccounting") : 0.0;
            }

            @Override
            public double getSumTaxAccounting() {
                return invoice.has("sumTaxAccounting") ? invoice.getDouble("sumTaxAccounting") : 0.0;
            }

            @Override
            public double getSumGrossAccounting() {
                return invoice.has("sumGrossAccounting") ? invoice.getDouble("sumGrossAccounting") : 0.0;
            }

            @Override
            public double getPaidAmount() {
                return invoice.has("paidAmount") ? invoice.getDouble("paidAmount") : 0.0;
            }

            @Override
            public String getCustomerInternalNote() {
                return invoice.has("customerInternalNote") ? invoice.getString("customerInternalNote") : null;
            }

            @Override
            public boolean isShowNet() {
                return invoice.has("showNet") && invoice.getBoolean("showNet");
            }

            @Override
            public ZonedDateTime getEnshrined() {
                return invoice.has("enshrined") ? ZonedDateTime.parse(invoice.getString("enshrined")) : null;
            }

            @Override
            public String getSendType() {
                return invoice.has("sendType") ? invoice.getString("sendType") : null;
            }

            @Override
            public String getSendPaymentReceivedNotificationDate() {
                return invoice.has("sendPaymentReceivedNotificationDate") ? invoice.getString("sendPaymentReceivedNotificationDate") : null;
            }
        };
    }

    public InvoicePos buildInvoicePos(JSONObject invoicePos) {
        return new InvoicePos() {
            @Override
            public String getId() {
                return invoicePos.has("id") ? String.valueOf(invoicePos.get("id")) : "";
            }

            @Override
            public String getObjectName() {
                return invoicePos.has("objectName") ? invoicePos.getString("objectName") : "";
            }

            @Override
            public ZonedDateTime getCreate() {
                return invoicePos.has("create") ? ZonedDateTime.parse(invoicePos.getString("create")) : null;
            }

            @Override
            public ZonedDateTime getUpdate() {
                return invoicePos.has("update") ? ZonedDateTime.parse(invoicePos.getString("update")) : null;
            }

            @Override
            public Invoice getInvoice() {
                return new SevDesk().getInvoice(invoicePos.getJSONObject("invoice").getString("id"));
            }

            @Override
            public Part getPart() {
                return new SevDesk().getPart(invoicePos.getJSONObject("part").getString("id"));
            }

            @Override
            public boolean isQuantity() {
                return invoicePos.has("quantity") && invoicePos.getBoolean("quantity");
            }

            @Override
            public String getPrice() {
                return invoicePos.has("price") ? invoicePos.getString("price") : "";
            }

            @Override
            public String getName() {
                return invoicePos.has("name") ? invoicePos.getString("name") : "";
            }

            @Override
            public Unity getUnity() {
                return new Unity() {
                    @Override
                    public String getId() {
                        return invoicePos.has("unity") && invoicePos.getJSONObject("unity").has("id")
                                ? String.valueOf(invoicePos.getJSONObject("unity").get("id"))
                                : "";
                    }

                    @Override
                    public String getObjectName() {
                        return invoicePos.has("unity") && invoicePos.getJSONObject("unity").has("objectName")
                                ? invoicePos.getJSONObject("unity").getString("objectName")
                                : "";
                    }
                };
            }

            @Override
            public SevClient getSevClient() {
                return new SevClient() {
                    @Override
                    public String getId() {
                        return invoicePos.has("sevClient") && invoicePos.getJSONObject("sevClient").has("id")
                                ? String.valueOf(invoicePos.getJSONObject("sevClient").get("id"))
                                : "";
                    }

                    @Override
                    public String getObjectName() {
                        return invoicePos.has("sevClient") && invoicePos.getJSONObject("sevClient").has("objectName")
                                ? invoicePos.getJSONObject("sevClient").getString("objectName")
                                : "";
                    }
                };
            }

            @Override
            public String getPositionNumber() {
                return invoicePos.has("positionNumber") ? invoicePos.getString("positionNumber") : "";
            }

            @Override
            public String getText() {
                return invoicePos.has("text") ? invoicePos.getString("text") : "";
            }

            @Override
            public String getDiscount() {
                return invoicePos.has("discount") ? invoicePos.getString("discount") : "";
            }

            @Override
            public String getTaxRate() {
                return invoicePos.has("taxRate") ? invoicePos.getString("taxRate") : "";
            }

            @Override
            public String getSumDiscount() {
                return invoicePos.has("sumDiscount") ? invoicePos.getString("sumDiscount") : "";
            }

            @Override
            public String getSumNetAccounting() {
                return invoicePos.has("sumNetAccounting") ? invoicePos.getString("sumNetAccounting") : "";
            }

            @Override
            public String getSumTaxAccounting() {
                return invoicePos.has("sumTaxAccounting") ? invoicePos.getString("sumTaxAccounting") : "";
            }

            @Override
            public String getSumGrossAccounting() {
                return invoicePos.has("sumGrossAccounting") ? invoicePos.getString("sumGrossAccounting") : "";
            }

            @Override
            public String getPriceNet() {
                return invoicePos.has("priceNet") ? invoicePos.getString("priceNet") : "";
            }

            @Override
            public String getPriceGross() {
                return invoicePos.has("priceGross") ? invoicePos.getString("priceGross") : "";
            }

            @Override
            public String getPriceTax() {
                return invoicePos.has("priceTax") ? invoicePos.getString("priceTax") : "";
            }
        };
    }


}
