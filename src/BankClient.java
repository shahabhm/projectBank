import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;

public class BankClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",9090);
        Scanner keyboardScanner = new Scanner(System.in);
        Formatter formatter = new Formatter(socket.getOutputStream());
        new receiveThread(socket.getInputStream()).start();
        while(true){
            formatter.format(keyboardScanner.nextLine()+"\n");
            formatter.flush();
        }
    }
}

class receiveThread extends Thread{
    Scanner scanner;
    String message;
    public receiveThread(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void run() {
        if ((message = scanner.nextLine()).equals("ERR_"))
            System.err.println(message.replaceFirst("ERR_" , ""));
        else System.out.println(scanner.nextLine());
    }
}
