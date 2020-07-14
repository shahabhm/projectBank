import java.security.SecureRandom;
import java.util.ArrayList;

public class Account {
    String firstName;
    String lastName;
    String user;
    String password;
    String id;
    int money;

    static ArrayList<Account> accounts = new ArrayList<>();

    public Account(String firstName, String lastName, String user, String password,String confirmPass) throws Exception {
        if (isUsernameUsed(user)) throw new Exception("username is not available");
        if (!password.equals(confirmPass)) throw new Exception ("passwords do not match");
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = user;
        this.password = password;
        this.id = createId();
        accounts.add(this);
        System.out.println(id);
    }

    public static boolean isUsernameUsed(String username) {
        for (Account a : accounts){
            if (a.getUser().equals(username)) return true;
        }
        return false;
    }

    public static boolean doesAccountExist(String user) {
        return getAccByName(user) != null;
    }

    public static boolean checkUserPass(String user , String password){
        if (Account.getAccByName(user)==null) return false;
        if (getAccByName(user).password.equals(password))return true;
        return false;
    }

    public static Account getAccByName(String user) {
        for (Account a : accounts){
            if (a.getUsername().equals(user)){
                return a;
            }
        }
        return null;
    }

    public static boolean userPassValidation(String user , String pass){
        if (getAccByName(user)==null) return false;
        return getAccByName(user).password.equals(pass);
    }

    private String getUsername() {
        return user;
    }

    public String getUser() {
        return user;
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

    String createId(){
        int len = 5;
        String AB = "0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String string = sb.toString();
        if (isIdUsed(string)) return createId();
        return string;
    }
}
