package io.github.pourya_moghaddam;

import io.github.pourya_moghaddam.util.SocketClientUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "client", description = "Simple socket server for chat")
public class App implements Runnable {

    @Option(names = {"-h", "--host"}, description = "Server host", defaultValue = "localhost")
    private String host;

    @Option(names = {"-p", "--port"}, description = "Server port", defaultValue = "12345")
    private int port;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        SocketClientUtils.runClient(host, port);
    }
}
