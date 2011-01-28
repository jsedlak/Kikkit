package core;
import java.io.*;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;


public class WarpList {
	public static final String DefaultSecretWarpFile = "secret-warps.txt";
	
	private ArrayList<WarpPoint> warps = new ArrayList<WarpPoint>();
	private String internalFilename;
	
	public WarpList(){
		load(DefaultSecretWarpFile);
	}
	
	public WarpList(String filename){
		load(filename);
	}
	
	public WarpPoint get(String username){
		if(warps == null || warps.size() <= 0) return null;
		
		for(WarpPoint w : warps){
			if(w.Username.equalsIgnoreCase(username)){
				return w;
			}
		}
		
		return null;
	}
	
	public void set(String username, Location location){
		set(username, location.getX(), location.getY(), location.getZ());
	}
	
	public void set(String username, double x, double y, double z){
		WarpPoint warp = get(username);
		
		if(warp != null){
			warp.X = x;
			warp.Y = y;
			warp.Z = z;
		}
		else{
			warp = new WarpPoint();
			warp.Username = username;
			warp.X = x;
			warp.Y = y;
			warp.Z = z;
			
			warps.add(warp);
		}
		
		save();
	}
	
	public boolean remove(String key){
		for(int k = warps.size() - 1; k >= 0; k--){
			WarpPoint wp = warps.get(k);
			
			if(wp.Username.equalsIgnoreCase(key)){
				warps.remove(k);
				
				return true;
			}
		}
		
		return false;
	}
	
	public void load(String filename){
		internalFilename = Kikkit.getCurrentWorld().getName() + "/" + filename;
		
		try{
			warps.clear();
			
			FileInputStream inputStream = new FileInputStream(internalFilename);
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(reader);
			
			String data = "";
			while(scanner.hasNextLine()){
				data = scanner.nextLine();
				
				String[] split = data.split(":");
				
				if(split.length < 4) continue;
				
				WarpPoint warp = new WarpPoint();
				
				warp.Username = split[0];
				warp.X = Double.parseDouble(split[1]);
				warp.Y = Double.parseDouble(split[2]);
				warp.Z = Double.parseDouble(split[3]);
				
				warps.add(warp);
			}
		}
		catch(Exception ex){}
	}
	
	public void save(){
		Kikkit.MinecraftLog.info("Saving warp list to " + internalFilename);
		
		FileWriter outputFile;
		
		try {		
			outputFile = new FileWriter(internalFilename, false);
			
			//PrintWriter out = new PrintWriter(outputFile);
			
			//for(String line : internalList){
			for(int i = 0; i < warps.size(); i++){
				//out.println(line);
				WarpPoint warp = warps.get(i);
				
				//outputFile.write(person + "\n");
				outputFile.write(warp.Username + ":" + warp.X + ":" + warp.Y + ":" + warp.Z + "\r\n");
			}
			
			outputFile.close();
		}
		catch (IOException e) {
			Kikkit.MinecraftLog.info(e.getMessage());
		}
	}
	
	public WarpPoint[] toArray(){
		if(warps == null || warps.size() == 0) return new WarpPoint[0];
		
		// TODO: Does this code work?
		//return warps.toArray(new WarpPoint[warps.size()]);
		
		WarpPoint[] returnValue = new WarpPoint[warps.size()];
		
		for(int i = 0; i < warps.size(); i++) returnValue[i] = warps.get(i);
		
		return returnValue;
	}
	
	public class WarpPoint {
		public String Username;
		public double X, Y, Z;
		
		public Location getLocation(){
			Location location = new Location(Kikkit.getCurrentWorld(), X, Y, Z);
			
			return location;
		}
		
		public Location getLocation(World world){
			return new Location(world, X, Y, Z);
		}
	}
}
