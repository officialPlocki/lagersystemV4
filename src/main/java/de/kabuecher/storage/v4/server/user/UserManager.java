package de.kabuecher.storage.v4.server.user;

import co.plocki.mysql.*;
import org.bouncycastle.crypto.generators.SCrypt;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UserManager {

    private final MySQLTable.fin userTable;

    public UserManager() {
        MySQLTable userTable = new MySQLTable();
        userTable.prepare("softwareUsers", "username", "passwordHash", "role", "invalidationDate", "creationDate", "company", "contractNumber");
        this.userTable = userTable.build();
    }

    public boolean addUser(User user) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        if(request.execute().isEmpty()) {

            MySQLInsert insert = new MySQLInsert();
            insert.prepare(userTable, user.getUsername(), user.getPassword(), user.getRole().toString(), user.invalidationDate(), user.creationDate(), user.getCompany(), user.getContractNumber());
            insert.execute();

            return true;
        }

        return false;
    }

    public List<User> getUsers() {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");

        List<User> list = new ArrayList<>();
        MySQLResponse response = request.execute();

        for (int i = 0; i < response.rawAll().size(); i++) {
            int finalI = i;
            list.add(new User() {
                @Override
                public String getUsername() {
                    return response.rawAll().get(finalI).get("username");
                }

                @Override
                public String getPassword() {
                    return response.rawAll().get(finalI).get("passwordHash");
                }

                @Override
                public Role getRole() {
                    return Role.valueOf(response.rawAll().get(finalI).get("role"));
                }

                @Override
                public long invalidationDate() {
                    return Long.parseLong(response.rawAll().get(finalI).get("invalidationDate"));
                }

                @Override
                public long creationDate() {
                    return Long.parseLong(response.rawAll().get(finalI).get("creationDate"));
                }

                @Override
                public String getCompany() {
                    return response.rawAll().get(finalI).get("company");
                }

                @Override
                public String getContractNumber() {
                    return response.rawAll().get(finalI).get("contractNumber");
                }
            });
        }

        for (User user : list) {
            if(user.invalidationDate() < System.currentTimeMillis()) {
                list.remove(user);
            }
        }

        return list;
    }

    public boolean removeUser(User user) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        if(!request.execute().isEmpty()) {

            MySQLDelete delete = new MySQLDelete();
            delete.prepare(userTable.getTableName());
            delete.addRequirement("username", user.getUsername());
            delete.execute();

            return true;
        }

        return false;
    }

    public boolean changePassword(User user, String newPassword) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        if(!request.execute().isEmpty()) {

            MySQLPush push = new MySQLPush();
            push.prepare(userTable.getTableName(), "passwordHash", Base64.getEncoder().encodeToString(SCrypt.generate(newPassword.getBytes(), user.getUsername().getBytes(), 65536, 8, 1, 1024)));
            push.addRequirement("username", user.getUsername());
            push.execute();

            return true;
        }

        return false;
    }

    public boolean changeRole(User user, Role newRole) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        if(!request.execute().isEmpty()) {

            MySQLPush push = new MySQLPush();
            push.prepare(userTable.getTableName(), "role", newRole.toString());
            push.addRequirement("username", user.getUsername());
            push.execute();

            return true;
        }

        return false;
    }

    public boolean passwordCorrect(User user, String password) {
        if(user.invalidationDate() < System.currentTimeMillis()) {
            return false;
        } else {
            return user.getPassword().equals(password);
        }
    }

    public boolean userExists(String username) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", username);

        return !request.execute().isEmpty();
    }

    public boolean userExists(User user) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        return !request.execute().isEmpty();
    }

    public Role getRole(User user) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", user.getUsername());

        return Role.valueOf(request.execute().getString("role"));
    }

    public User getUser(String username) {
        MySQLRequest request = new MySQLRequest();
        request.prepare("softwareUsers");
        request.addRequirement("username", username);

        if(!request.execute().isEmpty()) {
            return new User() {
                @Override
                public String getUsername() {
                    return request.execute().getString("username");
                }

                @Override
                public String getPassword() {
                    return request.execute().getString("passwordHash");
                }

                @Override
                public Role getRole() {
                    return Role.valueOf(request.execute().getString("role"));
                }

                @Override
                public long invalidationDate() {
                    return Long.parseLong(request.execute().getString("invalidationDate"));
                }

                @Override
                public long creationDate() {
                    return Long.parseLong(request.execute().getString("creationDate"));
                }

                @Override
                public String getCompany() {
                    return request.execute().getString("company");
                }

                @Override
                public String getContractNumber() {
                    return request.execute().getString("contractNumber");
                }
            };
        }

        return null;
    }

}
