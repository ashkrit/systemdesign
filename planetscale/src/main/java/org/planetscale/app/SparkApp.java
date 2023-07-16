package org.planetscale.app;

import com.google.gson.Gson;
import org.planetscale.app.service.UserRepository;
import org.planetscale.app.service.UserService;
import spark.Spark;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class SparkApp {
    public static void main(String[] args) {
        Spark.port(8080);

        UserService service = new UserService(new UserRepository());
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
