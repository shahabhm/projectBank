public class DepositReceipt extends Receipt {

    public DepositReceipt(Token token,int money, Account destination, String description) throws Exception {
        super(token,money, null, destination, description);
    }

    public void execute(){
        destination.deposit(money);
        this.paid=true;
        this.type = "deposit";
    }
}
