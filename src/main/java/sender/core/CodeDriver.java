package sender.core;

import javax.sound.sampled.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeDriver {

	private boolean isServerRunning = false;
	private AudioSender audioSender;

	private Mixer.Info [] mixerInfo;
	private Mixer.Info selectedMixerInfo;

	private Line.Info [] dataLinesInfo;
	private Line.Info selectedDataLineInfo;

	private AudioFormat[] formats;
	private AudioFormat selectedFormat;

	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

	public CodeDriver(){

		AudioInfo.displayMixerInfo();
		try {
			// initialize mixer selection. Assume default selection as 0'th element
			mixerInfo = AudioSystem.getMixerInfo();
			selectMixer(0);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public boolean startServer()
	{
		LOGGER.log(Level.INFO, "Trying to start the server...");
		if(isServerRunning)return true;

		audioSender = new AudioSender(selectedDataLineInfo, selectedFormat);

		try{
			audioSender.start();

		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			isServerRunning = false;
			return false;
		}

		isServerRunning = true;
		LOGGER.log(Level.INFO, "Server start successful...");
		return true;
	}

	public boolean stopServer()
	{
		LOGGER.log(Level.INFO, "Trying to stop the server...");
		if(!isServerRunning) return true;

		try{
			if(audioSender!=null){audioSender.kill(); audioSender.join();}

		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			isServerRunning = true;
			return false;
		}

		isServerRunning = false;
		LOGGER.log(Level.INFO, "Server stop successful...");
		return true;
	}

	public void selectMixer(int index) {
		selectedMixerInfo = mixerInfo[index];
		updateLineInfo();
		updateFormatInfo();
	}

	public void selectLine(int index) {
		selectedDataLineInfo = dataLinesInfo[index];
		updateFormatInfo();
	}

	public void selectFormat(int index) {
		selectedFormat = formats[index];
	}

	private void updateLineInfo() {
		Mixer mixer = AudioSystem.getMixer(selectedMixerInfo);
		dataLinesInfo = mixer.getTargetLineInfo();
		selectedDataLineInfo = dataLinesInfo.length > 0 ? dataLinesInfo[0] : null;
	}

	private void updateFormatInfo() {
		if (selectedDataLineInfo!=null && selectedDataLineInfo instanceof DataLine.Info){
			formats = ((DataLine.Info) selectedDataLineInfo).getFormats();
			selectedFormat = formats[0];
		}
		else {
			formats = new AudioFormat[0];
			selectedFormat = null;
		}
	}
	/* getters and setters */

	// render Mixer Info to Strings for UI
	public String [] getMixers() {
		String [] mixersString = new String[mixerInfo.length];
		for(int i=0; i<mixerInfo.length; i++){
			mixersString[i] = mixerInfo[i].toString();
		}
		return  mixersString;
	}

	// render Line Info to Strings for UI
	public String [] getLines() {
		String [] linesString = new String[dataLinesInfo.length];
		for(int i=0; i<dataLinesInfo.length; i++){
			linesString[i] = dataLinesInfo[i].toString();
		}
		return linesString;
	}

	// render Formats to Strings for UI
	public String [] getFormats() {
		String [] formatsString = new String[formats.length];
		for(int i=0; i<formats.length; i++){
			formatsString[i] = formats[i].toString();
		}
		return formatsString;
	}

	public String getMixer() {
		return selectedMixerInfo.toString();
	}

	public String getLine() {
		return selectedDataLineInfo !=null ? selectedDataLineInfo.toString() : "NO DATALINE";
	}

	public String getFormat() {
		return selectedFormat !=null ? selectedFormat.toString() : "NO FORMAT";
	}
}