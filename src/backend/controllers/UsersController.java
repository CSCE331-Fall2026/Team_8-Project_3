package controllers;

import java.sql.SQLException;

import io.javalin.http.Context;
import io.javalin.config.JavalinConfig;

import models.User;
import models.CreateUserRequest;
import services.UsersService;

public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.post("/users", this::createUser);
        config.routes.get("/users/{userID}", this::getUserByID);
        config.routes.put("/users/{userID}", this::updateUser);
        config.routes.delete("/users/{userID}", this::deleteUser);
    }

    // POST /users
    public void createUser(Context ctx) throws SQLException {
        CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);

        try {
            User createdUser = usersService.createUser(request);
            ctx.status(201).json(createdUser);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // GET /users/{userID}
    public void getUserByID(Context ctx) throws SQLException {
        int userID = ctx.pathParamAsClass("userID", Integer.class).get();

        User user = usersService.getUserById(userID);
        if (user == null) {
            ctx.status(404).result("User not found");
            return;
        }
        ctx.json(user);
    }

    // PUT /users/{userID}
    public void updateUser(Context ctx) throws SQLException {
        int userID = ctx.pathParamAsClass("userID", Integer.class).get();
        CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);

        try {
            User updated = usersService.updateUser(userID, request);
            if (updated == null) {
                ctx.status(404).result("User not found");
                return;
            }
            ctx.json(updated);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // DELETE /users/{userID}
    public void deleteUser(Context ctx) throws SQLException {
        int userID = ctx.pathParamAsClass("userID", Integer.class).get();

        boolean deleted = usersService.deleteUser(userID);
        if (!deleted) {
            ctx.status(404).result("User not found");
            return;
        }
        ctx.status(204);
    }
}
