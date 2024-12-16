package de.kabuecher.storage.v4.storage;

import co.plocki.json.JSONFile;
import org.json.JSONObject;

public class UnitManagement {
    private final JSONFile jsonFile;

    public UnitManagement() {
        this.jsonFile = new JSONFile("./.kabuecher/storage.json");
    }

    public boolean removeItem(String storeName, String stackName, String boxId, String itemId, int amount) {
        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName); // Corrected get() to getJSONObject()
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                if (stacks.has(stackName)) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    if (stack.has(boxId)) {
                        JSONObject box = stack.getJSONObject(boxId);
                        if (box.has(itemId)) {
                            if (box.getInt(itemId) <= amount) {
                                box.remove(itemId);
                            } else {
                                box.put(itemId, box.getInt(itemId) - amount);
                            }
                            return true;
                        } else {
                            return false; // Item ID not found in the box
                        }
                    } else {
                        return false; // Box ID not found in the stack
                    }
                } else {
                    return false; // Stack name not found
                }
            } else {
                return false; // No stacks in the store
            }
        } else {
            return false; // Store name not found
        }
    }


    // Add an item to a box
    public void addItemToBox(String storeName, String stackName, String boxId, String itemId, int quantity) {
        if (jsonFile.has(storeName)) {
            JSONObject store = jsonFile.get(storeName);
            if (store.has("stacks")) {
                JSONObject stacks = store.getJSONObject("stacks");
                if (stacks.has(stackName)) {
                    JSONObject stack = stacks.getJSONObject(stackName);
                    if (stack.has(boxId)) {
                        JSONObject box = stack.getJSONObject(boxId);
                        box.put(itemId, box.optInt(itemId, 0) + quantity);
                    } else {
                        JSONObject newBox = new JSONObject();
                        newBox.put(itemId, quantity);
                        stack.put(boxId, newBox);
                    }
                } else {
                    JSONObject newStack = new JSONObject();
                    JSONObject newBox = new JSONObject();
                    newBox.put(itemId, quantity);
                    newStack.put(boxId, newBox);
                }
            } else {
                JSONObject newStacks = new JSONObject();
                JSONObject newStack = new JSONObject();
                JSONObject newBox = new JSONObject();
                newBox.put(itemId, quantity);
                newStack.put(boxId, newBox);
                newStacks.put(stackName, newStack);
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
        }
    }

    // Retrieve the entire JSON structure
    public JSONObject getAllData() {
        return jsonFile.getFileObject();
    }

    // Save changes to the file
    public void saveChanges() {
        try {
            jsonFile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
