package core.economy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Material;
import org.bukkit.Server;

import core.*;

import core.bukkit.ItemConstants;

public class Market {
	public static final String DefaultMarketFilename = "market.dat";
	
	private String filename;
	private Kikkit plugin;
	private String currencyName = "kredits";
	private ArrayList<Integer> bannedItemIds = new ArrayList<Integer>();
	
	private ArrayList<MarketedGood> goods = new ArrayList<MarketedGood>();
	
	public Market(Kikkit plugin){
		this(plugin, DefaultMarketFilename);
	}
	
	public Market(Kikkit plugin, String filename){
		this.plugin = plugin;
		
		load(Kikkit.getCurrentWorld().getName() + "/" + filename);
	}
	
	private void load(String filename){
		this.filename = filename; 
		
		GenericConfig gc = plugin.getConfig();
		if(gc != null && gc.hasKey("currencyName"))
			currencyName = gc.getValue("currencyName");
		
		goods.clear();
		
		FileInputStream inputStream;
		InputStreamReader reader;
		
		Scanner scanner = null;
		try{

			inputStream = new FileInputStream(filename);
			reader = new InputStreamReader(inputStream);
			
			scanner = new Scanner(reader);
			
			String data = "";
			while(scanner.hasNextLine()){
				data = scanner.nextLine();
				
				if(data.startsWith("bannedIds=")){
					String[] idSplit = data.substring("bannedIds=".length()).split(",");
					
					bannedItemIds.clear();
					for(String idString : idSplit){
						try{
							int id = Integer.parseInt(idString);
							
							bannedItemIds.add(id);
						}catch(Exception ex){}
					}
					continue;
				}
				
				String[] split = data.split(",");
				
				MarketedGood good = loadGood(data, split);
				
				if(good != null) goods.add(good);
			}
		}
		catch(FileNotFoundException fnfe){
			// First run. Generate and save!
			loadForFirstRun();
			save();
		}
		finally {
			if(scanner != null){
				try { scanner.close(); }
				catch(Exception ex){ }
			}
		}
	}
	
	public void save(){
		FileWriter outputFile;
		
		try {		
			outputFile = new FileWriter(filename, false);
			
			outputFile.write("bannedIds=");
			for(int i = 0; i < bannedItemIds.size(); i++){
				outputFile.write(bannedItemIds.get(i));
				if(i < bannedItemIds.size() - 1) outputFile.write(",");
			}
			outputFile.write("\n");
			
			for(MarketedGood good : goods){
				outputFile.write(saveGood(good) + "\n");
			}
			
			outputFile.close();
		}
		catch (IOException e) {
			Kikkit.MinecraftLog.info(e.getMessage());
		}
	}
	
	public MarketedGood getGoods(String itemName){
		return getGoods(ItemConstants.ConvertToId(itemName));
	}
	
	public MarketedGood getGoods(int id){
		if(bannedItemIds.contains(id)){
			return null;
		}
		
		for(int i = 0; i < goods.size(); i++){
			MarketedGood good = goods.get(i);
			
			if(good.getId() == id) return good;
		}
		
		return null;
	}
	
	private MarketedGood loadGood(String line, String[] data){
		// id,amount,buy,sell
		if(data.length < 4) return null;
		
		int id, amount, sellPrice, buyPrice;
		
		try{
			id = Integer.parseInt(data[0]);
			amount = Integer.parseInt(data[1]);
			buyPrice = Integer.parseInt(data[2]);
			sellPrice = Integer.parseInt(data[3]);
		}
		catch(NumberFormatException nfe){
			Kikkit.MinecraftLog.info("Couldn't load the market good from:");
			Kikkit.MinecraftLog.info(line);
			
			return null;
		}
		
		return new MarketedGood(this, id, buyPrice, sellPrice, amount);
	}
	
	private String saveGood(MarketedGood good){
		return good.getId() + "," + good.getAmount() + "," + good.getBuyPrice() + "," + good.getSellPrice();
	}
	
	private void loadForFirstRun(){
		for(Material material : Material.values()){
			goods.add(
					new MarketedGood(this, material.getId(), 200, 200, 5)
			);
		}
	}
	
	protected Kikkit getPlugin(){ return plugin; }
	protected Server getServer(){ return plugin.getServer(); }
	
	public String getCurrencyName() { return currencyName; }
}
