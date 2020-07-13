import java.security.SecureRandom;
import java.util.ArrayList;

public class Receipt {
    private static final int RECEIPT_ID_LENGTH = 5;
    int money;
    String id;
    Account source;
    Account destination;
    String description;
    private static ArrayList <Receipt> receipts = new ArrayList<>();
    private boolean paid;

    public Receipt(int money, Account source, Account destination, String description){
        this.money = money;
        this.source = source;
        this.destination = destination;
        this.description = description;
        this.id = createId(RECEIPT_ID_LENGTH);
        this.paid=false;
    }

    public void execute(){

    }

    String createId( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String string = sb.toString();
        if (doesReceiptExist(string)) return createId(len);
        return string;
    }

    static boolean doesReceiptExist(String string) {
        for (Receipt receipt : receipts){
            if (receipt.getId().equals(string)){
                return true;
            }
        }
        return false;
    }

    private static Receipt getReceiptById(String id){
        for (Receipt r : receipts){
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public void payReceipt(String id) throws Exception {
        Receipt receipt = getReceiptById(id);
        if (receipt.paid) throw new Exception("receipt is paid before");
        receipt.execute();
        paid = true;
    }

    public static void pay(String id){
        getReceiptById(id).execute();
    }

    private String getId() {
        return this.id;
    }
}
