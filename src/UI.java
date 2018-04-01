import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class UI
{
	private static final double UPDATE_TITLE_PERIOD = 1.0; 
	
	private JFrame frame;
	
	private BufferedImage buffer;
	
	private double updateTitleTimer;
	
	private Client client;
	
	private ScenePane scenePane;
	
	public UI(Client client)
	{
		this.client = client;
		
		frame = new JFrame();
		
		buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		scenePane = new ScenePane();
		
		Container content = frame.getContentPane();
		
		content.setLayout(new BorderLayout());

		content.add(scenePane, BorderLayout.CENTER);

		scenePane.requestFocusInWindow();
		
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void update(double dt)
	{
		updateTitle(dt);
		updateComponents(dt);

		paint();
	}
	
	private void updateTitle(double dt)
	{
		updateTitleTimer += dt;
		
		if(updateTitleTimer > UPDATE_TITLE_PERIOD)
		{
			updateTitleTimer = 0;
			
			Updater updater = client.getUpdater();
			
			frame.setTitle(String.format("Green Cap | FPS: %d | Ticks: %d", updater.getFPS(), updater.getTicks()));
		}
	}
	
	private void updateComponents(double dt)
	{
		if(scenePane.isVisible())
			scenePane.update(dt);
	}
	
	private void paint()
	{
		Container content = frame.getContentPane();
		
		Dimension contentSize = content.getSize(),
				  bufferSize = new Dimension(buffer.getWidth(), buffer.getHeight());
		
		if(contentSize.width == 0 || contentSize.height == 0)
			return;
		
		if(!contentSize.equals(bufferSize))
			buffer = new BufferedImage(contentSize.width, contentSize.height, BufferedImage.TYPE_INT_ARGB);
		
		content.paint(buffer.getGraphics());
		content.getGraphics()
			   .drawImage(buffer, 0, 0, null);
	}
	
	public void start()
	{
		frame.setVisible(true);
		
		while(!frame.isVisible()) {}
	}

	public void stop()
	{
		frame.setVisible(false);
	}
	
	public ScenePane getScenePane() {
		return scenePane;
	}
	
	public class ScenePane extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public ScenePane()
		{
			Controller controller = client.getController();
			
			addKeyListener(controller);
			addFocusListener(controller);
	
			setFocusable(true);
		}
		
		public void update(double dt)
		{			
			recieveCommands(dt);
			
			client.getController()
				  .update(dt);
			
			client.getScene()
				  .update(dt);			
		}
		
		private void recieveCommands(double dt)
		{			
			List<String> commands = new ArrayList<String>();
			
			try 
			{
				while(client.getInput().ready())
				{
					String command = client.getInput().readLine();
					
					commands.add(command);
				}
			} 
			catch(IOException e) 
			{
				commands.clear();
			}
			
			if(commands.isEmpty())
				return;
			
//			String updateCommand = commands.remove(0);
			
//			long updateTime = Long.parseLong(updateCommand.split(" ")[1]);
			
			for(String command : commands)
				readCommand(command);
			
//			long interpolateTime = System.nanoTime() - (long)(dt * 1e9) - updateTime;
//			
//			client.getScene()
//				  .update(interpolateTime);
		}
		
		private void readCommand(String command)
		{
			String[] tokens = command.split(" ");
			
			switch(tokens[0])
			{
				case "player":
					Scene scene = client.getScene();
					
					String playerName = tokens[1];
					
					if(!scene.contains(playerName))
					{
						Player player = new Player();
						
						player.setName(playerName);
						
						scene.addPlayer(player);
					}
						
					scene.getPlayer(playerName)
						 .parseUpdateCommand(command);
			}
		}
		
		public void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			g.setColor(Color.white);
			
			g.fillRect(0, 0, getWidth(), getHeight());
			
			Camera camera = client.getCamera();
			
			AffineTransform Tx = camera.createTransform(this);
			
			g2d.transform(Tx);
			
			Scene scene = client.getScene();
			
			scene.paint(g2d);
		}
	}
}
