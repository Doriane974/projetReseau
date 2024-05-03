import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LauncherSkeleton {

    private ServerSocket serverSocket;

    public void run() throws IOException {
        // Start server
        startServer();

        // Create BufferedReader for console input
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        // Create PrintWriter for console output
        PrintWriter consoleWriter = new PrintWriter(System.out, true);

        // écoute les commandes
        boolean keepGoing = true;
        while (keepGoing) {
            // Lecture de la commande depuis la console
            final String commande = consoleReader.readLine();
            if (commande == null || commande.equalsIgnoreCase("exit")) {
                keepGoing = false; // Sortie de la boucle si la commande est 'exit' ou si la lecture est null
                continue; // Passe à l'itération suivante de la boucle
            }

            // Envoi de la commande au serveur et réception de la réponse
            String response = sendCommandToServer(commande.trim());

            // Affichage de la réponse dans la console
            consoleWriter.println(response);
        }


        // Shutdown server
        stopServer();
    }


    private void startServer() throws IOException {
        serverSocket = new ServerSocket(1337);
        System.out.println("Server started on port 1337.");

        // Accept client connections in a separate thread
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);

                    // Handle client communication in another thread
                    new Thread(() -> {
                        try {
                            handleClient(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                // Traitement de la commande reçue du client
                processCommand(inputLine, writer);
            }
        }
    }

    private String sendCommandToServer(String command) {
        try (
                Socket socket = new Socket("localhost", 1337);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Envoi de la commande au serveur
            writer.println(command);

            // Lecture de la réponse du serveur
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }

            return responseBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error communicating with server: " + e.getMessage();
        }
    }


    private void processCommand(String cmd, PrintWriter writer) {
        String[] parts = cmd.split(" ");
        String command = parts[0];

        switch (command) {
            case "solve":
                int difficulty = Integer.parseInt(parts[1]);
                solveTask(difficulty, writer);
                break;
            case "cancel":
                cancelTask(writer);
                break;
            case "status":
                showStatus(writer);
                break;
            case "quit":
                try {
                    writer.println("Server is shutting down...");
                    writer.flush();
                    stopServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                writer.println("Unknown command: " + command);
                writer.flush();
        }
    }

    private void solveTask(int difficulty, PrintWriter writer) {
        String result = mine(difficulty);
        writer.println("Solution found: " + result);
        writer.flush();
    }

    private void cancelTask(PrintWriter writer) {
        writer.println("Task cancelled.");
        writer.flush();
    }

    private void showStatus(PrintWriter writer) {
        writer.println("Status: X mineurs connectés.");
        writer.flush();
    }

    private String mine(int difficulty) {
        // Ici, vous implémenterez l'algorithme de minage avec la difficulté spécifiée
        // Par exemple, vous pouvez générer un hash avec un certain nombre de zéros en préfixe
        // Retournez le résultat du minage
        return "dummy_hash";
    }

    private void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server stopped.");
        }
    }

    public static void main(String[] args) throws IOException {
        new LauncherSkeleton().run();
    }
}

