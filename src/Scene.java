import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
	private List<Player> players;
	
	private Client client;
	
	public Scene(Client client)
	{
		this.client = client;
		
		players = new ArrayList<Player>();
	}
	
	public void update(double dt)
	{
		for(Player player : players)
			player.update(dt);
	}
	
	public void paint(Graphics2D g)
	{	
		Rectangle2 frame = client.getCamera().getFrame(client.getUI().getScenePane());

		int startx = 20 * ((int) frame.x / 20),
			starty = 20 * ((int) frame.y / 20);
		
		Color color1 = null, color2 = null;
		
		if(Math.abs(starty) % 40 == 0)
		{
			color1 = Color.gray;
			color2 = Color.white;
		}
		else
		{
			color2 = Color.gray;
			color1 = Color.white;
		}
				
		for(int y = starty; y <= starty + frame.height; y += 20)
		{
			Color color = Math.abs(startx) % 40 == 0 ? color1 : color2;
			
			for(int x = startx; x <= startx + frame.width; x += 20)
			{				
				g.setColor(color);
				
				g.fillRect(x, y, 20, 20);
			
				if(color == Color.white)
					color = Color.gray;
				else
					color = Color.white;
			}
			
			color = color1; color1 = color2; color2 = color;
		}	
				
		for(Player player : players)
			player.paint((Graphics2D)g.create());
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}

	public boolean contains(String playerName)
	{
		for(Player player : players)
			if(player.getName().equals(playerName))
				return true;
		return false;
	}

	public Player getPlayer(String playerName)
	{
		for(Player player : players)
			if(player.getName().equals(playerName))
				return player;
		return null;
	}
}
