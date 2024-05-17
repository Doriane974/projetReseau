package fr.ul.miage.projetReseau;

//Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
import java.io.*;
//import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
//import java.net.SocketException;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.Random;
//import java.nio.charset.StandardCharsets;


/**
 * La classe Serveur : ici on parle avec l'API web, on obtiens les taches à toruver, on envoie tout ça aux clients
 * Pour l'instant, le serveur peut se connecter avec un unique client
 */
public class Serveur {

    //private static Socket clientSocket; // socket for communication
    //private static ServerSocket server; // server socket
    //private static BufferedReader in; // socket read stream
    //private static BufferedWriter out; // socket write stream

    private static String payload = ""; //La données que le client doit utiliser pour trouver le hash qui va bien
    private static int nonce = 0;//Le nonce par lequel le client doit debuter sa recherche
    private static int increment = 0;//l'increment a ajouter apres chaque essai
    private static int difficulty = 0;//La difficulté attenue (i.e) le nombre de 0 par lequel le hash gagnant doit debuter
    //public static boolean phase2 = false;

    /*
     * Retrieves the current payload value.
     *
     * @return The current payload value.
     */
    /*public String getPayload() {
        return payload;
    }*/

    /**
     * Sets the payload value.
     *
     * @param PayloadVal The new value for the payload.
     */
    public static void setPayload(String PayloadVal) {
        payload = PayloadVal;
    }

    /*
     * Retrieves the current nonce value.
     *
     * @return The current nonce value.
     */
    /*public int getNonce() {
        return nonce;
    }*/

    /**
     * Sets the nonce value.
     *
     * @param nonceVal The new value for the nonce.
     */
    public static void setNonce(int nonceVal) {
        nonce = nonceVal;
    }

    /*
     * Retrieves the current increment value.
     *
     * @return The current increment value.
     */
    //public int getIncrement() {
    //    return increment;
    //}

    /**
     * Sets the increment value.
     *
     * @param incrementVal The new value for the increment.
     */
    public static void setIncrement(int incrementVal) {
        increment = incrementVal;
    }

    /*
     * Retrieves the current difficulty value.
     *
     * @return The current difficulty value.
     */
    //public int getDifficulty() {
    //    return difficulty;
    //}

    /**
     * Sets the difficulty value.
     *
     * @param difficultyVal The new value for the difficulty.
     */
    public static void setDifficulty(int difficultyVal) {
        difficulty = difficultyVal;
    }


    /**
     * Obtiens les données de l'api web, qu'il faudra ensuite transmettre au client
     * ATTENTION : le prof a dit que le nonce recu de l'api web est bien un nobre :
     *  il faut separer chaque charatere recu, et prendre la valeur correspondante
     *  (modulo 256 ou quelquechose comme ça)
     * Il vaut mieux le faire au moment de cette fonction, c'est mieux pour le rest edu code
     */
    public static void getWork() {
        //données au hasard pour pouvoir tester le code.
        // partie de Olha et Oleksandra
        setPayload("def");
        setNonce(78910);
        setDifficulty(3);
        setIncrement(2);
    }

    /**
     * Fonction permettant d'obtenir le password pour se connecter au serveur.
     * Pour l'instant j'ai fait un truc pourri, je sais pas ce que le prof attend
     * Voila ce qui est dit dans le sujet :  Idéalement, le mot de passe est aléatoire, généré par le serveur au démarrage
     * et fourni aux clients par un canal sécurisé.
     * Je ne sais pas comment faire un canal sécurisé
     * @return String : le mot de passe qui sera reconnu par le serveur
     */
    public static String getPassword() {
        //A mieux faire aussi, juste pour pouvoir tester le reste du code
        return "password1";
    }


