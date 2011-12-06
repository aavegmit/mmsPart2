
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
class shotInfo {

    int numFrames;
    double weight;
    //List<Integer> keyFrames = new ArrayList<Integer>();
    LinkedHashMap<Integer, Integer> keyFrames = new LinkedHashMap<Integer, Integer>();

    //sort keyFrameHashMap
    public static void sortKeyFramesHashMapOnKey() {
        for (Map.Entry<Integer, shotInfo> entry : videoToShots.shotHashMap.entrySet()) {
            Map<Integer, Integer> treeMap = new TreeMap<Integer, Integer>(entry.getValue().keyFrames);
            entry.getValue().keyFrames.clear();
            entry.getValue().keyFrames.putAll(treeMap);
        }

        /*for (Map.Entry<Integer, shotInfo> entry : videoToShots.shotHashMap.entrySet()) {
        System.out.println("Working on: "+ entry.getKey());
        List list = new LinkedList(entry.getValue().keyFrames.entrySet());
        Collections.sort(list, new Comparator() {
        //@Override
        public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());
        }
        });
        entry.getValue().keyFrames.clear();
        //Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
        Map.Entry res = (Map.Entry) it.next();
        entry.getValue().keyFrames.put((Integer)res.getKey(), (Integer)res.getValue());
        }
        }*/
    }

    //sorts the shots based on the computed weights
    public static void sortShotHashMapOnWeight() {
        List list = new LinkedList(videoToShots.shotHashMap.entrySet());
        Collections.sort(list, new Comparator() {
            //@Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry<Integer, shotInfo>) (o2)).getValue().weight).compareTo(((Map.Entry<Integer, shotInfo>) (o1)).getValue().weight);
            }
        });
        videoToShots.shotHashMap.clear();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry res = (Map.Entry) it.next();
            videoToShots.shotHashMap.put((Integer) res.getKey(), (shotInfo) res.getValue());
        }
    }

    // adding key to keyFrames hash map and incrementing weight if exists
    public void addKeyToKeyFramesHashMap(Integer entry) {
        if (keyFrames.containsKey(entry)) {
            int temp = keyFrames.get(entry);
            temp += 1;
            keyFrames.put(entry, temp);
        } else {
            keyFrames.put(entry, 1);
        }
    }

    //computing the weight of each SHOT
    public static void computeShotWeight() {
        for (Map.Entry<Integer, shotInfo> entry : videoToShots.shotHashMap.entrySet()) {
            int sum = 0;
            for (Map.Entry<Integer, Integer> sub_entry : entry.getValue().keyFrames.entrySet()) {
                sum += sub_entry.getValue();
            }
            if((double) entry.getValue().keyFrames.size() > 0)
                entry.getValue().weight = (double) sum / (double) entry.getValue().keyFrames.size();
        }
    }

    //Writing the shot to file and input is shot starting frame and numFrames in it
    public static void writeShotHashMapToFile(int start, int len) {
        try {
            //FileOutputStream fos = null;
            //FileInputStream fis = new FileInputStream(videoToShots.file);
            RandomAccessFile fis = new RandomAccessFile(videoToShots.file, "r");
            //fos = new FileOutputStream("videoOutput.rgb");
            byte temp[] = new byte[videoToShots.Height * videoToShots.Width * 3];
            fis.seek((long)start*(long)videoToShots.Height*(long)videoToShots.Width*3);
            //videoSummarize.fos.seek(videoSummarize.fos.length());
//            System.out.println("Now writing...");
            if((start+len) > videoToShots.numFrames)
                len = (int)videoToShots.numFrames - start;
            while (len != 0) {
                fis.read(temp);
                videoSummarize.fos.write(temp);
                len--;
            }
            fis.close();
        } catch (FileNotFoundException ex) {
	    System.out.println("exception 1") ;
            Logger.getLogger(videoToShots.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e){
	    System.out.println("exception 2") ;
	}
    }
}
