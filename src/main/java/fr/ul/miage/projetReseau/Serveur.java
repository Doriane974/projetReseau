package fr.ul.miage.projetReseau;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serveur {

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int numberOfThread;
        private volatile boolean running = true;

        public ClientHandler(Socket socket, int numberOfThread) {
            this.clientSocket = socket;
            this.numberOfThread = numberOfThread;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                while (running) {
                    if (in.ready()) {
                        String messageClient = in.readLine();
                        System.out.println(messageClient);
                        String messageToRespond = listenClient(processMessageClient(messageClient));
                        if ((messageToRespond != null) && (!messageToRespond.equals(""))) {
                            out.println(messageToRespond);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void restartClient() {
            try {
                if (!running) {
                    if (clientSocket.isClosed()) {

                        clientSocket = new Socket(clientSocket.getInetAddress(), clientSocket.getPort());
                    }
                    running = true;
                    Thread newThread = new ClientHandler(clientSocket, this.numberOfThread);
                    newThread.start();
                    sendWhoAreYou();
                }
            } catch (Exception e) {
                System.err.println("Error restarting client handler: " + e.getMessage());
            }
        }



        public void sendProgress() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("PROGRESS");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendCancelled() {
            try {
                running = false;
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("CANCELLED");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        public void sendWhoAreYou() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("WHO_ARE_YOU_?");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] processMessageClient(String messageClient) {
            return messageClient.split("\\s+");
        }

        String listenClient(String[] messageClientTab) {
            String response = "";
            switch (messageClientTab[0]) {
                case "ITS_ME":
                    response = "GIMME_PASSWORD";
                    break;
                case "PASSWD":
                    if (messageClientTab.length == 2) {
                        if (messageClientTab[1].equals(getPassword())) {
                            response = "HELLO_YOU";
                        } else {
                            response = "YOU_DONT_FOOL_ME";
                        }
                    }
                    break;
                case "READY":
                    System.out.println("\n" + ANSI_BLUE + "     GENERATE WORK         ");
                    System.out.println("---------------------------\n" + ANSI_RESET);
                    generate_work();

                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("NONCE " + numberOfThread + " " + increment);
                        out.println("PAYLOAD " +    data);
                        out.println("SOLVE " +    difficulty);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "FOUND":
                    System.out.println("\n" + ANSI_BLUE + "     VALIDATE WORK         ");
                    System.out.println("---------------------------\n" + ANSI_RESET);
                    if (messageClientTab.length == 3) {
                        hashRes = messageClientTab[1];
                        nonceRes = messageClientTab[2];
                        if (validate_work(String.valueOf(hashRes), difficulty, nonceRes)) {
                            response = "SOLVED";
                            broadcastMessage("SOLVED"); // Send SOLVED to ALL the clients when one found the solution

                            // When we finish a task ,the serveur send the difficulty incremented of one to all the clients,
                            // so that they can solve the next difficulty
                            difficulty++;
                            broadcastMessage(ANSI_BLUE + "DIFFICULTY " + ANSI_RESET + difficulty);
                            generate_work();
                        }
                    }
                    break;
                case "TESTING":
                    System.out.println("Client " + numberOfThread + " is TESTING");
                    break;
                case "NOPE":
                    System.out.println("Client " + numberOfThread + " is not testing");
                    break;
            }
            return response;
        }




    }

    private static String data = "";
    private static int start = 0;
    private static String hashRes = "";
    private static String nonceRes = "";
    private static int increment = 0;
    private static int difficulty = 0;
    private static final String API_KEY = "recoNRuTzI2uLiS9X"; // Your API key here
    private static List<ClientHandler> clientHandlers = new ArrayList<>();
    private static ServerSocket serverSocket;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[0;34m";


    public static String getPassword() {
        return "password1";
    }

    public static void main(String[] args) {


        int numberOfClients = 1;
        int chosenDifficulty = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d") && i + 1 < args.length) {
                try {
                    chosenDifficulty = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Difficulté spécifiée incorrecte." );
                    System.exit(1);
                }
                i++;
            } else if (args[i].equals("-n") && i + 1 < args.length) {
                try {
                    numberOfClients = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Nombre de clients spécifié incorrecte ." );
                    System.exit(1);
                }
                i++;
            } else {
                System.err.println("Utilisation : java Serveur.java -d <difficulté> [-n <nombre_de_clients>]");
                System.exit(1);
            }
        }
        increment = numberOfClients;

        if (chosenDifficulty == 0) {
            System.err.println("Vous devez spécifier une difficulté avec l'option -d.");
            System.exit(1);
        }
        difficulty = chosenDifficulty;

        try {
            serverSocket = new ServerSocket(1337);
            System.out.println(ANSI_BLUE + "-------------------------");
            System.out.println("         SERVEUR         ");
            System.out.println("-------------------------" + ANSI_RESET + "\n");
            System.out.println( ANSI_BLUE +  "Server started." + ANSI_RESET + " Waiting for clients...");

            while (clientHandlers.size() < numberOfClients) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(ANSI_BLUE + "Client connected: "+ ANSI_RESET + clientSocket);
                System.out.println("Waiting for "+(numberOfClients-clientHandlers.size()-1)+" more client(s)\n");
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers.size());
                clientHandlers.add(clientHandler);
            }


            for (ClientHandler ch : clientHandlers) {
                System.out.println( ANSI_BLUE  + "        ClientWorker:      ");
                System.out.println("---------------------------\n" + ANSI_RESET);
                ch.start();
                ch.sendWhoAreYou();
            }
            listenForCommands();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(ANSI_BLUE + "Server shutting down..." + ANSI_RESET );
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
            System.out.println(ANSI_BLUE + "Cleanup completed." + ANSI_RESET );
        }));
    }



    private static void listenForCommands() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println( "Commandes disponibles : " + ANSI_BLUE + " \n\t-CANCELLED :"  + ANSI_RESET + " stopper tous les clients connectés\n\t" + ANSI_BLUE + "-PROGRESS :"  + ANSI_RESET + " connaitre l'etat des clients connectés\n");
            String commandeUser = scanner.nextLine();
            if (commandeUser.equals("CANCELLED")) {
                clientHandlers.forEach(ClientHandler::sendCancelled);
                System.out.print(ANSI_BLUE + "Enter new difficulty: " + ANSI_RESET);
                String newDifficulty = scanner.nextLine();
                try {
                    difficulty = Integer.parseInt(newDifficulty);
                    clientHandlers.forEach(ClientHandler::restartClient);
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_RED + "Invalid difficulty format." + ANSI_RESET);
                }
            } else if (commandeUser.equals("PROGRESS")) {
                clientHandlers.forEach(ClientHandler::sendProgress);
            }
        }
    }





    private static void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                PrintWriter out = new PrintWriter(clientHandler.clientSocket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static void getData(String responseText) {
        Pattern patternData = Pattern.compile("\"data\":\"(.*?)\"");
        Matcher matcherData = patternData.matcher(responseText);
        if (matcherData.find()) {
            data = matcherData.group(1);
            System.out.println("Data: " + data + "\n");
        } else {
            System.out.println(ANSI_RED + "Data not found in response." + ANSI_RESET);
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    private static void errorResponse(HttpURLConnection conn, int responseCode) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            throw new IOException(ANSI_RED + "HTTP/1.1 " +  ANSI_RESET + responseCode + "\n" + response.toString());
        }
    }

    public static void generate_work() {
        data = "a";
        start = 0;

        try {

            URL url = new URL("https://projet-raizo-idmc.netlify.app/.netlify/functions/generate_work?d=" + difficulty);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

            int responseCode = conn.getResponseCode();
            String response = readResponse(conn);


            if (responseCode == 200 || responseCode == 201) {
                System.out.println(ANSI_GREEN + "HTTP/1.1 200 " + ANSI_RESET +  "OK");
                System.out.println(ANSI_GREEN + "HTTP/1.1 201" + ANSI_RESET +  " Created \n");

                getData(response);
            } else if (responseCode >= 400) {
                if (responseCode == 409) {
                    System.out.println(ANSI_RED + "HTTP/1.1  409 Conflict: " + ANSI_RESET + "Difficulty " + difficulty + " already solved.");
                }
                System.out.println(responseCode);
                errorResponse(conn, responseCode);
            }

        } catch (IOException e) {
            String message = e.getMessage();


            if (message.contains("409")) {
                System.out.println(ANSI_RED + "HTTP/1.1  409 Conflict: " + ANSI_RESET + "Difficulty " + difficulty + " already solved.");

                difficulty++;
                broadcastMessage("NEW_DIFFICULTY " + difficulty);
                generate_work();

            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error making GET request: \n" + ANSI_RESET + e.getMessage() + "\n");
        }
    }

    public static boolean validate_work(String hash, int difficulty, String nonce) {
        String urlString = "https://projet-raizo-idmc.netlify.app/.netlify/functions/validate_work";
        String requestBody = String.format("{\"d\":%d,\"n\":\"%s\",\"h\":\"%s\"}", difficulty, nonce, hash);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json;utf-8");
            connection.setDoOutput(true);



            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 409) {
                System.out.println(ANSI_RED + "HTTP/1.1" + ANSI_RESET +  " 409 Conflict: difficulty " + difficulty + " already solved");
                return true;
            } else if (responseCode >= 400) {
                errorResponse(connection, responseCode);
            }

            String responseText = readResponse(connection);

            if (responseCode == 200) {
                System.out.println(ANSI_GREEN + "HTTP/1.1" + ANSI_RESET + " 200 OK");
                System.out.println(responseText);
                return true;
            }

        } catch (Exception e) {
            System.err.println(ANSI_RED + "Error making POST request: " + ANSI_RESET + requestBody);
            System.err.println("Exception Message: " + e.getMessage());
        }

        return false;
    }

}