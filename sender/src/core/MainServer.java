package core;

import usefulDS.Connection;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/* main server which receives all incoming connections */
public class MainServer extends Thread {
	
	private Integer SERVER_PORT;
	private ServerSocket ss;
	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	MainServer(Integer port){this.SERVER_PORT=port;}
	
	public void print_connections()
	{
		LOGGER.log(Level.INFO, "Present list of connections");
		for(int i = 0; i<CodeDriver.MAX_CONNECTIONS ; i++)
		{
			if(CodeDriver.connections[i] != null)
				LOGGER.log(Level.INFO, CodeDriver.connections[i].toString());
		}
	}
	
	public void stopServer()
	{
		try {ss.close();}catch(Exception e) {LOGGER.log(Level.SEVERE, e.getStackTrace().toString());}
	}
	
	public void Listen() throws IOException 
	{
		ss = new ServerSocket(SERVER_PORT);
		
		while(ss!=null && !ss.isClosed())
		{
			Socket s = null;
			try {
				LOGGER.log(Level.INFO, "Waiting for connections on .. "+SERVER_PORT);
				s = ss.accept();
				LOGGER.log(Level.INFO,"A new client is connected" );
				
				DataInputStream dis  = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				
				/* read UDP server information immediately */
				String IP 		= dis.readUTF();
				Integer PORT 	= dis.readInt();
				
				Connection new_connection = new Connection(s.getInetAddress(),PORT,dis,dos,s);
				
				if(CodeDriver.emptyIndex!=-1)
				CodeDriver.connections[CodeDriver.emptyIndex] = new_connection;
				
				//update the emptyIndex
				for(int i=CodeDriver.emptyIndex; i<CodeDriver.MAX_CONNECTIONS ;i++)
				{
					CodeDriver.emptyIndex = -1;
					if(CodeDriver.connections[i]==null) {CodeDriver.emptyIndex = i; break;}
				}
				
				print_connections();
				LOGGER.log(Level.INFO, "EmptyIndex : "+CodeDriver.emptyIndex);
				
			}catch(Exception e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
				if(s!=null)s.close();
				if(ss!=null)ss.close();
				break;
			}
		}
	}
	
	@Override
	public void run()
	{
		LOGGER.log(Level.INFO, this.getClass().getName()+" : started");
		try {
			Listen();
		}catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
		}
		LOGGER.log(Level.INFO, this.getClass().getName()+" : dead");
	}
}
