package music.onestream;

/**
 * Created by ruspe_000 on 2017-01-26.
 */

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

public class MusicGetter {
    String[] files;
    Integer[] fileData;
    URI[] fileURI;
    static String testDirectory = "./app/src/main/res/raw/";
    String directory = "R.raw";


    public void getRawFiles() {
        Field[] fields = R.raw.class.getFields();
        ArrayList<Integer> tempFiles = new ArrayList<Integer>();
        ArrayList<String> tempNames = new ArrayList<String>();
        for(Field f : fields)
            try {
                tempFiles.add(f.getInt(null));
                if (f!= null && f.getName() != null) {
                    tempNames.add(f.getName());
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) { }

        fileData = new Integer[tempFiles.size()];
        files = new String[tempFiles.size()];
        for (int i = 0; i < tempFiles.size(); i++)
        {
            fileData[i] = tempFiles.get(i);
            files[i] = tempNames.get(i);
        }

        return;
    }

    public MusicGetter() {
        this.getRawFiles();
        this.fileURI = null;
    }

    public Integer[] getFileData() {
        return this.fileData;
    }

    public MusicGetter(String directory)
    {
        if (!directory.equals("Default")) {
            this.files = fileNames(directory);
            this.fileURI = getFiles(directory);
            this.fileData = null;
        }
        else this.getRawFiles();
    }

    public String[] getFileStrings() {
        return this.files;
    }

    public String getDirectory()
    {
        return this.directory;
    }

    public String getTestDirectory()
    {
        return testDirectory;
    }

    public String getFileIfValid(String fname){
        String[] acceptedTypes = {".mp3", ".wav", "mp4", ".3gp", ".flac", ".mid", ".ogg", ".mkv"};
        for (int i = 0; i < acceptedTypes.length; i++)
        {
            String[] str = fname.split(acceptedTypes[i]);
            if (!str[0].equals(fname))
            {
                return str[0];
            }
        }

        return null;
    }

    public String[] fileNames(String directoryPath) {

        File dir = new File(directoryPath);
        Collection<String> files = new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                if(file.isFile()) {
                    String fname = file.getName();
                    fname = getFileIfValid(fname);
                    if (fname != null) {
                        files.add(fname);
                    }
                }
            }
        }
        this.files = files.toArray(new String[]{});
        return this.files;
    }

    public URI[] getFiles(String directoryPath) {

        File dir = new File(directoryPath);
        ArrayList<File> files = new ArrayList<File>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                if(file.isFile() && (getFileIfValid(file.getName()) != null)) {
                    files.add(file);
                }
            }
        }
        URI[] fileInts = new URI[files.size()];
        for (int i = 0; i < files.size(); i++)
        {
            fileInts[i] = files.get(i).toURI();
        }
        this.fileURI = fileInts;
        return this.fileURI;
    }

    public File selectSong(int numItems) throws IOException{

        File f = new File(files[numItems]);
        return f;
    }

    public int selectRandomSongAsInt() throws IOException{
        return (int)(Math.random()*files.length);
    }

    public File selectRandomSong() throws IOException{
        File f = new File(files[(int)(Math.random()*files.length)]);
        return f;
    }

    public static void main(String[] args) throws IOException {
        MusicGetter fs = new MusicGetter(testDirectory);
        System.out.println(fs.fileNames(fs.getTestDirectory())[1]);
        System.out.println(fs.selectSong(0));
        System.out.println(fs.selectRandomSong());

    }

}
