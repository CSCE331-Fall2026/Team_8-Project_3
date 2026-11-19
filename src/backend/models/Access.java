package models;

import io.javalin.security.RouteRole;

public enum Access implements RouteRole {
    PUBLIC, // for public routes
    TOKEN_ONLY // for any signed in user
}
