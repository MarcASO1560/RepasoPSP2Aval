import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SmtpServer {
    public static void main(String[] args) {
        final int port = 2525; // Puerto SMTP estándar

        // Intenta iniciar el servidor en el puerto especificado y entra en un bucle para aceptar conexiones de clientes.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor SMTP iniciado en el puerto " + port);

            // Bucle infinito para aceptar conexiones de clientes. El servidor permanecerá activo y esperando conexiones entrantes.
            while (true) {
                try {
                    // Acepta una conexión entrante del cliente.
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                    // Establece flujos de entrada y salida para comunicarse con el cliente.
                    try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                        // Enviar el saludo del servidor al cliente indicando que el servidor SMTP está listo.
                        out.println("220 mi-servidor-smtp.com SMTP Server");

                        String line;
                        // Bucle para leer los comandos enviados por el cliente y procesarlos.
                        while ((line = in.readLine()) != null) {
                            System.out.println("Comando recibido: " + line);

                            // Procesa el comando HELLO.
                            if (line.toUpperCase().startsWith("HELLO")) {
                                // Verifica si el comando viene con un argumento adicional y responde adecuadamente.
                                String responseText = line.length() > 5 ? line.substring(5).trim() : "cliente";
                                out.println("250 Hello " + responseText + ", pleased to meet you");
                            }
                            // Permite al cliente terminar la conexión mediante el comando "QUIT".
                            else if (line.toUpperCase().startsWith("EXIT")) {
                                out.println("221 SMTP-SERVER closing connection");
                                break; // Salir del bucle para cerrar la conexión con este cliente.
                            } else {
                                // Responde con un genérico "250 OK" para cualquier otro comando recibido.
                                out.println("250 OK");
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error al crear flujos de entrada/salida: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Error al aceptar conexión de cliente: " + e.getMessage());
                    // No se cierra el servidor en este punto; se intenta aceptar la siguiente conexión.
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto " + port + ": " + e.getMessage());
            // Error al iniciar el servidor, probablemente debido a un problema con el puerto especificado.
        }
    }
}
//Conexión mediante TELNET: telnet localhost 2525