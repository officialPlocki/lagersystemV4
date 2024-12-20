package de.kabuecher.storage.v4.storage;

import co.plocki.json.JSONFile;
import de.kabuecher.storage.v4.Main;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitManagement {
    private final JSONFile jsonFile;

    public UnitManagement() {
        this.jsonFile = new JSONFile("./.kabuecher/storage.json");
    }

    public boolean removeItem(String storeName, String stackName, String boxId, String itemId, int amount) {
        Main.addToLog("Removing " + amount + " of item " + itemId + " from box " + boxId + " in stack " + stackName + " in store " + storeName);
        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName);
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                if (stacks.has(stackName)) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    if (stack.has(boxId)) {
                        JSONObject box = stack.getJSONObject(boxId);
                        if (box.has(itemId)) {
                            if (box.getInt(itemId) <= amount) {
                                box.remove(itemId);
                                Main.addToLog("Removing " + amount + " of item " + itemId + " from box " + boxId + " in stack " + stackName + " in store " + storeName);
                            } else {
                                Main.addToLog("Removing " + amount + " of item " + itemId + " from box " + boxId + " in stack " + stackName + " in store " + storeName);
                                box.put(itemId, box.getInt(itemId) - amount);
                            }
                            jsonFile.put(storeName, store);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public JSONObject searchForItemInStore(String storeName, String itemId, int quantity) {
        Main.addToLog("Searching for " + quantity + " of item " + itemId + " in store " + storeName);
        List<String> out = new ArrayList<>();
        JSONObject result = new JSONObject();

        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName);
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                for (String stackName : stacks.keySet()) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    for (String boxId : stack.keySet()) {
                        JSONObject box = stack.getJSONObject(boxId);
                        if (box.has(itemId)) {
                            if (box.getInt(itemId) >= quantity) {
                                result.put(boxId, quantity);
                                Main.addToLog("Found " + quantity + " of item " + itemId + " in box " + boxId + " in stack " + stackName + " in store " + storeName);
                                return result;
                            } else {
                                result.put(boxId, box.getInt(itemId));
                                quantity -= box.getInt(itemId);
                                out.add(boxId);
                                Main.addToLog("Found " + box.getInt(itemId) + " of item " + itemId + " in box " + boxId + " in stack " + stackName + " in store " + storeName);

                                while (quantity > 0) {
                                    JSONObject object = searchForItemInStoreExcept(storeName, itemId, quantity, out.toArray(String[]::new));
                                    if(object == null || object.isEmpty()) {
                                        Main.addToLog("Could not find enough of item " + itemId + " in store " + storeName);
                                        return null;
                                    } else {
                                        result.put(String.valueOf(object.keys().next()), object.getInt(object.keys().next()));
                                        quantity -= object.getInt(object.keys().next());
                                        out.add(String.valueOf(object.keys().next()));
                                        Main.addToLog("Found " + object.getInt(object.keys().next()) + " of item " + itemId + " in box " + object.keys().next() + " in store " + storeName);
                                    }
                                }

                                Main.addToLog("Found " + result.toString() + " of item " + itemId + " in store " + storeName);
                                return result;
                            }
                        }
                    }
                }
            }
        }
        Main.addToLog("Could not find enough of item " + itemId + " in store " + storeName);
        return null;
    }

    private JSONObject searchForItemInStoreExcept(String storeName, String itemId, int quantity, String[]... exceptionalBoxIds) {
        Main.addToLog("Searching for " + quantity + " of item " + itemId + " in store " + storeName + " except for " + exceptionalBoxIds);
        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName);
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                for (String stackName : stacks.keySet()) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    for (String boxId : stack.keySet()) {
                        boolean isExceptional = false;
                        for (String[] exceptionalBoxId : exceptionalBoxIds) {
                            if (exceptionalBoxId[0].equals(boxId)) {
                                isExceptional = true;
                                break;
                            }
                        }
                        if (!isExceptional) {
                            System.out.println("found");
                            JSONObject box = stack.getJSONObject(boxId);
                            if (box.has(itemId)) {
                                if (box.getInt(itemId) >= quantity) {
                                    JSONObject result = new JSONObject();
                                    result.put(boxId, quantity);
                                    Main.addToLog("Found " + quantity + " of item " + itemId + " in box " + boxId + " in stack " + stackName + " in store " + storeName);
                                    return result;
                                } else {
                                    JSONObject result = new JSONObject();
                                    result.put(boxId, box.getInt(itemId));
                                    Main.addToLog("Found " + box.getInt(itemId) + " of item " + itemId + " in box " + boxId + " in stack " + stackName + " in store " + storeName);
                                    return result;
                                }
                            }
                        }
                    }
                }
            }
        }

        Main.addToLog("Could not find enough of item " + itemId + " in store " + storeName);
        return null;
    }


    // Add an item to a box
    public void addItemToBox(String storeName, String stackName, String boxId, String itemId, int quantity) {
        Main.addToLog("Adding " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName);
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                if (stacks.has(stackName)) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    if (stack.has(boxId)) {
                        JSONObject box = stack.getJSONObject(boxId);
                        box.put(itemId, box.optInt(itemId, 0) + quantity);
                        store.put("stacks", stacks);
                        jsonFile.put(storeName, store);
                        Main.addToLog("Added " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
                    } else {
                        JSONObject newBox = new JSONObject();
                        newBox.put(itemId, quantity);
                        stack.put(boxId, newBox);
                        store.put("stacks", stacks);
                        jsonFile.put(storeName, store);
                        Main.addToLog("Added " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
                    }
                } else {
                    JSONObject newStack = new JSONObject();
                    JSONObject newBox = new JSONObject();
                    newBox.put(itemId, quantity);
                    newStack.put(boxId, newBox);
                    stacks.put(stackName, newStack);
                    store.put("stacks", stacks);
                    jsonFile.put(storeName, store);
                    Main.addToLog("Added " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
                }
            } else {
                JSONObject newStacks = new JSONObject();
                JSONObject newStack = new JSONObject();
                JSONObject newBox = new JSONObject();
                newBox.put(itemId, quantity);
                newStack.put(boxId, newBox);
                newStacks.put(stackName, newStack);
                store.put("stacks", newStacks);
                jsonFile.put(storeName, store);
                Main.addToLog("Added " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
            }
        } else {
            JSONObject newStore = new JSONObject();
            JSONObject newStacks = new JSONObject();
            JSONObject newStack = new JSONObject();
            JSONObject newBox = new JSONObject();
            newBox.put(itemId, quantity);
            newStack.put(boxId, newBox);
            newStacks.put(stackName, newStack);
            newStore.put("stacks", newStacks);
            jsonFile.put(storeName, newStore);
            Main.addToLog("Added " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + storeName);
        }
    }

    public JSONObject getAllData() {
        return jsonFile.getFileObject();
    }

    public void saveChanges() {
        try {
            jsonFile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
