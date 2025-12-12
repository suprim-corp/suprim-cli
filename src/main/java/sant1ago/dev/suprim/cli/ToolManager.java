package sant1ago.dev.suprim.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;

/**
 * Manages installation and execution of Suprim tools.
 * Tools are downloaded to ~/.suprim/tools/
 */
public class ToolManager {

    private static final String SUPRIM_DIR = System.getProperty("user.home") + "/.suprim";
    private static final String TOOLS_DIR = SUPRIM_DIR + "/tools";
    private static final String GITHUB_RELEASE_BASE = "https://github.com/suprim-corp";

    public enum Tool {
        MIGRATION("suprim-migration", "suprim", "suprim-migration.jar"),
        LICENSE("suprim-license", "suprim", "suprim-license.jar");

        private final String artifactId;
        private final String repo;
        private final String jarName;

        Tool(String artifactId, String repo, String jarName) {
            this.artifactId = artifactId;
            this.repo = repo;
            this.jarName = jarName;
        }

        public String getArtifactId() { return artifactId; }
        public String getRepo() { return repo; }
        public String getJarName() { return jarName; }
    }

    private static final Map<String, Tool> TOOL_ALIASES = Map.of(
            "migration", Tool.MIGRATION,
            "migrate", Tool.MIGRATION,
            "license", Tool.LICENSE
    );

    /**
     * Get tool by name or alias.
     */
    public static Optional<Tool> getTool(String name) {
        return Optional.ofNullable(TOOL_ALIASES.get(name.toLowerCase()));
    }

    /**
     * Check if a tool is installed.
     */
    public static boolean isInstalled(Tool tool) {
        return Files.exists(getToolPath(tool));
    }

    /**
     * Get path to installed tool JAR.
     */
    public static Path getToolPath(Tool tool) {
        return Path.of(TOOLS_DIR, tool.getJarName());
    }

    /**
     * Install a tool by downloading from GitHub Releases.
     */
    public static void install(Tool tool, String version) throws IOException, InterruptedException {
        Files.createDirectories(Path.of(TOOLS_DIR));

        String url = String.format("%s/%s/releases/download/v%s/%s",
                GITHUB_RELEASE_BASE, tool.getRepo(), version, tool.getJarName());

        System.out.println("Downloading " + tool.getArtifactId() + " v" + version + "...");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request,
                HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            // Try following redirect
            var location = response.headers().firstValue("Location");
            if (location.isPresent()) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(location.get()))
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            }

            if (response.statusCode() != 200) {
                throw new IOException("Download failed: HTTP " + response.statusCode() +
                        ". Check version: " + version);
            }
        }

        Path targetPath = getToolPath(tool);
        try (InputStream in = response.body()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Installed to: " + targetPath);
    }

    /**
     * Uninstall a tool.
     */
    public static void uninstall(Tool tool) throws IOException {
        Path path = getToolPath(tool);
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("Uninstalled: " + tool.getArtifactId());
        } else {
            System.out.println("Not installed: " + tool.getArtifactId());
        }
    }

    /**
     * Run a tool with arguments.
     */
    public static int run(Tool tool, String... args) throws IOException, InterruptedException {
        if (!isInstalled(tool)) {
            System.err.println("Tool not installed: " + tool.getArtifactId());
            System.err.println("Run: suprim install " + tool.name().toLowerCase());
            return 1;
        }

        ProcessBuilder pb = new ProcessBuilder();
        String[] cmd = new String[args.length + 3];
        cmd[0] = "java";
        cmd[1] = "-jar";
        cmd[2] = getToolPath(tool).toString();
        System.arraycopy(args, 0, cmd, 3, args.length);

        pb.command(cmd);
        pb.inheritIO();

        Process process = pb.start();
        return process.waitFor();
    }

    /**
     * List installed tools.
     */
    public static void listInstalled() {
        System.out.println("Installed tools:");
        for (Tool tool : Tool.values()) {
            String status = isInstalled(tool) ? "[installed]" : "[not installed]";
            System.out.printf("  %-20s %s%n", tool.getArtifactId(), status);
        }
    }
}
