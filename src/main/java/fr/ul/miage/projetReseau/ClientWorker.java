package fr.ul.miage.projetReseau;

import java.io.*;
import java.net.*;
import java.security.*;

public class ClientWorker {

    //les données pour la connexion
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 1337;

    //les données pour miner les données
    private static int nonce;
    private static int difficulty;
    private static String payload;
    private static int increment;

    //les status du client
    private static boolean connected = false;
    private static boolean waiting = false;
    private static boolean working = false;

    private static boolean nonceReceived = false;
    private static boolean payloadReceived = false;
    private static boolean solveReceived = false;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String serverMessage = in.readLine();
            System.out.println("Server: " + serverMessage);

            if ("WHO_ARE_YOU_?".equals(serverMessage)) {
                out.println("ITS_ME");
                try {
                    Thread.sleep(2000);//Je met 2 secondes pour dormir pour pouvoir lancer plusieurs client en meme temps et tester le paralele
                } catch (InterruptedException e) {
                    System.out.println("Sleep failed");
                }
                serverMessage = in.readLine();
                System.out.println("Server: " + serverMessage);

                if ("GIMME_PASSWORD".equals(serverMessage)) {
                    out.println("PASSWD " + getPassword());//ici faire getPassword()

                    serverMessage = in.readLine();
                    System.out.println("Server: " + serverMessage);

                    if ("HELLO_YOU".equals(serverMessage)) {
                        connected = true;
                        out.println("READY");
                        waiting = true;

                    }
                }
            }
            serverMessage = in.readLine();
            System.out.println("Server: " + serverMessage);
            if(connected){

                if ("OK".equals(serverMessage)) {



                    while((!nonceReceived) || (!payloadReceived) || (!solveReceived)){
                        serverMessage = in.readLine();
                        System.out.println("Server: " + serverMessage);
                        if (serverMessage.startsWith("NONCE")) {
                            String[] nonceParts = serverMessage.split("\\s+");
                            if (nonceParts.length == 3) {
                                nonce = Integer.parseInt(nonceParts[1]);
                                increment = Integer.parseInt(nonceParts[2]);
                                nonceReceived = true;
                            }

                        }
                        else if (serverMessage.startsWith("PAYLOAD")) {
                            String[] payloadParts = serverMessage.split("\\s+");
                            if(payloadParts.length == 2) {
                                payload = payloadParts[1];
                                payloadReceived = true;
                            }

                        }
                        else if (serverMessage.startsWith("SOLVE")) {
                            String[] solveParts = serverMessage.split("\\s+");
                            if(solveParts.length == 2) {
                                difficulty = Integer.parseInt(solveParts[1]);
                                solveReceived = true;
                            }


                        }

                    }
                    waiting = false;
                    working = true;
                    if(working) {
                        //int length = Integer.parseInt(serverMessage.split(" ")[1]);
                        String hash = mineBlock(difficulty, increment, nonce, payload);
                        //String hash = findHash(nonce, payload, difficulty, length);
                        if (hash != null) {
                            out.println("FOUND " + hash + " " + nonce);
                            System.out.println("Hash trouvé: " + hash);
                        }
                    }
                    working = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
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
        return applySha256(Integer.toString(increment) + Integer.toString(nonce) + payload);
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


////Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
//
////TODO : - regler l'affichage UTF8
////       - implementer protocole (progress !!)//doriane
////       - tache données par l'API / Communivation avec l'API//olha ou oleksandra
////       - Donner le password de facon sécuriser je sais pas la // Je sais pas
