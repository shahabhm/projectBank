import java.security.SecureRandom;
import java.util.ArrayList;

public class Receipt {
    int amount;
    String id;

    private static ArrayList <Receipt> receipts = new ArrayList<>();





    String createId( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String string = sb.toString();
        if (isIdUsed(string)) return createId(len);
        return string;
    }

    private boolean isIdUsed(String string) {
        for (Receipt receipt : receipts){
            if (receipt.getId().equals(string)){
                return true;
            }
        }
        return false;
    }

    private String getId() {
        return this.id;
    }
}
