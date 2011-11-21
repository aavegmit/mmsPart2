import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.swing.text.Position;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Manu Bhadoria
 */
public class AudioLevels {

    private File audioFile;
    private FileOutputStream rawFile ;
    private FileInputStream rawFileInput;
    private Position curPosition;
    private final int EXTERNAL_BUFFER_SIZE =  524288 ; // 128Kb
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine dataLine ;
    private int rawBytesCount ;
    private float keyFrameThreshold;

    enum Position {

	LEFT, RIGHT, NORMAL
    };

    /**
     * CONSTRUCTOR
     */
    //public PlaySound(InputStream waveStream) {
    public AudioLevels(String audioFileName) {
	this.audioFile = new File(audioFileName);
	this.rawBytesCount = 0 ;
	this.keyFrameThreshold = (float)0.8 ;
	curPosition = Position.NORMAL;

	try {
	    this.rawFile = new FileOutputStream("tempRaw.wav");
	    this.rawFileInput = new FileInputStream("tempRaw.wav") ;
	} catch (Exception e) {
	    System.out.println(e.getMessage()) ;
	}

	this.audioInputStream = null;
	try {
	    audioInputStream = AudioSystem.getAudioInputStream(this.audioFile);
	} catch (UnsupportedAudioFileException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} catch (IOException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	// Obtain the information about the AudioInputStream
	this.audioFormat = audioInputStream.getFormat();
	System.out.println(audioFormat.getFrameSize() + " ,frame rate " + audioFormat.getFrameRate()) ;
	Info info = new Info(SourceDataLine.class, audioFormat);
    }

    public int getVideoFrameNo(int audioFrameNo){
	float timeSeek = (float)(audioFrameNo / audioFormat.getFrameRate()) ;
	return (int)timeSeek*24 ;
    }

    public int getAudioFrameNo(int videoFrameNo){
	float timeSeek = (float)(videoFrameNo / 24 ) ;
	return (int)(timeSeek*audioFormat.getFrameRate()) ;
    }

    public void writeToRawFile(int audioFrameNo, float time){
	int noOfFrame = (int)(time * audioFormat.getFrameRate()) ;
	int initialByte = audioFrameNo*audioFormat.getFrameSize() ;
	int finalByte = initialByte + noOfFrame*audioFormat.getFrameSize() ;
	rawBytesCount += finalByte - initialByte ;
	int readBytes = 0;
	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
	AudioInputStream stream ;
	int tempBytes = finalByte - initialByte ;
	stream = null ;
	try {
	    stream = AudioSystem.getAudioInputStream(this.audioFile);
	} catch (UnsupportedAudioFileException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} catch (IOException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	try {
	    stream.skip(initialByte) ;
	    while (tempBytes > 0) {
		if (tempBytes > this.EXTERNAL_BUFFER_SIZE){
		    readBytes = stream.read(audioBuffer, 0, audioBuffer.length);
		    if (readBytes >= 0) {
			System.out.println(readBytes) ;
			rawFile.write(audioBuffer, 0, this.EXTERNAL_BUFFER_SIZE) ;
		    }
		    tempBytes = tempBytes - this.EXTERNAL_BUFFER_SIZE ;
		}
		else{
		    readBytes = stream.read(audioBuffer, 0, tempBytes);
		    if (readBytes >= 0) {
			System.out.println(readBytes) ;
			System.out.println(audioBuffer[100]) ;
			rawFile.write(audioBuffer,0,tempBytes) ;
		    }
		    tempBytes = 0 ;
		}
	    }
	} catch (IOException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} 
    }

    public void writeToWavFile(){
	try{
	    rawFile.flush() ;
	    rawFile.close() ;
	} catch (IOException e){
	    System.out.println("Exception") ;
	}
	AudioInputStream stream = new AudioInputStream(rawFileInput, audioFormat, rawBytesCount/audioFormat.getFrameSize()) ;
	try{
	    AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File("out.wav"));
	} catch(Exception e){
	}
    }

    public void summarize(int percentage){
	// Find the frames(based on percentage) needed in the summary
	long totalFrames = videoToShots.numFrames * percentage / 100 ;
	// Sort the shots based on weight
	
	// Loop over these shots
	// Sort the key frames based on its weight
	// Loop over the key frames
	// Before adding a frame, check for overlapping
	// If overlapping present, update this list with overlapping
    }


    public void shorten() {
	int readBytes = 0;
	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

	for (Map.Entry<Integer, shotInfo> entry : videoToShots.shotHashMap.entrySet()) {
	    int videoFN = entry.getKey() ;
	    int startFrameNo = getAudioFrameNo(videoFN) ;
	    int lastFrameNo = getAudioFrameNo(videoFN + videoToShots.shotHashMap.get(videoFN).numFrames) ;
	    AudioInputStream stream ;
	    stream = null ;
	    try {
		stream = AudioSystem.getAudioInputStream(this.audioFile);
	    } catch (UnsupportedAudioFileException e1) {
		try {
		    throw new PlayWaveException(e1);
		} catch (PlayWaveException ex) {
		    Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } catch (IOException e1) {
		try {
		    throw new PlayWaveException(e1);
		} catch (PlayWaveException ex) {
		    Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }

	    int tempFrameNo = startFrameNo ;
	    float tempVal = (float)0.0 ;
	    try {
		int tempBytes = (lastFrameNo - startFrameNo) * audioFormat.getFrameSize() ;
		stream.skip(startFrameNo*audioFormat.getFrameSize()) ;
		while (tempBytes > 0) {
		    if (tempBytes > this.EXTERNAL_BUFFER_SIZE){
			readBytes = stream.read(audioBuffer, 0, audioBuffer.length);
			tempBytes = tempBytes - this.EXTERNAL_BUFFER_SIZE ;
		    }
		    else{
			readBytes = stream.read(audioBuffer, 0, tempBytes);
			tempBytes = 0 ;
		    }
		    for(int cn = 0 ; cn < readBytes - 1 ; cn+=2) {
			float audioVal = (float)( ( ( audioBuffer[cn+1] << 8 ) | ( audioBuffer[cn] & 0xff ) ) / 32768.0 ); 
			    if( Math.abs(audioVal) > this.keyFrameThreshold){
				// Add tempFrameNo as key
				System.out.println("New key frame " + tempFrameNo ) ;
			    }
			++tempFrameNo;
		    }
		}
	    } catch (IOException e1) {
		try {
		    throw new PlayWaveException(e1);
		} catch (PlayWaveException ex) {
		    Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } 
	}
    }
} 

