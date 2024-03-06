import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Pop3Client {

    public static void main(String[] args) {
        String serverAddress = "192.168.1.77"; // Dirección del servidor
        int serverPort = 110; // Puerto del servidor
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // Imprimir mensaje de bienvenida del servidor
            System.out.println("Servidor dice: " + fromServer.readLine());

            // Autenticación
            System.out.print("Introduce tu nombre de usuario: ");
            String userName = scanner.nextLine();
            toServer.writeBytes("USER " + userName + "\n");

            System.out.print("Introduce tu contraseña: ");
            String password = scanner.nextLine();
            toServer.writeBytes("PASS " + password + "\n");

            // Crear un hilo para leer mensajes del servidor
            Thread readMessages = new Thread(() -> {
                try {
                    String messageFromServer;
                    while ((messageFromServer = fromServer.readLine()) != null) {
                        System.out.println(messageFromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Se perdió la conexión con el servidor.");
                }
            });

            readMessages.start();

            // Bucle para enviar mensajes al servidor
            System.out.println("Escribe tus mensajes (escribe 'EXIT' para salir):");
            while (true) {
                String messageToSend = scanner.nextLine();
                toServer.writeBytes(messageToSend + "\n");
                if ("EXIT".equalsIgnoreCase(messageToSend)) {
                    break; // Salir del bucle si el usuario escribe 'EXIT'
                }
            }

            readMessages.join(); // Esperar a que el hilo de lectura termine

        } catch (IOException | InterruptedException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
