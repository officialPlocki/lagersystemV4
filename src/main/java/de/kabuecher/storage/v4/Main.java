package de.kabuecher.storage.v4;

import co.plocki.json.JSONFile;
import co.plocki.json.JSONValue;
import de.kabuecher.storage.v4.client.desktop.DesktopContentBodyHandler;
import de.kabuecher.storage.v4.client.desktop.DisplayDriver;
import de.kabuecher.storage.v4.client.panels.DesktopSetupFrame;
import org.json.JSONObject;

public class Main {

    private static JSONFile jsonFile;
    public static String sevdesk_api_token;
    public static DisplayDriver displayDriver;
    public static DesktopContentBodyHandler bodyHandler;


    public static JSONFile getJsonFile() {
        return jsonFile;
    }

    public static void main(String[] args) throws Exception {

        jsonFile = new JSONFile("./.kabuecher/config.json",
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        return new JSONObject("{\"value\":\"your_api_key_here\"}");
                    }

                    @Override
                    public String objectName() {
                        return "sevdesk_api_token";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        //label printer & normal printer
                        return new JSONObject("{\"printer\":\"\", \"label_printer\":\"\"}");
                    }

                    @Override
                    public String objectName() {
                        return "printerConfig";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        //label printer & normal printer
                        return new JSONObject("{\"api_key\":\"\"}");
                    }

                    @Override
                    public String objectName() {
                        return "sendcloud";
                    }
                },
                new JSONValue() {
                    @Override
                    public JSONObject object() {
                        //label printer & normal printer
                        return new JSONObject("{\"mode\":\"client\"}");
                    }

                    @Override
                    public String objectName() {
                        return "config";
                    }
                }
        );

        if(jsonFile.isNew()) {
            jsonFile.save();
            new DesktopSetupFrame();
        }
        sevdesk_api_token = jsonFile.get("sevdesk_api_token").getString("value");

        String mode = jsonFile.get("config").getString("mode");

        if(mode.equalsIgnoreCase("client")) {
            displayDriver = new DisplayDriver();
            bodyHandler = displayDriver.getBodyHandler();

            displayDriver.show(true);
        } else if(mode.equalsIgnoreCase("server")) {

        }
    }

}
