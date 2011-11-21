
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Manu Bhadoria
 */
public class videoSummarize {

    /**
     * @param args the command line arguments
     */
    static RandomAccessFile fos;
    
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
         fos = new RandomAccessFile("videoOutput.rgb", "rw");
        
        //Thread soundShotThread = new Thread(new PlaySound(audioFileName));
        Thread videoShotThread = new Thread(new videoToShots(videoFileName));
        
        //soundShotThread.start();
        videoShotThread.start();

        try {
            //delay for one second
            //Thread.sleep(500000);
            //soundShotThread.join();
            videoShotThread.join();
            fos.close();
            System.out.println("Main exiting..");
        } catch (IOException ex) {
            Logger.getLogger(videoSummarize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
        }
    }
}
