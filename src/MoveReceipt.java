public class MoveReceipt extends Receipt {
    public MoveReceipt(Token token,int money, Account source, Account destination, String description) throws Exception {
        super(token ,money, source, destination, description);
    }

    public void execute() throws Exception {
        source.withdraw(money);
        destination.deposit(money);
        this.paid = true;
    }
}
