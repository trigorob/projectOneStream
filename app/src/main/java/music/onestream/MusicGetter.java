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
    ArrayList<Song> songs;
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

        songs = new ArrayList<Song>();
        for (int i = 0; i < tempFiles.size(); i++)
        {
            Song song = new Song(tempNames.get(i), tempFiles.get(i).toString(), "<Unknown>", "<Unknown>", "LocalRaw", i);
            songs.add(song);
        }

        return;
    }

    public MusicGetter() {
        this.getRawFiles();
    }

    public MusicGetter(String directory)
    {
        if (!directory.equals("Default")) {
            this.songs = fileNames(directory);
            this.type = 1;
        }
        else
        {
            this.getRawFiles();
            this.type = 0;
        }
    }

    public ArrayList<Song> getFileStrings() {
        return this.songs;
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

    public ArrayList<Song> fileNames(String directoryPath) {

        File dir = new File(directoryPath);
        ArrayList<String> filess = new ArrayList<String>();
        ArrayList<String> artists = new ArrayList<String>();
        ArrayList<String> albums = new ArrayList<String>();
        ArrayList<File> files = new ArrayList<File>();
        if(dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            for (File file : listFiles) {
                if (file.isFile()) {
                    String fname = file.getName();
                    fname = getFileIfValid(fname);
                    if (fname != null) {

                        files.add(file);
                        filess.add(fname);
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(file.getPath());
                        String artistName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                        artists.add(artistName);
                        albums.add(albumName);
                    }
                }
            }

            this.songs = new ArrayList<Song>();
            for (int i = 0; i < files.size(); i++) {
                Song song = new Song(filess.get(i), files.get(i).toURI().toString(), "<Unknown>", "<Unknown>", "Local", i);
                songs.add(song);
            }
        }
        return this.songs;
    }

}
