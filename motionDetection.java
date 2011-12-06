
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Random;
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
public class motionDetection {

    static int blockSize = 16;
    static int K = 16;
    static double thresholdDistance = 70;

    public static void motionDetetcionAlgo() {
        int index = 0;
        try {
            System.out.println("MOTION DETECTION ALGO STARTED...........................");
            //Thread.currentThread().sleep(2);
            RandomAccessFile fis = new RandomAccessFile(videoToShots.file, "r");
            Random randomNum = new Random();
            byte prevRGBFrame[] = new byte[videoToShots.Height * videoToShots.Width * 3];
            byte currRGBFrame[] = new byte[videoToShots.Height * videoToShots.Width * 3];
            double prevYUVFrame[] = new double[videoToShots.Height * videoToShots.Width];
            double currYUVFrame[] = new double[videoToShots.Height * videoToShots.Width];

            for (Map.Entry<Integer, shotInfo> entry : videoToShots.shotHashMap.entrySet()) {
                int startPoint = entry.getKey();
                int length = startPoint + entry.getValue().numFrames;
                //System.out.println("KEY FRAME: "+startPoint+" Till What Frame: "+length);
                //int length = startPoint + 480;
                fis.seek(((long)startPoint * (long)videoToShots.Height * (long)videoToShots.Width * 3));
                fis.read(prevRGBFrame, 0, videoToShots.Height * videoToShots.Width * 3);
                prevYUVFrame = videoToShots.convertToYUV(prevRGBFrame);
                for (int i = startPoint + 1; i < length; i+=6) {
                    //prevYUVFrame = videoToShots.convertToYUV(prevRGBFrame);
                    //index = i;
                    fis.seek((long)i*(long)videoToShots.Height*(long)videoToShots.Width*3);
                    fis.read(currRGBFrame, 0, videoToShots.Height * videoToShots.Width * 3);
                    currYUVFrame = videoToShots.convertToYUV(currRGBFrame);
                    
                    double distance = 0, temp = 0, counter=0;
                    for (int j = 0; j < 16; j++) {
                        int startHeight = randomNum.nextInt(videoToShots.Height - blockSize);
                        int startWidth = randomNum.nextInt(videoToShots.Width - blockSize);
                        temp = findMacroBlockAndComputeDistance(startHeight, startWidth, prevYUVFrame, currYUVFrame);
                        if(temp < 0)
                            counter++;
                        else{
                            distance+=temp;
                        }
                    }
//                    System.out.println("Distance: "+distance);
                    //setting as key frame as distance is above threshold
                    if(distance > thresholdDistance ) {//|| counter >= 15){
                        //make curr frame a key frame
                        entry.getValue().addKeyToKeyFramesHashMap(i);
//                        System.out.println("Threshold crossed...Distance is "+distance);
                    }
                    System.arraycopy(currYUVFrame, 0, prevYUVFrame, 0, currYUVFrame.length);
                }
//                break;
            }
            fis.close();
        //} catch (InterruptedException ex) {
          //  Logger.getLogger(motionDetection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //System.out.println("THIS IS THE PROBLEM(index) : ("+index+") "+(long)(index*videoToShots.Height*videoToShots.Width*3));
            Logger.getLogger(motionDetection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //reads in 16x16 block of data from previousYUV frame and looks up into currentYUV frame
    //if found exactly the same then find the distance of this 16x16 macro block with the 16x16 currFrame
    //if less than threshold, do nothing
    //else MAKE IT A KEY FRAME
    public static double findMacroBlockAndComputeDistance(int startHeight, int startWidth, double prevYUVFrame[], double currYUVFrame[]) {
        double distance = 0;
        int startHeightK, startWidthK, endHeightK, endWidthK;
        double macroBlock[][] = new double[blockSize][blockSize];
//        System.out.println("...............Printing macro block at : "+startHeight+" "+startWidth);
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                //NEED TO COMPUTE FROM WHERE WE NEED TO FIND DATA????
                macroBlock[i][j] = prevYUVFrame[((startHeight + i) * videoToShots.Width) + startWidth + j];
                //System.out.print(macroBlock[i][j]+" ");
            }
          //  System.out.println();
        }

        //got the macro block and now set up the region in which it will be searched in currYUV Frame
        startHeightK = startHeight - K;
        if (startHeightK < 0) {
            startHeightK = 0;   
        }
        startWidthK = startWidth - K;
        if (startWidthK < 0) {
            startWidthK = 0;
        }
        endHeightK = (startHeight + blockSize) + K;
        if (endHeightK > videoToShots.Height) {
            endHeightK = videoToShots.Height;
        }
        endWidthK = (startWidth + blockSize) + K;
        if (endWidthK > videoToShots.Width) {
            endWidthK = videoToShots.Width;
        }
        //(0,0) && (42,32)
        //System.out.println("K area is in the region(start, end): ("+startHeightK+","+startWidthK+") ("+endHeightK+","+endWidthK+")");
        //now search
        distance = findMacroBlockInKArea(startHeight, startWidth, macroBlock, startHeightK, startWidthK, endHeightK, endWidthK, currYUVFrame);
        return distance;
    }

    public static double findMacroBlockInKArea(int startHeight, int startWidth, double macroBlock[][], int startHeightK, int startWidthK, int endHeightK, int endWidthK, double currYUVFrame[]) {

        for (int i = startHeightK; i < endHeightK-blockSize; i++) {
            for (int j = startWidthK; j < endWidthK-blockSize; j++) {
                double sum = 0;
                for (int m = 0; m < blockSize; m++) {
                    for (int n = 0; n < blockSize; n++) {
                        sum+=Math.abs((macroBlock[m][n]-currYUVFrame[((i+m)*videoToShots.Width)+j+n]));
                    }
                }
                //if(i==startHeight && j==startWidth)
                    //System.out.println("Sum for macroBlock is: "+sum);
                if(sum < 25){
//                    System.out.println("MATCH FOUND!!!!!");
//                    System.out.println("Pos : ("+i+","+j+")");
                    double x1, x2, y1, y2;
                    x2 = i+blockSize/2.0;
                    y2 = j+blockSize/2.0;
                    x1 = startHeight+blockSize/2.0;
                    y1 = startWidth+blockSize/2.0;
                    
                    return Math.sqrt(Math.pow(y2-y1, 2) + Math.pow(x2-x1, 2));
                }
            }
        }
        return -1;
    }
}
