import java.util.Timer;
import java.util.logging.Logger;

/*
 * EnterMod: A basic hMod plugin for testing how to develop plugins for Minecraft
 * Author: John Sedlak
 * Created: 2011-01-04
 * Notes:
 *  Seriously java, what is up with your stupid POS DateTime implementation. It
 * 	is almost as bad as javascript's, and php's "interpretations" of how such 
 *  functionality should exist. Anyways, that is the reason for suppressing the 
 *  deprecation warning.
 *  
 *  I'd also like to take the space here to explain how I VIOLENTLY HATE Eclipse.
 *  It's feature list is good. And by all means it works. I can create Java files
 *  easily enough and compile them into a Jar without going through too many hoops.
 *  But for the love of god the intellisense and auto-code-formatting need some
 *  major work. They are intrusive at the least, and absurdly infuriating at the
 *  worst of times. If someone made a VS2010 Java IDE, I would rather pay $800 for
 *  that instead of downloading this free crap.
 */
//@SuppressWarnings("deprecation")
public class Kikkit extends Plugin {
	public static Logger MinecraftLog = null;						// Used to log stuff
	public static final String PublicName = "Entry Mod";	// Our mod's name
	public static final String Version = "1.0";				// The version!
	public static final long UPDATE_INTERVAL = 10000;		// How often the plugin should update itself
	
	private KikkitListener emListener;	// Used to handle server events
	private boolean isEnabled = true;		// Whether or not the plugin is enabled
	
	private boolean whitelistDisableOverride = false;	// Whether or not the whitelist is being overridden to a DISABLED state.
	private boolean firelistOverride = false; 			// Whether or not the firelist is active
	
	private Whitelist fireWhitelist;		// Who can ignite stuff
	private TemporaryWhitelist tempWhitelist;
	
	private GenericConfig genConfig;		// Generic configuration loading
	
	public Kikkit(){
		// Instantiate our listener object
		emListener = new KikkitListener(this);
		
		// Get the logging device
		if(MinecraftLog == null)
			MinecraftLog = Logger.getLogger("Minecraft");
	}
	
	public void enable(){
		isEnabled = true;
	}
	
	public void disable(){
		isEnabled = false;
	}
	
	public void initialize(){
		// Tell the console that we have started loading the plugin
		MinecraftLog.info(PublicName + " " + Version + " is being initialized.");
	
		// Load the configuration, and the whitelist files.
		genConfig = new GenericConfig("config/em.config");
		tempWhitelist = new TemporaryWhitelist("config/em-whitelist.txt", genConfig, "wl-");
		fireWhitelist = new Whitelist("config/em-fire.txt");
		
		// TODO: Move this into the whitelist. Make a special whitelist class for this.
		if(!genConfig.hasKey("wl-whiteouts")){
			whitelistDisableOverride = true;
		}
		else{
			whitelistDisableOverride = false;
		}
		
		if(tempWhitelist.getIsEnabled()){
			// Output some info about when the whitelist will take affect
			MinecraftLog.info("Whitelist has been loaded.");
		}
		
		// HOOK! Wasn't that a movie? Anyways, attach some event handlers (I'm a C#er, okay?)
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.IGNITE, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, emListener, this, PluginListener.Priority.MEDIUM);
		/*etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ARM_SWING, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_PICK_UP, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PHYSICS, emListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.FLOW, emListener, this, PluginListener.Priority.MEDIUM);*/
		
		// Setup a timer so that the update method gets called (and call it)
		Timer updateTimer = new Timer();
		updateTimer.schedule(new KikkitUpdater(this), 0, UPDATE_INTERVAL);
		
		//update();
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
				broadcast(Colors.Purple + "The server has entered a whiteout.");
			}
			else {
				MinecraftLog.info("Disabling whitelist temporarily.");
				broadcast(Colors.Purple + "The server has exited a whiteout.");
			}
		}
		/*if(whitelistDisableOverride) return;
		
		// Get the current datetime
		Date currentDate = new Date();
		int currentHour = currentDate.getHours();
		
		// Are we in a whiteout?
		boolean isInWhitelistPeriod = currentHour >= wlStart.getHours() && currentHour <= wlEnd.getHours();
		
		// If it isn't enabled, do we need to enable it?
		if(!isWhitelistEnabled && isInWhitelistPeriod){
			isWhitelistEnabled = true;
			logger.info("Enabling whitelist temporarily.");
		}
		else if(isWhitelistEnabled && !isInWhitelistPeriod){
			logger.info("Disabling whitelist temporarily.");
			isWhitelistEnabled = false;
		}*/
	}
	
	public boolean canPlayerLogin(Player player){
		//MinecraftLog.info(player.getName() + " is trying to log on.");
		//MinecraftLog.info("    override: " + whitelistDisableOverride);
		//MinecraftLog.info("    isOnList: " + tempWhitelist.isOnList(player.getName()));
		
		return whitelistDisableOverride || tempWhitelist.isOnList(player.getName());
	}
	
	public boolean canPlayerIgnite(Player player){
		return firelistOverride || fireWhitelist.isOnList(player.getName());
	}
	
	// Gets whether or not the plugin is enabled
	public boolean getIsEnabled(){ 
		return isEnabled; 
	}
	
	public boolean getIsWhitelistEnabled(){ 
		return tempWhitelist.getIsEnabled(); 
	}
	
	public void setWhitelistOverride(boolean value){ 
		whitelistDisableOverride = value;
		
		MinecraftLog.info("Whitelist Disable Override has been set to " + whitelistDisableOverride);
	}	
	
	public boolean getFireListOverride(){
		return firelistOverride;
	}
	
	public void setFireListOverride(boolean value){
		firelistOverride = value;
	}
}
