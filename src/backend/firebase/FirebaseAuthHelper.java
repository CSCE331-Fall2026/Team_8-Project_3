package firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.javalin.http.Context;

public final class FirebaseAuthHelper {
    private FirebaseAuthHelper() {}

    public static FirebaseToken verifyToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        try {
            return FirebaseAuth.getInstance().verifyIdToken(header.substring(7));
        } catch (FirebaseAuthException e) {
            return null;
        }
    }
}