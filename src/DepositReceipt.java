public class DepositReceipt extends Receipt {

    public DepositReceipt(Token token,int money, Account destination, String description) throws Exception {
        super(token,money, null, destination, description, "deposit");
    }

    public void execute()throws Exception{
        Account.getAccById(destId).deposit(money);
        this.paid=true;
    }
}
