public class MoveReceipt extends Receipt {
    public MoveReceipt(int money, Account source, Account destination, String description) {
        super(money, source, destination, description);
    }

    public void execute(){
        source.withdraw(money);
        destination.deposit(money);
    }
}
