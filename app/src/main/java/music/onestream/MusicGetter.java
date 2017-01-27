package music.onestream;

/**
 * Created by ruspe_000 on 2017-01-26.
 */

import android.util.ArraySet;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class MusicGetter {

    //"src/main/res/foodlists/Berries.txt"
    String[] files;
    String directory = "./app/src/main/res/raw/";

    public MusicGetter(ArrayList<File> f) {
        this.files = fileNames("./app/src/main/res/raw/");
    }

    public String[] getFileStrings() {
        return files;
    }

    public String getDirectory()
    {
        return this.directory;
    }

    public static String[] fileNames(String directoryPath) {

        File dir = new File(directoryPath);
        Collection<String> files = new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                if(file.isFile()) {
                    String fname = file.getName();
                    String[] s = fname.split(".mp3");
                    fname = s[0];
                    System.out.println(fname);
                    files.add(fname);
                }
            }
        }
        return files.toArray(new String[]{});
    }

    public static Integer[] getFiles(String directoryPath) {

        File dir = new File(directoryPath);
        Collection<File> files = new ArrayList<File>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                if(file.isFile()) {
                    files.add(file);
                }
            }
        }
        return files.toArray(new Integer[]{});
    }

    public File selectSong(int numItems) throws IOException{

        File f = new File(files[numItems]);
        return f;
    }

    public File selectRandomSong() throws IOException{
        File f = new File(files[(int)(Math.random()*files.length)]);
        return f;
    }

    public static void main(String[] args) throws IOException {
        MusicGetter fs = new MusicGetter(null);
        System.out.println(fs.selectSong(0));
        System.out.println(fs.selectRandomSong());

    }

}
