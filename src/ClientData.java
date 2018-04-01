import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientData
{
	private Socket socket;
	
	private BufferedReader input;
	
	private PrintWriter output;
	
	private Player player;
	
	public ClientData(Socket socket) throws IOException
	{
		this.socket = socket;
		
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		output = new PrintWriter(socket.getOutputStream());
	
		player = new Player();
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public BufferedReader getInput() {
		return input;
	}
	
	public PrintWriter getOutput() {
		return output;
	}
	
	public Player getPlayer() {
		return player;
	}
}
