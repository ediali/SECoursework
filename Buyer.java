package auctionhouse;

public class Buyer {
	public final String name;
	public final String address;
	public final String bankAccount;
	public final String bankAuthCode;
	
	public Buyer(String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
		this.name = name;
		this.address = address;
		this.bankAccount = bankAccount;
		this.bankAuthCode = bankAuthCode;
	}
	
}
