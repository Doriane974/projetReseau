package fr.ul.miage.projetReseau;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.nio.ByteBuffer;

public class ClientWorker {

    //les données pour la connexion
    private static Socket socket ;//= new Socket(SERVER_ADDRESS, SERVER_PORT);
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 1337;

    //les données pour miner les données
    private static int start;
    private static int difficulty;
    private static String data;
    private static int increment;

    //les status du client
    private static boolean enlighten = false;
    private static boolean working = false;


    private static boolean nonceReceived = false;
    private static boolean payloadReceived = false;
    private static boolean solveReceived = false;

    public static String hashFound;
    public static String nonceFound;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[0;34m";

    static class Miner implements Runnable {
        public Miner() { }

        public void run() {
            working = true;
            mineBlock(difficulty, increment, start, data);
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(ANSI_BLUE + "FOUND " + ANSI_RESET + hashFound + " " + nonceFound);
            }catch (IOException e){
                e.printStackTrace();
            }
            working = false;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(ANSI_BLUE + "-------------------------");
            System.out.println("      CLIENT WORKER         ");
            System.out.println("-------------------------" + ANSI_RESET + "\n");
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            while(true) {//fonction a faire ici
                System.out.println(ANSI_BLUE + "         Serveur         ");
                System.out.println("-------------------------" + ANSI_RESET + "\n");

                while (!enlighten) {
                    String messageServeur = in.readLine();
                    System.out.println(messageServeur);
                    String messageARepondre = listenServer(processServeurCommand(messageServeur), null);

                    if ((messageARepondre != null) && !messageARepondre.isEmpty()) {
                        out.println(messageARepondre);
                    }
                }

                Miner miner = new Miner();
                Thread miningThread = new Thread(miner);
                miningThread.start();

                System.out.println("Mining started. Send" + ANSI_RED + " CANCELLED" +  ANSI_RESET + " to stop the miner.");
                while (working) {
                    String messageServeur = in.readLine();
                    System.out.println(messageServeur);
                    String messageARepondre = listenServer(processServeurCommand(messageServeur), miningThread);

                    if ((messageARepondre != null) && (!messageARepondre.equals(""))) {
                        out.println(messageARepondre);
                    }
                }
            }



        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String listenServer(String[] serveurArgs, Thread miningThread){
        String response = "";
        switch(serveurArgs[0]){
            case "WHO_ARE_YOU_?":
                response = "ITS_ME";
                break;
            case "GIMME_PASSWORD":
                response = "PASSWD "+getPassword();

                break;
            case "OK":
                //On a rien a faire dans ce cas, on laisse la chaine reponse vide
                break;

            case "YOU_DONT_FOOL_ME":
                try {
                    socket.close();
                    System.out.println("Connection closed due to authentication failure.");
                    return "quit";
                } catch (IOException e) {
                    System.err.println("Error when trying to close the socket: " + e.getMessage());
                    return "error";
                }

            case "SOLVED":
            case "HELLO_YOU" :
                response = "READY";
                break;
            case "PROGRESS":
                response = working ? "TESTING":"NOPE";
                break;
            case "CANCELLED":
                if(miningThread!=null) {
                    miningThread.interrupt();
                }
                response = "READY";
                enlighten = false;
                break;
            case "NONCE":
                if(serveurArgs.length==3){
                    start = Integer.parseInt(serveurArgs[1]);
                    increment = Integer.parseInt(serveurArgs[2]);
                    nonceReceived = true;
                    verifyReceivings();
                }
                break;
            case "PAYLOAD":
                if(serveurArgs.length==2){
                    data = serveurArgs[1];
                    payloadReceived = true;
                    verifyReceivings();
                }
                break;
            case "SOLVE":
                if(serveurArgs.length==2){
                    difficulty = Integer.parseInt(serveurArgs[1]);
                    solveReceived = true;
                    verifyReceivings();
                }
                break;

            default:
                break;
        }
        return response;
    }

    static public void verifyReceivings(){
        if(nonceReceived && payloadReceived && solveReceived){
            enlighten = true;
            nonceReceived = payloadReceived = solveReceived = false;
        }
    }

    public static String[] processServeurCommand(String command){
        return command.split("\\s+");
    }

    public static String convertByteToHexaString(byte[] tabBytes){

        //StringBuffer hexRes = new StringBuffer();
        StringBuilder hexRes = new StringBuilder();
        //for (int i = 0; i < tabBytes.length; i++) {
        for (byte b:tabBytes){
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexRes.append('0');
            hexRes.append(hex);
        }
        return hexRes.toString();
    }


    public static void mineBlock(int difficulty, int increment, int nonce, String payload) {
        //System.out.println("Just to be sure : increment = "+increment+" et start = "+nonce);
        try {
            String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * "0"
            byte[] dataByte = payload.getBytes(StandardCharsets.UTF_8);
            int dataByteLength = dataByte.length;
            byte[] byteFullData = new byte[dataByteLength + 4];
            System.arraycopy(dataByte, 0, byteFullData, 0, dataByteLength);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            while(true) {

                byte[] nonceByte = ByteBuffer.allocate(4).putInt(nonce).array();
                System.arraycopy(nonceByte, 0, byteFullData,dataByteLength, nonceByte.length);
                byte[] hash = digest.digest(byteFullData);

                String hexString = convertByteToHexaString(hash);// new StringBuffer();

                if (hexString.startsWith(target)) {
                    nonceFound = convertByteToHexaString(nonceByte);
                    hashFound = hexString;

                    return;
                }
                nonce += increment;
            }
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();

        }
    }

    public static String getPassword(){
        return "password1";
    }
}