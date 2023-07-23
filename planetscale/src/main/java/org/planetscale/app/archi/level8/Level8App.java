package org.planetscale.app.archi.level8;

import com.google.gson.Gson;
import org.planetscale.app.ServerApp;
import org.planetscale.app.archi.Client;
import org.planetscale.app.archi.level3.LoadBalancer;
import org.planetscale.app.archi.level5.CacheRepository;
import org.planetscale.app.archi.level5.Level5App;
import org.planetscale.app.service.EventQueue;
import org.planetscale.app.service.EventQueue.EmailDomainCount;
import org.planetscale.app.service.EventQueue.Event;
import org.planetscale.app.service.EventQueue.TotalUserCount;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Concepts:
 * Event driven processing
 */
public class Level8App {

    public static EventQueue createQueue() {
        return new EventQueue() {
            private Consumer<Event> consumer;
            final ExecutorService es = Executors.newCachedThreadPool();

            @Override
            public void registerConsumer(Consumer<Event> consumer) {
                this.consumer = consumer;
            }

            @Override
            public void publish(Event message) {
                es.submit(() -> consumer.accept(message));
            }
        };
    }

    public static void main(String[] args) {


        var port = 8080;

        var cachedMethods = Set.of("users");


        Map<String, Object> reply = new ConcurrentHashMap<>();
        Consumer<Event> consumer = event -> {
            switch (event) {
                case TotalUserCount tc -> {
                    reply.put(tc.requestId(), Math.abs(current().nextInt()));
                }
                case EmailDomainCount tc -> {
                    reply.put(tc.requestId(), Math.abs(current().nextInt()));
                }
            }
        };

        var queue = createQueue();
        queue.registerConsumer(consumer);
        var repository = CacheRepository
                .create(Level5App.createReplicatedRepo(), cachedMethods);

        ServerApp
                .startService(port, repository, queue, reply);

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

                return Level8App.execute(client, loadBalancer.url() + "users");
            }
        };
    }

    private static Client browserClient(LoadBalancer loadBalancer) {
        return new Client() {
            final HttpClient client = HttpClient.newHttpClient();

            @Override
            public String execute() {

                String result = Level8App.executePost(client, loadBalancer.url() + "analytics/usercount");
                var g = new Gson().fromJson(result, Map.class);
                String id = g.get("requestId").toString();

                String value = Level8App.execute(client, loadBalancer.url() + "analytics/usercount/" + id);

                return value;
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

    private static String executePost(HttpClient client, String endPoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endPoint))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            return client
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
