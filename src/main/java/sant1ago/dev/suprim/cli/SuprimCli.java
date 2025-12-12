package sant1ago.dev.suprim.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import sant1ago.dev.suprim.cli.commands.*;

/**
 * Main CLI entry point for Suprim tools.
 * Commands are dynamically registered based on installed tools.
 */
@Command(
        name = "suprim",
        description = "Suprim - Database Tools Manager",
        mixinStandardHelpOptions = true,
        version = "0.0.1-SNAPSHOT"
)
public class SuprimCli implements Runnable {

    @Option(names = {"-c", "--config"}, description = "Config file path", defaultValue = "suprim.yml")
    public String configFile;

    @Override
    public void run() {
        System.out.println("Suprim - Database Tools Manager");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  install <tool>     Install a tool (migration, license)");
        System.out.println("  uninstall <tool>   Uninstall a tool");
        System.out.println("  list               List installed tools");

        if (ToolManager.isInstalled(ToolManager.Tool.MIGRATION)) {
            System.out.println("  migrate            Run pending migrations");
            System.out.println("  rollback           Rollback last batch");
            System.out.println("  reset              Rollback all migrations");
            System.out.println("  status             Show migration status");
            System.out.println("  make:migration     Create new migration file");
        } else {
            System.out.println();
            System.out.println("  Tip: Run 'suprim install migration' to enable migration commands");
        }

        if (ToolManager.isInstalled(ToolManager.Tool.LICENSE)) {
            System.out.println("  license            License management");
        }

        System.out.println();
        System.out.println("Use 'suprim <command> --help' for more info.");
    }

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new SuprimCli());

        // Core commands (always available)
        cmd.addSubcommand("install", new InstallCommand());
        cmd.addSubcommand("uninstall", new UninstallCommand());
        cmd.addSubcommand("list", new ListCommand());
        cmd.addSubcommand("help", new CommandLine.HelpCommand());

        // Migration commands (only if installed)
        if (ToolManager.isInstalled(ToolManager.Tool.MIGRATION)) {
            cmd.addSubcommand("migrate", new MigrateCommand());
            cmd.addSubcommand("rollback", new RollbackCommand());
            cmd.addSubcommand("reset", new ResetCommand());
            cmd.addSubcommand("status", new StatusCommand());
            cmd.addSubcommand("make:migration", new MakeMigrationCommand());
        }

        // License commands (only if installed)
        if (ToolManager.isInstalled(ToolManager.Tool.LICENSE)) {
            cmd.addSubcommand("license", new LicenseCommand());
        }

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
