package auctionhouse;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Lot {
	public final String sellerName;
	public final int number;
	public String description;
	public Money reservePrice;
	public List<String> interested = new ArrayList<String>();
	public HashMap<String, Money> bids = new HashMap<>();
	public String auctioneerAddress;
   	public Money currentBid = new Money("0.0");

	public Lot(String sellerName,
            int number,
            String description,
            Money reservePrice) {
		this.sellerName = sellerName;
		this.number = number;
		this.description = description;
		this.reservePrice = reservePrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Money getReservePrice() {
		return reservePrice;
	}

	public void setReservePrice(Money reservePrice) {
		this.reservePrice = reservePrice;
	}

	public List<String> getInterested() {
		return interested;
	}

	public void setInterested(List<String> interested) {
		this.interested = interested;
	}

	public String getSellerName() {
		return sellerName;
	}

	public int getNumber() {
		return number;
	} 
	
}
