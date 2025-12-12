package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sant1ago.dev.suprim.cli.ToolManager;

import java.util.concurrent.Callable;

/**
 * Uninstall Suprim tools.
 */
@Command(
        name = "uninstall",
        description = "Uninstall a Suprim tool"
)
public class UninstallCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Tool to uninstall: migration, license")
    String toolName;

    @Override
    public Integer call() {
        var tool = ToolManager.getTool(toolName);

        if (tool.isEmpty()) {
            System.err.println("Unknown tool: " + toolName);
            return 1;
        }

        try {
            ToolManager.uninstall(tool.get());
            return 0;
        } catch (Exception e) {
            System.err.println("Uninstall failed: " + e.getMessage());
            return 1;
        }
    }
}
