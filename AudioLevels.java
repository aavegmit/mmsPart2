import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private OutputStream rawFile ;
    private FileInputStream rawFileInput;
    private Position curPosition;
    private final int EXTERNAL_BUFFER_SIZE =  524288 ; // 128Kb
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine dataLine ;
    private int rawBytesCount ;

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

	// opens the audio channel
	this.dataLine = null;
	try {
	    dataLine = (SourceDataLine) AudioSystem.getLine(info);
	    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
	} catch (LineUnavailableException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	if (dataLine.isControlSupported(FloatControl.Type.PAN)) {
	    FloatControl pan = (FloatControl) dataLine.getControl(FloatControl.Type.PAN);
	    if (curPosition == Position.RIGHT) {
		pan.setValue(1.0f);
	    } else if (curPosition == Position.LEFT) {
		pan.setValue(-1.0f);
	    }
	}
    }

    public int getVideoFrameNo(int audioFrameNo){
	float timeSeek = (float)(audioFrameNo / audioFormat.getFrameRate()) ;
	return (int)timeSeek*24 ;
    }

    public void writeToRawFile(int audioFrameNo, float time){
	int noOfFrame = (int)(time * audioFormat.getFrameRate()) ;
	int initialByte = audioFrameNo*audioFormat.getFrameSize() ;
	System.out.println(initialByte) ;
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
			rawFile.write(audioBuffer) ;
		    }
		    tempBytes = tempBytes - this.EXTERNAL_BUFFER_SIZE ;
		}
		else{
		    readBytes = stream.read(audioBuffer, 0, tempBytes);
		    if (readBytes >= 0) {
			rawFile.write(audioBuffer) ;
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
	AudioInputStream stream = new AudioInputStream(rawFileInput, audioFormat, rawBytesCount/audioFormat.getFrameSize()) ;
	try{
	    AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File("out.wav"));
	} catch(Exception e){
	}
    }

    public void shorten() {
	// Starts the music :P
	dataLine.start();
	int readBytes = 0;
	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

	try {
	    while (readBytes != -1) {
		int counter = 0 ;
		readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
		if (readBytes >= 0) {
//		    dataLine.write(audioBuffer, 0, readBytes);
		    float tempLevel = (float)0.0 ;
		    System.out.println("readbytes = " + readBytes) ;
//		    for(int cn = 0 ; cn < readBytes - 1 ; cn+=2) {
//			float audioVal = (float)( ( ( audioBuffer[cn+1] << 8 ) | ( audioBuffer[cn] & 0xff ) ) / 32768.0 ); 
//			if (audioVal != tempLevel){
//			    //			    System.out.println(audioVal + " byte = " + cn ) ;
//			    tempLevel = audioVal ;
//			}
//			else{
//			    ++counter ;
//			}
//		    }
		    System.out.println("==============   " + counter) ;
		    rawFile.write(audioBuffer) ;
		    AudioInputStream stream = new AudioInputStream(rawFileInput, audioFormat, readBytes/audioFormat.getFrameSize()) ;
		    AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File("out.wav"));
		    break ;
		}
	    }
	} catch (IOException e1) {
	    try {
		throw new PlayWaveException(e1);
	    } catch (PlayWaveException ex) {
		Logger.getLogger(AudioLevels.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} finally {
	    // plays what's left and and closes the audioChannel
	    dataLine.drain();
	    dataLine.close();
	}

    }
    }
