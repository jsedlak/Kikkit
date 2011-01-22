package core.economy;

// EVERYTHING IS FROM THE PLAYER'S PERSPECTIVE
// SELL = player to market
// BUY  = market to player
public class MarketedGood {
	private int itemId;
	private int currentBuyPrice = 2;
	private int currentSellPrice = 1;
	private long amountInBin = 0;
	
	private Market market;
	
	public MarketedGood(Market parentMarket, int id, int buyPrice, int sellPrice, int amount){
		itemId = id;
		currentBuyPrice = buyPrice;
		currentSellPrice = sellPrice;
		amountInBin = amount;
		
		market = parentMarket;
	}
	
	public int sell(int amount){
		return sell(getSellPrice(), amount);
	}
	
	public int sell(int price, int amount){
		if(!willSellAt(price)){
			return 0;
		}
		
		amountInBin += amount;
		
		currentSellPrice -= 2;
		currentBuyPrice -= 1;
		if(currentSellPrice <= 0) currentSellPrice = 1;
		if(currentBuyPrice <= 0) currentBuyPrice = 1;
		
		getMarket().save();
		
		return amount;
	}
	
	public int buy(int amount){
		return buy(getBuyPrice(), amount);
	}
	
	public int buy(int price, int amount){
		if(!willBuyAt(price)){
			return 0;
		}
		
		int sold = amount;
		
		if(amount > amountInBin){
			sold += (amountInBin - amount);
		}
		
		amountInBin -= sold;
		
		currentSellPrice += 1;
		currentBuyPrice += 3;
		
		getMarket().save();
		
		return sold;
	}
	
	public boolean willBuyAt(int price){
		return price <= getBuyPrice();
	}
	
	public boolean willSellAt(int price){
		return price >= getSellPrice();
	}
	
	public int getId() { return itemId; }
	public int getBuyPrice() { return currentBuyPrice; }
	public int getSellPrice() { return currentSellPrice; }
	public long getAmount() { return amountInBin; }
	public Market getMarket() { return market; }
}
