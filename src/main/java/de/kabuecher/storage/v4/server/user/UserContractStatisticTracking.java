package de.kabuecher.storage.v4.server.user;

import co.plocki.mysql.MySQLInsert;
import co.plocki.mysql.MySQLTable;

public class UserContractStatisticTracking {

    private final MySQLTable.fin trackingTable;

    public UserContractStatisticTracking() {
        MySQLTable trackingTable = new MySQLTable();
        trackingTable.prepare("contractTracking", "contractNumber", "action", "cost", "actionTime");
        this.trackingTable = trackingTable.build();
    }

    public void addShippingCost(double cost, String contractNumber) {
        MySQLInsert insert = new MySQLInsert();
        insert.prepare(trackingTable, contractNumber, "shipping", cost, System.currentTimeMillis());
        insert.execute();
    }

    public void addAction(String action, String contractNumber) {
        MySQLInsert insert = new MySQLInsert();
        insert.prepare(trackingTable, contractNumber, action, 0.0, System.currentTimeMillis());
        insert.execute();
    }

}
