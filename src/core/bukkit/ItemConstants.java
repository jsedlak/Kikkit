package core.bukkit;

import org.bukkit.*;

public abstract class ItemConstants {
	public static final int WaterBucketId = 326;
	public static final int LavaBucketId = 327;
	public static final int TntId = 46;
	
	public static int ConvertToId(String itemName){
		/*if(itemName.equalsIgnoreCase("woodenplank"))
			itemName = "wooden plank";
		else if(itemName.equalsIgnoreCase("goldore"))
			itemName = "gold ore";
		else if(itemName.equalsIgnoreCase("ironore"))
			itemName = "iron ore";*/
		
		Material mat = Material.getMaterial(itemName.toUpperCase().replaceAll(" ", "_"));
		
		if(mat != null) return mat.getId();
		
		return -1;
	}
}
