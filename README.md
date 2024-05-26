## Description
Our project aims to find a nonce that, when concatenated with an initial data, generates a hash starting with a specified number of leading zeros. It consists of two classes:
* Server: waits for clients to connect and assigns them a task, provided by a web application accessible at the address: https://projet-raizo-idmc.netlify.app/
* ClientWorker: once they receive their instructions, the connected clients search for a hash using the process known as Proof Of Work. Once a client finds the desired hash, it sends its response to the server, which validates its response with the web application.

## How to Execute
To run our code, we recommend using a terminal. Our method is as follows:
* Navigate to a terminal in the directory containing the project sources.
* To run the server: **java Serveur.java -d [difficulty] -n [number of clients]**
* To run the client(s): **java ClientWorker.java**

You need a terminal for each client to connect. It is also possible to run the ClientWorker class directly in your IDE, provided you have enabled multiple instances of this class to be executed.
Regarding the server execution:
* The option **-d [difficulty]** represents the difficulty level the clients need to solve.
* The option **-n** represents the number of clients that will be awaited before starting the protocol.
  
For example, if you enter the command **java Serveur.java -d 6 -n 4** in your terminal, the server will wait for 4 clients to connect, and then it will send a task of difficulty 4 to all connected clients.
