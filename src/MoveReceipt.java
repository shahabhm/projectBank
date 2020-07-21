public class MoveReceipt extends Receipt {
    public MoveReceipt(Token token,int money, Account source, Account destination, String description) throws Exception {
        super(token ,money, source, destination, description, "move");
    }

    public void execute() throws Exception {
        Account.getAccById(sourceId).withdraw(money);
        Account.getAccById(destId).deposit(money);
        this.paid = true;
    }
}
