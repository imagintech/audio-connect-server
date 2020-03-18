package sender.core;

import server.ServerInfo;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioSender extends Thread{

	private DatagramSocket ds = null;
	private DatagramSocket bCast = null;
	private DatagramPacket bCastPacket;
	private InetAddress bCastAddress;
	private Integer bCastPort = 7777;
	private Integer bCastAudioPort = 6666;
	private float sampleRate = 16000.0f;
	private float frameRate;

	private TargetDataLine targetDataLine;
	private Line.Info targetDataLineInfo;
	private AudioFormat audioFormat;

	private static boolean status = true;

	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	AudioSender(Line.Info targetDataLineInfo, AudioFormat audioFormat)
	{
		sampleRate = audioFormat.getSampleRate() < 0 ? sampleRate : audioFormat.getSampleRate();
		frameRate = sampleRate;
		this.targetDataLineInfo = targetDataLineInfo;
		this.audioFormat = new AudioFormat(audioFormat.getEncoding(), sampleRate, audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), frameRate, audioFormat.isBigEndian());
	}

	private void initialize() {
		LOGGER.log(Level.INFO, "Initializing Audio Sender...");
		try {

			// try to get the line
			status = true;
			targetDataLine = (TargetDataLine) AudioSystem.getLine(targetDataLineInfo);

			bCastAddress = InetAddress.getByName("255.255.255.255");
			ds = new DatagramSocket();
			bCast = new DatagramSocket();
			bCast.setBroadcast(true);

			ServerInfo bCastLiveData = new ServerInfo("DISCOVER_CONNECT_SERVER_REQUEST");
//			audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000,16,1,2, 16000,false);
			bCastLiveData.setSampleRate(audioFormat.getSampleRate());
			bCastLiveData.setSampleSize(audioFormat.getSampleSizeInBits());
			bCastLiveData.setChannels(audioFormat.getChannels());
			bCastLiveData.setFrameSize(audioFormat.getFrameSize());
			bCastLiveData.setBigEndian(audioFormat.isBigEndian());
			bCastLiveData.setPort(bCastAudioPort);

			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(bStream);
			objectOutput.writeObject(bCastLiveData);
			objectOutput.close();

			byte[] serializedData = bStream.toByteArray();

			bCastPacket = new DatagramPacket(serializedData, serializedData.length, bCastAddress, bCastPort);

		}catch(Exception e)
		{
			LOGGER.log(Level.INFO, "Audio Sender initialization failed...");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
		initialize();
		try {
		        targetDataLine.open(audioFormat);
		
		        int numBytesRead;
		        int CHUNK_SIZE = audioFormat.getFrameSize() * 256;
		        
		        int Sz = CHUNK_SIZE < targetDataLine.getBufferSize() ? CHUNK_SIZE : targetDataLine.getBufferSize() ;
		        byte[] audioData = new byte[Sz];
		        targetDataLine.start();
		        
		        while(status)
		        {
		        	numBytesRead = targetDataLine.read(audioData, 0, CHUNK_SIZE);
		        	if(numBytesRead>0)
					SendDataToClients(audioData, numBytesRead);
		        	else
		        	{
		        		break;
		        	}
		        	audioData = new byte[Sz];
		        }
		
		}catch (Exception e) {
	           	LOGGER.log(Level.INFO, "Exception occurred in recording..");
	           	LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}finally {
			LOGGER.log(Level.INFO, "Closing sockets...");
			targetDataLine.close();
			bCast.close();
			ds.close();
		}

		LOGGER.log(Level.INFO, this.getClass().getName()+" : dead");
	}

	private void SendDataToClients(byte[] audio , int Nbytes)
	{
		if(ds==null)return;
		
		/* send a broadcast here */
		sendLiveIndicationBroadcast();

		/* Broadcast audio to everyone */
		try
		{
			DatagramPacket DpSend = new DatagramPacket(audio, Nbytes, bCastAddress, bCastAudioPort);
			ds.send(DpSend);

		}catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			status=false;
		}

	}
	
	private void sendLiveIndicationBroadcast()
	{
        try {
          bCast.send(bCastPacket);
        } catch (Exception e) {
        	e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
	}
}
