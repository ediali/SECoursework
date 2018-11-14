/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {

    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    
    public static HashMap<Integer, Lot> lots = new HashMap<>();
    public static HashMap<Integer, CatalogueEntry> catalogues = new HashMap<>();

    private HashMap<String, Buyer> buyers = new HashMap<>();;
    private HashMap<String, Seller> sellers = new HashMap<>();
    
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {

    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        Buyer buyer = new Buyer(name, address,bankAccount,bankAuthCode);
        if (buyers.containsValue(buyer)) {
    			return Status.error("Buyer is already registered");
        }
        else {
            buyers.put(name, buyer);
            return Status.OK();
        }
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        Seller seller = new Seller(name, address,bankAccount);
        if (sellers.containsKey(name)){
        		return new Status(Status.Kind.ERROR);
        }
        else {
            sellers.put(name, seller);
            return Status.OK();         	
        }     
    }

    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        if (sellers.containsKey(sellerName)) {
            Lot lot = new Lot(sellerName, number, description, reservePrice);
            CatalogueEntry entry = new CatalogueEntry(number, description, LotStatus.UNSOLD);
            catalogues.put(number,entry);
            lots.put(number, lot);
            return Status.OK();  
        }
        else {
            return Status.error("Seller is not registered");
        }

  
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        int[] numbers = new int[catalogues.size()];
        int i = 0;
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        
        for (Map.Entry<Integer, CatalogueEntry> entry : catalogues.entrySet()) {
        		numbers[i] = entry.getKey();
        		i++;
        }
        Arrays.sort(numbers);
        
        for (int j = 0; j < numbers.length; j++) {
        		catalogue.add(catalogues.get(numbers[j]));
        }
        
        logger.fine("Catalogue: " + catalogue.toString());
        return catalogue;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        Lot lot = lots.get(lotNumber);
        if (lot.interested.contains(buyerName)) {
    			return Status.error("Buyer has already noted interest");
        }
        else {
        		lot.interested.add(buyerName);
        		return Status.OK();
        }     
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
    		if (lots.containsKey(lotNumber)) {
    	        CatalogueEntry entry = catalogues.get(lotNumber);
    	        if (entry.status == LotStatus.UNSOLD) {
    	        		entry.status = LotStatus.IN_AUCTION;
    	        		catalogues.put(lotNumber, entry);
    	        }
    	        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
    	        return Status.OK();
    		}
    		else {
    			return Status.error("Lot does not exist");
    		}
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
    		CatalogueEntry entry = catalogues.get(lotNumber);
	        if (entry.status == LotStatus.IN_AUCTION) {
	            Lot lot = lots.get(lotNumber);
	            if (lot.interested.contains(buyerName)){
	            		lot.bids.put(buyerName, bid);
	            }
	        }
    	
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        return Status.OK();    
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
    		Money highestBid = new Money("0");
    		String highestBuyer = "";
        Status s = new Status(Status.Kind.SALE);
    		CatalogueEntry entry = catalogues.get(lotNumber);
	    		if (entry.status == LotStatus.IN_AUCTION) {
	    			Lot lot = lots.get(lotNumber);
	    			for (Map.Entry<String, Money> bid : lot.bids.entrySet()) {
	    				if (!bid.getValue().lessEqual(highestBid)) {
	    					highestBid = bid.getValue();
	    					highestBuyer = bid.getKey();
	    				}
	    			}
	    			if (highestBid.lessEqual(lot.reservePrice)) {
	    				entry.status = LotStatus.UNSOLD;
	    				s = new Status(Status.Kind.NO_SALE);
	    			}
	    			else {
	    				MockBankingService bank = new MockBankingService();
	    				Status transferStatus = bank.transfer(
	    						buyers.get(highestBuyer).address, 
	    						buyers.get(highestBuyer).bankAuthCode, 
	    						sellers.get(lot.getSellerName()).address, 
	    						highestBid);
	    				if (transferStatus == Status.OK()) {
	    					entry.status = LotStatus.SOLD;
	    				}
	    				else {
	    					entry.status = LotStatus.SOLD_PENDING_PAYMENT;
	    					s = new Status(Status.Kind.SALE_PENDING_PAYMENT);
	    				}
	    			}
	    		}

        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        return s;
    }
}
