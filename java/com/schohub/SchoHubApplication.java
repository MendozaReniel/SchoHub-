package com.schohub;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoHubApplication {

    private static final Logger log = LoggerFactory.getLogger(SchoHubApplication.class);

    public static void main(String[] args) {
        Path dataDirectory = resolveDataDirectory();
        // Read by application.properties: spring.datasource.url=jdbc:h2:file:${schohub.data.dir}/schohub
        System.setProperty("schohub.data.dir", dataDirectory.toString().replace('\\', '/'));
        log.info("SchoHub accounts, profiles and sessions are stored in {}", dataDirectory);
        SpringApplication.run(SchoHubApplication.class, args);
    }

    /**
     * Always resolves to this project's own {@code data} folder, so the same database file is
     * used whether the app is started from the packaged jar, from Maven, from an IDE, or from
     * any other working directory. Set the SCHOHUB_DATA_DIR environment variable to override.
     */
    static Path resolveDataDirectory() {
        String override = System.getenv("SCHOHUB_DATA_DIR");
        if (override != null && !override.isBlank()) {
            return Paths.get(override).toAbsolutePath().normalize();
        }
        return resolveProjectRoot().resolve("data").toAbsolutePath().normalize();
    }

    private static Path resolveProjectRoot() {
        for (Path candidate : candidateLocations()) {
            Path root = projectRootOf(candidate);
            if (root != null) {
                return root;
            }
        }
        return Paths.get("").toAbsolutePath().normalize();
    }

    /** Places the running code (and therefore the project) may be found, most specific first. */
    private static List<Path> candidateLocations() {
        List<Path> locations = new ArrayList<>();
        Path codeSource = codeSourceLocation();
        if (codeSource != null) {
            locations.add(codeSource);
        }
        for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
            if (!entry.isBlank()) {
                locations.add(Paths.get(entry).toAbsolutePath().normalize());
            }
        }
        locations.add(Paths.get("").toAbsolutePath().normalize());
        return locations;
    }

    /** The jar, classes directory or other location the application classes were loaded from. */
    private static Path codeSourceLocation() {
        try {
            URL url = SchoHubApplication.class.getProtectionDomain().getCodeSource().getLocation();
            String spec = url.toString();
            int bang = spec.indexOf("!/");
            if (spec.startsWith("jar:")) {
                // Spring Boot launches from jar:file:/.../target/app.jar!/BOOT-INF/classes!/
                String inner = bang > 0 ? spec.substring(4, bang) : spec.substring(4);
                return Paths.get(URI.create(inner)).toAbsolutePath().normalize();
            }
            return Paths.get(url.toURI()).toAbsolutePath().normalize();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Walks up from the jar/classes location to the folder that holds pom.xml (the project
     * root), so the {@code data} folder is always the project's. Returns the starting folder
     * when no project marker is above it (e.g. the jar was copied elsewhere).
     */
    private static Path projectRootOf(Path location) {
        Path start = Files.isDirectory(location) ? location : location.getParent();
        if (start == null) {
            return null;
        }
        Path current = start;
        for (int depth = 0; depth < 5 && current != null; depth++) {
            if (Files.isRegularFile(current.resolve("pom.xml"))) {
                return current;
            }
            current = current.getParent();
        }
        return start;
    }
}
