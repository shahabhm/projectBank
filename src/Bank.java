import java.io.IOException;
import java.net.ServerSocket;

public class Bank {
    int port;
    boolean debug;
    int connectedClients = 0;

    public Bank(int port, boolean debug) {
        this.port = port;
        this.debug = debug;
    }

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("IP : 127.0.0.1\nPORT : 9090");
        while (true){
            new BankServer(serverSocket.accept(),this , debug).start();
            connectedClients++;
        }
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public void reduceClientCount() {
        connectedClients--;
    }
}
