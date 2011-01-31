package core;

import java.io.File;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import core.economy.*;
import core.players.PlayerManager;

/*
 * Kikkit
 * Description: A basic hMod plugin that employs a temporary whitelist 
 * and stops lava and fire use.
 * Author: John Sedlak
 * Created: 2011-01-04
 */
@SuppressWarnings("deprecation")
public class Kikkit extends JavaPlugin {
	public static Kikkit Current = null;
	public static Logger MinecraftLog = null;				// Used to log stuff
	public static boolean IsDebugging = false;
	
	public static int DEGRIEF_ITEM_ID = 280;
	public static int MAX_IGNITE_ATTEMPTS = 5;
	
	public static final int DAY = 0;
	public static final int NIGHT = 13500;
	
	public static String getPluginName(){
		return Current.getDescription().getName() + " v" + Current.getDescription().getVersion();
	}
	
	public static String getTimeStampString(Date datetime){
		return datetime.getHours() + ":" + datetime.getMinutes() + ":" + datetime.getSeconds();
	}
	
	public static World getCurrentWorld(){
		return Current.getServer().getWorlds()[0];
	}
	
	private KickCounter igniteKickCounter = new KickCounter();
	
	private KikkitPlayerListener playerListener;	// Used to handle server events
	private KikkitBlockListener  blockListener;
	private KikkitEntityListener entityListener;
	@SuppressWarnings("unused")
	private KikkitWorldListener  worldListener;
	
	private boolean isEnabled = true;		// Whether or not the plugin is enabled
	
	private Whitelist fireWhitelist;		// Who can ignite stuff
	private TemporaryWhitelist tempWhitelist;
	
	private GenericConfig genConfig;		// Generic configuration loading
	
	private WarpList secretWarpList;
	private WarpList homeWarpList;
	private WarpList hModWarpList;
	
	private SecurityManager securityManager;
	private PlayerManager playerManager;
	
	private Market currentMarket;
	
	private String messageOfTheDay = "";
	
	Timer updateTimer;
	
	public Kikkit(
			PluginLoader pluginLoader, Server instance, 
			PluginDescriptionFile desc,
			File folder, File plugin, 
			ClassLoader cLoader) {
		
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        Current = this;
        
		// Instantiate our listener object
		playerListener = new KikkitPlayerListener(this);
		blockListener = new KikkitBlockListener(this);
		entityListener = new KikkitEntityListener(this);
		worldListener = new KikkitWorldListener(this);
		
		// Get the logging device
		if(MinecraftLog == null)
			MinecraftLog = Logger.getLogger("Minecraft");
	}
	
	public void onEnable(){
		MinecraftLog.info(getPluginName() + " has been enabled.");
		isEnabled = true;
		
		initialize();
	}
	
	public void onDisable(){
		MinecraftLog.info(getPluginName() + " has been disabled.");
		isEnabled = false;
		
		if(updateTimer != null){
			updateTimer.cancel();
			updateTimer = null;
		}
	}
	
	protected void initialize(){
		// Tell the console that we have started loading the plugin
		MinecraftLog.info(getPluginName() + " (Kr1sc) has been initialized.");
		
		// Load the configuration, and the whitelist files.
		securityManager = new SecurityManager();
		genConfig = new GenericConfig("config/kikkit.config");
		tempWhitelist = new TemporaryWhitelist("config/kikkit-whitelist.txt", genConfig, "wl-");
		fireWhitelist = new Whitelist("config/kikkit-fire.txt");
		secretWarpList = new WarpList("secret-warps.txt");
		homeWarpList = new WarpList("player-homes.txt");
		hModWarpList = new WarpList("warps.txt");
		playerManager = new PlayerManager(this);
		currentMarket = new Market(this);
		
		loadConfiguration();
		
		// HOOK! Wasn't that a movie? Anyways, attach some event handlers (I'm a C#er, okay?)
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, new KikkitUpdater(this), Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_BLOCK, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
		//pm.registerEvent(Event.Type.ITEM_SPAWN, worldListener, Priority.Normal, this);
		
		// Setup a timer so that the update method gets called (and call it)
		//updateTimer = new Timer();
		//updateTimer.schedule(new KikkitUpdater(this), 0, UPDATE_INTERVAL);
		
		broadcast(ChatColor.DARK_PURPLE + getPluginName() + " has been initialized.");
	}
	
	protected void loadConfiguration(){
		try{
			if(genConfig.hasKey("max-ignites")){
				MAX_IGNITE_ATTEMPTS = Parser.TryParseInt(genConfig.getValue("max-ignites"), 5);
				
				MinecraftLog.info("MAX_IGNITE_ATTEMPTS has been set to " + MAX_IGNITE_ATTEMPTS);
			}
			
			if(genConfig.hasKey("motd")){
				messageOfTheDay = genConfig.getValue("motd");
			}
		} catch(Exception ex){
			MinecraftLog.info(ex.getMessage() + "\n" + ex.getStackTrace());
		}
	}
	
	public boolean canUseCommand(Player player, String command){
		return canUseCommand(player.getName(), command);
	}
	
	public boolean canUseCommand(String player, String command){
		boolean returnValue = securityManager.canUseCommand(player, command);
		
		if(Kikkit.IsDebugging) MinecraftLog.info(player + " is trying to use " + command + " and result is " + returnValue);
		
		return returnValue;
	}
	
	public void broadcast(String msg){
		for(Player player : getServer().getOnlinePlayers()){
			player.sendMessage(msg);
		}
	}
	
	/*
	 * Updates the plugin periodically.
	 */
	public void update(){
		//MinecraftLog.info("UPDATED KIKKIT");
		
		boolean current = tempWhitelist.getIsEnabled();
		
		tempWhitelist.update();
		
		if(current != tempWhitelist.getIsEnabled()){
			if(tempWhitelist.getIsEnabled()){
				MinecraftLog.info("Enabling whitelist temporarily.");
				broadcast(ChatColor.DARK_PURPLE + "Server entered a whiteout until (at least) " + getTimeStampString(tempWhitelist.getCurrentPeriod().End) + ".");
			}
			else {
				MinecraftLog.info("Disabling whitelist temporarily.");
				broadcast(ChatColor.DARK_PURPLE + "The server has exited a whiteout.");
			}
		}
	}
	
	public boolean canPlayerLogin(Player player){
		return tempWhitelist.isOnList(player.getName()) || 
			securityManager.isInGroup(player, Groups.Vip) || 
			securityManager.isInGroup(player, Groups.Moderator) || 
			securityManager.isInGroup(player, Groups.Admin);
	}
	
	public boolean canPlayerIgnite(Player player){
		return fireWhitelist.isOnList(player.getName()) || 
			securityManager.isInGroup(player, Groups.Vip) || 
			securityManager.isInGroup(player, Groups.Moderator) || 
			securityManager.isInGroup(player, Groups.Admin);
	}
	
	public String getMotd(){
		return messageOfTheDay;
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
	
	public WarpList getHomeWarpList(){
		return homeWarpList;
	}
	
	public WarpList getServerModWarps(){
		return hModWarpList;
	}
	
	public GenericConfig getConfig(){
		return genConfig;
	}
	
	public KickCounter getIgnitionKickCounter(){
		return igniteKickCounter;
	}
	
	public SecurityManager getSecurityManager(){
		return securityManager;
	}
	
	public PlayerManager getPlayerManager(){
		return playerManager;
	}
	
	public Market getMarket(){ return currentMarket; }
	
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
