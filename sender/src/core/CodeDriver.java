package core;

import usefulDS.Connection;

public class CodeDriver {

	public static int MAX_CONNECTIONS = 4;
	public static int emptyIndex = 0;
	public static Connection[] connections;

	private boolean isServerRunning = false;
	private MainServer mainServer;
	private AudioSender audioSender;
	private static int MAIN_SERVER_PORT = 5058;

	public CodeDriver()
	{
		connections = new Connection[MAX_CONNECTIONS];;
	}

	public boolean startServer()
	{
		if(isServerRunning)return true;

		mainServer  = new MainServer(MAIN_SERVER_PORT);
		audioSender = new AudioSender(MAIN_SERVER_PORT);

		try{
			mainServer.start();
			audioSender.start();

		}catch (Exception e) {
			e.printStackTrace();
			isServerRunning = false;
			return false;
		}

		isServerRunning = true;
		return true;
	}

	public boolean stopServer()
	{
		if(!isServerRunning) return true;

		try{
			if(audioSender!=null){audioSender.kill(); audioSender.join();}
			if(mainServer!=null) {mainServer.stopServer(); mainServer.join();}

		}catch (Exception e) {
			e.printStackTrace();
			isServerRunning = true;
			return false;
		}

		isServerRunning = false;
		return true;
	}
}