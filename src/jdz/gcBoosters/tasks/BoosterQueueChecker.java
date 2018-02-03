
package jdz.gcBoosters.tasks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import jdz.gcBoosters.data.QueuedBooster;
import jdz.gcBoosters.event.BoosterEndEvent;
import jdz.gcBoosters.event.BoosterStartEvent;
import lombok.Getter;

public class BoosterQueueChecker {
	@Getter private static final BoosterQueueChecker instance = new BoosterQueueChecker();
	
	@Getter private final Map<String, QueuedBooster> activeBoosters = new HashMap<String, QueuedBooster>();
	@Getter private final Map<String, QueuedBooster> nextBoosters = new HashMap<String, QueuedBooster>();
	
	private BoosterQueueChecker() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(GCBoosters.instance, ()->{
			for (String queue: Booster.getQueues())
				if (BoosterDatabase.getInstance().isRunning(queue)) {
					QueuedBooster booster = BoosterDatabase.getInstance().peek(queue);
					QueuedBooster lastBooster = activeBoosters.get(queue);
					
					if (lastBooster == null) {
						booster.setStartTime(System.currentTimeMillis());
						new BoosterStartEvent(booster).call();
						activeBoosters.put(queue, booster);
						continue;
					}
					
					if (!booster.equals(lastBooster)) {
						activeBoosters.put(queue, booster);
						if (booster.getBooster().equals(lastBooster.getBooster()) && booster.getPlayer().equals(lastBooster.getPlayer()))
							continue;
						new BoosterEndEvent(lastBooster).call();
						new BoosterStartEvent(booster).call();
					} 
					
					else if (lastBooster.getStartTime() != 0 && lastBooster.getStartTime() + lastBooster.getBooster().getDuration()*60*1000 < System.currentTimeMillis()){
						BoosterDatabase.getInstance().dequeue(lastBooster.getPlayer(), lastBooster.getBooster());
						lastBooster.getBooster().executeEndCommands();
					}
					
					nextBoosters.put(queue, BoosterDatabase.getInstance().getNext(queue));
				}
				else {
					if (activeBoosters.get(queue) != null) {
						new BoosterEndEvent(activeBoosters.get(queue)).call();
						activeBoosters.remove(queue);
					}
					if (!BoosterDatabase.getInstance().isQueueEmpty(queue)) {
						BoosterDatabase.getInstance().activateQueued(queue);
						QueuedBooster booster = BoosterDatabase.getInstance().peek(queue);
							booster.getBooster().executeStartCommands();
					}
				}
		}, 0, 200);
	}

}