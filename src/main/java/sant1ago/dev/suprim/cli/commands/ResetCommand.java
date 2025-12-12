package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import sant1ago.dev.suprim.cli.SuprimCli;
import sant1ago.dev.suprim.cli.ToolManager;
import sant1ago.dev.suprim.cli.ToolManager.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reset all migrations (rollback everything).
 * Delegates to suprim-migration tool.
 */
@Command(
        name = "reset",
        description = "Rollback all database migrations"
)
public class ResetCommand implements Callable<Integer> {

    @ParentCommand
    SuprimCli parent;

    @Option(names = {"-c", "--config"}, description = "Config file path")
    String configFile;

    @Option(names = {"--force"}, description = "Skip confirmation")
    boolean force;

    @Override
    public Integer call() {
        try {
            List<String> args = new ArrayList<>();
            args.add("reset");

            if (force) {
                args.add("--force");
            }

            String config = configFile != null ? configFile : parent.configFile;
            if (config != null && !config.equals("suprim.yml")) {
                args.add("-c");
                args.add(config);
            }

            return ToolManager.run(Tool.MIGRATION, args.toArray(new String[0]));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}
