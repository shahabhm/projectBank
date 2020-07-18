import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankServer extends Thread {
    private static HashMap<Token,Account> tokenAccountHashMap = new HashMap<>();
    Socket socket;
    InputStream is;
    OutputStream os;
    private Bank bank;
    boolean debug;
    Scanner scanner;
    Formatter formatter;
    public BankServer(Socket socket, Bank bank , boolean debug) throws IOException {
        this.socket = socket;
        os = socket.getOutputStream();
        is = socket.getInputStream();
        scanner = new Scanner(is);
        formatter = new Formatter(os);
        this.bank = bank;
        this.debug = debug;
    }

    @Override
    public void run() {
        formatter.format("hello " + getName().replaceFirst("Thread-","") + "\n");
        formatter.flush();
        while (true) { try { doCommand(); } catch (Exception e) { break; } }
    }

    private void doCommand() throws Exception {
        String command = scanner.nextLine();
        try {
            debugPrint("client "+this.getName().replaceFirst("Thread-","")+
                    "  :  " + command);
            if (command.startsWith("create_account ")) createAccount(command);
            else if (command.startsWith("get_token ")) getToken(command);
            else if (command.startsWith("create_receipt ")) createReceipt(command);
            else if (command.startsWith("pay ")) payReceipt(command);
            else if (command.startsWith("get_balance ")) getBalance(command);
            else if (command.equals("exit")) exit();
            else throw new InvalidInputException();

        } catch (Exception e) {
            if (e instanceof IOException) System.out.println("close error");
            else System.err.println(e.getMessage());
        }
    }

    private void getBalance(String command) throws Exception {
        Pattern pattern = Pattern.compile("^get_balance (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Account account = getAccountFromToken (matcher.group(1));
        System.out.println(account.getMoney());
    }//done

    private void createAccount(String command) throws Exception {
        Pattern pattern = Pattern.compile("^create_account (\\w+) (\\w+) (\\w+) (\\w+) (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Account.createAccount(matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
    }//done

    private void getToken(String command) throws Exception{
        Pattern pattern = Pattern.compile("^get_token (\\w+) (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        if (!Account.userPassValidation(matcher.group(1),matcher.group(2)))
            throw new Exception("invalid username or password");
        Token t = new Token(Account.getAccByName(matcher.group(1)));
        tokenAccountHashMap.put(t,t.getAccount());
        System.out.println(t.getId());
    }//done

    private void createReceipt(String command) throws Exception {
        Pattern pattern = Pattern.compile(
                //token type money dest source description
                "^create_receipt (\\w+) (\\w+) (-{0,1}\\d+\\.{0,1}\\d*) (\\S+) (\\S+)(\\.*)" //fixme regex
        );
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new Exception("invalid parameters passed");
        if (!tokenAccountHashMap.containsKey(Token.getTokenById(matcher.group(1))))
            throw new Exception("token is invalid");
        Token token = Token.getTokenById(matcher.group(1));
        if (!token.isTokenValid()) throw new Exception("token expired");
        checkCreateReceipt(command);
        switch (matcher.group(2)){
            case "deposit" :{
                if (matcher.group(5).equals("-1")) throw new Exception("invalid account id");
                new DepositReceipt(token,Integer.parseInt(matcher.group(3)),
                    Account.getAccByName(matcher.group(5)),matcher.group(6));
                break;}

            case "withdraw" :{
                    if (matcher.group(4).equals("-1")) throw new Exception("invalid account id");
                    new WithdrawReceipt(token,Integer.parseInt(matcher.group(3)),
                    Account.getAccByName(matcher.group(4)),matcher.group(6));
                break;}

            case "move" : {
                if (matcher.group(4).equals("-1")||matcher.group(5).equals("-1")) throw new Exception("invalid account id");
                new MoveReceipt(token,Integer.parseInt(matcher.group(3)),Account.getAccByName(matcher.group(4)),
                        Account.getAccByName(matcher.group(5)),matcher.group(6));}
        }
    }

//token type money sor des desc
    private void checkCreateReceipt(String command) throws Exception {
        Pattern pattern = Pattern.compile(
                "^create_receipt (\\w+) (\\w+) (-{0,1}\\d+\\.{0,1}\\d*) (\\S+) (\\S+)(\\.*)$" //fixme regex
        );
        Matcher matcher = pattern.matcher(command);
        matcher.find();

        if (!(matcher.group(2).equals("withdraw")||
                matcher.group(2).equals("deposit")||
                matcher.group(2).equals("move"))) throw new Exception("invalid receipt type");

        try {
            int x = Integer.parseInt(matcher.group(3));
            if (x<1) throw new Exception();
        }catch (Exception e){
            throw new Exception("invalid money");
        }
        if (matcher.group(4).equals("-1"));
        else if (!Account.deosAccountIdExist(matcher.group(4))) throw new Exception("source account id is invalid");

        if (matcher.group(5).equals("-1"));
        else if (!Account.deosAccountIdExist(matcher.group(5))) throw new Exception("dest account id is invalid");

        if (matcher.group(5).equals(matcher.group(4))) throw new Exception ("equal source and dest account");

        if (!matcher.group(6).matches("^\\w*$"))throw new Exception("your input contains invalid characters");
    }

    private void payReceipt(String command) throws Exception {
        Pattern pattern = Pattern.compile("^pay (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Receipt.pay(matcher.group(1));
    }//done

    private Account getAccountFromToken(String tokenId) throws Exception {
        Token token = Token.getTokenById (tokenId);
        if (token==null) throw new Exception("token is invalid");
        if (!token.isTokenValid()) throw new Exception("token expired");
        return tokenAccountHashMap.get(token);
    }//done

    private void exit() throws IOException {
        socket.close();
        bank.reduceClientCount();
        debugPrint("connected Clients : " +Integer.toString( bank.getConnectedClients()));
    }

    private void debugPrint(String text){
        if (debug){
            System.out.println(text);
        }
    }
}