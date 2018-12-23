package core;

import usefulDS.Connection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioSender extends Thread{

	DatagramSocket ds = null;
	DatagramSocket bCast = null;
	private static boolean status = true;
	String bCastData = "DISCOVER_CONNECT_SERVER_REQUEST";
	String SEPERATOR = "|";
	DatagramPacket bCastPacket;
	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	AudioSender(Integer mainServerPort)
	{
		try {
			 ds = new DatagramSocket();
			 bCast = new DatagramSocket();
			 bCast.setBroadcast(true);
			 bCastData = bCastData + SEPERATOR + mainServerPort;
			 bCastPacket = new DatagramPacket(bCastData.getBytes(), bCastData.getBytes().length, InetAddress.getByName("255.255.255.255"), 7777);
			
			}catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
			}
	}
	
	public void kill()
	{
		status=false;
	}
	
	@Override
	public void run()
	{
		LOGGER.log(Level.INFO, this.getClass().getName()+" : started");
		try {
				/*Initialize Audio Recording sources*/
		        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000,16,2,4,1000,false);
		        TargetDataLine microphone;
		        
		        /*Set Target Data Line*/
		        microphone = AudioSystem.getTargetDataLine(format);
		        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		        microphone = (TargetDataLine) AudioSystem.getLine(info);
		        microphone.open(format);
		
		        int numBytesRead;
		        int CHUNK_SIZE = 1024;
		        
		        int Sz = CHUNK_SIZE < microphone.getBufferSize() ? CHUNK_SIZE : microphone.getBufferSize() ;
		        byte[] audioData = new byte[Sz];
		        microphone.start();
		        
		        
		        while(status)
		        {
		        	numBytesRead = microphone.read(audioData, 0, CHUNK_SIZE);
		        	if(numBytesRead>0)
					SendDataToClients(audioData, numBytesRead);
		        	else
		        	{
		        		microphone.close();
		        		break;
		        	}
		        	audioData = new byte[Sz];
		        }
		
		}catch (Exception e) {
	           	LOGGER.log(Level.INFO, "Exception occured in recording..");
	           	LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
		}

		LOGGER.log(Level.INFO, this.getClass().getName()+" : dead");
	}

	private void SendDataToClients(byte[] audio , int Nbytes)
	{
		if(ds==null)return;
		
		Integer port;
		InetAddress ip;
		
		/* send a broadcast here */
		sendLiveIndicationBroadcast();
		
		for(Connection conn : CodeDriver.connections)
		{
			if(conn==null)continue;
			try
			{
				ip = conn.get_client_ip();
				port = conn.get_client_port();
				
				byte[] myAudio = audio;
				
				DatagramPacket DpSend = new DatagramPacket(myAudio, Nbytes, ip, port);
				
				ds.send(DpSend);
				
			}catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
				status=false;
			}
		}
	}
	
	private void sendLiveIndicationBroadcast()
	{
        //Try the 255.255.255.255 first
        try {
          bCast.send(bCastPacket);
        } catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
        }
	}
}
