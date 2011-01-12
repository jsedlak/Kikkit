import java.util.*;

public class KickCounter extends ArrayList<KickCount> {
	private static final long serialVersionUID = -3528554355763329373L;

	public int checkAndSet(String user){
		for(int i = 0; i < size(); i++){
			KickCount kc = this.get(i);
			
			if(kc.Username.equalsIgnoreCase(user)) {
				kc.Count++;
				return kc.Count;
			}
		}
		
		KickCount count = new KickCount();
		count.Username = user;
		count.Count = 1;
		
		add(count);
		
		return count.Count;
	}
}
