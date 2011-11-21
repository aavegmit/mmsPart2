
import java.io.FileNotFoundException;

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
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        // get the command line parameters
        if (args.length < 3) {
            System.err.println("Invalid number of arguments");
            return;
        }
        String videoFileName = args[0];
        String audioFileName = args[1];
        int percentage = Integer.parseInt(args[2]);

	AudioLevels al = new AudioLevels(audioFileName) ;
	al.writeToRawFile(31150, 1) ;
	System.out.println("====++++++===================") ;
	al.writeToRawFile(211500, 7) ;
	al.writeToWavFile() ;
//	System.out.println(al.getVideoFrameNo(22050)) ;
//	shotInfo si = new shotInfo() ;
//	si.numFrames = 24*500;
//	videoToShots.shotHashMap.put(0, si) ;
//	al.shorten() ;

        
        //Thread soundShotThread = new Thread(new PlaySound(audioFileName));
//        Thread videoShotThread = new Thread(new videoToShots(videoFileName));
//        
//        //soundShotThread.start();
//        videoShotThread.start();
//
//        try {
//            //delay for one second
//            //Thread.sleep(500000);
//            //soundShotThread.join();
//            videoShotThread.join();
//            System.out.println("Main exiting..");
//        } catch (InterruptedException e) {
//        }
    }
}
