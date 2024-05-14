//Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
import java.io.*;
//import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
//import java.nio.charset.StandardCharsets;

public class Serveur {

    //private static Socket clientSocket; // socket for communication
    //private static ServerSocket server; // server socket
    //private static BufferedReader in; // socket read stream
    //private static BufferedWriter out; // socket write stream

    private String payload = "";
    private int nonce = 0;
    private int increment = 0;
    private int difficulty = 0;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void getWork() {
        //données au hasard pour pouvoir tester le code.
        // partie de Olha et Oleksandra
        setPayload("abc");
        setNonce(23456);
        setDifficulty(2);
        setIncrement(1);
    }

    public static String getPassword() {
        //A mieux faire aussi, juste pour pouvoir tester le reste du code
        return "password1";
    }

    public static boolean testHash(String nonce, int difficulty) {
        //TODO : faire un meilleur test peut etre ? (Maud)
        //       ou valider avec l'api web validate work ? (Olha & Sasha)
        String onVeutCeDebut = "";
        for (int i = 0; i < difficulty; i++) {
            onVeutCeDebut = "0" + onVeutCeDebut;
        }
        return nonce.startsWith(onVeutCeDebut);
    }

    public static void main(String[] args) {
        try {
        ServerSocket serverSocket = new ServerSocket(1337);
        System.out.println("Server started. Waiting for client...");


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    out.println("WHO_ARE_YOU_?");

                    String clientMessage;
                    while ((clientMessage = in.readLine()) != null) {
                        System.out.println("Client: " + clientMessage);
                        if (clientMessage.equalsIgnoreCase("quit")) {
                            break;
                        }
                        //String serverMessage = "";// = userInput.readLine();
                        switch (clientMessage) {
                            case "ITS_ME":
                                out.println("GIMME_PASSWORD");
                                break;
                            case "READY":
                                out.println("OK");
                                break;
                            case "test_manuel":
                                out.println("WHO_ARE_YOU_?");
                                break;
                            case "PASSWD\\s\\w+\\s*": //PAS VALIDER !! NE RENTRE JAMAIS ICI !!!
                                out.println("Server: You gave me a password, You said '" + clientMessage + "'. Type 'quit' to exit.");
                                break;
                            default:
                                if (clientMessage.startsWith("PASSWD")) {
                                    if (clientMessage.contains(getPassword())) {
                                        out.println("HELLO_YOU");
                                    } else {
                                        out.println("YOU_DONT_FOOL_ME");
                                        //out.println("NONCE 234 4");
                                    }
                                } else if (clientMessage.startsWith("FOUND")) {
                                    out.println("Verification...Cliquer sur entrer (sinon aie aie aie beug ! )");
                                    if (testHash("string", 2)) {
                                        out.println("SOLVED");
                                    } else {
                                        out.println("ERROR");
                                    }
                                } else {
                                    out.println("Server: You said '" + clientMessage + "'. Type 'quit' to exit.");
//                                BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
//                                System.out.print("Server: ");
//                                serverMessage = serverInput.readLine();
//                                out.println(serverMessage);
                                }
                                break;
                        }

                        //out.println("Server: You said '" + clientMessage + "'. Type 'quit' to exit.");
                    }
                }catch (SocketException e) {
                    System.err.println("Client connection reset: " + e.getMessage());
                }
                System.out.println("Client disconnected: " + clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}