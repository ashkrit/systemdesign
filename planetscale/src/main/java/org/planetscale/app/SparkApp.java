package org.planetscale.app;

import com.google.gson.Gson;
import spark.Spark;

import static spark.Spark.get;

public class SparkApp {
    public static void main(String[] args) {
        Spark.port(8080);

        UserRepository userRepository = new UserRepository();
        get("/hello", (req, res) -> "Hello World");
        get("/users", (req, res) -> {

            res.type("application/json");

            return new Gson().toJson(userRepository.users());

        });

    }


}
