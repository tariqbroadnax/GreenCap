import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client
{
	private Updater updater;
	
	private UI ui;

	private Socket socket;

	private PrintWriter output;
	
	private BufferedReader input;
	
	private Scene scene;
	
	private Camera camera;

	private Controller controller;
	
	private Player player;
	
	public Client()
	{
		updater = new Updater(this);
		
		camera = new Camera();
		
		scene = new Scene(this);
	
		controller = new Controller(this);
		
		player = new Player();
		
		ui = new UI(this);
		
		scene.addPlayer(player);
		
		camera.setFocus(player.getPosition());
	}
	
	public void update(double dt)
	{
		ui.update(dt);
	}
	
	public void start()
	{		
		try
		{
			connectToServer();
			
			ui.start();
		
			updater.start();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		updater.stop();
		
		ui.stop();
	}
	
	private void connectToServer() throws IOException
	{
		String hostname = InetAddress.getLocalHost().getHostName();
		
		int port = 222;
		
		socket = new Socket(hostname, port);

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		output = new PrintWriter(socket.getOutputStream());
	
		String playerName = input.readLine();
		
		player.setName(playerName);
	}
	
	public Updater getUpdater() {
		return updater;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public UI getUI() {
		return ui;
	}
	
	public BufferedReader getInput() {
		return input;
	}
	
	public PrintWriter getOutput() {
		return output;
	}
	
}
