package org.planetscale.app;

import com.google.gson.Gson;
import org.planetscale.app.UserRepository.User;
import spark.Spark;

import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

public class SparkApp {
    public static void main(String[] args) {
        Spark.port(8080);

        UserRepository userRepository = new UserRepository();
        get("/hello", (req, res) -> "Hello World");
        get("/users", (req, res) -> {

            res.type("application/json");

            return new Gson().toJson(userRepository.users());

        });
        post("/user", (req, res) -> {

            res.type("application/json");


            Gson gson = new Gson();
            UserRequest user = gson.fromJson(req.body(), UserRequest.class);
            userRepository.register(user.as(UUID.randomUUID().toString()));

            return gson.toJson(userRepository.users());

        });

    }


}
