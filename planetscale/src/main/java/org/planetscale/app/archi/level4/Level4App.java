package org.planetscale.app.archi.level4;

import org.h2.Driver;
import org.planetscale.app.ServerApp;
import org.planetscale.app.archi.Client;
import org.planetscale.app.archi.level3.LoadBalancer;
import org.planetscale.app.service.ConnectionHandler;
import org.planetscale.app.service.DatabaseUserRepository;
import org.planetscale.app.service.UserRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Concepts :
 * Database connection to master/leader and replicator/follower for load balancing of write and read traffic.
 */
public class Level4App {

    public static void main(String[] args) {


        Function<ConnectionHandler, Connection> masterConnection = ConnectionHandler::openConnection;
        Function<ConnectionHandler, Connection> replica = ConnectionHandler::openConnection;
        var config = Map.of(
                "driverClassName", Driver.class.getName(),
                "url", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1",
                "user", "sa",
                "password", "sa");

        var repository = new ReplicatedUserRepository(config, masterConnection, replica);
        var port = 8080;

        ServerApp.startService(port, repository);

        var loadBalancer = new LoadBalancer(serverLists(port));

        var browser = browserClient(loadBalancer);
        var mobile = mobileClient(loadBalancer);


        IntStream.range(0, 10)
                .forEach($ -> System.out.println(browser.execute()));

        IntStream.range(0, 10)
                .forEach($ -> System.out.println(mobile.execute()));


    }

    private static Client mobileClient(LoadBalancer loadBalancer) {
        return new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level4App.execute(client, loadBalancer.url() + "users");
            }
        };
    }

    private static Client browserClient(LoadBalancer loadBalancer) {
        return new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {
                return Level4App.execute(client, loadBalancer.url() + "users");
            }


        };
    }

    private static List<String> serverLists(int port) {
        return IntStream.range(0, 5)
                .mapToObj($ -> String.format("http://localhost:%s/", port))
                .toList();
    }

    private static String execute(HttpClient client, String endPoint) {
        try {
            var request = HttpRequest.newBuilder()
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
