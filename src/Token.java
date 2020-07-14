import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.StreamHandler;

public class Token {
    private static final int TOKEN_LENGTH = 5;
    String token;
    Date produce;
    Account account;
    static ArrayList<Token> tokens = new ArrayList<>();
    public Token(Account account) {
        this.token = createToken(TOKEN_LENGTH);
        produce = new Date();
        this.account = account;
        tokens.add(this);
    }

    public static Token getTokenById(String tokenId) {
        for (Token t : tokens){
            if (t.getId().equals(tokenId)) return t;
        }
        return null;
    }

    String getId() {
        return token;
    }

    public boolean isTokenValid(){
        return (new Date().getTime() - produce.getTime())/1000/3600 < 1;
    }
    
    public boolean isTokenUsed(String string){
        for (Token t : tokens){
            if (t.token.equals(string)) return true;
        }
        return false;
    }

    String createToken( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String string = sb.toString();
        if (isTokenUsed(string)) return createToken(len);
        return string;
    }

    public Account getAccount() {
        return account;
    }
}
