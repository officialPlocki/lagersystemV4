package de.kabuecher.storage.v4.client.sevdesk.builder;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.sevdesk.SevDeskClient;
import de.kabuecher.storage.v4.client.sevdesk.Contact;
import de.kabuecher.storage.v4.client.sevdesk.Part;
import de.kabuecher.storage.v4.client.sevdesk.SevClient;
import de.kabuecher.storage.v4.client.sevdesk.offer.Offer;
import de.kabuecher.storage.v4.client.sevdesk.offer.OfferPos;
import org.json.JSONObject;

import java.time.ZonedDateTime;

public class OfferBuilder {
 
    public Offer buildOffer(JSONObject offer) {

        Main.addToLog("Building offer from JSON object: " + offer.toString());

        return new Offer() {
            @Override
            public String getId() {
                return String.valueOf(offer.get("id"));
            }

            @Override
            public String getObjectName() {
                return offer.getString("objectName");
            }

            @Override
            public ZonedDateTime getCreate() {
                return ZonedDateTime.parse(offer.getString("create"));
            }

            @Override
            public ZonedDateTime getUpdate() {
                return ZonedDateTime.parse(offer.getString("update"));
            }

            @Override
            public String getOrderNumber() {
                return offer.getString("orderNumber");
            }

            @Override
            public Contact getContact() {
                return new SevDeskClient().getContact(offer.getJSONObject("contact").getString("id"));
            }

            @Override
            public String getOrderDate() {
                return offer.getString("orderDate");
            }

            @Override
            public String getStatus() {
                return offer.getString("status");
            }

            @Override
            public String getHeader() {
                return offer.getString("header");
            }

            @Override
            public String getHeadText() {
                return offer.getString("headText");
            }

            @Override
            public String getFootText() {
                return offer.getString("footText");
            }

            @Override
            public Country getAddressCountry() {
                return new Country() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("addressCountry").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("addressCountry").getString("objectName");
                    }
                };
            }

            @Override
            public SevUser getCreateUser() {
                return new SevUser() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("createUser").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("createUser").getString("objectName");
                    }
                };
            }

            @Override
            public SevClient getSevClient() {
                return new SevClient() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("sevClient").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("sevClient").getString("objectName");
                    }
                };
            }

            @Override
            public String getDeliveryTerms() {
                return offer.getString("deliveryTerms");
            }

            @Override
            public String getPaymentTerms() {
                return offer.getString("paymentTerms");
            }

            @Override
            public Origin getOrigin() {
                return new Origin() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("origin").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("origin").getString("objectName");
                    }
                };
            }

            @Override
            public String getVersion() {
                return offer.getString("version");
            }

            @Override
            public String getSmallSettlement() {
                return offer.getString("smallSettlement");
            }

            @Override
            public SevUser getContactPerson() {
                return new SevUser() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("contactPerson").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("contactPerson").getString("objectName");
                    }
                };
            }

            @Override
            public String getTaxRate() {
                return offer.getString("taxRate");
            }

            @Override
            public TaxRule getTaxRule() {
                return new TaxRule() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("taxRule").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("taxRule").getString("objectName");
                    }
                };
            }

            @Override
            public TaxSet getTaxSet() {
                return new TaxSet() {
                    @Override
                    public String getId() {
                        return String.valueOf(offer.getJSONObject("taxSet").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offer.getJSONObject("taxSet").getString("objectName");
                    }
                };
            }

            @Override
            public String getTaxText() {
                return offer.getString("taxText");
            }

            @Override
            public String getTaxType() {
                return offer.getString("taxType");
            }

            @Override
            public String getOrderType() {
                return offer.getString("orderType");
            }

            @Override
            public ZonedDateTime getSendDate() {
                return ZonedDateTime.parse(offer.getString("sendDate"));
            }

            @Override
            public String getAddress() {
                return offer.getString("address");
            }

            @Override
            public String getCurrency() {
                return offer.getString("currency");
            }

            @Override
            public String getSumNet() {
                return offer.getString("sumNet");
            }

            @Override
            public String getSumTax() {
                return offer.getString("sumTax");
            }

            @Override
            public String getSumGross() {
                return offer.getString("sumGross");
            }

            @Override
            public String getSumDiscounts() {
                return offer.getString("sumDiscounts");
            }

            @Override
            public String getSumNetForeignCurrency() {
                return offer.getString("sumNetForeignCurrency");
            }

            @Override
            public String getSumTaxForeignCurrency() {
                return offer.getString("sumTaxForeignCurrency");
            }

            @Override
            public String getSumGrossForeignCurrency() {
                return offer.getString("sumGrossForeignCurrency");
            }

            @Override
            public String getSumDiscountsForeignCurrency() {
                return offer.getString("sumDiscountsForeignCurrency");
            }

            @Override
            public String getCustomerInternalNote() {
                return offer.getString("customerInternalNote");
            }

            @Override
            public boolean isShowNet() {
                return offer.getBoolean("showNet");
            }

            @Override
            public String getSendType() {
                return offer.getString("sendType");
            }
        };
    }

    public OfferPos buildOfferPos(JSONObject offerPos) {
        return new OfferPos() {
            @Override
            public String getId() {
                return String.valueOf(offerPos.get("id"));
            }

            @Override
            public Offer getOffer() {
                return new SevDeskClient().getOffer(offerPos.getJSONObject("offer").getString("id"));
            }

            @Override
            public Part getPart() {
                return new SevDeskClient().getPart(offerPos.getJSONObject("part").getString("id"));
            }

            @Override
            public int getQuantity() {
                return offerPos.getInt("quantity");
            }

            @Override
            public double getPrice() {
                return offerPos.getDouble("price");
            }

            @Override
            public double getPriceTax() {
                return offerPos.getDouble("priceTax");
            }

            @Override
            public double getPriceGross() {
                return offerPos.getDouble("priceGross");
            }

            @Override
            public String getName() {
                return offerPos.getString("name");
            }

            @Override
            public Unity getUnity() {
                return new Unity() {
                    @Override
                    public String getId() {
                        return String.valueOf(offerPos.getJSONObject("unity").get("id"));
                    }

                    @Override
                    public String getObjectName() {
                        return offerPos.getJSONObject("unity").getString("objectName");
                    }
                };
            }

            @Override
            public int getPositionNumber() {
                return offerPos.getInt("positionNumber");
            }

            @Override
            public String getText() {
                return offerPos.getString("text");
            }

            @Override
            public double getDiscount() {
                return offerPos.getDouble("discount");
            }

            @Override
            public boolean isOptional() {
                return offerPos.getBoolean("optional");
            }

            @Override
            public double getTaxRate() {
                return offerPos.getDouble("taxRate");
            }
        };
    }

}
