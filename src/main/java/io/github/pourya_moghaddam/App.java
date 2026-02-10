package io.github.pourya_moghaddam;

import io.github.pourya_moghaddam.util.SocketClientUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command-line client application for a simple socket-based chat.
 */
@Command(name = "client", description = "Simple socket server for chat")
public class App implements Runnable {

    @Option(names = {"-h", "--host"}, description = "Server host", defaultValue = "localhost")
    private String host;

    @Option(names = {"-p", "--port"}, description = "Server port", defaultValue = "12345")
    private int port;

    /**
     * Application entry point.
     * Parses command-line arguments and runs the client.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Executes the client logic when the command is invoked.
     * Delegates the actual connection and chat handling to {@link SocketClientUtils}.
     */
    @Override
    public void run() {
        SocketClientUtils.runClient(host, port);
    }
}
