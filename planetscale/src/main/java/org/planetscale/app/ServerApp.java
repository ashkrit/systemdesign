package org.planetscale.app;

import com.google.gson.Gson;
import org.planetscale.app.service.InMemoryUserRepository;
import org.planetscale.app.service.UserRepository;
import org.planetscale.app.service.UserService;
import spark.Spark;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class ServerApp {
    public static void main(String[] args) {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        startService(port);

    }

    public static void startService(int port) {
        startService(port, new InMemoryUserRepository());
    }


    public static void startService(int port, UserRepository repository) {
        Spark.port(port);

        UserService service = new UserService(repository);

        get("/hello", (req, res) -> "Hello World");
        get("/users", (req, res) -> {

            res.type("application/json");

            return new Gson().toJson(service.users());

        });
        post("/user", (req, res) -> {

            res.type("application/json");


            Gson gson = new Gson();
            UserRequest user = gson.fromJson(req.body(), UserRequest.class);
            service.register(user.as(UUID.randomUUID().toString()));

            return gson.toJson(service.users());

        });
    }
}
