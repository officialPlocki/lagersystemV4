package de.kabuecher.storage.v4.server.console.command;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.kabuecher.storage.v4.server.user.Role;
import de.kabuecher.storage.v4.server.user.User;
import de.kabuecher.storage.v4.server.user.UserManager;
import org.bouncycastle.crypto.generators.SCrypt;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

public class UserCommand extends Command {

    @Override
    public void run(String[] args) {
        if(args.length == 0) {
            //output help
            System.out.println("User command");
            System.out.println("Usage: user <action> [args]");
            System.out.println("Actions:");
            System.out.println("add [username] [invalidateTimeInDays] [company]");
            System.out.println("remove [username]");
            System.out.println("list");
            System.out.println("setRole [username] [role <INTERN, EXTERN>]");

        } else {
            String action = args[0];
            if(action.equalsIgnoreCase("add")) {
                if(args.length != 4) {
                    if(args.length == 1) {
                        System.out.println("Username missing");
                    } else if(args.length == 2) {
                        System.out.println("Invalidate time in days missing");
                    } else {
                        System.out.println("Company missing");
                    }
                } else {
                    UserManager userManager = new UserManager();

                    //generate secure 16 character password
                    StringBuilder password = new StringBuilder();
                    for(int i = 0; i < 16; i++) {
                        password.append((char) (Math.random() * 26 + 97));
                    }

                    User user = new User() {
                        @Override
                        public String getUsername() {
                            return args[1];
                        }

                        @Override
                        public String getPassword() {
                            return Base64.getEncoder().encodeToString(SCrypt.generate(password.toString().getBytes(), args[1].getBytes(), 65536, 8, 1, 1024));
                        }

                        @Override
                        public Role getRole() {
                            return Role.EXTERN;
                        }

                        @Override
                        public long invalidationDate() {
                            return System.currentTimeMillis() + (Long.parseLong(args[2]) * (((24 * 60) * 60) * 1000));
                        }

                        @Override
                        public long creationDate() {
                            return System.currentTimeMillis();
                        }

                        @Override
                        public String getCompany() {
                            return args[3];
                        }
                        @Override
                        public String getContractNumber() {
                            ZonedDateTime zonedDateTime = ZonedDateTime.now();
                            HashCode hashCode;
                            File tempFile;
                            try {
                                tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                Files.write(zonedDateTime.toString().getBytes(), tempFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                hashCode = Files.hash(tempFile, Hashing.md5());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            tempFile.delete();
                            return hashCode.toString();
                        }
                    };

                    userManager.addUser(user);
                    File userInformation = new File(args[1] + ".txt");
                    try {
                        userInformation.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Files.write(("Username: " + args[1] + "\nPassword: " + password.toString() + "\nCompany: " + args[3] + "\nContract Number: " + user.getContractNumber()).getBytes(), userInformation);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if(action.equalsIgnoreCase("remove")) {
                if(args.length != 2) {
                    System.out.println("Username missing");
                } else {
                    UserManager userManager = new UserManager();
                    userManager.removeUser(userManager.getUser(args[1]));
                }
            } else if(action.equalsIgnoreCase("list")) {
                System.out.println("Listing users");

                UserManager userManager = new UserManager();
                for(User user : userManager.getUsers()) {
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Role: " + user.getRole());
                    System.out.println("Company: " + user.getCompany());
                    System.out.println("Contract Number: " + user.getContractNumber());
                    System.out.println("Invalidation date: " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(user.invalidationDate()), ZonedDateTime.now().getZone()));
                    System.out.println("Creation date: " + ZonedDateTime.ofInstant(Instant.ofEpochMilli(user.creationDate()), ZonedDateTime.now().getZone()));
                    System.out.println();
                }

            } else if(action.equalsIgnoreCase("setRole")) {
                if(args.length != 3) {
                    if (args.length == 2) {
                        System.out.println("Username missing");
                    } else {
                        System.out.println("Role missing");
                    }
                } else {
                    UserManager userManager = new UserManager();
                    User user = userManager.getUser(args[1]);
                    userManager.changeRole(user, Role.valueOf(args[2].toUpperCase()));

                    System.out.println("Role set to " + args[2]);
                }
            } else {
                System.out.println("User command");
                System.out.println("Usage: user <action> [args]");
                System.out.println("Actions:");
                System.out.println("add [username] [invalidateTimeInDays] [company]");
                System.out.println("remove [username]");
                System.out.println("list");
                System.out.println("setRole [username] [role <INTERN, EXTERN>]");
            }
        }
    }

}
