package sant1ago.dev.suprim.cli.commands;

import picocli.CommandLine.Command;
import sant1ago.dev.suprim.cli.ToolManager;

import java.util.concurrent.Callable;

/**
 * List installed Suprim tools.
 */
@Command(
        name = "list",
        description = "List installed Suprim tools"
)
public class ListCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        ToolManager.listInstalled();
        return 0;
    }
}
