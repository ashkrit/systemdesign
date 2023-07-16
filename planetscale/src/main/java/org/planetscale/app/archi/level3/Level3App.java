package org.planetscale.app.archi.level3;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Concepts :
 * Load balancer for app server
 */
public class Level3App {

    public static void main(String[] args) {


        UserRepository repository = dbRepo();

        int port = 8080;

        ServerApp.startService(port, repository);

        LoadBalancer loadBalancer = new LoadBalancer(serverLists(port));

        Client browser = new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level3App.execute(client, loadBalancer.url() + "users");
            }


        };

        Client mobile = new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level3App.execute(client, loadBalancer.url() + "users");
            }
        };


        IntStream.range(0, 10)
                .forEach($ -> System.out.println(browser.execute()));

        IntStream.range(0, 10)
                .forEach($ -> System.out.println(mobile.execute()));


    }

    private static List<String> serverLists(int port) {
        return IntStream.range(0, 5)
                .mapToObj($ -> String.format("http://localhost:%s/", port))
                .toList();
    }

    private static DatabaseUserRepository dbRepo() {
        return new DatabaseUserRepository(new HashMap<>() {{
            put("driverClassName", Driver.class.getName());
            put("url", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
            put("user", "sa");
            put("password", "sa");
        }});
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
