public class WithdrawReceipt extends Receipt {
    public WithdrawReceipt(int money, Account source, String description) {
        super(money, source, null, description);
    }

    public void execute (){
        source.withdraw(money);
    }
}
