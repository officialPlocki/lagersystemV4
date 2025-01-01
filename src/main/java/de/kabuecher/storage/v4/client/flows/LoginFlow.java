package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.LoginBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.BodyType;
import org.bouncycastle.crypto.generators.SCrypt;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;

public class LoginFlow {

    public LoginFlow() {

        LoginBody loginBody = new LoginBody();
        loginBody.addAction("confirm_button", new BodyType.ActionEventRunnable() {
            @Override
            public void handleActionEvent(ActionEvent event) {
                handleLogin(loginBody);
            }

            @Override
            public void handleKeyEvent(KeyEvent event) {

            }

            @Override
            public void run() {

            }
        });

        Main.bodyHandler.setContentBody(loginBody);
    }

    private void handleLogin(LoginBody body) {
        String username = body.getTextField("username_textfield").getText();
        String password = body.getTextField("password_textfield").getText();

        password = Base64.getEncoder().encodeToString(SCrypt.generate(password.getBytes(), username.getBytes(), 65536, 8, 1, 1024));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", username)
                .header("password", password)
                .GET()
                .build();

        boolean success;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            success = response.statusCode() != 401 && response.statusCode() != 502;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(success) {
            System.out.println("Login successful");
            Main.lastAction = System.currentTimeMillis();
            Main.username = username;
            Main.passwordHash = password;
            Main.timeout = false;

            Main.bodyHandler.setContentBody(null);

            Main.displayDriver.getPanel().getLogoutButton().setEnabled(true);
        } else {
            body.getLabel("action_label").setText("Ungültige Anmeldedaten oder Konto abgelaufen.");
        }
    }

}
