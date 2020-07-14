import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankClient extends Thread {
    HashMap<Token,Account> tokenAccountHashMap = new HashMap<>();
    Socket socket;
    InputStream is;
    OutputStream os;
    private Object Bank;

    public BankClient(Socket socket) throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
        System.out.println(this);
    }


    @Override
    public void run() {
        while (true) doCommand();
    }

    private void doCommand() {
        String command = new Scanner(is).nextLine();
        try {
            if (command.startsWith("create_account ")) createAccount(command);
            else if (command.startsWith("get_token ")) getToken(command);
            else if (command.startsWith("create_receipt ")) createReceipt(command);
            else if (command.startsWith("pay ")) payReceipt(command);
            else if (command.startsWith("get_balance ")) getBalance(command);
            else throw new InvalidInputException();
        } catch (Exception e) {
            System.err.println(e.getMessage());
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
        new Account (matcher.group(1),matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(5));
    }//done just need to change the
    // way of returning the id

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
                "^create_receipt (\\w+) (\\w+) (-{0,1}\\d+\\.{0,1}\\d*$) (\\S+) (\\S+)(\\.*)" //fixme regex
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
                "^create_receipt (\\w+) (\\w+) (-{0,1}\\d+\\.{0,1}\\d*$) (\\S+) (\\S+)(\\.*)$" //fixme regex
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

        if (!Account.doesAccountExist(matcher.group(4))) throw new Exception("source account id is invalid");

        if (!Account.doesAccountExist(matcher.group(5))) throw new Exception("dest account id is invalid");

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
}