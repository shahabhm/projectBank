import com.gilecode.yagson.com.google.gson.annotations.Expose;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public abstract class Receipt implements Saveable , Serializable {
    String type;
    int money;
    String id;
    String sourceId,destId;
    String description;
    boolean paid;
    private transient static ArrayList <Receipt> receipts = new ArrayList<>();


    public Receipt(Token token,int money, Account source, Account destination
            , String description, String type) throws Exception {
        this.money = money;
        if (source==null)this.sourceId = "-1";
        else this.sourceId = source.getId();
        if (destination==null) destId = "-1";
        else destId = destination.getId();
        this.description = description;
        this.id = createId(Bank.ID_LENGTH);
        this.paid=false;
        this.type = type;
        receipts.add(this);
        save();
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

    public static void pay(String id) throws Exception {
        Receipt r = getReceiptById(id);
        if (r==null) throw new Exception("invalid receipt id");
        if (r.paid) throw new Exception("receipt is paid before");
        r.execute();
        r.save();
    }

    public static ArrayList<Receipt> getSelectedReceipts(Account account, String type) throws Exception {
        ArrayList<Receipt > selection = new ArrayList<>();
        if (type.equals("+"))for (Receipt r : receipts){
            if (account.equals(Account.getAccById(r.destId))) selection.add(r);
        }
        else if (type.equals("-")){
            for (Receipt receipt : receipts) {
                if (account.equals(Account.getAccById(receipt.sourceId))) selection.add(receipt);
            }
        }
        else if (type.equals("*")){
            for (Receipt receipt : receipts) {
                if (account.equals(Account.getAccById(receipt.sourceId))||account.equals
                        (Account.getAccById(receipt.destId))){
                    selection.add(receipt);
                }
            }
        }
        else{
            Receipt receipt = getReceiptById(type);
            if (receipt==null) throw new Exception("invalid receipt id");
            if (account.equals(Account.getAccById(receipt.destId))||
            account.equals(Account.getAccById(receipt.sourceId)));
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

    private void save() throws Exception {
        try {ObjectSaver.serializeDataOut(this,"rec");}catch (Exception e){
            throw new Exception("database error");
        }
    }

}
