package music.onestream;

/**
 * Created by ruspe_000 on 2017-01-26.
 */

import android.media.MediaMetadataRetriever;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

public class MusicGetter {
    String[][] files;
    static String testDirectory = "./app/src/main/res/raw/";
    String directory = "R.raw";
    private int type; //0 => Integer files, 1 => Uri

    public int getType() {
        return type;
    }

    public void getRawFiles() {
        Field[] fields = R.raw.class.getFields();
        ArrayList<Integer> tempFiles = new ArrayList<Integer>();
        ArrayList<String> tempNames = new ArrayList<String>();
        for(Field f : fields)
            try {
                    tempFiles.add(f.getInt(null));
                    if (f != null && f.getName() != null) {
                        tempNames.add(f.getName());
                    }
                //Resource files dont have this data, so we dont need to look for it
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) { }

        files = new String[tempFiles.size()][4];
        for (int i = 0; i < tempFiles.size(); i++)
        {
            files[i][0] = tempNames.get(i);
            files[i][1] = tempFiles.get(i).toString();
            files[i][2] = "<Unknown>";
        }

        return;
    }

    public MusicGetter() {
        this.getRawFiles();
    }

    public MusicGetter(String directory)
    {
        if (!directory.equals("Default")) {
            this.files = fileNames(directory);
            getFiles(directory);
            this.type = 1;
        }
        else
        {
            this.getRawFiles();
            this.type = 0;
        }
    }

    public String[][] getFileStrings() {
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

    public String[][] fileNames(String directoryPath) {

        File dir = new File(directoryPath);
        ArrayList<String> filess = new ArrayList<String>();
        ArrayList<String> artists = new ArrayList<String>();
        if(dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            for (File file : listFiles) {
                if (file.isFile()) {
                    String fname = file.getName();
                    fname = getFileIfValid(fname);
                    if (fname != null) {
                        filess.add(fname);

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(file.getPath());
                        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        artists.add(albumName);
                    }
                }
            }

            this.files = new String[filess.size()][4];
            for (int i = 0; i < files.length; i++) {
                this.files[i][0] = filess.get(i);
                this.files[i][2] = artists.get(i);
            }
        }
        return this.files;
    }

    public void getFiles(String directoryPath) {
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
            this.files[i][1] = files.get(i).toURI().toString();
        }
    }

    public File selectSong(int numItems) throws IOException{

        File f = new File(files[numItems][0]);
        return f;
    }

    public int selectRandomSongAsInt() throws IOException{
        return (int)(Math.random()*files.length);
    }

    public File selectRandomSong() throws IOException{
        File f = new File(files[(int)(Math.random()*files.length)][0]);
        return f;
    }

    public static void main(String[] args) throws IOException {
        MusicGetter fs = new MusicGetter(testDirectory);
        System.out.println(fs.fileNames(fs.getTestDirectory())[1]);
        System.out.println(fs.selectSong(0));
        System.out.println(fs.selectRandomSong());

    }

}
