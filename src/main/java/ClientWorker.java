//Code JavaRush https://javarush.com/fr/groups/posts/fr.654.les-classes-socket-et-serversocket-ou--bonjour-serveur--pouvez-vous-mentendre
import java.io.*;
import java.net.Socket;
import java.util.Random;
//import java.nio.charset.StandardCharsets;

public class ClientWorker {
    private boolean mining;
    private boolean nonceReceived;
    private int nonce;
    private int increment;
    private boolean payloadReceived;
    private String payload;
    private boolean difficultyReceived;
    private int difficulty;

    //private static Socket clientSocket; // socket for communication
    //private static BufferedReader reader; // we need a reader that reads from the console, otherwise
    // do we know what the client wants to say?
    //private static BufferedReader in; // socket read stream
    //private static BufferedWriter out; // socket write stream


    public ClientWorker() {
        this.mining = false;
        this.nonceReceived = false;
        this.nonce = 0;
        this.increment = 0;
        this.payloadReceived = false;
        this.payload = "";
        this.difficultyReceived = false;
        this.difficulty = 0;
    }

    /*public ClientWorker(boolean nonceReceived, int nonce, int increment, boolean payloadReceived, String payload, boolean difficultyReceived, int difficulty) {
        this.nonceReceived = nonceReceived;
        this.nonce = nonce;
        this.increment = increment;
        this.payloadReceived = payloadReceived;
        this.payload = payload;
        this.difficultyReceived = difficultyReceived;
        this.difficulty = difficulty;
    }*/

    public static String getPassword() {
        return "password1";
    }



    void setNonceReceived(boolean val) {
        this.nonceReceived = val;
    }

    public boolean isMining() {
        return this.mining;
    }

    public void setMining(boolean mining) {
        this.mining = mining;
    }

