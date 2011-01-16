package core;
import java.util.*;


@SuppressWarnings("deprecation")
public class TemporaryWhitelist extends Whitelist {
	public class WhiteoutPeriod {
		public Date Start;
		public Date End;
		
		public WhiteoutPeriod(Date start, Date end){
			Start = start;
			Start.setSeconds(0);
			
			End = end;
			End.setSeconds(59);
		}
		
		public boolean isInPeriod(Date dateToCheck){
			
			return compare(dateToCheck, Start) >= 0 && compare(dateToCheck, End) <= 0;
		}
		
		private int compare(Date d1, Date d2){
			
			Date left = (Date)d1.clone();
			left.setYear(2010);
			left.setMonth(1);
			left.setDate(0);
			
			Date right = (Date)d2.clone();
			right.setYear(2010);
			right.setMonth(1);
			right.setDate(0);
			
			int returnValue = left.compareTo(right);
			
			return returnValue;
		}
	}
	
	private ArrayList<WhiteoutPeriod> periods = new ArrayList<WhiteoutPeriod>();
	
	private WhiteoutPeriod currentPeriod = null;
	private boolean isEnabled;
	
	public TemporaryWhitelist(String filename, GenericConfig config, String prefix){
		super(filename);
		
		if(!config.hasKey(prefix.concat("whiteouts"))){
			isEnabled = false;
			setIsOverriden(true);
			Kikkit.MinecraftLog.info("Couldn't find key: " + prefix.concat("whiteouts"));
		}
		else{
			String[] data = config.getValue(prefix.concat("whiteouts")).split(",");
			
			for(String periodString : data){
				String[] periodData = periodString.split("-");
				
				if(periodData.length == 2){
					WhiteoutPeriod p = new WhiteoutPeriod(
						fromPeriodDataString(periodData[0].split(":")),
						fromPeriodDataString(periodData[1].split(":"))
					);
					
					periods.add(p);
					
					Kikkit.MinecraftLog.info("[" + prefix + "] Added period from " + p.Start + " to " + p.End + ".");
				}
			}
		}
	}
	
	private Date fromPeriodDataString(String[] data){
		Date date = new Date();
		
		try{
			date.setHours(Integer.parseInt(data[0]));
			date.setMinutes(Integer.parseInt(data[1]));
		}
		catch(Exception ex){
			Kikkit.MinecraftLog.info("There is an error in the whiteout data string.");
		}
		finally{
			
		}
		
		return date;
	}
	
	@Override
	public boolean isOnList(String playerName){
		if(!isEnabled) return true;
		
		return super.isOnList(playerName);
	}
	
	public void update(){
		Date currentDate = new Date();
		
		boolean enabled = false;
		currentPeriod = null;
		for(WhiteoutPeriod p : periods){
			
			if(p.isInPeriod(currentDate)){
				currentPeriod = p;
				enabled = true;
				break;
			}
		}
		
		isEnabled = enabled;
	}
	
	public boolean getIsEnabled(){ return isEnabled; }
	public WhiteoutPeriod getCurrentPeriod() { return currentPeriod; }
}
