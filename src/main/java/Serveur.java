//Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Serveur {

    //private static Socket clientSocket; // socket for communication
    //private static ServerSocket server; // server socket
    //private static BufferedReader in; // socket read stream
    //private static BufferedWriter out; // socket write stream
    public static String getPassword(){
        return "password1";
    }

    public static Boolean testHash(String hash, String nonce){
        return true;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1337);
            System.out.println("Server started. Waiting for client...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Welcome to the server! Type 'quit' to exit.");

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Client: " + clientMessage);
                    if (clientMessage.equalsIgnoreCase("quit")) {
                        break;
                    }
                    switch(clientMessage){
                        case "ITS_ME":
                            out.println("GIMME_PASSWORD");
                            break;
                        case "READY":
                            out.println("OK");
                            break;
                        case "test_manuel":
                            out.println("WHO_ARE_YOU");
                            break;
                        case "PASSWD\\s\\w+\\s*": //PAS VALIDER !! NE RENTRE JAMAIS ICI !!!
                            out.println("Server: You gave me a password, You said '" + clientMessage + "'. Type 'quit' to exit.");
                            break;
                            /*case "PASSWD\\s\\w+\n":
                                String deuz = word.split("\\s+")[1];
                                if(deuz.equals(getPassword())){
                                    response = "HELLO_YOU\n";
                                }
                                else{
                                    response = "YOU_DONT_FOOL_ME\n";
                                }
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;
                            case "FOUND\\s\\w+\\s\\w+\n":
                                String deuxieme = word.split("\\s+")[1];
                                String troisieme = word.split("\\s+")[2];
                                if(testHash(deuxieme,troisieme)){
                                    response = "SOLVED\n";
                                }
                                else{
                                    response = "ERROR\n";
                                }
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;*/
                        default :
                            out.println("Server: You said '" + clientMessage + "'. Type 'quit' to exit.");
                            break;
                    }

                    //out.println("Server: You said '" + clientMessage + "'. Type 'quit' to exit.");
                }

                System.out.println("Client disconnected: " + clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /*CODE AVEC PROTOCOLE
    public static void main(String[] args) {

        try {
            try {
                server = new ServerSocket(1337); // server socket listening on port 4004
                System.out.println("Server is running!"); // server would be nice
                // announce your launch
                clientSocket = server.accept(); // accept() will wait until
                //someone won't want to connect

                //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String word = "";

                boolean quit = false;
                try { // after establishing a connection and recreating the socket for communication with the client, you can go
                    // to create I/O streams.
                    // now we can receive messages
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    out.write("WHO_ARE_YOU\n");
                    out.flush();;
                    while(!quit) {
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        // and send
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        word = in.readLine(); // wait for the client to write something to us
                        System.out.println(word);
                        // without hesitation responds to the client
                        String response = "";
                        switch (word){
                            case "ITS_ME\n" :
                                response = "GIMME_PASSWORD\n";
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;
                            case "READY\n" :
                                response = "OK\n";
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;
                                //On sort du swhitch case
                            case "PASSWD\\s\\w+\n":
                                String deuz = word.split("\\s+")[1];
                                if(deuz.equals(getPassword())){
                                    response = "HELLO_YOU\n";
                                }
                                else{
                                    response = "YOU_DONT_FOOL_ME\n";
                                }
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;
                            case "FOUND\\s\\w+\\s\\w+\n":
                                String deuxieme = word.split("\\s+")[1];
                                String troisieme = word.split("\\s+")[2];
                                if(testHash(deuxieme,troisieme)){
                                    response = "SOLVED\n";
                                }
                                else{
                                    response = "ERROR\n";
                                }
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                break;
                            default :
                                response = "NON!\n";
                                out.write(response);
                                out.flush(); // push everything out of the buffer
                                quit = true;
                                break;
                        }
                        word = in.readLine();
                        System.out.println(word);
                    }


                } finally { // in any case, the socket will be closed
                    clientSocket.close();
                    // streams would also be nice to close
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Server closed!");
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }*/
}

