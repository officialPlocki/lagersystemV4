package de.kabuecher.storage.v4.server.storage;

import co.plocki.mysql.*;
import de.kabuecher.storage.v4.Main;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class UnitManagement {

    private final MySQLTable.fin fin;

    public UnitManagement() {

        MySQLTable storageTable = new MySQLTable();
        storageTable.prepare("storage", "storeName", "stackName", "boxId", "itemId", "amount");
        fin = storageTable.build();

    }

    public boolean removeItem(String storeName, String stackName, String boxId, String itemId, int amount) {
        Main.addToLog("Removing " + amount + " of item " + itemId + " from box " + boxId + " in stack " + stackName + " in store " + storeName + "(" + System.currentTimeMillis() + ")");

        MySQLRequest request = new MySQLRequest();
        request.prepare("storage");
        request.addRequirement("storeName", storeName);
        request.addRequirement("stackName", stackName);
        request.addRequirement("boxId", boxId);
        request.addRequirement("itemId", itemId);
        request.addRequirement("amount", amount);

        MySQLResponse response = request.execute();

        if(response.isEmpty()) {
            return false;
        }

        if(response.getInt("amount") == amount) {
            MySQLDelete delete = new MySQLDelete();
            delete.prepare("storage");
            delete.addRequirement("storeName", storeName);
            delete.addRequirement("stackName", stackName);
            delete.addRequirement("boxId", boxId);
            delete.addRequirement("itemId", itemId);
            delete.execute();
        } else {
            MySQLPush update = new MySQLPush();
            update.prepare("storage", "amount", response.getInt("amount") - amount);
            update.addRequirement("storeName", storeName);
            update.addRequirement("stackName", stackName);
            update.addRequirement("boxId", boxId);
            update.addRequirement("itemId", itemId);
            update.execute();
        }
        return true;
    }

    public JSONObject searchForItemInStore(String storeName, String itemId, int quantity) {
        Main.addToLog("Searching for " + quantity + " of item " + itemId + " in store " + storeName + "(" + System.currentTimeMillis() + ")");

        MySQLRequest request = new MySQLRequest();
        request.prepare("*", "storage");
        request.addRequirement("storeName", storeName);
        request.addRequirement("itemId", itemId);

        MySQLResponse response = request.execute();

        if(response.isEmpty()) {
            return null;
        }

        JSONObject result = new JSONObject();

        List<HashMap<String, String>> rawAll = response.rawAll();
        rawAll.sort(Comparator.comparingInt(o -> Integer.parseInt(o.get("amount"))));

        for (int i = 0; i < rawAll.size(); i++) {
            HashMap<String, String> keyMap = response.rawAll().get(i);

            if(keyMap.get("amount").equals(String.valueOf(quantity))) {
                result.put(keyMap.get("boxId"), quantity);
                return result;
            } else if(Integer.parseInt(keyMap.get("amount")) < quantity) {
                result.put(keyMap.get("boxId"), Integer.parseInt(keyMap.get("amount")));
                quantity -= Integer.parseInt(keyMap.get("amount"));
            } else {
                result.put(keyMap.get("boxId"), quantity);
                return result;
            }
        }

        return result;
    }

    public void addItemToBox(String storeName, String stackName, String boxId, String itemId, int quantity) {
        Main.addToLog("Adding " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName + "(" + System.currentTimeMillis() + ")");

        MySQLRequest request = new MySQLRequest();
        request.prepare("storage");
        request.addRequirement("storeName", storeName);
        request.addRequirement("stackName", stackName);
        request.addRequirement("boxId", boxId);
        request.addRequirement("itemId", itemId);

        MySQLResponse response = request.execute();

        if(response.isEmpty()) {
            MySQLInsert insert = new MySQLInsert();
            insert.prepare(fin, storeName, stackName, boxId, itemId, quantity);
            insert.execute();
        } else {
            MySQLPush update = new MySQLPush();
            update.prepare("storage", "amount", quantity);
            update.addRequirement("storeName", storeName);
            update.addRequirement("stackName", stackName);
            update.addRequirement("boxId", boxId);
            update.addRequirement("itemId", itemId);
            update.execute();
        }
    }

    public JSONObject getStoredItems(String storeName) {
        Main.addToLog("Getting all stored items in store " + storeName + "(" + System.currentTimeMillis() + ")");

        MySQLRequest request = new MySQLRequest();
        request.prepare("storage");
        request.addRequirement("storeName", storeName);

        MySQLResponse response = request.execute();

        if(response.isEmpty()) {
            return null;
        }

        JSONObject result = new JSONObject();

        result.put(storeName, new JSONObject());

        for (int i = 0; i < response.rawAll().size(); i++) {
            HashMap<String, String> keyMap = response.rawAll().get(i);

            if(!result.has(keyMap.get("stackName"))) {
                result.put(keyMap.get("stackName"), new JSONObject());
            }
            JSONObject stack = result.getJSONObject(keyMap.get("stackName"));

            if(!stack.has(keyMap.get("boxId"))) {
                stack.put(keyMap.get("boxId"), new JSONObject());
            }
            JSONObject box = stack.getJSONObject(keyMap.get("boxId"));

            box.put(keyMap.get("itemId"), keyMap.get("amount"));

            if(!result.getJSONObject(storeName).has("stacks")) {
                result.getJSONObject(storeName).put("stacks", new JSONObject());
            }

            if(!result.getJSONObject(storeName).getJSONObject("stacks").has(keyMap.get("stackName"))) {
                result.getJSONObject(storeName).getJSONObject("stacks").put(keyMap.get("stackName"), new JSONObject());
            }

            if(!result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).has(keyMap.get("boxId"))) {
                result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).put(keyMap.get("boxId"), new JSONObject());
            }

            if(!result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).getJSONObject(keyMap.get("boxId")).has(keyMap.get("itemId"))) {
                result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).getJSONObject(keyMap.get("boxId")).put(keyMap.get("itemId"), keyMap.get("amount"));
            } else {
                result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).getJSONObject(keyMap.get("boxId")).put(keyMap.get("itemId"),
                        result.getJSONObject(storeName).getJSONObject("stacks").getJSONObject(keyMap.get("stackName")).getJSONObject(keyMap.get("boxId")).getInt(keyMap.get("itemId")) + Integer.parseInt(keyMap.get("amount")));
            }
        }

        System.out.println(result);

        return result;
    }

}
