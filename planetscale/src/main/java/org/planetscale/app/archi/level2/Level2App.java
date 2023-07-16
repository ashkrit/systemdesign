package org.planetscale.app.archi.level2;

import org.h2.Driver;
import org.planetscale.app.ServerApp;
import org.planetscale.app.archi.Client;
import org.planetscale.app.service.DatabaseUserRepository;
import org.planetscale.app.service.UserRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

public class Level2App {

    public static void main(String[] args) {

        int port = 8080;
        UserRepository repository = new DatabaseUserRepository(new HashMap<>() {{
            put("driverClassName", Driver.class.getName());
            put("url", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
            put("user", "sa");
            put("password", "sa");
        }});

        ServerApp.startService(port,repository);

        String url = String.format("http://localhost:%s/", port);

        Client browser = new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level2App.execute(client, url + "users");
            }


        };

        Client mobile = new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level2App.execute(client, url + "users");
            }
        };


        System.out.println(browser.execute());
        System.out.println(mobile.execute());


    }

    private static String execute(HttpClient client, String endPoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endPoint))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            return client
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
