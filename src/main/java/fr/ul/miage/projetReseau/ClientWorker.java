package fr.ul.miage.projetReseau;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class ClientWorker {

    //les données pour la connexion
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 1337;

    //les données pour miner les données
    private static int start;
    private static int difficulty;
    private static String data;
    private static int increment;

    //les status du client
    //private static boolean unknown = true;
    //private static boolean connected = false;
    private static boolean ready = false;
    private static boolean working = false;
    //private static boolean latent = false;


    private static boolean nonceReceived = false;
    private static boolean payloadReceived = false;
    private static boolean solveReceived = false;

    static class Miner implements Runnable {
        public Miner() { }

        public void run() {
            working = true;
            mineBlock(difficulty, increment, start, data);
            working = false;
        }

        public void stop() {
            working = false;
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while(true) {
                while (!ready) {
                    String messageServeur = in.readLine();
                    String messageARepondre = ecouteServeur(socket, processServeurCommand(messageServeur), null);
                    System.out.println("messageARepondre = " +messageARepondre);
                    if ((messageARepondre != "") && (messageARepondre != null)) {

                        out.println(messageARepondre);
                    }
                }

                Miner miner = new Miner();
                Thread miningThread = new Thread(miner);
                miningThread.start();

                System.out.println("Mining started. Send CANCELLED to stop the miner.");
                while (working) {
                    String messageServeur = in.readLine();
                    String messageARepondre = ecouteServeur(socket, processServeurCommand(messageServeur), miningThread);

                    if ((messageARepondre != null) && (messageARepondre != "")) {
                        out.println(messageARepondre);
                    }
                    ecouteServeur(socket, processServeurCommand(messageServeur), miningThread);
                }
            }



            //while(true){//voir comment quitter la boucle

            //}

        }catch(IOException e){
            e.printStackTrace();
        }
        //mainProtocol();
//        try {
//            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//            String serverMessage = in.readLine();
//            System.out.println("Server: " + serverMessage);
//
//            if ("WHO_ARE_YOU_?".equals(serverMessage)) {
//                out.println("ITS_ME");
//                try {
//                    Thread.sleep(2000);//Je met 2 secondes pour dormir pour pouvoir lancer plusieurs client en meme temps et tester le paralele
//                } catch (InterruptedException e) {
//                    System.out.println("Sleep failed");
//                }
//                serverMessage = in.readLine();
//                System.out.println("Server: " + serverMessage);
//
//                if ("GIMME_PASSWORD".equals(serverMessage)) {
//                    out.println("PASSWD " + getPassword());//ici faire getPassword()
//
//                    serverMessage = in.readLine();
//                    System.out.println("Server: " + serverMessage);
//
//                    if ("HELLO_YOU".equals(serverMessage)) {
//                        connected = true;
//                        out.println("READY");
//
//                    }
//                }
//            }
//            serverMessage = in.readLine();
//            System.out.println("Server: " + serverMessage);
//            if(connected){
//
//                if ("OK".equals(serverMessage)) {
//
//
//
//                    while((!nonceReceived) || (!payloadReceived) || (!solveReceived)){
//                        serverMessage = in.readLine();
//                        System.out.println("Server: " + serverMessage);
//                        if (serverMessage.startsWith("NONCE")) {
//                            String[] nonceParts = serverMessage.split("\\s+");
//                            if (nonceParts.length == 3) {
//                                nonce = Integer.parseInt(nonceParts[1]);
//                                increment = Integer.parseInt(nonceParts[2]);
//                                nonceReceived = true;
//                            }
//
//                        }
//                        else if (serverMessage.startsWith("PAYLOAD")) {
//                            String[] payloadParts = serverMessage.split("\\s+");
//                            if(payloadParts.length == 2) {
//                                payload = payloadParts[1];
//                                payloadReceived = true;
//                            }
//
//                        }
//                        else if (serverMessage.startsWith("SOLVE")) {
//                            String[] solveParts = serverMessage.split("\\s+");
//                            if(solveParts.length == 2) {
//                                difficulty = Integer.parseInt(solveParts[1]);
//                                solveReceived = true;
//                            }
//
//
//                        }
//
//                    }
//                    working = true;
//
//                    //int length = Integer.parseInt(serverMessage.split(" ")[1]);
//                    String hash = mineBlock(difficulty, increment, nonce, payload);
//                    //String hash = findHash(nonce, payload, difficulty, length);
//                    if (hash != null) {
//                        out.println("FOUND " + hash + " " + nonce);
//                        System.out.println("Hash trouvé: " + hash);
//                    }
//
//                    working = false;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static String ecouteServeur(Socket socket, String[] serveurArgs, Thread miningThread){
        String reponse = "";
        System.out.print("Serveur : "+serveurArgs[0] );
        if(serveurArgs.length>1){
            System.out.print(" "+serveurArgs[1]);
        }
        if(serveurArgs.length>2){
            System.out.print(" "+serveurArgs[2]);
        }
        if(serveurArgs.length>3){
            System.out.print(" "+serveurArgs[3]);
        }
        System.out.println("\n");

        switch(serveurArgs[0]){
            case "WHO_ARE_YOU_?":
                reponse = "ITS_ME";
                break;
            case "GIMME_PASSWORD":
                reponse = "PASSWD "+getPassword();
                break;
            case "OK":
                //On a rien a faire dans ce cas, on laisse la chaine reponse vide
                break;
            case "HELLO_YOU":
                reponse = "READY";
                //changer d'etat
                break;
            case "YOU_DONT_FOOL_ME":
                reponse = "quit";
//                try {
//                    socket.close();
//                }catch (IOException e){
//                    System.out.println("Error when you dont fool me received");
//                }
//                //reponse = null;//il faut fermer la connextion
                //changer detat
                break;
            case "SOLVED":
                reponse = "READY";
                //Changer d'etat
                break;
            case "PROGRESS":
                reponse = working ? "TESTING":"NOPE";
                break;
            case "CANCELLED":
                if(miningThread!=null) {
                    miningThread.interrupt();
                }
                reponse = "READY";
                ready = false;
                //changer detat
                //arreter le thread
                break;
            case "NONCE":
                if(serveurArgs.length==3){
                    start = Integer.parseInt(serveurArgs[1]);
                    increment = Integer.parseInt(serveurArgs[2]);
                    nonceReceived = true;
                }
                if(nonceReceived && payloadReceived && solveReceived){
                    ready = true;
                    System.out.println("Client ready ? :"+ready);
                    nonceReceived = payloadReceived = solveReceived = false;
                }
                break;
            case "PAYLOAD":
                if(serveurArgs.length==2){
                    data = serveurArgs[1];
                    payloadReceived = true;
                }
                if(nonceReceived && payloadReceived && solveReceived){
                    ready = true;
                    System.out.println("Client ready ? :"+ready);
                    nonceReceived = payloadReceived = solveReceived = false;
                }
                break;
            case "SOLVE":
                if(serveurArgs.length==2){
                    difficulty = Integer.parseInt(serveurArgs[1]);
                    solveReceived = true;
                }
                if(nonceReceived && payloadReceived && solveReceived){
                    ready = true;
                    System.out.println("Client ready ? :"+ready);
                    nonceReceived = payloadReceived = solveReceived = false;
                }
                break;
            default:

                break;
        }
        System.out.println("Client : "+reponse);
        return reponse;
    }
    //    public static void phaseIdentification(Socket socket,BufferedReader in,PrintWriter out){
//        try {
//
//
//            String serverMessage = in.readLine();
//            System.out.println("Server: " + serverMessage);
//                if ("WHO_ARE_YOU_?".equals(serverMessage)) {
//                    out.println("ITS_ME");
//                    try {
//                        Thread.sleep(2000);//Je met 2 secondes pour dormir pour pouvoir lancer plusieurs client en meme temps et tester le paralele
//                    } catch (InterruptedException e) {
//                        System.out.println("Sleep failed");
//                    }
//                    serverMessage = in.readLine();
//                    System.out.println("Server: " + serverMessage);
//
//                    if ("GIMME_PASSWORD".equals(serverMessage)) {
//                        out.println("PASSWD " + getPassword());//ici faire getPassword()
//
//                        serverMessage = in.readLine();
//                        System.out.println("Server: " + serverMessage);
//
//                        if ("HELLO_YOU".equals(serverMessage)) {
//                            connected = true;
//                            out.println("READY");
//                            ready = true;
//
//                        }
//                    }
//                }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void phasePreparation(Socket socket,BufferedReader in,PrintWriter out) {
//        try {
//
//            String serverMessage = in.readLine();
//            System.out.println("Server: " + serverMessage);
//
//            if ("OK".equals(serverMessage)) {
//                while ((!nonceReceived) || (!payloadReceived) || (!solveReceived)) {
//                    serverMessage = in.readLine();
//                    System.out.println("Server: " + serverMessage);
//
//                    if (serverMessage.startsWith("NONCE")) {
//                        String[] nonceParts = serverMessage.split("\\s+");
//                        if (nonceParts.length == 3) {
//                            nonce = Integer.parseInt(nonceParts[1]);
//                            increment = Integer.parseInt(nonceParts[2]);
//                            nonceReceived = true;
//                        }
//                    } else if (serverMessage.startsWith("PAYLOAD")) {
//                        String[] payloadParts = serverMessage.split("\\s+");
//                        if (payloadParts.length == 2) {
//                            payload = payloadParts[1];
//                            payloadReceived = true;
//                        }
//                    } else if (serverMessage.startsWith("SOLVE")) {
//                        String[] solveParts = serverMessage.split("\\s+");
//                        if (solveParts.length == 2) {
//                            difficulty = Integer.parseInt(solveParts[1]);
//                            solveReceived = true;
//                        }
//                    }
//                }
//                working = true;
//
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }
//    public static void phaseCalcul(Socket socket,BufferedReader in,PrintWriter out){
//
//            //int length = Integer.parseInt(serverMessage.split(" ")[1]);
//            String hash = mineBlock(difficulty, increment, nonce, payload);
//            //String hash = findHash(nonce, payload, difficulty, length);
//            if (hash != null) {
//                out.println("FOUND " + hash + " " + nonce);
//                System.out.println("Hash trouvé: " + hash);
//            }
//
//            working = false;
//
//    }
//    public static void principalProtocole(Socket socket,BufferedReader in,PrintWriter out){
//        if (!connected) {
//            phaseIdentification(socket, in, out);
//        }
//        if (ready) {
//            phasePreparation(socket, in, out);
//        }
//        if (working) {
//            phaseCalcul(socket, in, out);
//        }
//    }
    public static String[] processServeurCommand(String command){
        return command.split("\\s+");
    }

    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexadecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateHash(int increment, int nonce, String payload) {
        return applySha256(payload + Integer.toString(increment+nonce));
    }

    public static String mineBlock(int difficulty, int increment, int nonce, String payload) {
        String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * "0"
        String hash = "";

        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash(increment, nonce, payload);
        }

        System.out.println("Nonce found: " + nonce); // Print the nonce for verification

        return hash;
    }

    public static String getPassword(){
        return "password1";
    }
}
