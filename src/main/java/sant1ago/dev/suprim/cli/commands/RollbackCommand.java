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
 * Rollback the last batch of migrations.
 * Delegates to suprim-migration tool.
 */
@Command(
        name = "rollback",
        description = "Rollback the last batch of migrations"
)
public class RollbackCommand implements Callable<Integer> {

    @ParentCommand
    SuprimCli parent;

    @Option(names = {"-c", "--config"}, description = "Config file path")
    String configFile;

    @Option(names = {"--step"}, description = "Number of batches to rollback", defaultValue = "1")
    int steps;

    @Override
    public Integer call() {
        try {
            List<String> args = new ArrayList<>();
            args.add("rollback");

            if (steps > 1) {
                args.add("--step");
                args.add(String.valueOf(steps));
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
