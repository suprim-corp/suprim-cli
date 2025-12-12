package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import sant1ago.dev.suprim.cli.SuprimCli;
import sant1ago.dev.suprim.cli.ToolManager;
import sant1ago.dev.suprim.cli.ToolManager.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Create a new migration file.
 * Delegates to suprim-migration tool.
 */
@Command(
        name = "make:migration",
        description = "Create a new migration file"
)
public class MakeMigrationCommand implements Callable<Integer> {

    @ParentCommand
    SuprimCli parent;

    @Parameters(index = "0", description = "Migration name (e.g., create_users_table)")
    String name;

    @Option(names = {"-c", "--config"}, description = "Config file path")
    String configFile;

    @Option(names = {"--create"}, description = "Table to create")
    String createTable;

    @Option(names = {"--table"}, description = "Table to modify")
    String modifyTable;

    @Override
    public Integer call() {
        try {
            List<String> args = new ArrayList<>();
            args.add("make:migration");
            args.add(name);

            if (createTable != null) {
                args.add("--create");
                args.add(createTable);
            }

            if (modifyTable != null) {
                args.add("--table");
                args.add(modifyTable);
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
