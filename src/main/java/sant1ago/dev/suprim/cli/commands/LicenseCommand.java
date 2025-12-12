package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import sant1ago.dev.suprim.cli.ToolManager;
import sant1ago.dev.suprim.cli.ToolManager.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * CLI command for license management.
 * Delegates to suprim-license tool.
 */
@Command(
        name = "license",
        description = "License management commands",
        subcommands = {
                LicenseCommand.Generate.class,
                LicenseCommand.Validate.class,
                LicenseCommand.Status.class
        }
)
public class LicenseCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("License management for commercial databases.");
        System.out.println();
        System.out.println("Subcommands:");
        System.out.println("  generate   Generate a new license key (vendor use)");
        System.out.println("  validate   Validate a license key");
        System.out.println("  status     Show current license status");
        System.out.println();
        System.out.println("Use 'suprim license <command> --help' for more info.");
    }

    /**
     * Generate a new license key (vendor use only).
     */
    @Command(name = "generate", description = "Generate a new license key (vendor use)")
    public static class Generate implements Callable<Integer> {

        @Option(names = {"--licensee", "-l"}, required = true, description = "License holder name")
        String licensee;

        @Option(names = {"--email", "-e"}, description = "Contact email")
        String email;

        @Option(names = {"--databases", "-d"}, required = true,
                description = "Allowed databases (comma-separated: oracle,sqlserver,db2)")
        String databases;

        @Option(names = {"--expires"}, description = "Expiration date (YYYY-MM-DD)")
        String expires;

        @Option(names = {"--private-key", "-k"}, required = true,
                description = "Path to private key file")
        String privateKeyPath;

        @Override
        public Integer call() {
            try {
                List<String> args = new ArrayList<>();
                args.add("generate");
                args.add("--licensee");
                args.add(licensee);
                args.add("--databases");
                args.add(databases);
                args.add("--private-key");
                args.add(privateKeyPath);

                if (email != null) {
                    args.add("--email");
                    args.add(email);
                }
                if (expires != null) {
                    args.add("--expires");
                    args.add(expires);
                }

                return ToolManager.run(Tool.LICENSE, args.toArray(new String[0]));
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    /**
     * Validate an existing license key.
     */
    @Command(name = "validate", description = "Validate a license key")
    public static class Validate implements Callable<Integer> {

        @Parameters(index = "0", description = "License key to validate")
        String licenseKey;

        @Override
        public Integer call() {
            try {
                return ToolManager.run(Tool.LICENSE, "validate", licenseKey);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    /**
     * Show current license status.
     */
    @Command(name = "status", description = "Show current license status")
    public static class Status implements Callable<Integer> {

        @Override
        public Integer call() {
            try {
                return ToolManager.run(Tool.LICENSE, "status");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}
