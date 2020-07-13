public class DepositReceipt extends Receipt {

    public DepositReceipt(int money, Account destination, String description) {
        super(money, null, destination, description);
    }

    public void execute(){
        destination.deposit(money);
    }
}
