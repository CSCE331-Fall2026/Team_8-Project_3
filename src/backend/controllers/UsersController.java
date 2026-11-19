package controllers;

import java.sql.SQLException;

import io.javalin.http.Context;
import io.javalin.config.JavalinConfig;
import com.google.firebase.auth.FirebaseToken;

import firebase.FirebaseAuthHelper;
import io.javalin.http.ForbiddenResponse;
import models.Access;
import models.User;
import models.CreateUserRequest;
import models.UserRole;
import services.UsersService;

public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.post("/users", this::createUser, Access.TOKEN_ONLY);
        config.routes.get("/users/me", this::getCurrentUser, Access.TOKEN_ONLY);
        config.routes.get("/users/{userID}", this::getUserByID, UserRole.values());
        config.routes.put("/users/{userID}", this::updateUser, UserRole.values());
        config.routes.delete("/users/{userID}", this::deleteUser, UserRole.values());
    }

    // POST /users
    public void createUser(Context ctx) throws SQLException {
        FirebaseToken token = FirebaseAuthHelper.verifyToken(ctx);
        if (token == null) {
            ctx.status(401).result("Missing or invalid token");
            return;
        }
        CreateUserRequest request = ctx.bodyAsClass(CreateUserRequest.class);

        try {
            User createdUser = usersService.createUser(request, token.getUid(), token.getEmail());
            ctx.status(201).json(createdUser);
        } catch (IllegalStateException e) {
            ctx.status(409).result(e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // GET /users/me
    public void getCurrentUser(Context ctx) throws SQLException {
        FirebaseToken token = FirebaseAuthHelper.verifyToken(ctx);
        if (token == null) {
            ctx.status(401).result("Missing or invalid token");
            return;
        }
        User user = usersService.getUserByFirebaseId(token.getUid());
        if (user == null) {
            ctx.status(404).result("Unknown user");
            return;
        }
        ctx.json(user);
    }

    // GET /users/{userID}
    public void getUserByID(Context ctx) throws SQLException {
        int userID = ctx.pathParamAsClass("userID", Integer.class).get();
        requireSelfOrManager(ctx, userID);

        User user = usersService.getUserById(userID);
        if (user == null) {
            ctx.status(404).result("User not found");
            return;
        }
        ctx.json(user);
    }

    // PUT /users/{userID} **Note that this allows users to change own role arbitrarily (customer can change to manager)
    // keeping as is for testing
    public void updateUser(Context ctx) throws SQLException {
        int userID = ctx.pathParamAsClass("userID", Integer.class).get();
        requireSelfOrManager(ctx, userID);
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
        requireSelfOrManager(ctx, userID);

        boolean deleted = usersService.deleteUser(userID);
        if (!deleted) {
            ctx.status(404).result("User not found");
            return;
        }
        ctx.status(204);
    }

    // Role Check Helpers
    private void requireSelfOrManager(Context ctx, int userID) {
        User caller = ctx.attribute("user");
        if (caller.userRole() != UserRole.MANAGER && !caller.userId().equals(userID)) {
            throw new ForbiddenResponse("Access denied");
        }
    }
}
