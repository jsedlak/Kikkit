package core;
import java.util.TimerTask;



public class KikkitUpdater extends TimerTask {

	private Kikkit plugin;
	
	public KikkitUpdater(Kikkit plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		plugin.update();
	}
}
