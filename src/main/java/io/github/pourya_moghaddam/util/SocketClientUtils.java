package io.github.pourya_moghaddam.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Utility class containing the core logic for the socket chat client.
 */
public class SocketClientUtils {

    private static boolean running = true;

    /**
     * Runs the chat client: connects to the server, starts message handling threads,
     * and manages the interactive chat session until exit.
     *
     * @param host the hostname or IP address of the server
     * @param port the port number the server is listening on
     */
    public static void runClient(String host, int port) {
        try (
            Socket socket = new Socket(host, port);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputStreamWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connected to " + host + ":" + port);

            printInitialMessage(inputStreamReader);

            Thread readerThread = createReaderThread(inputStreamReader);

            readFromConsoleAndSend(outputStreamWriter);

            readerThread.join();

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + host);
        } catch (IOException | InterruptedException e) {
            System.err.println("Connection error: " + e.getMessage());
        }

        System.out.println("Client stopped.");
    }

    /**
     * Prints the initial welcome/greeting message(s) sent by the server
     * until it sees the line containing "Type 'bye'".
     *
     * @param reader the reader connected to the server's input stream
     * @throws IOException if an I/O error occurs while reading
     */
    private static void printInitialMessage(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.contains("Type 'bye'")) {
                break;
            }
        }
    }

    /**
     * Creates and starts a virtual thread that continuously reads messages
     * from the server and prints them to the console.
     *
     * @param reader the reader connected to the server's input stream
     * @return the started reader thread
     */
    private static Thread createReaderThread(BufferedReader reader) {
        return Thread.startVirtualThread(() -> {
            try {
                String message;
                while (running) {
                    message = reader.readLine();

                    if (message == null) {
                        System.out.println("\n[Server closed the connection]");
                        System.exit(1);
                    }

                    if (message.trim().equalsIgnoreCase("bye")) {
                        System.out.println("\n[Server left the chat]");
                        System.exit(1);
                    }

                    clearCurrentLine();
                    System.out.println("Server: " + message);
                    printPrompt();
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("\n[Connection lost]");
                }
                running = false;
            } finally {
                running = false;
            }
        });
    }

    /**
     * Reads user input from the console and sends it to the server.
     * Supports "bye" as an exit command.
     *
     * @param writer the writer connected to the server's output stream
     * @throws IOException if an I/O error occurs while reading from console
     */
    private static void readFromConsoleAndSend(PrintWriter writer) throws IOException {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            printPrompt();
            String userInput;
            while (running && (userInput = console.readLine()) != null) {
                if (userInput.trim().equalsIgnoreCase("bye")) {
                    writer.println("bye");
                    writer.flush();
                    System.out.println("[You left the chat]");
                    running = false;
                    break;
                }

                writer.println(userInput);
                writer.flush();
                printPrompt();
            }
        }
    }

    /**
     * Prints the user input prompt ("You: ").
     */
    private static void printPrompt() {
        System.out.print("You: ");
        System.out.flush();
    }

    /**
     * Clears the current console line by overwriting it with spaces.
     * Used to remove the old prompt before printing a new server message.
     */
    private static void clearCurrentLine() {
        System.out.print("\r" + " ".repeat(60) + "\r");
        System.out.flush();
    }
}
