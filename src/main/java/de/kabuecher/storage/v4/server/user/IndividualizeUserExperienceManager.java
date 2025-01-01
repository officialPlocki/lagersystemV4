package de.kabuecher.storage.v4.server.user;

import co.plocki.mysql.MySQLInsert;
import co.plocki.mysql.MySQLPush;
import co.plocki.mysql.MySQLRequest;
import co.plocki.mysql.MySQLTable;

public class IndividualizeUserExperienceManager {

    private final MySQLTable.fin contractExecutionTable;

    public IndividualizeUserExperienceManager() {
        MySQLTable contractExecutionTable = new MySQLTable();
        contractExecutionTable.prepare("contractExecution", "contractNumber", "offerId", "confirmationDate", "shippingNumber", "shippingService", "assignedDate");
        this.contractExecutionTable = contractExecutionTable.build();
    }

    public void giveExecution(String contractNumber, String offerId) {
        MySQLInsert insert = new MySQLInsert();
        insert.prepare(contractExecutionTable, contractNumber, offerId, "", "", "", System.currentTimeMillis());
        insert.execute();
    }

    public void confirmExecution(String contractNumber, String shippingNumber, String shippingService) {
        MySQLPush push = new MySQLPush();

        push.prepare(contractExecutionTable.getTableName(), "shippingNumber", shippingNumber);
        push.addRequirement("contractNumber", contractNumber);
        push.execute();

        push.prepare(contractExecutionTable.getTableName(), "shippingService", shippingService);
        push.addRequirement("contractNumber", contractNumber);
        push.execute();

        push.prepare(contractExecutionTable.getTableName(), "confirmationDate", System.currentTimeMillis());
        push.addRequirement("contractNumber", contractNumber);
        push.execute();
    }

    public boolean hasExecution(String contractNumber, String offerID) {
        MySQLRequest request = new MySQLRequest();
        request.prepare(contractExecutionTable.getTableName());
        request.addRequirement("contractNumber", contractNumber);
        request.addRequirement("offerId", offerID);
        return !request.execute().isEmpty();
    }

    public boolean isExecution(String offerID) {
        MySQLRequest request = new MySQLRequest();
        request.prepare(contractExecutionTable.getTableName());
        request.addRequirement("offerId", offerID);
        return !request.execute().isEmpty();
    }

}