    /**
     * Fonction qui doit converser avec l'api Web ( a faire par olha/sasha)
     * Permet de valider un hash qui a été trouvé par un client
     * @param hash hash a valider aupres de l'API web
     * @param difficulty la difficulté demandée pour ce hash
     * @return e hash trouvé par la fonction
     */
    public static boolean validateTheWork(String hash, int difficulty) {
        //TODO : faire un meilleur test peut etre ? (Maud)
        //       ou valider avec l'api web validate work ? (Olha & Sasha)
        String onVeutCeDebut = "";
        for (int i = 0; i < difficulty; i++) {
            onVeutCeDebut = "0" + onVeutCeDebut;
        }
        return hash.startsWith(onVeutCeDebut);
    }


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1337)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        //boolean phase2 = false; //permettra de rentrer dans la phase de recuperage des travaux a faire aupres de l'apiWeb
//        //boolean phase3 = false; // permetre de rentrer dans la phase d'attente de reponse par le client
//        //boolean breakPhase = false;
//        try {
//        ServerSocket serverSocket = new ServerSocket(1337);//on crée un socket, le serveur ecoute sur le port 1337 : il n'acceptera aucune connexion qui ne vient pas sur ce port la
//        System.out.println("Server started. Waiting for client...");
//            while (true) { //boucle infinie , qui contient toute la communication avec le client
//                Socket clientSocket = serverSocket.accept();//on attend qu'un client se connecte, quand c'est le cas on accepte et on peut passer a la suite
//                System.out.println("Client connected: " + clientSocket);
//                try {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//on recevra les message via in
//                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);//on enverra des messages via out
//                    //int i = 0;
//                    //while(true && i<5) {
//                        System.out.println("********************Nouvelle tache **************\n");
//                        out.println("WHO_ARE_YOU_?");//on envoie ça direct, des qu'un client e connecte il recevra WHO_ARE_YOU, pour lancer le protocole
//
//                        String clientMessage;
//
//                        while (((clientMessage = in.readLine()) != null) && !phase2) {
//                            phaseIdentification(out, clientMessage);
//                        }
//                        while ((clientMessage = in.readLine()) != null) { //ici il faudrait que j'arrive a le sortir de la boucle while qui sert vraiment à rien'
//                            phaseReceptionDesConsignes(out);
//                            break;
//                        }
//                        while (((clientMessage = in.readLine()) != null)) {// && !phase3) {
//                            phaseTrouvonsUnHash(out, clientMessage);
//                        }
//                        //i++;
//
//                    //}
//
//                }catch (SocketException e) {
//                    System.err.println("Client connection reset: " + e.getMessage());
//                }
//                System.out.println("Client disconnected: " + clientSocket);
//                clientSocket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /*@Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("WHO_ARE_YOU_?");

                String clientMessage;
                while (((clientMessage = in.readLine()) != null) && !phase2) {
                    phaseIdentification(out, clientMessage);
                }
                while ((clientMessage = in.readLine()) != null) {
                    phaseReceptionDesConsignes(out);
                    break;
                }
                while (((clientMessage = in.readLine()) != null)) {
                    phaseTrouvonsUnHash(out, clientMessage);
                }

            } catch (SocketException e) {
                System.err.println("Client connection reset: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error in client communication: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }*/
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("WHO_ARE_YOU_?");
                String clientResponse = in.readLine();
                System.out.println("Client: " + clientResponse);

                if ("ITS_ME".equals(clientResponse)) {
                    out.println("GIMME_PASSWORD");
                    clientResponse = in.readLine();
                    System.out.println("Client: " + clientResponse);

                    if (clientResponse.startsWith("PASSWD")) {
                        String[] clientParts = clientResponse.split("\\s+");
                        if (clientParts.length == 2 && clientParts[1].equals(getPassword())){
                            out.println("HELLO_YOU");
                        }else{
                            out.println("YOU_DONT_FOOL_ME");
                        }
                        clientResponse = in.readLine();
                        System.out.println("Client: " + clientResponse);

                        if(clientResponse.startsWith("READY")) {
                            out.println("OK");
                        }

                        getWork();
                        //int nonce =
                        out.println("NONCE " + nonce + " "+increment);
                        out.println("PAYLOAD "+payload);
                        out.println("SOLVE "+difficulty);

                        clientResponse = in.readLine();
                        System.out.println("Client: " + clientResponse);

                        if (clientResponse.startsWith("FOUND")) {
                            String[] parts = clientResponse.split(" ");
                            if (parts.length == 3) {
                                String hash = parts[1];
                                int foundNonce = Integer.parseInt(parts[2]);

                                if (validateTheWork(hash, 2)) {
                                    System.out.println("Result for nonce " + foundNonce + ": " + hash + " is valid");
                                } else {
                                    System.out.println("Result for nonce " + foundNonce + ": " + hash + " is invalid");
                                }
                            }
                        }
                    }
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}