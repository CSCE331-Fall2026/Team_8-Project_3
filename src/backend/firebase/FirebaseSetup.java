package firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.internal.FirebaseProcessEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class FirebaseSetup {
    private static final Path LOCAL_ENV = Path.of(".env");
    private static final String EMULATOR_HOST = "FIREBASE_AUTH_EMULATOR_HOST";
    private static final String PROJECT_ID = "FIREBASE_PROJECT_ID";
    private static final String DEFAULT_PROJECT_ID = "team8project3-2f2ac";
    private static final Map<String, String> local = loadLocalEnv();

    private FirebaseSetup() {}

    // Prod credentials should be (GOOGLE_APPLICATION_CREDENTIALS)
    // For emulator: emulator + placeholder credentials
    public static void init() {
        String emulatorHost = setting(EMULATOR_HOST);
        if (emulatorHost == null) {
            FirebaseApp.initializeApp();
            return;
        }

        FirebaseProcessEnvironment.setenv(EMULATOR_HOST, emulatorHost);

        GoogleCredentials placeholder = GoogleCredentials.newBuilder().build();
        String projectId = setting(PROJECT_ID);
        FirebaseApp.initializeApp(FirebaseOptions.builder()
                .setProjectId(projectId == null ? DEFAULT_PROJECT_ID : projectId)
                .setCredentials(placeholder)
                .build());
        System.out.println("Firebase Auth: using emulator at " + emulatorHost);
    }

    // Use .env as fallback
    private static String setting(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = local.get(name);
        }
        return value == null || value.isBlank() ? null : value;
    }

    private static Map<String, String> loadLocalEnv() {
        Map<String, String> values = new HashMap<>();
        if (!Files.exists(LOCAL_ENV)) {
            return values;
        }
        try {
            for (String line : Files.readAllLines(LOCAL_ENV)) {
                String trimmed = line.trim();
                int eq = trimmed.indexOf('=');
                if (trimmed.isEmpty() || trimmed.startsWith("#") || eq < 1) {
                    continue;
                }
                String value = trimmed.substring(eq + 1).trim().replaceAll("^\"|\"$", "");
                values.put(trimmed.substring(0, eq).trim(), value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + LOCAL_ENV.toAbsolutePath(), e);
        }
        return values;
    }
}