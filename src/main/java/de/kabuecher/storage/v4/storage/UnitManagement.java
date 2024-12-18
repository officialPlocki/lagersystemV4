package de.kabuecher.storage.v4.storage;

import co.plocki.json.JSONFile;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnitManagement {
    private final JSONFile jsonFile;

    public UnitManagement() {
        this.jsonFile = new JSONFile("./.kabuecher/storage.json");
    }

    public boolean removeItem(String storeName, String stackName, String boxId, String itemId, int amount) {
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
                            } else {
                                box.put(itemId, box.getInt(itemId) - amount);
                            }
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
                                return result;
                            } else {
                                result.put(boxId, box.getInt(itemId));
                                quantity -= box.getInt(itemId);
                                out.add(boxId);

                                while (quantity > 0) {
                                    JSONObject object = searchForItemInStoreExcept(storeName, itemId, quantity, out.toArray(String[]::new));
                                    if(object == null || object.isEmpty()) {
                                        return null;
                                    } else {
                                        result.put(String.valueOf(object.keys().next()), object.getInt(object.keys().next()));
                                        quantity -= object.getInt(object.keys().next());
                                    }
                                }

                                return result;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private JSONObject searchForItemInStoreExcept(String storeName, String itemId, int quantity, String[]... exceptionalBoxIds) {
        //if in a box isn't enough, search in other boxes and return the quantity in multiple boxes
        //return nothing if there is no box left with the item

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
                        if (isExceptional) {
                            continue;
                        }
                        JSONObject box = stack.getJSONObject(boxId);
                        if (box.has(itemId)) {
                            if (box.getInt(itemId) >= quantity) {
                                JSONObject result = new JSONObject();
                                result.put(boxId, quantity);
                                return result;
                            } else {
                                JSONObject result = new JSONObject();
                                result.put(boxId, box.getInt(itemId));
                                return result;
                            }
                        }
                    }
                }
            }
        }

        return null;
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
