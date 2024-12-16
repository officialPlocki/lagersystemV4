package de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl;

import org.json.JSONObject;

public interface ItemObject {

    String getName();

    String getEAN();

    String getPartID();

    int amount();

    JSONObject storageUnit();

}
