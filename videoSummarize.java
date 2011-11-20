/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.File;

/**
 *
 * @author Manu Bhadoria
 */
public class videoSummarize {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // get the command line parameters
        if (args.length < 2) {
            System.err.println("Invalid number of arguments");
            return;
        }
        String videoFileName = args[0];
        String audioFileName = args[1];

	// Create shots and key frames using histogram technique
	// Pass in the shots and key frames to the audio algo

	AudioLevels al = new AudioLevels(audioFileName) ;
	al.writeToRawFile(22050*40, 20) ;
	al.writeToWavFile() ;
//	al.shorten() ;
//	System.out.println(al.getVideoFrameNo(22050)) ;

    }
}
