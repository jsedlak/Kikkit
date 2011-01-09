import java.util.*;

@SuppressWarnings("deprecation")
public class TemporaryWhitelist extends Whitelist {
	private class WhiteoutPeriod {
		public Date Start;
		public Date End;
		
		public WhiteoutPeriod(Date start, Date end){
			Start = start;
			End = end;
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
			
			//Kikkit.MinecraftLog.info("    " + d1 + " to " + d2 + " = " + returnValue);
			
			return returnValue; //left.compareTo(right);
		}
	}
	
	private ArrayList<WhiteoutPeriod> periods = new ArrayList<WhiteoutPeriod>();
	
	//private Date wlStart, wlEnd;
	private boolean isEnabled;
	
	public TemporaryWhitelist(String filename, GenericConfig config, String prefix){
		super(filename);
		
		if(!config.hasKey(prefix.concat("whiteouts"))){
			isEnabled = false;
			Kikkit.MinecraftLog.info("Couldn't find key: " + prefix.concat("whiteouts"));
		}
		else{
			/*
			String[] start = config.getValue(prefix.concat("start")).split(":");
			String[] end = config.getValue(prefix.concat("end")).split(":");
			
			wlStart = new Date();
			wlStart.setHours(Integer.parseInt(start[0]));
			wlStart.setMinutes(Integer.parseInt(start[1]));
			
			wlEnd = new Date();
			wlEnd.setHours(Integer.parseInt(end[0]));
			wlEnd.setMinutes(Integer.parseInt(end[1]));
			
			Kikkit.MinecraftLog.info("Whitelist will run from " + wlStart + " to " + wlEnd);*/
			//isEnabled = true;
			
			String[] data = config.getValue(prefix.concat("whiteouts")).split(",");
			
			for(String periodString : data){
				String[] periodData = periodString.split("-");
				
				if(periodData.length == 2){
					WhiteoutPeriod p = new WhiteoutPeriod(
						fromPeriodDataString(periodData[0].split(":")),
						fromPeriodDataString(periodData[1].split(":"))
					);
					
					periods.add(p);
					
					Kikkit.MinecraftLog.info("Added period from " + p.Start + " to " + p.End + ".");
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
		for(WhiteoutPeriod p : periods){
			
			if(p.isInPeriod(currentDate)){
				enabled = true;
				break;
			}
			
			//Kikkit.MinecraftLog.info("Validating data against " + p.Start + " to " + p.End + " [" + enabled + "]");
		}
		
		isEnabled = enabled;
		/*Date start = wlStart;
		Date end = wlEnd;
		*/
		/*Kikkit.MinecraftLog.info("compare(currentDate, start): " + compare(currentDate, start));
		Kikkit.MinecraftLog.info("compare(currentDate, end)  : " + compare(currentDate, end));*/
		
		//isEnabled = compare(currentDate, start) >= 0 && -compare(currentDate, end) < 0;
	}
	
	/*
	public Date getStart(){ return wlStart; }
	public Date getEnd(){ return wlEnd; }*/
	public boolean getIsEnabled(){ return isEnabled; }
	
	/*
	private int compare(Date d1, Date d2){
		int h1 = d1.getHours();
		int h2 = d2.getHours();
		
		int m1 = d1.getMinutes();
		int m2 = d2.getMinutes();
		
		if(h1 < h2) return -1;
		if(h1 > h2) return 1;
		
		if(m1 < m2) return -1;
		if(m1 > m2) return 1;
		
		return 0;
		/*Date left = (Date)d1.clone();
		left.setYear(2010);
		left.setMonth(1);
		left.setDate(0);
		
		Date right = (Date)d2.clone();
		right.setYear(2010);
		right.setMonth(1);
		right.setDate(0);
		
		return left.compareTo(right);
	}*/
}