//Code COURS + DORIANE
//import java.io.*;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.util.Locale;
//import java.util.Scanner;
//
//public class Serveur implements Runnable {
//    private final Charset charset;
//    public Serveur(Charset var1) {
//        this.charset = var1;
//    }
//    public static void main(String[] args) {
//        String var1 = args.length == 1 ? args[0] : "ISO-8859-15";
//        (new Serveur(Charset.forName(var1))).run();
//    }
//
//    public void cli(){
//        displayCommands();
//    }
//
//    public static void displayCommands() {
//        System.out.println("Liste des commandes disponible :");
//        System.out.println("- START : Initialise le serveur, qui se met en attente.");//Utilisateur
//        //System.out.println("- WHO_ARE_YOU_? : Première commande envoyée par le serveur à un client qui vient de se connecter."); //Automatique
//        //System.out.println("- GIMME_PASSWORD : Demande au client le mot de passe pour se connecter."); //Automatique
//        //System.out.println("- OK : Envoyé en réponse à la commande READY pour informer le client que l’on a bien pris en compte sa participation."); //automatique
//        //System.out.println("- HELLO_YOU : Confirme la validité du mot de passe. Le client est désormais considéré comme connecté."); //automatique
//        //System.out.println("- YOU_DONT_FOOL_ME : Indique au client que le mot de passe est incorrect. La connexion est fermée après cet envoi.");//automatique
//        //System.out.println("- SOLVED : Indique aux workers qu’une solution a été trouvée et qu’ils doivent abandonner le travail en cours.");//automatique
//        System.out.println("- PROGRESS : Demande l’état d’un worker.");//utilisateur
//        System.out.println("- CANCELLED : Indique aux workers qu’ils doivent abandonner le travail en cours. ");//utilisateur
//        System.out.println("- NONCE [start increment] : Indique au client le nonce par lequel il doit débuter sa recherche et l’incrément à ajouter après chaque essai. Les deux valeurs sont des nombres. ");//utilisateur pour l'instant, peut etre API ensuite
//        System.out.println("- PAYLOAD [data] : Indique au client les données qu’il va utiliser pour trouver le hash qui va bien. [data] est une chaîne de caractères utf-8.");//utilisateur pour l'instant, API ensuite surement
//        System.out.println("- SOLVE difficulty : Indique au client la difficulté attendue.");//utilisateur, peut etre API ensuite
//    }
//
//    @Override
//    public void run() {
//        boolean starting = false;
//        displayCommands();
//        Scanner scanner = new Scanner(System.in);
//        while(!starting){
//
//            System.out.print("\nCommande : ");
//            String commandUser = scanner.next();
//            if(commandUser.equalsIgnoreCase("START")) {
//                starting = true;
//            }
//            else if(commandUser.equalsIgnoreCase("INFO")){
//                System.out.println("Write START please <3");
//
//            }
//
//        }
//        scanner.close();
//        try {
//            final InetAddress bindAddress = InetAddress.getByName("0.0.0.0");
//            try(final ServerSocket serverSocket = new ServerSocket(1337, 1, bindAddress)) {
//                System.out.println("Attente de connexion ...");
//                //try{
//                final Socket clientSocket = serverSocket.accept();
//                InputStream messageRecu = clientSocket.getInputStream();//in
//                OutputStream messageAEnvoyer = clientSocket.getOutputStream();//out
//                String response = "WHO_ARE_YOU";
//                messageAEnvoyer.write(response.getBytes(StandardCharsets.UTF_8));
//
//
//                //Recevoir le message
//                byte[] messageRecuDuClient = new byte[80];
//                //L'enregistrer
//                int numRead = messageRecu.read(messageRecuDuClient);
//                String messageClient = new String(messageRecuDuClient, 0, numRead, this.charset);
//                System.out.println("reçu : " + messageClient);
//
//
//                //envoyer la reponse au client
//                messageAEnvoyer.write(messageClient.toUpperCase().getBytes(StandardCharsets.UTF_8));
//
//                clientSocket.close();//on ferme la connexion
//            }
//        } catch(IOException e) {
//            // que faire de mieux ici ?!?
//            e.printStackTrace(System.err);
//        }
//    }
//}

/* //Code CHAT GPT
import java.io.*;
import java.net.*;
import java.util.*;

class Server {
    private static final int PORT = 12345;
    private static final String PASSWORD = "secret";

    private static final String WHO_ARE_YOU = "WHO_ARE_YOU";
    private static final String ITS_ME = "ITS_ME";
    private static final String GIMME_PASSWORD = "GIMME_PASSWORD";
    private static final String PASSWORD_PREFIX = "PASSWORD ";
    private static final String READY = "READY";
    private static final String HELLO_YOU = "HELLO_YOU";
    private static final String PROGRESS = "PROGRESS";

    private static final String OK = "OK";
    private static final String YOU_DONT_FOOL_ME = "YOU_DONT_FOOL_ME";
    private static final String NOPE = "NOPE";

    private List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                output.println(WHO_ARE_YOU);
                String response;
                while ((response = input.readLine()) != null) {
                    switch (response) {
                        case ITS_ME:
                            output.println(GIMME_PASSWORD);
                            break;
                        case READY:
                            output.println(OK);
                            break;
                        case PROGRESS:
                            output.println(NOPE); //A modifier plus tard, deux cas de figures ici au lieu de 1
                            break;
                        default:
                            if (response.startsWith(PASSWORD_PREFIX)) {
                                String password = response.substring(PASSWORD_PREFIX.length());
                                if (password.equals(PASSWORD)) {
                                    output.println(HELLO_YOU);
                                } else {
                                    output.println(YOU_DONT_FOOL_ME);
                                }
                            }
                            break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket);
                clients.remove(this);
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
*/
