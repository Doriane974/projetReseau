package fr.ul.miage.projetReseau;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Serveur {

    /**
     * La classe ClientHandler permet de gerer les differents clients qui se connectent au serveur
     */
    static class ClientHandler extends Thread {
        private Socket clientSocket;
        //private BufferedReader in;
        //private PrintWriter out;
        private int numberOfThread;

        public ClientHandler(Socket socket, int numberOfThread) {
            this.clientSocket = socket;
            this.numberOfThread = numberOfThread;
        }


        @Override
        public void run() {
            try {
                while(true) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String messageCLient = in.readLine();
                    System.out.println(messageCLient);
                    String messageARepondre = ecouteClient(processMessageClient(messageCLient));
                    if ((messageARepondre!=null) && (messageARepondre != "")) {
                        out.println(messageARepondre);
                    }
                    //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    //messageCLient = in.readLine();
                }

                //clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendProgress(){
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("PROGRESS");

            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public void sendCancelled(){
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("CANCELLED");

            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public void sendWhoAreYou(){
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("WHO_ARE_YOU_?");

            }catch(IOException e){
                e.printStackTrace();
            }
        }


        String[] processMessageClient(String messageClient){
            System.out.println("Message du client : "+messageClient);
            return messageClient.split("\\s+");
        }

        String ecouteClient(String[] messageClientTab){
            String messageARepondre = "";
            switch (messageClientTab[0]){
                case "ITS_ME":
                    messageARepondre = "GIMME_PASSWORD";
                    break;
                case "PASSWD":

                    if(messageClientTab.length==2){
                        if(messageClientTab[1].equals(getPassword())){
                            messageARepondre="HELLO_YOU";
                        }
                        else{
                            messageARepondre="YOU_DONT_FOOL_ME";
                            //messageARepondre="YOU_DONT_FOOL_ME";
                        }
                    }
                    break;
                case "READY":
                    generate_work(); // Est ce que on les fait tout de suite ou pas les SOLVE/PAYLOAD/NONCE ??
                    System.out.println("----------------------\nOn a gener le travail, pret a l'envoyer : ");
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("NONCE "+numberOfThread+" "+increment);
                        out.println("PAYLOAD "+data);
                        out.println("SOLVE "+difficulty);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
                case "FOUND":
                    System.out.println("FOUND SOMETHING !!!");
                    if(messageClientTab.length==3){
                        hashRes = messageClientTab[1];
                        nonceRes = messageClientTab[2];
                        if(validate_work(String.valueOf(hashRes),difficulty, nonceRes)){
                            messageARepondre = "SOLVED";
                        };

                    }
                    break;
                case "TESTING":
                    System.out.println("Client "+numberOfThread+" is TESTING");
                    break;
                case "NOPE":
                    System.out.println("Client "+numberOfThread+" is not testing");
                    break;
            }
            return messageARepondre;
        }
    }
    private static String data = "";
    private static int start = 0;
    private static String hashRes = "";
    private static String nonceRes = "";
    private static int increment = 0;
    private static int difficulty = 0;
    private static boolean envoieProgress = false;
    private static boolean envoieCancelled = false;
    private static final String API_KEY = "recoNRuTzI2uLiS9X"; // Your API key here

    /**
     * Obtiens les données de l'api web, qu'il faudra ensuite transmettre au client
     * ATTENTION : le prof a dit que le nonce recu de l'api web est bien un nobre :
     *  il faut separer chaque charatere recu, et prendre la valeur correspondante
     *  (modulo 256 ou quelquechose comme ça)
     * Il vaut mieux le faire au moment de cette fonction, c'est mieux pour le rest edu code
     */
//    public static void getWork() {
//        //données au hasard pour pouvoir tester le code.
//        // partie de Olha et Oleksandra
//        data = "def";
//        start = 78910;
//        difficulty = 8;
//        //increment = 2;
//    }

    public static String getPassword() {
        //A mieux faire aussi, juste pour pouvoir tester le reste du code
        return "password1";
    }

//    public static boolean validateTheWork(String hash, int difficulty) {
//        //TODO : valider avec l'api web validate work (Olha & Sasha)
//        String onVeutCeDebut = "";
//        for (int i = 0; i < difficulty; i++) {
//            onVeutCeDebut = "0" + onVeutCeDebut;
//        }
//        return hash.startsWith(onVeutCeDebut);
//    }

    public static void main(String[] args) {
        int numberOfClients = 1; // Par défaut, 1 client
        int chosenDifficulty = 0;

        // Vérifier les arguments de la ligne de commande
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-d") && i + 1 < args.length) {
                try {
                    chosenDifficulty = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Difficulté spécifiée incorrecte.");
                    System.exit(1);
                }
                i++;
            } else if (args[i].equals("-n") && i + 1 < args.length) {
                try {
                    numberOfClients = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Nombre de clients spécifié incorrect.");
                    System.exit(1);
                }
                i++;
            } else {
                System.err.println("Utilisation : java Serveur -d <difficulté> [-n <nombre_de_clients>]");
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
            ServerSocket serverSocket = new ServerSocket(1337);
            List<ClientHandler> clientHandlers = new ArrayList<>();
            System.out.println("Server started. Waiting for clients...");

            //on connecte 8 clients
            while (clientHandlers.size() < numberOfClients) {//on veut que 8 clients se connectent au serveur avant de commencer le travail
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers.size());
                clientHandlers.add(clientHandler);

            }

            //on lance les 8 threads
            for(int i = 0; i<clientHandlers.size(); i++) {
                clientHandlers.get(i).start();
                clientHandlers.get(i).sendWhoAreYou();
                //threadList.get(i).start();m
            }
            while (true) {
                //On regarde si l'utilisateur veut envoyer des commandes spéciales aux threads
                Scanner scanner = new Scanner(System.in);
                System.out.println("Commandes disponibles : \n \t-CANCELLED : stopper tous les clients connectés\n\t-PROGRESS : connaitre l'etat des clients connectés");
                String commandeUser = scanner.nextLine();
                if (commandeUser.equals("CANCELLED")) {
                    clientHandlers.forEach(ClientHandler::sendCancelled);
                } else if (commandeUser.equals("PROGRESS")) {
                    clientHandlers.forEach(ClientHandler::sendProgress);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }



    private static void getData(String responseText) {
        Pattern patternData = Pattern.compile("\"data\":\"(.*?)\"");
        Matcher matcherData = patternData.matcher(responseText);
        if (matcherData.find()) {
            String dataval = matcherData.group(1);
            data = dataval;
        } else {
            System.out.println("Data not found in response.");
        }
    }

    // read response API
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

    // read error API ~ difference bertween readResponse and readErrorResponse in
//  the parameters conn.getInputStream() and conn.getErrorStream()
    private static void errorResponse(HttpURLConnection conn, int responseCode) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("*******************");
            System.out.println("ERROR");
            System.out.println("*******************");
            throw new IOException("HTTP/1.1 " + responseCode  + "\n" + response.toString());

        }


    }


    public static void generate_work() {
        //données au hasard pour pouvoir tester le code.
        data = "def";
        start = 0;
        //difficulty = 2;
        //increment = 1;



        try {
            URL url = new URL("https://projet-raizo-idmc.netlify.app/.netlify/functions/generate_work?d=" + difficulty);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY); // s’authentification

            int responseCode = conn.getResponseCode();
            String response = readResponse(conn); // method for readind response

            if (responseCode == 200 || responseCode == 201){
                System.out.println("HTTP/1.1 OK");
                System.out.println("HTTP/1.1 201 Created ");
                System.out.println(response);
                getData(response); // method for get "data" from json file and save it to payload
            } else if (responseCode >= 400) {
                errorResponse(conn, responseCode);
            }

        } catch (Exception e) {
            System.out.println("Error making GET request: " + e.getMessage());
        }
    }

    public static boolean validate_work(String hash, int difficulty, String nonce) {

        String urlString = "https://projet-raizo-idmc.netlify.app/.netlify/functions/validate_work";
        //String requestBody = String.format("{\"d\":%d,\"n\":\"%d\",\"h\":\"%s\"}", difficulty, nonce, hash);
        String requestBody = String.format("{\"d\":%d,\"n\":\"%s\",\"h\":\"%s\"}", difficulty, nonce, hash);


        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json;utf-8");
            connection.setDoOutput(true);

            System.out.println("Request Body: " + requestBody);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode >= 400) {
                errorResponse(connection, responseCode);
            }

            String responseText = readResponse(connection);

            if (responseCode == 200) {
                System.out.println("HTTP/1.1 200 OK");
                System.out.println(responseText);
                return true;
            }


        } catch (Exception e) {
            System.err.println("Error making POST request:");
            System.err.println("URL: " + urlString);
            System.err.println("Request Body: " + requestBody);
            System.err.println("Exception Message: " + e.getMessage());
        }

        return false;
    }




}