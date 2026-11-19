package firebase;

import com.google.firebase.auth.FirebaseToken;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import java.util.Set;

import models.Access;
import models.User;
import services.UsersService;

public final class AccessControl {
    private AccessControl() {}

    public static void register(JavalinConfig config, UsersService usersService) {
        config.routes.beforeMatched(ctx -> {
            Set<RouteRole> roles = ctx.routeRoles();
            if (roles.contains(Access.PUBLIC)) {
                return;
            }

            if (roles.isEmpty()) {
                throw new ForbiddenResponse("Route needs access rules configured");
            }

            FirebaseToken token = FirebaseAuthHelper.verifyToken(ctx);
            if (token == null) {
                throw new UnauthorizedResponse("Missing or invalid token");
            }
            ctx.attribute("firebaseToken", token);
            if (roles.contains(Access.TOKEN_ONLY)) {
                return;
            }

            User user = usersService.getUserByFirebaseId(token.getUid());
            if (user == null) {
                throw new ForbiddenResponse("Must be logged in");
            }
            ctx.attribute("user", user);
            if (!roles.contains(user.userRole())) {
                throw new ForbiddenResponse("Action not permitted");
            }
        });
    }
}
