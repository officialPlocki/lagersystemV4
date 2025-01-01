package de.kabuecher.storage.v4.client.sevdesk.builder;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.sevdesk.Part;
import de.kabuecher.storage.v4.client.sevdesk.SevClient;
import org.json.JSONObject;

import java.time.ZonedDateTime;

public class PartBuilder {

    public Part buildPart(JSONObject part) {

        Main.addToLog("Building part from JSON object: " + part.toString());

        return new Part() {
            @Override
            public String getId() {
                return part.has("id") ? String.valueOf(part.get("id")) : "";
            }

            @Override
            public String getObjectName() {
                return part.has("objectName") ? part.getString("objectName") : "";
            }

            @Override
            public ZonedDateTime getCreate() {
                return part.has("create") ? ZonedDateTime.parse(part.getString("create")) : null;
            }

            @Override
            public ZonedDateTime getUpdate() {
                return part.has("update") ? ZonedDateTime.parse(part.getString("update")) : null;
            }

            @Override
            public String getName() {
                return part.has("name") ? part.getString("name") : "";
            }

            @Override
            public String getPartNumber() {
                return part.has("partNumber") ? part.getString("partNumber") : "";
            }

            @Override
            public String getText() {
                return part.has("text") ? part.getString("text") : "";
            }

            @Override
            public Category getCategory() {
                if (part.has("category")) {
                    JSONObject category = part.getJSONObject("category");
                    return new Category() {
                        @Override
                        public String getId() {
                            return category.has("id") ? String.valueOf(category.get("id")) : "";
                        }

                        @Override
                        public String getObjectName() {
                            return category.has("objectName") ? category.getString("objectName") : "";
                        }
                    };
                }
                return null; // Default to null if no category is found
            }

            @Override
            public int getStock() {
                return part.has("stock") ? part.getInt("stock") : 0;
            }

            @Override
            public boolean isStockEnabled() {
                return part.has("stockEnabled") && part.getInt("stockEnabled") == 1;
            }

            @Override
            public Unity getUnity() {
                if (part.has("unity")) {
                    JSONObject unity = part.getJSONObject("unity");
                    return new Unity() {
                        @Override
                        public String getId() {
                            return unity.has("id") ? String.valueOf(unity.get("id")) : "";
                        }

                        @Override
                        public String getObjectName() {
                            return unity.has("objectName") ? unity.getString("objectName") : "";
                        }
                    };
                }
                return null; // Default to null if no unity is found
            }

            @Override
            public double getPrice() {
                return part.has("price") ? part.getDouble("price") : 0.0;
            }

            @Override
            public double getPriceNet() {
                return part.has("priceNet") ? part.getDouble("priceNet") : 0.0;
            }

            @Override
            public double getPriceGross() {
                return part.has("priceGross") ? part.getDouble("priceGross") : 0.0;
            }

            @Override
            public SevClient getSevClient() {
                if (part.has("sevClient")) {
                    JSONObject sevClient = part.getJSONObject("sevClient");
                    return new SevClient() {
                        @Override
                        public String getId() {
                            return sevClient.has("id") ? String.valueOf(sevClient.get("id")) : "";
                        }

                        @Override
                        public String getObjectName() {
                            return sevClient.has("objectName") ? sevClient.getString("objectName") : "";
                        }
                    };
                }
                return null; // Default to null if no sevClient is found
            }

            @Override
            public double getPricePurchase() {
                return part.has("pricePurchase") ? part.getDouble("pricePurchase") : 0.0;
            }

            @Override
            public double getTaxRate() {
                return part.has("taxRate") ? part.getDouble("taxRate") : 0.0;
            }

            @Override
            public int getStatus() {
                return part.has("status") ? part.getInt("status") : 0;
            }

            @Override
            public String getInternalComment() {
                return part.has("internalComment") ? part.getString("internalComment") : "";
            }
        };
    }
}
