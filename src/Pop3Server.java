import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Pop3Server {
    public static void main(String[] args) {
        final int port = 110; // Define el puerto estándar POP3 para escuchar conexiones entrantes.

        // Intenta iniciar el servidor en el puerto especificado y entra en un bucle para aceptar conexiones.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor POP3 iniciado en el puerto " + port);

            // Bucle infinito para esperar y gestionar conexiones de clientes.
            while (true) {
                try {
                    // Acepta una conexión entrante y crea un socket para el cliente.
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                    // Inicia un nuevo hilo para manejar la sesión del cliente, permitiendo múltiples clientes simultáneos.
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    System.err.println("Error al aceptar la conexión del cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto " + port + ": " + e.getMessage());
        }
    }

    // Clase para manejar individualmente cada conexión de cliente.
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                // Envía un mensaje de bienvenida al cliente.
                out.write("+OK Servidor POP3 listo\r\n");
                out.flush();

                // Bucle para leer y responder a los comandos del cliente.
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Comando recibido: " + line);

                    // Finaliza la sesión si se recibe el comando "EXIT".
                    if ("EXIT".equalsIgnoreCase(line)) {
                        out.write("+OK Hasta luego\r\n");
                        out.flush();
                        break;
                    } else {
                        // Responde afirmativamente a otros comandos.
                        out.write("+OK\r\n");
                        out.flush();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
            } finally {
                // Asegura que la conexión del cliente se cierra adecuadamente.
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar la conexión del cliente: " + e.getMessage());
                }
            }
        }
    }
}
// Para conectar: telnet localhost 110
