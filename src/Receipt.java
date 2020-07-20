import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public abstract class Receipt implements Saveable , Serializable {
    String type;
    private static final int RECEIPT_ID_LENGTH = 2;
    int money;
    String id;
    Account source;
    Account destination;
    String description;
    boolean paid;
    private transient static ArrayList <Receipt> receipts = new ArrayList<>();

    public Receipt(Token token,int money, Account source, Account destination, String description) throws IOException {
        this.money = money;
        this.source = source;
        this.destination = destination;
        this.description = description;
        this.id = createId(RECEIPT_ID_LENGTH);
        this.paid=false;
        receipts.add(this);
        ObjectSaver.serializeDataOut(this , "rec");
    }

    public void execute() throws Exception {

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

    public static void pay(String id) throws Exception {
        Receipt r = getReceiptById(id);
        if (r==null) throw new Exception("invalid receipt id");
        r.execute();
    }

    public static ArrayList<Receipt> getSelectedReceipts(Account account, String type) throws Exception {
        ArrayList<Receipt > selection = new ArrayList<>();
        if (type.equals("+"))for (Receipt r : receipts){
            if (r.destination.equals(account)) selection.add(r);

        }
        else if (type.equals("-")){
            for (Receipt receipt : receipts) {
                if (receipt.source.equals(account)) selection.add(receipt);
            }
        }
        else if (type.equals("*")){
            for (Receipt receipt : receipts) {
                if (receipt.source.equals(account)||receipt.destination.equals(account)){
                    selection.add(receipt);
                }
            }
        }
        else{
            Receipt receipt = getReceiptById(type);
            if (receipt==null) throw new Exception("invalid receipt id");
            if (receipt.destination.equals(account)||
            receipt.source.equals(account));
            else throw new Exception("invalid receipt id");
            selection.add(receipt);
        }
    return selection;
    }

    public static void addReceipt(Receipt r){
        receipts.add(r);
    }

    public String getId() {
        return this.id;
    }
}
