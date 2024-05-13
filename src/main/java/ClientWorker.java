//Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientWorker {

    private static Socket clientSocket; // socket for communication
    private static BufferedReader reader; // we need a reader that reads from the console, otherwise
    // do we know what the client wants to say?
    private static BufferedReader in; // socket read stream
    private static BufferedWriter out; // socket write stream

    public static String getPassword(){
        return "password1";
    }


    //CODE AUQUEL REVENIR SI LE PROCHAIN TEST NE FONCTIONNE PAS
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1337);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Server: " + serverMessage);
                if (serverMessage.equalsIgnoreCase("quit")) {
                    break;
                }
//                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//                System.out.print("Client: ");
                String clientMessage = "";// = userInput.readLine();
                switch (serverMessage){
                    case "WHO_ARE_YOU":
                        out.println("ITS_ME");
                        break;
                    case "PROGRESS":
                        if(true) {//Faire la condition qui dit que ça teste
                            out.println("TESTING");
                        }else{
                            out.println("NOPE");
                        }
                        break;
                    default :
                        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Client: ");
                        clientMessage = userInput.readLine();
                        out.println(clientMessage);
                }

                if (clientMessage.equalsIgnoreCase("quit")) {
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*CODE AVEC PROTOCOLE
    public static void main(String[] args) {

        Boolean quit = false;
        try {
            try {
                // address - local host, port - 4004, same as the server
                clientSocket = new Socket("localhost", 1337); // with this line we request
                //System.out.println("Did you have something to say? Enter it here:");
                // if the connection happened and the threads were successfully created - we can
                // work further and offer the client something to enter
                // if not, an exception will be thrown
                //String word = reader.readLine(); // wait for the client to do something
                String word = "";
                while(!quit) {
                    // the server has access to the connection
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    // read messages from the server
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // write there
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


                    String serverWord = "";
                    serverWord = in.readLine();//on attend le message du serveur

                    switch (serverWord){
                        case "WHO_ARE_YOU\n" :
                            word = "ITS_ME\n";
                            // will not write to the console
                            out.write(word + "\n"); // send a message to the server
                            out.flush();

                            break;
                        case "GIMME_PASSWORD\n" :
                            word = "PASSWD" + getPassword()+"\n";
                            // will not write to the console
                            out.write(word + "\n"); // send a message to the server
                            out.flush();
                            break;
                        case "HELLO_YOU\n":
                            word = "READY\n";
                            out.write(word + "\n"); // send a message to the server
                            out.flush();
                            break;
                        case "PROGRESS\n":
                            word = "NOPE\n";//pour le moment, a changer quand on fera le test
                            break;
                        default :
                            word = "QUIT\n";
                            out.write(word + "\n"); // send a message to the server
                            out.flush();
                            quit = true;
                            break;
                    }
                    //serverWord = in.readLine(); // wait for the server to say
                    System.out.println(serverWord); // received - display
                    //if (word.equals("Quit\n")) { // il faudra regler que il y ait un dernier message a ecrire, apres avoir ecris quit, on devrait soritr, la il demande un message inutile
                    //    quit = true;
                    //}
                }
            } finally { // in any case, you need to close the socket and streams
                System.out.println("The client has been closed...");
                if(clientSocket!=null) {
                    clientSocket.close();
                }
                if(in!=null) {
                    in.close();
                }
                if(out!=null) {
                    out.close();
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }*/
}
//TODO : - Regler le probleme du message en trop cote client une fois quit écrit
//       - regler l'affichage UTF8
//       - boucle while DONE
//       - implementer protocole
//       - plusieurs clients simultané
//       - protocole minage nonce
//       - tache données par l'API / Communivation avec l'API
//       - Donner le password de facon sécuriser je sais pas la
//

/* //Code Cours + doriane

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class ClientWorker implements Runnable {
    private final Charset charset;

    public static void main(String[] var0) {
        String var1 = var0.length == 1 ? var0[0] : "ISO-8859-15";
        (new ClientWorker(Charset.forName(var1))).run();
    }

    public ClientWorker(Charset var1) {
        this.charset = var1;
    }

    public void run() {
        try {
            InetAddress var1 = InetAddress.getByName("127.0.0.1"); //on obtient une adresse IP sur le réseaux pour 127.0.0.1
            Socket var2 = new Socket(var1, 1337); //on crée une connexion

            try {
                OutputStream var3 = var2.getOutputStream(); //on envoie des données
                InputStream var4 = var2.getInputStream(); //on recoit des donnés
                BufferedReader var5 = new BufferedReader(new InputStreamReader(System.in, "UTF-8")); //on demande d'entrée un message
                byte[] var6 = new byte[80];
                String var7 = var5.readLine();//on stocke ce qu(on v envoyer dans var7
                if (var7 != null) {
                    var3.write((var7 + "\n").getBytes(this.charset)); //on ecrit ce qu'on a recu de var 3 dans var7
                    var3.flush();//on nettoie var3
                    int var8 = var4.read(var6);//on gare en mémoire la taille de ce qu'on a recu, qui était stocké dans var6 et recu par var4
                    System.out.println(new String(var6, 0, var8, this.charset));//on affiche le message recu du serveur
                }
            } catch (Throwable var10) {
                try {
                    var2.close();//on ferme la connexion
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }

                throw var10;
            }

            var2.close();
        } catch (IOException var11) {
            var11.printStackTrace(System.err);
        }

    }
}
*/

//Code Chat GPT
//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//class Client {
//    private static final String SERVER_IP = "127.0.0.1";
//    private static final int PORT = 12345;
//    private static final String PASSWORD = "password123";
//
//    private static final String WHO_ARE_YOU = "WHO_ARE_YOU";
//    private static final String ITS_ME = "ITS_ME";
//    private static final String GIMME_PASSWORD = "GIMME_PASSWORD";
//    private static final String PASSWORD_PREFIX = "PASSWORD ";
//    private static final String READY = "READY";
//    private static final String HELLO_YOU = "HELLO_YOU";
//    private static final String PROGRESS = "PROGRESS";
//
//    private static final String OK = "OK";
//    private static final String YOU_DONT_FOOL_ME = "YOU_DONT_FOOL_ME";
//    private static final String NOPE = "NOPE";
//
//    private BufferedReader input;
//    private PrintWriter output;
//
//    public Client() {
//        try {
//            Socket socket = new Socket(SERVER_IP, PORT);
//            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            output = new PrintWriter(socket.getOutputStream(), true);
//            startCommunication();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void startCommunication() throws IOException {
//        String response;
//        while ((response = input.readLine()) != null) {
//            if (response.equals(WHO_ARE_YOU)) {
//                output.println(ITS_ME);
//            } else if (response.equals(GIMME_PASSWORD)) {
//                output.println(PASSWORD_PREFIX + PASSWORD);
//            } else if (response.equals(HELLO_YOU)) {
//                output.println(READY);
//            } else if (response.equals(PROGRESS)) {
//                output.println(NOPE);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        new Client();
//    }
//}

