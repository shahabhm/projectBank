public class WithdrawReceipt extends Receipt {
    public WithdrawReceipt(Token token,int money, Account source, String description) throws Exception {
        super(token,money, source, null, description , "withdraw");
    }

    public void execute () throws Exception {
        Account.getAccById(sourceId).withdraw(money);
        this.paid = true;
    }
}