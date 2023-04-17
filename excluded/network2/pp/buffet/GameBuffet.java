package network2.pp.buffet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import engine.events.Event;
import engine.events.EventRegistry;
import engine.game.GameControler;
import engine.kpdpevents.InfluenceEffectEvent;
import engine.kpdpevents.gameevent.GameEvent;

public class GameBuffet {
	
	public GameBuffet() {
	}
	
	public void register(GameControler game) {
		EventRegistry<GameEvent> gameeventregistry=new EventRegistry<>();
		EventRegistry<InfluenceEffectEvent> effecteventregistry=new EventRegistry<>();
		game.setGameEventRegistry(gameeventregistry);
		game.setInfluenceEffectEventRegistry(effecteventregistry);
		
		gameeventregistry.register(event->{
			set(event.getClass(), event);
			wait(event.getClass());
		});
		effecteventregistry.register(event->{
			set(event.getClass(), event);
			wait(event.getClass());
		});
	}
	
	public Dish<String> cardasked=new Dish<>();
	public Dish<Integer> cardtoplay=new Dish<>();
	public Dish<Integer> cardplayed=new Dish<>();
	public Dish<Boolean> continueaftercard=new Dish<>();
	
	public Dish<String> columnasked=new Dish<>();
	public Dish<Integer> columntoplay=new Dish<>();
	public Dish<Integer> columnplayed=new Dish<>();
	public Dish<Boolean> continueaftercolumn=new Dish<>();
	
	private Map<Class<?>,Object> eventmap=new HashMap<>();
	private Set<Class<?>> eventkey=new HashSet<>();
	
	public void relaunch(Class<?> eventtype) {
		eventkey.add(eventtype);
	}

	public void wait(Class<?> eventtype) {
		while(!eventkey.contains(eventtype)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
		eventkey.remove(eventtype);
	}
	
	public <T extends Object> T get(Class<T> eventtype) {
		Object ret=eventmap.get(eventtype);
		if(ret!=null && eventtype.isInstance(ret)) {
			eventmap.remove(eventtype);
			return eventtype.cast(ret);
		}
		else return null;
	}

	public void set(Class<?> eventtype, Event value) {
		eventmap.put(eventtype, value);
	}
	
	public <T extends Object> T waitAndGet(Class<T> eventtype) {
		T ret=null;
		while((ret=get(eventtype))==null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
		return ret;
	}
	
}
