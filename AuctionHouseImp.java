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
    private Parameters parameters;
	
    public HashMap<Integer, Lot> lots = new HashMap<>();
    public HashMap<Integer, CatalogueEntry> catalogues = new HashMap<>();

    private HashMap<String, Buyer> buyers = new HashMap<>();;
    private HashMap<String, Seller> sellers = new HashMap<>();
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
    
    private String errorBanner(String messageName) {
    	return  LS 
    	          + "-------------------------------------------------------------" + LS
    	          + "ERROR: " + messageName + LS
    	          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {
    		this.parameters = parameters;
    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        Buyer buyer = new Buyer(name, address,bankAccount,bankAuthCode);
        if (buyers.containsKey(buyer.name)) {
            logger.fine(errorBanner("Buyer is already registered"));
    			return new Status(Status.Kind.ERROR);
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
        		logger.fine(errorBanner("Seller is already registered"));
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
            logger.fine(errorBanner("Seller isn't registered"));
			return new Status(Status.Kind.ERROR);

        }
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCataloge"));
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
    	        logger.fine(errorBanner("Buyer has already noted interest"));
    			return new Status(Status.Kind.ERROR);
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
    		MessagingService message = parameters.messagingService;
    		if (lots.containsKey(lotNumber)) {
    	        CatalogueEntry entry = catalogues.get(lotNumber);
    	        Lot lot = lots.get(lotNumber);
    	        lot.auctioneerAddress = auctioneerAddress;
    	        if (entry.status == LotStatus.UNSOLD) {
    	        		entry.status = LotStatus.IN_AUCTION;
    	        		catalogues.put(lotNumber, entry);
    	        		String sellerName = lot.sellerName;
    	        		for (Map.Entry<String ,Buyer> buyer : buyers.entrySet()) {
    	        			String buyerName = buyer.getKey();
    	        			if (lot.interested.contains(buyerName)) {
    	        				message.auctionOpened((buyer.getValue()).address, lotNumber);
    	        			}
    	        		}
	        		message.auctionOpened(sellers.get(sellerName).address, lotNumber);
	    	        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
	    	        
	    	        return Status.OK();
    	        }      
    	    		else {
    	    	        logger.fine(errorBanner("Auction for lot "+ lotNumber + " not opened"));
        			return new Status(Status.Kind.ERROR);    			
        		}

    		}
    		else {
    	        logger.fine(errorBanner("Lot doesn't exist"));
    			return new Status(Status.Kind.ERROR);    			
    		}
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
		MessagingService message = parameters.messagingService;
		
    		CatalogueEntry entry = catalogues.get(lotNumber);
	        if (entry.status == LotStatus.IN_AUCTION) {
	            Lot lot = lots.get(lotNumber);
	            String sellerName = lot.sellerName;
	            String auctioneerAddress = lot.auctioneerAddress;
	            if (lot.interested.contains(buyerName)){
	            		Money difference = bid.subtract(lot.currentBid);
	            		if (!difference.lessEqual(parameters.increment) || difference.equals(parameters.increment)) {
	            			lot.bids.put(buyerName, bid);
		            		for (String b : lot.interested) {
		            			if (buyers.containsKey(b) && b != buyerName) {
		            				message.bidAccepted(buyers.get(b).address, lotNumber, bid);
		            			}
		            		}
		            		message.bidAccepted(sellers.get(sellerName).address, lotNumber, bid);
		            		message.bidAccepted(auctioneerAddress, lotNumber, bid);
	            			lot.currentBid = bid;
	            	        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));
	            	        return Status.OK();    
	            		}
	            		else {
	            			logger.fine(errorBanner("Bid is smaller than increment"));
		            		return new Status(Status.Kind.ERROR);
	            		}
	            }
	            else {
        				logger.fine(errorBanner("Buyer has not registered interest or does not exist"));
	            		return new Status(Status.Kind.ERROR);
	            }
	        }
	        else {
    				logger.fine(errorBanner("Lot is not in auction"));
	        		return new Status(Status.Kind.ERROR);
	        }
   
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
    		Money highestBid = new Money("0");
    		String highestBuyer = "";
    		MessagingService message = parameters.messagingService;
    		BankingService bank = parameters.bankingService;
        Status s = new Status(Status.Kind.SALE);
    		CatalogueEntry entry = catalogues.get(lotNumber);
	    		if (entry.status == LotStatus.IN_AUCTION) {
	    			Lot lot = lots.get(lotNumber);
	    			String sellerName = lot.sellerName;
	    			for (Map.Entry<String, Money> bid : lot.bids.entrySet()) {
	    				if (!bid.getValue().lessEqual(highestBid) || bid.getValue().equals(highestBid)) {
	    					highestBid = bid.getValue();
	    					highestBuyer = bid.getKey();
	    				}
	    			}
	    			if (highestBid.lessEqual(lot.reservePrice) && highestBid.compareTo(lot.reservePrice) != 0) {
	    				entry.status = LotStatus.UNSOLD;
	    				for (Map.Entry<String ,Buyer> buyer : buyers.entrySet()) {
	    	        			String buyerName = buyer.getKey();
	    	        			if (lot.interested.contains(buyerName)) {
	    	        				message.lotUnsold((buyer.getValue()).address, lotNumber);
	    	        			}
	    	        		}
	            		message.lotUnsold(sellers.get(sellerName).address, lotNumber);
	    				s = new Status(Status.Kind.NO_SALE);
	    			}
	    			else {
	    				Status transferStatus2 = bank.transfer(
	    						parameters.houseBankAccount, 
	    						parameters.houseBankAuthCode, 
	    						sellers.get(sellerName).bankAccount, 
	    						highestBid.subtract(new Money(parameters.commission+"")));
	    				
	    				Status transferStatus = bank.transfer(
	    						buyers.get(highestBuyer).bankAccount, 
	    						buyers.get(highestBuyer).bankAuthCode, 
	    						parameters.houseBankAccount, 
	    						highestBid.add(new Money(parameters.buyerPremium+"")));
	    		

	    				if (transferStatus.kind.equals(Status.OK().kind)&&transferStatus2.kind.equals(Status.OK().kind)) {
	    					entry.status = LotStatus.SOLD;
	    					for (Map.Entry<String ,Buyer> buyer : buyers.entrySet()) {
		    	        			String buyerName = buyer.getKey();
		    	        			if (lot.interested.contains(buyerName)) {
		    	        				message.lotSold((buyer.getValue()).address, lotNumber);

		    	        			}
		    	        		}
		            		message.lotSold(sellers.get(sellerName).address, lotNumber);
	    				}
	    				else {
	    					entry.status = LotStatus.SOLD_PENDING_PAYMENT;
	    				}
	    			}
	    		}

        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        return s;
    }
}