    public boolean isNonceReceived() {
        return nonceReceived;
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

    public boolean isPayloadReceived() {
        return payloadReceived;
    }

    public void setPayloadReceived(boolean payloadReceived) {
        this.payloadReceived = payloadReceived;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isDifficultyReceived() {
        return difficultyReceived;
    }

    public void setDifficultyReceived(boolean difficultyReceived) {
        this.difficultyReceived = difficultyReceived;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }


    public String mineNonce(int nonce, String payload, int difficulty) {
        //Faire l'algo de hachage (Maud)
        //pour l'instant je renvoie un truc nul, juste pour pouvoir tester l'appli
//        Random rand = new Random();
//        String res = String.valueOf(nonce);
//        for (int i = 0; i < difficulty; i++) {
//            res = String.valueOf(rand.nextInt(0,10)) + res;
//        }
        return payload;
    }

    public static void main(String[] args) {
        ClientWorker clientWorker = new ClientWorker();
        try {
            Socket socket = new Socket("localhost", 1337);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String serverMessage ="";

            //PHASE 1 : validation client/serveur
            boolean connected = false;//pour passer a la phase 2 -> reception des consignes
            boolean taskComplete = false;//pour passer a la phase 3 -> Hashage
            while ( (!connected) && ((serverMessage = in.readLine()) != null)) {
                System.out.println("Server: " + serverMessage);
                if (serverMessage.equalsIgnoreCase("quit")) {
                    break;
                }
//                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//                System.out.print("Client: ");
                //String clientMessage = "";// = userInput.readLine();
                switch (serverMessage) {
                    case "WHO_ARE_YOU_?":
                        out.println("ITS_ME");
                        break;
                    case "HELLO_YOU":
                        out.println("READY");
                        break;
                    case "GIMME_PASSWORD":
                        out.println("PASSWD "+getPassword());
                        break;
                    case "OK" :
                        connected = true;//on pourra passer à la phase 2
                        out.println("en attente ...");
                    case "YOU_DONT_FOOL_ME":
                        out.println("j'ai reçu you don't fool me");
//                    case "PROGRESS":
//                        if (clientWorker.isMining()) {//Faire la condition qui dit que ça teste
//                            out.println("TESTING");
//                        } else {
//                            out.println("NOPE");
//                        }
//                        break;
                    default:
                        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Client: ");
                        //clientMessage = userInput.readLine();
                        out.println("quit");
//                      if(serverMessage.startsWith("NONCE")){
//                      }
//                        if (serverMessage.startsWith("PAYLOAD")) {
//                            int sizePayload = "PAYLOAD".length();
//                            int sizeMessage = serverMessage.length();
//                            clientWorker.setPayload(serverMessage.substring(sizePayload + 1));
//                            clientWorker.setPayloadReceived(true);
//                            System.out.println("Payload received : " + clientWorker.getPayload());
//                        }
//                        else if(serverMessage.startsWith("NONCE")) {
//                            String[] solveArgs = serverMessage.split("\\s+");
//                            if (solveArgs.length == 3) {
//                                try {
//                                    clientWorker.setNonce(Integer.parseInt(solveArgs[1]));
//                                    clientWorker.setIncrement(Integer.parseInt(solveArgs[2]));
//                                    clientWorker.setNonceReceived(true);
//                                    System.out.println("Nonce set to " + clientWorker.getNonce() + " and increment set to " + clientWorker.getIncrement());
//                                } catch (NumberFormatException e) {
//                                    System.err.println("Invalid NONCE message format");
//                                }
//                            } else {
//                                System.err.println("Invalid NONCE message format");
//                            }
//                        }
//                        else if(serverMessage.startsWith("SOLVE")){
//                            String[] solveArgs = serverMessage.split("\\s+");
//                            if (solveArgs.length == 2) {
//                                try {
//                                    clientWorker.setDifficulty(Integer.parseInt(solveArgs[1]));
//                                    clientWorker.setDifficultyReceived(true);
//                                    System.out.println("Difficulty set to " + clientWorker.getDifficulty());
//                                } catch (NumberFormatException e) {
//                                    System.err.println("Invalid SOLVE message format");
//                                }
//                            } else {
//                                System.err.println("Invalid SOLVE message format");
//                            }
                }
//                if (clientMessage.equalsIgnoreCase("quit")) {
//                    break;
//                }
            }
            System.out.println("On est identifié, on passe à l'etape suivante");
            if(connected){

                while ( !taskComplete && ((serverMessage = in.readLine()) != null)) {
                    System.out.println("Server: " + serverMessage);
                    String[] serverArgs = serverMessage.split("\\s+");
                    if (serverMessage.equalsIgnoreCase("quit")) {
                        break;
                    }
//                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//                System.out.print("Client: ");
                    String clientMessage = "";// = userInput.readLine();

                    if(serverArgs.length>0){
                        switch (serverArgs[0]) {
                            case "SOLVED" :
                                //Abandonner le travail en cours
                                break;
                            case "NONCE" :
                                if (serverArgs.length == 3) {
                                    try {
                                        clientWorker.setNonce(Integer.parseInt(serverArgs[1]));
                                        clientWorker.setIncrement(Integer.parseInt(serverArgs[2]));
                                        clientWorker.setNonceReceived(true);
                                        System.out.println("Nonce set to " + clientWorker.getNonce() + " and increment set to " + clientWorker.getIncrement());
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid NONCE message format");
                                    }
                                }
                                else {
                                    System.err.println("Invalid NONCE message format");
                                }
                                break;
                            case "PAYLOAD":
                                if(serverArgs.length ==2){
                                    try{
                                        clientWorker.setPayload(serverArgs[1]);
                                        clientWorker.setPayloadReceived(true);
                                        System.out.println("Payload set to " + clientWorker.getPayload());
                                    }catch(NumberFormatException e){
                                        System.err.println("Invalid PAYLOAD message format");
                                    }
                                }
                                break;
                            case "SOLVE":
                                if (serverArgs.length == 2) {
                                    try {
                                        clientWorker.setDifficulty(Integer.parseInt(serverArgs[1]));
                                        clientWorker.setDifficultyReceived(true);
                                        System.out.println("Difficulty set to " + clientWorker.getDifficulty());
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid SOLVE message format");
                                    }
                                }
                                else {
                                    System.err.println("Invalid SOLVE message format");
                                }
                                break;
                            case "PROGRESS":
                                if (clientWorker.isMining()) {//Faire la condition qui dit que ça teste
                                    out.println("TESTING");
                                } else {
                                    out.println("NOPE");
                                }
                                break;
                            case "CANCELLED":
                                out.println("on a recu cancelled");
                                //Abandonner le travail en cours
                                break;
                            default:
                                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                                System.out.print("Client: ");
                                clientMessage = userInput.readLine();
                                out.println(clientMessage);
                                break;

                        }
                        if(clientWorker.isDifficultyReceived() && clientWorker.isNonceReceived() && clientWorker.isPayloadReceived()){
                            taskComplete = true; //on peut passer dans la phase 3, celle ou on fait le hachage
                            System.out.println("task Complete = " +taskComplete);
                        }//Dans la phase 3, il y aura deux thread par client worker. Un qui fait l'algo de hachage,
                        // et qui envoie au serveur le resultat quand c'est finito, un qui est en attente des messages du serveur, pour savoir si
                        //il faut continuer la tache ou non.
                        if (clientMessage.equalsIgnoreCase("quit")) {
                            break;
                        }
                    }
                }
            }

            if(taskComplete){
                //Faire la phase 3
                System.out.println("Test TaskComplete : "+clientWorker.mineNonce(clientWorker.getNonce(), clientWorker.getPayload(), clientWorker.getDifficulty()));
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//TODO : - regler l'affichage UTF8
//       - implementer protocole//doriane
//       - plusieurs clients simultané//doriane
//       - protocole minage nonce //Maud ou doriane
//       - tache données par l'API / Communivation avec l'API//olha ou oleksandra
//       - Donner le password de facon sécuriser je sais pas la // Je sais pas
//


