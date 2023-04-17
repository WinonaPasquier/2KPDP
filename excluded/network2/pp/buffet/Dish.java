package network2.pp.buffet;

public class Dish<T> {
	private T dish=null;
	
	public Dish() {
	}
	
	public synchronized void  set(T n) {
		dish=n;
	}
	
	public synchronized T get() {
		T ret=dish;
		dish=null;
		return ret;
	}
	
	public synchronized T look() {
		return dish;
	}
	
	public T waitAndGet() {
		T ret;
		while((ret=get())==null) {
			try {Thread.sleep(100);}catch (Exception e) { }
		}
		return ret;
	}
}
