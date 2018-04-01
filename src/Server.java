import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server
{
	private final static int TARGET_UPS = 30;
	
	private Thread connectThread, updateThread;
	
	private ServerSocket socket;
	
	private List<ClientData> clientData;
	
	private boolean running;
	
	public Server()
	{
		clientData = Collections.synchronizedList(new ArrayList<ClientData>());
		
		connectThread = new Thread(() -> recieveClients());
		updateThread = new Thread(() -> update());
	}
	
	public void start()
	{
		if(!running)
		{
			try 
			{
				socket = new ServerSocket(222);
				
				connectThread.start();
				updateThread.start();
			
				running = true;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void stop()
	{
		if(running)
		{
			try {
				socket.close();
			} catch (IOException e) {}
			
			running = false;
		}
	}
	
	private void recieveClients()
	{
		while(running)
		{
			try 
			{
				Socket clientSocket = socket.accept();
				
				ClientData newClient = new ClientData(clientSocket);
			
				String playerName = "player" + (int)(Math.random() * 100000);
				
				boolean validPlayerName = false;
				
				while(!validPlayerName)
				{
					validPlayerName = true;
					
					for(ClientData client : clientData)
					{
						if(client.getPlayer().getName().equals(playerName))
						{
							validPlayerName = false;
							break;
						}
					}
				}
				
				newClient.getOutput().println(playerName);
				newClient.getPlayer().setName(playerName);
				
				synchronized(clientData) {
					clientData.add(newClient);
				}
			} 
			catch (IOException e) {
				stop();
			}
		}
	}
	
	private void update()
	{
		long start, end = System.nanoTime(),
			 period = (long)(1.0e9 / TARGET_UPS);
		
		while(running)
		{
			start = System.nanoTime();
			
			synchronized(clientData) 
			{
				recieveCommands();
				updateScene((start - end) / 1e9);
				sendCommands();
			}
			
			end = System.nanoTime();
			
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
	
	private void recieveCommands()
	{
		for(ClientData client : clientData)
		{
			try 
			{
				while(client.getInput().ready())
				{
					String txt = client.getInput().readLine();
									
					recieveCommand(client, txt);
				}
			} 
			catch (IOException e) 
			{
				clientData.remove(client);
			}
		}
	}

	private void updateScene(double dt)
	{
		for(ClientData client : clientData)
		{
			Player player = client.getPlayer();
			
			player.update(dt);
		}
	}

	private void sendCommands()
	{
		List<String> commands = createCommands();
		
		for(ClientData client : clientData)
		{
			for(String command : commands)
			{
				client.getOutput()
					  .println(command);
			}
			
			client.getOutput()
				  .flush();
		}
	}
	
	private List<String> createCommands() 
	{
		List<String> commands = new ArrayList<String>();
		
		commands.add("update " + System.nanoTime());
		
		for(ClientData client : clientData)
			commands.add(client.getPlayer().getUpdateCommand());
	
		return commands;
	}

	private void recieveCommand(ClientData data, String txt) throws IOException
	{
		String[] parts = txt.split(" ");
		
		if(parts.length == 0)
			return;
		
		String command = parts[0];
		
		switch(command)
		{
			case "move":
				if(parts.length >= 3)
				{
					int movex = Integer.parseInt(parts[1]),
						movey = Integer.parseInt(parts[2]);
					
					data.getPlayer().move(movex, movey);
				}
		}
	}
}
