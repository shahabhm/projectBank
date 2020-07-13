import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankClient implements Runnable {
    Socket socket;
    InputStream is;
    OutputStream os;
    private Object Bank;

    public BankClient(Socket socket) throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }


    @Override
    public void run() {
        while (true) doCommand();
    }

    private void doCommand() {
        String command = new Scanner(is).nextLine();
        try {
            if (command.startsWith("create_account")) createAccount(command);
            else if (command.startsWith("get_token")) getToken(command);
            else if (command.startsWith("create_receipt")) createReceipt(command);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void createAccount(String command) throws Exception {
        //first last user pass pass
        Pattern pattern = Pattern.compile("^create_account (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+)&");
        Matcher matcher = pattern.matcher(command);
        if (! matcher.find()) throw new InvalidInputException();
        if (!Account.isUsernameAvail(matcher.group(3))) throw new Exception("username is not available");
        if (!matcher.group(4).equals(matcher.group(5))) throw new Exception("passwords do not match");
        new Account (matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4));
    }

    private void getToken(String command) throws Exception{
        Pattern pattern = Pattern.compile("^get_token (\\w+) (\\w+)&");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        if (!Account.isUserPassCorrect(matcher.group(1),matcher.group(2)))
            throw new Exception("invalid username or password");
        Token t = new Token(Account.getAccByName(matcher.group(1)));

    }

    private void createReceipt(String command) throws InvalidInputException {
        Pattern pattern = Pattern.compile("^create_receipt (\\w+) (\\w+) (\\w+) (\\w+) (\\w+)");
        Matcher mathcer = pattern.matcher(command);
        if (!mathcer.find()) throw new InvalidInputException();

    }
}
