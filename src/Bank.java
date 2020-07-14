import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Bank {
    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        while (true){
            new BankClient(serverSocket.accept()).start();
        }
    }
}
