import java.util.Timer;
import java.util.logging.Logger;
import java.util.Date;

/*
 * Kikkit
 * Description: A basic hMod plugin that employs a temporary whitelist 
 * and stops lava and fire use.
 * Author: John Sedlak
 * Created: 2011-01-04
 */
@SuppressWarnings("deprecation")
public class Kikkit extends Plugin {
	public static Logger MinecraftLog = null;				// Used to log stuff
	public static final String PublicName = "Kikkit";		// Our mod's name
	public static final String Version = "1.2";				// The version!
	public static final long UPDATE_INTERVAL = 30000;		// How often the plugin should update itself
	
	public static String getPluginName(){
		return PublicName + " v" + Version;
	}
	
	public static String getTimeStampString(Date datetime){
		return datetime.getHours() + ":" + datetime.getMinutes() + ":" + datetime.getSeconds();
	}
	
	private KikkitListener emListener;	// Used to handle server events
	private boolean isEnabled = true;		// Whether or not the plugin is enabled
	
	private Whitelist fireWhitelist;		// Who can ignite stuff
	private TemporaryWhitelist tempWhitelist;
	
	private GenericConfig genConfig;		// Generic configuration loading
	
	private WarpList secretWarpList;
	
	Timer updateTimer;
	
	public Kikkit(){
		// Instantiate our listener object
		emListener = new KikkitListener(this);
		
		// Get the logging device
		if(MinecraftLog == null)
			MinecraftLog = Logger.getLogger("Minecraft");
	}
	
	public void enable(){
		MinecraftLog.info(getPluginName() + " has been enabled.");
		isEnabled = true;
	}
	
	public void disable(){
		MinecraftLog.info(getPluginName() + " has been disabled.");
		isEnabled = false;
		
		if(updateTimer != null){
			updateTimer.cancel();
			updateTimer = null;
		}
	}
	
	public void initialize(){
		// Tell the console that we have started loading the plugin
		MinecraftLog.info(getPluginName() + " is being initialized.");
	
		// Load the configuration, and the whitelist files.
		genConfig = new GenericConfig("config/em.config");
		tempWhitelist = new TemporaryWhitelist("config/em-whitelist.txt", genConfig, "wl-");
		fireWhitelist = new Whitelist("config/em-fire.txt");
		secretWarpList = new WarpList();
		
		// HOOK! Wasn't that a movie? Anyways, attach some event handlers (I'm a C#er, okay?)
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.IGNITE, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, emListener, this, PluginListener.Priority.MEDIUM);
		
		// Setup a timer so that the update method gets called (and call it)
		updateTimer = new Timer();
		updateTimer.schedule(new KikkitUpdater(this), 0, UPDATE_INTERVAL);
	}
	
	public void broadcast(String msg){
		for(Player player : etc.getServer().getPlayerList()){
			player.sendMessage(msg);
		}
	}
	
	/*
	 * Updates the plugin periodically.
	 */
	public void update(){
		boolean current = tempWhitelist.getIsEnabled();
		
		tempWhitelist.update();
		
		if(current != tempWhitelist.getIsEnabled()){
			if(tempWhitelist.getIsEnabled()){
				MinecraftLog.info("Enabling whitelist temporarily.");
				broadcast(Colors.Purple + "Server entered a whiteout until (at least) " + getTimeStampString(tempWhitelist.getCurrentPeriod().End) + ".");
			}
			else {
				MinecraftLog.info("Disabling whitelist temporarily.");
				broadcast(Colors.Purple + "The server has exited a whiteout.");
			}
		}
	}
	
	public boolean canPlayerLogin(Player player){
		return tempWhitelist.isOnList(player.getName()) || player.isInGroup(Groups.Vip) || player.isInGroup(Groups.Moderator) || player.isAdmin();
	}
	
	public boolean canPlayerIgnite(Player player){
		return fireWhitelist.isOnList(player.getName()) || player.isInGroup(Groups.Vip) || player.isInGroup(Groups.Moderator) || player.isAdmin();
	}
	
	// Gets whether or not the plugin is enabled
	public boolean getIsEnabled(){ 
		return isEnabled; 
	}
	
	public TemporaryWhitelist getTemporaryWhitelist(){
		return tempWhitelist;
	}
	
	public Whitelist getFireWhitelist(){
		return fireWhitelist;
	}
	
	public WarpList getSecretWarpList(){
		return secretWarpList;
	}
	
	/*
	public boolean getIsWhitelistEnabled(){ 
		return tempWhitelist.getIsEnabled(); 
	}
	
	public void setWhitelistOverride(boolean value){ 
		//whitelistDisableOverride = value;
		tempWhitelist.setIsOverriden(value);
		
		MinecraftLog.info("Whitelist Override has been set to " + tempWhitelist.getIsOverriden());
	}	
	
	public boolean getFireListOverride(){
		//return firelistOverride;
		return fireWhitelist.getIsOverriden();
	}
	
	public void setFireListOverride(boolean value){
		//firelistOverride = value;
		fireWhitelist.setIsOverriden(value);
	}*/
}
