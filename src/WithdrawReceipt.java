public class WithdrawReceipt extends Receipt {
    public WithdrawReceipt(Token token,int money, Account source, String description) throws Exception {
        super(token,money, source, null, description);
    }

    public void execute () throws Exception {
        source.withdraw(money);
        this.paid = true;
        this.type = "withdraw";
    }
}