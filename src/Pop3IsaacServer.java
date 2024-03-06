import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AuxiliaryAgent extends Thread {

    private static final Set<AuxiliaryAgent> clients = Collections.synchronizedSet(new HashSet<>());
    private DataOutputStream toClient;
    private BufferedReader fromClient;
    private Socket clientSocket;
    private String clientName;

    private String[] credential;

    private String[] credential1;

    private String message;

    private String message1;

    private String message2;

    String password = "1234";

    private List<String> data;
    private String USER;

    private String PASS;

    public AuxiliaryAgent(Socket client) {
        this.clientSocket = client;
        start(); // Inicia el hilo al crear la instancia
    }

    @Override
    public void run() {
        try {
            clients.add(this); // Añade este cliente a la lista al iniciar
            Initialize();
            while (true) {
                Authentication();
                Operations();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                clients.remove(this); // Remueve el cliente de la lista al finalizar
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    // Método para enviar mensajes a todos los clientes
    private static void broadcastMessage(String message) {
        synchronized (clients) { // Asegura la sincronización al iterar
            for (AuxiliaryAgent client : clients) {
                try {
                    client.toClient.writeBytes(message + "\n");
                } catch (IOException e) {
                    System.out.println("Error broadcasting: " + e.getMessage());
                }
            }
        }
    }

    public void Initialize() throws IOException {
        toClient = new DataOutputStream(clientSocket.getOutputStream());
        fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        toClient.writeBytes("Server POP3 is active\n\n");
    }

    public void Authentication() throws IOException {
        this.message = fromClient.readLine();
        this.credential = this.message.split(" ");
        if (this.credential[0].contains("USER")) {
            USER = this.credential[1];
            if (!USER.isEmpty()) {
                // Asigna el nombre del usuario a clientName después de verificar que es uno de los usuarios válidos
                this.clientName = USER; // Esta línea asigna el valor a clientName

                toClient = new DataOutputStream(clientSocket.getOutputStream());
                toClient.writeBytes("OK\n");

                this.message1 = fromClient.readLine();
                this.credential1 = this.message1.split(" ");
                if (this.credential1[0].contains("PASS")) {
                    PASS = this.credential1[1];
                    if (PASS.equals(password)) {
                        toClient = new DataOutputStream(clientSocket.getOutputStream());
                        toClient.writeBytes("OK\n");
                    } else {
                        // Considera añadir lógica aquí para manejar la autenticación fallida
                    }
                }
            } else {
                // Considera añadir una respuesta para usuarios no reconocidos
            }
        } else {
            // Considera manejar comandos que no sean USER adecuadamente
        }
    }

    public void Operations() throws IOException {
        System.out.println("Bienvenido: " + this.clientName); // Asumiendo que 'clientName' guarda el nombre del cliente
        // No es necesario volver a inicializar 'toClient' aquí, ya se inicializó en 'Initialize'

        while (true) {
            this.message2 = fromClient.readLine();
            if (this.message2.equals("EXIT")) {
                broadcastMessage("Hasta luego " + this.clientName + " :)"); // Avisa a todos de la desconexión
                return; // Sale del bucle para terminar el hilo
            } else {
                broadcastMessage(this.clientName + ": " + this.message2); // Envía el mensaje a todos los clientes
            }
        }
    }
}

public class Pop3IsaacServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(110)) {
            System.out.println("Server is running...");
            while (true) {
                Socket clientConnection = serverSocket.accept();
                System.out.println("Client connected.");
                new AuxiliaryAgent(clientConnection);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}