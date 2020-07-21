import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Account implements Saveable, Serializable {
    String firstName;
    String lastName;
    String userName;
    String password;
    String id;
    int money;

    static transient ArrayList<Account> accounts = new ArrayList<>();

    public Account(String firstName, String lastName, String userName, String password, String confirmPass) throws Exception {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.id = createId();
        accounts.add(this);
        ObjectSaver.serializeDataOut(this , "acc");
    }

    public static boolean isUsernameUsed(String username) {
        for (Account a : accounts){
            if (a.getUserName().equals(username)) return true;
        }
        return false;
    }

    public static boolean doesUserExist(String user) {
        return getAccByUserName(user) != null;
    }

    public static boolean doesAccountIdExist(String id){
        for (Account a : accounts){
            if (a.getId().equals(id))return true;
        }
        return false;
    }

    public static boolean checkUserPass(String user , String password){
        if (Account.getAccByUserName(user)==null) return false;
        if (getAccByUserName(user).password.equals(password))return true;
        return false;
    }

    public static Account getAccByUserName(String userName) {
        for (Account a : accounts){
            if (a.getUsername().equals(userName)){
                return a;
            }
        }
        return null;
    }

    public static Account getAccById(String id){
        for (Account a : accounts){
            if (a.getId().equals(id))return a;
        }
        return null;
    }

    public static boolean userPassValidation(String user , String pass){
        if (getAccByUserName(user)==null) return false;
        return getAccByUserName(user).password.equals(pass);
    }

    private String getUsername() {
        return userName;
    }

    public String getUserName() {
        return userName;
    }

    public void deposit(int amount){
        money+=amount;
    }

    public void withdraw(int amount) throws Exception {
        if (money<amount) throw new Exception("source account does not have enough money");
        money-=amount;
    }

    public boolean isIdUsed(String string){
        for (Account a : accounts){
            if (a.getId().equals(string)) return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public int getMoney(){
        return money;
    }

    public static void addAccount (Account account){
        accounts.add(account);
    }

    String createId(){

        String AB = "0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( Bank.ID_LENGTH );
        for(int i = 0; i < Bank.ID_LENGTH; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String string = sb.toString();
        if (isIdUsed(string)) return createId();
        return string;
    }
}
