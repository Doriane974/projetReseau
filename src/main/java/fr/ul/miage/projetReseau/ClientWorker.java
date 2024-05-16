package fr.ul.miage.projetReseau;

import java.io.*;
import java.net.*;
import java.security.*;

public class ClientWorker {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 1337;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String serverMessage = in.readLine();
            System.out.println("Server: " + serverMessage);

            if ("WHO_ARE_YOU_?".equals(serverMessage)) {
                out.println("ITS_ME");
                try {
                    Thread.sleep(2000);//Je met 2 secondes pour dormir pour pouvoir lancer plusieurs client en meme temps et tester le paralele
                }catch(InterruptedException e){
                    System.out.println("Sleep failed");
                }
                serverMessage = in.readLine();
                System.out.println("Server: " + serverMessage);

                if ("GIMME_PASSWORD".equals(serverMessage)) {
                    out.println("PASSWD password1");

                    serverMessage = in.readLine();
                    System.out.println("Server: " + serverMessage);

                    if ("HELLO_YOU".equals(serverMessage)) {
                        serverMessage = in.readLine();
                        System.out.println("Server: " + serverMessage);

                        if ("OK".equals(serverMessage)) {
                            serverMessage = in.readLine();
                            System.out.println("Server: " + serverMessage);

                            if (serverMessage.startsWith("NONCE")) {
                                String[] nonceParts = serverMessage.split(" ");
                                int nonce = Integer.parseInt(nonceParts[1]);
                                int difficulty = Integer.parseInt(nonceParts[2]);

                                serverMessage = in.readLine();
                                System.out.println("Server: " + serverMessage);

                                if (serverMessage.startsWith("PAYLOAD")) {
                                    String payload = serverMessage.split(" ")[1];

                                    serverMessage = in.readLine();
                                    System.out.println("Server: " + serverMessage);

                                    if (serverMessage.startsWith("SOLVE")) {
                                        int length = Integer.parseInt(serverMessage.split(" ")[1]);

                                        String hash = findHash(nonce, payload, difficulty, length);
                                        if (hash != null) {
                                            out.println("FOUND " + hash + " " + nonce);
                                            System.out.println("Hash trouvé: " + hash);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String findHash(int nonce, String payload, int difficulty, int length) {
        // Implement your hashing logic here to find a valid hash
        return "00080d0b01944a48c6a0a41b62a52a40ca7a0f5cf17ed95563566126ffcc97b8"; // Placeholder
    }
}

//
////Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
//import java.io.*;
//import java.net.Socket;
//
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.Random;
////import java.nio.charset.StandardCharsets;
//
//public class ClientWorker{
//    private boolean mining;
//    private boolean nonceReceived;
//    private int nonce;
//    private int increment;
//    private boolean payloadReceived;
//    private String payload;
//    private boolean difficultyReceived;
//    private int difficulty;
//    private boolean connected;
//
//    //private static Socket clientSocket; // socket for communication
//    //private static BufferedReader reader; // we need a reader that reads from the console, otherwise
//    // do we know what the client wants to say?
//    //private static BufferedReader in; // socket read stream
//    //private static BufferedWriter out; // socket write stream
//
//
//    public ClientWorker() {
//        this.mining = false;
//        this.nonceReceived = false;
//        this.nonce = 0;
//        this.increment = 0;
//        this.payloadReceived = false;
//        this.payload = "";
//        this.difficultyReceived = false;
//        this.difficulty = 0;
//        this.connected = false;
//    }
//
//    /*public ClientWorker(boolean nonceReceived, int nonce, int increment, boolean payloadReceived, String payload, boolean difficultyReceived, int difficulty) {
//        this.nonceReceived = nonceReceived;
//        this.nonce = nonce;
//        this.increment = increment;
//        this.payloadReceived = payloadReceived;
//        this.payload = payload;
//        this.difficultyReceived = difficultyReceived;
//        this.difficulty = difficulty;
//    }*/
//
//    public static String getPassword() {
//        return "password1";
//    }
//    void setNonceReceived(boolean val) {
//        this.nonceReceived = val;
//    }
//    public boolean isMining() {
//        return this.mining;
//    }
//    public void setMining(boolean mining) {
//        this.mining = mining;
//    }
//    public boolean isNonceReceived() {
//        return nonceReceived;
//    }
//    public boolean isConnected() {
//        return connected;
//    }
//    private void setConnected(boolean b) {
//        this.connected = b;
//    }
//    public int getNonce() {
//        return nonce;
//    }
//    public void setNonce(int nonce) {
//        this.nonce = nonce;
//    }
//    public int getIncrement() {
//        return increment;
//    }
//    public void setIncrement(int increment) {
//        this.increment = increment;
//    }
//    public boolean isPayloadReceived() {
//        return payloadReceived;
//    }
//    public void setPayloadReceived(boolean payloadReceived) {
//        this.payloadReceived = payloadReceived;
//    }
//    public String getPayload() {
//        return payload;
//    }
//    public void setPayload(String payload) {
//        this.payload = payload;
//    }
//    public boolean isDifficultyReceived() {
//        return difficultyReceived;
//    }
//    public void setDifficultyReceived(boolean difficultyReceived) {
//        this.difficultyReceived = difficultyReceived;
//    }
//    public int getDifficulty() {
//        return difficulty;
//    }
//    public void setDifficulty(int difficulty) {
//        this.difficulty = difficulty;
//    }
//
//    //Applies Sha256 to a string and returns the result.
//    public static String applySha256(String input){
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            //Applies sha256 to our input,
//            byte[] hash = digest.digest(input.getBytes("UTF-8"));
//            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
//            for (int i = 0; i < hash.length; i++) {
//                String hex = Integer.toHexString(0xff & hash[i]);
//                if(hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        }
//        catch(Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public String calculateHash(int increment, int nonce, String payload) {
//        String calculatedhash = applySha256(
//                Integer.toString(increment) +
//                        Integer.toString(nonce) +
//                        payload
//        );
//        return calculatedhash;
//    }
//    public String mineBlock(int difficulty, int increment, int nonce, String payload) {
//        String hash =new String(new char[difficulty]).replace('\0', '1');
//        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
//        while(!hash.substring( 0, difficulty).equals(target)) {
//            nonce ++;
//            hash = calculateHash(increment,nonce,payload);
//        }
//        return hash;
//    }
//
//
//
//    public void phase1(PrintWriter out, String serverMessage){
//        System.out.println("Server: " + serverMessage);
//        switch (serverMessage) {
//            case "WHO_ARE_YOU_?":
//                out.println("ITS_ME");
//                break;
//            case "HELLO_YOU":
//                out.println("READY");
//                break;
//            case "GIMME_PASSWORD":
//                out.println("PASSWD " + getPassword());
//                break;
//            case "OK":
//                this.connected = true;//on pourra passer à la phase 2
//                out.println("en attente ...");
//            case "YOU_DONT_FOOL_ME":
//                out.println("j'ai reçu you don't fool me");
//            default:
//                System.out.print("Message Recu : " + serverMessage);
//                break;
//        }
//
//    }
//
//    public void handlePhase1(PrintWriter out, BufferedReader in) throws IOException {
//        String serverMessage;
//        while ((serverMessage = in.readLine()) != null) {
//            System.out.println("Server: " + serverMessage);
//
//            switch (serverMessage) {
//                case "WHO_ARE_YOU_?":
//                    out.println("ITS_ME");
//                    break;
//                case "HELLO_YOU":
//                    out.println("READY");
//                    break;
//                case "GIMME_PASSWORD":
//                    out.println("PASSWD " + getPassword());
//                    break;
//                case "OK":
//                    return;  // Sortir de la phase 1
//                case "YOU_DONT_FOOL_ME":
//                    System.out.println("Password incorrect");
//                    return;
//                default:
//                    System.out.println("Message inconnu: " + serverMessage);
//                    break;
//            }
//        }
//    }
//    public void handlePhase2(PrintWriter out, BufferedReader in) throws IOException {
//        String serverMessage;
//        while ((serverMessage = in.readLine()) != null) {
//            System.out.println("Server: " + serverMessage);
//
//            String[] serverArgs = serverMessage.split("\\s+");
//            switch (serverArgs[0]) {
//                case "NONCE":
//                    setNonce(Integer.parseInt(serverArgs[1]));
//                    setIncrement(Integer.parseInt(serverArgs[2]));
//                    setNonceReceived(true);
//                    break;
//                case "PAYLOAD":
//                    setPayload(serverArgs[1]);
//                    setPayloadReceived(true);
//                    break;
//                case "SOLVE":
//                    setDifficulty(Integer.parseInt(serverArgs[1]));
//                    setDifficultyReceived(true);
//                    break;
//                default:
//                    System.out.println("Message inconnu: " + serverMessage);
//                    break;
//            }
//
//            if (isNonceReceived() && isPayloadReceived() && isDifficultyReceived()) {
//                return;  // Sortir de la phase 2
//            }
//        }
//    }
//    public void handlePhase3(PrintWriter out) {
//        String hashResult = mineBlock(getDifficulty(), getIncrement(), getNonce(), getPayload());
//        System.out.println("Hash trouvé: " + hashResult);
//        out.println("FOUND " + hashResult + " " + getNonce());
//    }
//
//    public static void main(String[] args) {
//        ClientWorker clientWorker = new ClientWorker();
//        try {
//            Socket socket = new Socket("localhost", 1337);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//            clientWorker.handlePhase1(out, in);
//            clientWorker.handlePhase2(out, in);
//            clientWorker.handlePhase3(out);
//
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
////    public static void main(String[] args) {
////        ClientWorker clientWorker = new ClientWorker();
////        //while(true) {
////            try {
////                Socket socket = new Socket("localhost", 1337);
////                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
////
////                String serverMessage = "";
////
////                //PHASE 1 : validation client/serveur
////                boolean connected = false;//pour passer a la phase 2 -> reception des consignes
////                boolean taskComplete = false;//pour passer a la phase 3 -> Hashage
////
////                while ((!clientWorker.isConnected()) && ((serverMessage = in.readLine()) != null)) {
////                    System.out.println("Server: " + serverMessage);
////                    if (serverMessage.equalsIgnoreCase("quit")) {
////                        break;
////                    }
////                    switch (serverMessage) {
////                        case "WHO_ARE_YOU_?":
////                            out.println("ITS_ME");
////                            break;
////                        case "HELLO_YOU":
////                            out.println("READY");
////                            break;
////                        case "GIMME_PASSWORD":
////                            try {
////                                Thread.sleep(5000);
////                            }catch(InterruptedException e){
////                                e.printStackTrace();
////                             }
////                            out.println("PASSWD " + getPassword());
////                            break;
////                        case "OK":
////                            clientWorker.setConnected(true);//on pourra passer à la phase 2
////                            out.println("en attente ...");
////                        case "YOU_DONT_FOOL_ME":
////                            out.println("j'ai reçu you don't fool me");
////                        default:
////                            System.out.print("Message Recu : " + serverMessage);
////                            break;
////                    }
////                }
////                System.out.println("On est identifié, on passe à l'etape suivante");
////                if (connected) {
////
////                    while (!taskComplete && ((serverMessage = in.readLine()) != null)) {
////                        System.out.println("Server: " + serverMessage);
////                        String[] serverArgs = serverMessage.split("\\s+");
////                        if (serverMessage.equalsIgnoreCase("quit")) {
////                            break;
////                        }
//////                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//////                System.out.print("Client: ");
////                        String clientMessage = "";// = userInput.readLine();
////
////                        if (serverArgs.length > 0) {
////                            switch (serverArgs[0]) {
////                                case "WHO_ARE_YOU_?":
////                                    out.println("ITS_ME");
////                                    break;
////                                case "SOLVED":
////                                    //Abandonner le travail en cours
////                                    break;
////                                case "NONCE":
////                                    if (serverArgs.length == 3) {
////                                        try {
////                                            clientWorker.setNonce(Integer.parseInt(serverArgs[1]));
////                                            clientWorker.setIncrement(Integer.parseInt(serverArgs[2]));
////                                            clientWorker.setNonceReceived(true);
////                                            System.out.println("Nonce set to " + clientWorker.getNonce() + " and increment set to " + clientWorker.getIncrement());
////                                        } catch (NumberFormatException e) {
////                                            System.err.println("Invalid NONCE message format");
////                                        }
////                                    } else {
////                                        System.err.println("Invalid NONCE message format");
////                                    }
////                                    break;
////                                case "PAYLOAD":
////                                    if (serverArgs.length == 2) {
////                                        try {
////                                            clientWorker.setPayload(serverArgs[1]);
////                                            clientWorker.setPayloadReceived(true);
////                                            System.out.println("Payload set to " + clientWorker.getPayload());
////                                        } catch (NumberFormatException e) {
////                                            System.err.println("Invalid PAYLOAD message format");
////                                        }
////                                    }
////                                    break;
////                                case "SOLVE":
////                                    if (serverArgs.length == 2) {
////                                        try {
////                                            clientWorker.setDifficulty(Integer.parseInt(serverArgs[1]));
////                                            clientWorker.setDifficultyReceived(true);
////                                            System.out.println("Difficulty set to " + clientWorker.getDifficulty());
////                                        } catch (NumberFormatException e) {
////                                            System.err.println("Invalid SOLVE message format");
////                                        }
////                                    } else {
////                                        System.err.println("Invalid SOLVE message format");
////                                    }
////                                    break;
////                                case "PROGRESS":
////                                    if (clientWorker.isMining()) {//Faire la condition qui dit que ça teste
////                                        out.println("TESTING");
////                                    } else {
////                                        out.println("NOPE");
////                                    }
////                                    break;
////                                case "CANCELLED":
////                                    out.println("on a recu cancelled");
////                                    //Abandonner le travail en cours
////                                    break;
////                                default:
////                                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
////                                    System.out.print("Client: ");
////                                    clientMessage = userInput.readLine();
////                                    out.println(clientMessage);
////                                    break;
////
////                            }
////                            if (clientWorker.isDifficultyReceived() && clientWorker.isNonceReceived() && clientWorker.isPayloadReceived()) {
////                                taskComplete = true; //on peut passer dans la phase 3, celle ou on fait le hachage
////                                System.out.println("task Complete = " + taskComplete);
////                            }//Dans la phase 3, il y aura deux thread par client worker. Un qui fait l'algo de hachage,
////                            // et qui envoie au serveur le resultat quand c'est finito, un qui est en attente des messages du serveur, pour savoir si
////                            //il faut continuer la tache ou non.
////                            if (clientMessage.equalsIgnoreCase("quit")) {
////                                break;
////                            }
////                        }
////                    }
////                }
////
////                if (taskComplete) {
////                    //Faire la phase 3
////                    String hashResult = clientWorker.mineBlock(clientWorker.getDifficulty(), clientWorker.getIncrement(), clientWorker.getNonce(), clientWorker.getPayload());
////                    System.out.println("Test TaskComplete : " + hashResult);
////                    out.println("Found " + hashResult + " " + clientWorker.getNonce());
////                }
////
////                socket.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////    }
//
//
//
//
//}
//
////TODO : - regler l'affichage UTF8
////       - implementer protocole//doriane
////       - plusieurs clients simultané//doriane
////       - protocole minage nonce //Maud ou doriane
////       - tache données par l'API / Communivation avec l'API//olha ou oleksandra
////       - Donner le password de facon sécuriser je sais pas la // Je sais pas
////       - Resoudre probleme du protocole qui ne se reexetura pas quand un nouveau client se connecte
////
//
//
