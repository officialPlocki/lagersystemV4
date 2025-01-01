package de.kabuecher.storage.v4.server.user;

public interface User {

    String getUsername();

    String getPassword();

    Role getRole();

    long invalidationDate();

    long creationDate();

    String getCompany();

    String getContractNumber();

}
