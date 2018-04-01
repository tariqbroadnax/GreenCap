
public class Updater implements Runnable
{
	private final static int TARGET_FPS = 60;
		
	private boolean running;
	
	private Thread thread;
	
	private int fps, ticks;
	
	private Client client;
	
	public Updater(Client client)
	{
		this.client = client;
		
		thread = new Thread(this);
	}
	
	public void start() 
	{
		if(!running)
		{
			running = true;
			
			thread.start();
		}
	}
	
	public void stop()
	{
		if(running)
			running = false;
	}
	
	public void run()
	{
		long start, end = System.nanoTime(),
			 period = (long)(1.0e9 / TARGET_FPS),
			 frameStart = end;
		
		int frames = 0;

		while(running)
		{
			start = System.nanoTime();
		
			client.update((start - end) / 1.0e9);
			
			end = System.nanoTime();
			
			ticks++; frames++;
			
			if(end - frameStart > 1e9)
			{
				fps = frames;
				frames = 0;
				frameStart = end;
			}
			
			try 
			{
				long remaining = period - (end - start),
					 sleep_ms = remaining / 1000000;
				
				int sleep_ns = (int)(remaining - 1000000 * sleep_ms);
				
				if(sleep_ms > 0)
					Thread.sleep(sleep_ms, sleep_ns);
			} 
			catch (InterruptedException e) {}
		}
	}

	public int getFPS() {
		return fps;
	}

	public int getTicks() {
		return ticks;
	}
}