import java.util.Date;

@SuppressWarnings("deprecation")
public class TemporaryWhitelist extends Whitelist {
	private Date wlStart, wlEnd;
	private boolean isEnabled;
	
	public TemporaryWhitelist(String filename, GenericConfig config, String prefix){
		super(filename);
		
		if(!config.hasKey(prefix.concat("start")) || !config.hasKey(prefix.concat("end"))){
			isEnabled = false;
		}
		else{
			String[] start = config.getValue(prefix.concat("start")).split(":");
			String[] end = config.getValue(prefix.concat("end")).split(":");
			
			wlStart = new Date();
			wlStart.setHours(Integer.parseInt(start[0]));
			wlStart.setMinutes(Integer.parseInt(start[1]));
			
			wlEnd = new Date();
			wlEnd.setHours(Integer.parseInt(end[0]));
			wlEnd.setMinutes(Integer.parseInt(end[1]));
		}
	}
	
	public TemporaryWhitelist(String filename, Date start, Date end) {
		super(filename);
		
		wlStart = start;
		wlEnd = end;
	}
	
	@Override
	public boolean isOnList(String playerName){
		if(!isEnabled) return true;
		
		return super.isOnList(playerName);
	}
	
	public void update(){
		Date currentDate = new Date();
		Date start = wlStart;
		Date end = wlEnd;
		
		isEnabled = compare(currentDate, start) >= 0 && compare(currentDate, end) < 0;
	}
	
	public Date getStart(){ return wlStart; }
	public Date getEnd(){ return wlEnd; }
	public boolean getIsEnabled(){ return isEnabled; }
	
	private int compare(Date d1, Date d2){
		Date left = (Date)d1.clone();
		left.setYear(2010);
		left.setMonth(1);
		left.setDate(0);
		
		Date right = (Date)d2.clone();
		right.setYear(2010);
		right.setMonth(1);
		right.setDate(0);
		
		return left.compareTo(right);
	}
}
