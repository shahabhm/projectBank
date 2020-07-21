import java.io.IOException;
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

    public Account(String firstName, String lastName, String userName,
                   String password) throws Exception {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.id = createId();
        accounts.add(this);
        save();
    }

    public static boolean isUsernameUsed(String username) {
        for (Account a : accounts){
            if (a.getUserName().equals(username)) return true;
        }
        return false;
    }

    public static Account getAccByUserName(String userName) {
        for (Account a : accounts){
            if (a.getUserName().equals(userName)){
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

    public String getUserName() {
        return userName;
    }

    public void deposit(int amount) throws Exception {
        money+=amount;
        save();
    }

    public void withdraw(int amount) throws Exception {
        if (money<amount) throw new Exception("source account does not have enough money");
        money-=amount;
        save();
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

    public void save() throws Exception {
        try {
            ObjectSaver.serializeDataOut(this , "acc");
        }catch (Exception e){
            throw new Exception("database error");
        }
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
