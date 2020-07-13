import java.util.ArrayList;

public class Account {
    String firstName;
    String lastName;
    String user;
    String password;
    Token token;

    static ArrayList<Account> accounts = new ArrayList<>();
    public Account(String firstName, String lastName, String user, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = user;
        this.password = password;
        accounts.add(this);
    }


    public static boolean isUsernameAvail(String username) {
        for (Account a : accounts){
            if (a.getUser().equals(username)) return true;
        }
        return false;
    }

    public void setToken(Token token){
        this.token = token;
    }

    public String getUser() {
        return user;
    }
}
