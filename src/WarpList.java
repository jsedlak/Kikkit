import java.io.*;
import java.util.*;

public class WarpList {
	public static final String DefaultSecretWarpFile = "secret-warps.txt";
	
	private ArrayList<WarpPoint> warps = new ArrayList<WarpPoint>();
	private String internalFilename;
	
	public WarpList(){
		load(DefaultSecretWarpFile);
	}
	
	public WarpPoint get(String username){
		for(WarpPoint w : warps){
			if(w.Username.equalsIgnoreCase(username)){
				return w;
			}
		}
		
		return null;
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
	
	public void load(String filename){
		internalFilename = filename;
		
		try{
			warps.clear();
			
			FileInputStream inputStream = new FileInputStream(filename);
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
	
	public class WarpPoint {
		public String Username;
		public double X, Y, Z;
	}
}
