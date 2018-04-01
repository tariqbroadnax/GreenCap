import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controller implements KeyListener, FocusListener
{
	private List<KeyEvent> pressedEvents,
						   releasedEvents;
	
	private Set<Integer> moveCodes;

	private Client client;
	
	private int prev_movex, prev_movey;
	
	public Controller(Client client)
	{
		this.client = client;
		
		pressedEvents = Collections.synchronizedList(new ArrayList<KeyEvent>());
		releasedEvents = Collections.synchronizedList(new ArrayList<KeyEvent>());
	
		moveCodes = new HashSet<Integer>();
	}
	
	public void update(double dt)
	{		
		synchronized(pressedEvents)
		{
			for(KeyEvent e : pressedEvents)
			{
				int code = e.getKeyCode();
				
				switch(code)
				{
					case KeyEvent.VK_UP:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_RIGHT:
						moveCodes.add(code);
						break;
				}
			}
			
			pressedEvents.clear();
		}
		
		synchronized(releasedEvents)
		{
			for(KeyEvent e : releasedEvents)
			{
				int code = e.getKeyCode();
				
				switch(code)
				{
					case KeyEvent.VK_UP:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_RIGHT:
						moveCodes.remove((Integer)code);
						break;
				}
			}
			
			releasedEvents.clear();
		}
		
		int movex = 0, movey = 0;
		
		for(int code : moveCodes)
		{
			if(code == KeyEvent.VK_UP)
				movey = -1;
			else if(code == KeyEvent.VK_DOWN)
				movey = 1;
			else if(code == KeyEvent.VK_LEFT)
				movex = -1;
			else if(code == KeyEvent.VK_RIGHT)
				movex = 1;
		}
	
		if(movex != prev_movex || movey != prev_movey)
		{
			client.getPlayer()
				  .move(movex, movey);
	
			client.getOutput()
				  .println("move " + movex + " " + movey);
			
			client.getOutput()
				  .flush();
			
			prev_movex = movex;
			prev_movey = movey;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		synchronized(pressedEvents)
		{
			pressedEvents.add(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		synchronized(releasedEvents)
		{
			releasedEvents.add(e);
		}
	}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void focusGained(FocusEvent e){}

	@Override
	public void focusLost(FocusEvent e)
	{
		synchronized(releasedEvents)
		{
			releasedEvents.clear();
		}
	}
}
