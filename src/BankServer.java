import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gilecode.yagson.*;
import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.GsonBuilder;

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
        if (debug)sendToCustomer("hello " + getName().replaceFirst("Thread-","") + "\n");
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
            else if (command.startsWith("get_transactions ")) getTransactions(command);
            else if (command.equals("exit")) exit();
            else throw new InvalidInputException();

        } catch (Exception e) {
            e.printStackTrace();
            sendToCustomer("ERR_"+e.getMessage());
        }
    }

    private void getTransactions(String command) throws Exception {
        Pattern pattern = Pattern.compile("^get_transactions (\\w+) (.+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Account account = getAccountFromToken(matcher.group(1));
        if (matcher.group(2).equals("+")) sendToCustomer(
                convertTransactionsToJson(Receipt.getSelectedReceipts(account,matcher.group(2)))
        );
        else if (matcher.group(2).equals("-"))sendToCustomer(
                convertTransactionsToJson(Receipt.getSelectedReceipts(account, matcher.group(2)))
        );
        else if (matcher.group(2).equals("*"))sendToCustomer(
                convertTransactionsToJson(Receipt.getSelectedReceipts(account, matcher.group(2)))
        );
        else sendToCustomer(convertTransactionsToJson(Receipt.getSelectedReceipts(account,matcher.group(2))));
    }

    private String convertTransactionsToJson(ArrayList<Receipt> receipts){
        String json = "";
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        for (Receipt r : receipts){
            System.out.println(r);
            json = json+"*";
            json = json + gson.toJson(r);
        }
        return json.replaceFirst("\\*$","");
    }

    private String formatTransactionJson(String json){
        json = json.replaceFirst("\\{\"@type\":\"[^\"]+\",\"@val\":" , "");
        json = json.replaceFirst("\\}" , "");
        return json;
    }

    private void getBalance(String command) throws Exception {
        Pattern pattern = Pattern.compile("^get_balance (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Account account = getAccountFromToken (matcher.group(1));
        sendToCustomer(Integer.toString(account.getMoney()));
    }//done

    private void sendToCustomer(String message) {
        formatter.format(message + "\n");
        formatter.flush();
    }

    private void createAccount(String command) throws Exception {
        Pattern pattern = Pattern.compile("^create_account (\\w+) (\\w+) (\\w+) (\\w+) (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        if (Account.isUsernameUsed(matcher.group(3))) throw new Exception("username is not available");
        if (!matcher.group(4).equals(matcher.group(5))) throw new Exception ("passwords do not match");
        Account account = new Account(matcher.group(1),matcher.group(2),
                matcher.group(3),matcher.group(4),matcher.group(5));
        sendToCustomer(account.getId());
    }//done

    private void getToken(String command) throws Exception{
        Pattern pattern = Pattern.compile("^get_token (\\w+) (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        if (!Account.userPassValidation(matcher.group(1),matcher.group(2)))
            throw new Exception("invalid username or password");
        Token t = new Token(Account.getAccByUserName(matcher.group(1)));
        tokenAccountHashMap.put(t,t.getAccount());
        sendToCustomer(t.getId());
    }//done

    private void createReceipt(String command) throws Exception {
        Pattern pattern = Pattern.compile(
                //token type money dest source description
                "^create_receipt (\\w+) (\\w+) (-{0,1}\\d+\\.{0,1}\\d*) (\\S+) (\\S+)(.*)$" //fixme regex
        );
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new Exception("invalid parameters passed");

        String tokenString = matcher.group(1);
        String type = matcher.group(2);
        String moneyString = matcher.group(3);
        String sourceString = matcher.group(4);
        String destString = matcher.group(5);
        String description  = matcher.group(6);
        int money;
        try {
            money = Integer.parseInt(moneyString);
            if (money<1) throw new Exception();
        }catch (Exception e) {throw new Exception("invalid money");}

        Token token = Token.getTokenById(tokenString);
        if (token==null) throw new Exception("token is invalid");
        if (!token.isTokenValid()) throw new Exception("token expired");

        if (Pattern.compile("[^a-zA-Z0-9_ ]").matcher(description).find())
            throw new Exception("your input contains invalid characters");

        Receipt receipt;
        switch (type){
            case "deposit" :{
                if (destString.equals("-1")) throw new Exception("invalid account id");
                if (!sourceString.equals("-1")) throw new Exception("invalid parameters passed");
                receipt = new DepositReceipt(token,money,
                    Account.getAccById(destString),description);
                break;}

            case "withdraw" :{
                    if (sourceString.equals("-1")) throw new Exception("invalid account id");
                    if (!destString.equals("-1")) throw new Exception("invalid parameters passed");
                    Account source = Account.getAccById(sourceString);
                    if (!token.getAccount().equals(source)) throw new Exception("token is invalid");
                receipt = new WithdrawReceipt(token,money,
                    source,description);
                break;}

            case "move" : {
                if (sourceString.equals(destString)) throw new Exception("equal source and dest account");
                if (matcher.group(4).equals("-1")||matcher.group(5).equals("-1")) throw new Exception("invalid account id");
                Account source = Account.getAccById(sourceString);
                if (!token.getAccount().equals(source)) throw new Exception("token is invalid");
                receipt = new MoveReceipt(token,money,Account.getAccById(sourceString),
                        Account.getAccById(destString),description);}
            break;
            default:
                throw new Exception("invalid receipt type");
        }
        sendToCustomer(receipt.getId());
    }

    private void payReceipt(String command) throws Exception {
        Pattern pattern = Pattern.compile("^pay (\\w+)$");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new InvalidInputException();
        Receipt.pay(matcher.group(1));
        sendToCustomer("done successfully");
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