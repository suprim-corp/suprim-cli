package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import sant1ago.dev.suprim.cli.ToolManager;
import sant1ago.dev.suprim.cli.ToolManager.Tool;

import java.util.concurrent.Callable;

/**
 * Install Suprim tools.
 */
@Command(
        name = "install",
        description = "Install Suprim tools (migration, license)"
)
public class InstallCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Tool to install: migration, license")
    String toolName;

    @Option(names = {"-v", "--version"}, description = "Version to install", defaultValue = "0.0.1")
    String version;

    @Override
    public Integer call() {
        var tool = ToolManager.getTool(toolName);

        if (tool.isEmpty()) {
            System.err.println("Unknown tool: " + toolName);
            System.err.println("Available tools: migration, license");
            return 1;
        }

        try {
            if (ToolManager.isInstalled(tool.get())) {
                System.out.println(tool.get().getArtifactId() + " is already installed.");
                System.out.println("Use --version to install a different version.");
            }
            ToolManager.install(tool.get(), version);
            System.out.println("Done!");
            return 0;
        } catch (Exception e) {
            System.err.println("Installation failed: " + e.getMessage());
            return 1;
        }
    }
}
