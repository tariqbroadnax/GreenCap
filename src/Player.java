import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player
{
	private static BufferedImage SPRITESHEET = null;
	
	private static final double STANDING_PERIOD = 0.15,
								MOVING_PERIOD = 0.075;
	
	private static final int STANDING_FRAMES = 2,
							 MOVING_FRAMES = 6;

	private static final double VEL = 80;
	
	private String name;
	
	private Vector pos;
	
	private State state;
	
	private double stateElapsed;

	private int movex, movey;
	
	private int dir;
	
	public Player()
	{
		name = "Player";
		
		pos = new Vector();
		
		dir = 1;
		
		state = State.STANDING;
	}
	
	public void update(double dt)
	{		
		updateState();
			
		stateElapsed += dt;	
		
		pos.x += movex * VEL * dt;
		pos.y += movey * VEL * dt;
	}
	
	private void updateState()
	{
		if(state == State.STANDING)
		{
			if(movex != 0 || movey != 0)
			{
				state = State.MOVING;
				stateElapsed = 0;
			}
		}
		else if(state == State.MOVING)
		{
			if(movex == 0 && movey == 0)
			{
				state = State.STANDING;
				stateElapsed = 0;
			}
		}
	}
	
	public void paint(Graphics2D g)
	{
		g.translate(pos.x, pos.y);
	
		paintSprite((Graphics2D) g.create());
		paintName((Graphics2D) g.create());
	}
	
	private void paintSprite(Graphics2D g)
	{
		BufferedImage frame = getFrame();
	
		g.translate(-frame.getHeight()/2, -frame.getWidth()/2);
		
		if(dir == 1)
			g.drawImage(frame, 0, 0, frame.getWidth(), frame.getHeight(), null);
		else
			g.drawImage(frame, frame.getWidth(), 0, -frame.getWidth(), frame.getHeight(), null);
	
	}
	
	private void paintName(Graphics2D g)
	{
		g.translate(0, -10);
		
		AffineTransform Tx = new AffineTransform(),
						curr = g.getTransform();

		Tx.setToTranslation(curr.getTranslateX(), curr.getTranslateY());
		
		g.setTransform(Tx);
		
		int width = g.getFontMetrics().stringWidth(name);
		
		g.translate(-width / 2, 0);
		
		g.setColor(Color.black);
		
		g.drawString(name, 0, 0);
	}

	public void move(int movex, int movey)
	{
		this.movex = movex; this.movey = movey;
		dir = movex == 0 ? dir : movex;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private BufferedImage getFrame() 
	{	
		int x = 0, y = 0, w = 12, h = 17;
		
		if(state == State.STANDING)
		{
			int frame = (int)(stateElapsed / STANDING_PERIOD);
			frame %= STANDING_FRAMES;
		
			x = 19 + frame * 16;
			y = 15;			
			
		}
		else if(state == State.MOVING)
		{
			int frame = (int)(stateElapsed / (MOVING_PERIOD));
			frame %= MOVING_FRAMES;

			x = 19 + frame * 16;
			y = 32;
			h = 16;
		}
		
		return SPRITESHEET.getSubimage(x, y, w, h);
	}
	
	public String getUpdateCommand() 
	{		
		return "player " + name + " " + pos.x + " " + pos.y + " " + state.ordinal() + " " + stateElapsed + " " + movex + " " + movey + " " + dir;
	}
	
	public void parseUpdateCommand(String command)
	{
		String[] symbols = command.split(" ");
		
		name = symbols[1];
		
		pos.x = Double.parseDouble(symbols[2]);
		pos.y = Double.parseDouble(symbols[3]);
		
		state = State.values()[Integer.parseInt(symbols[4])];
		
		stateElapsed = Double.parseDouble(symbols[5]);
				
		movex = Integer.parseInt(symbols[6]);
		movey = Integer.parseInt(symbols[7]);
		
		dir = Integer.parseInt(symbols[8]);
	}
	
	public Vector getPosition() {
		return pos;
	}
	
	public String getName() {
		return name;
	}
	
	static 
	{
		try 
		{
			SPRITESHEET = ImageIO.read(new File("green_cap.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private enum State 
	{
		STANDING, MOVING
	}
}
