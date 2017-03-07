package music.onestream.musicgetter;

/**
 * Created by ruspe_000 on 2017-01-26.
 */

import android.media.MediaMetadataRetriever;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import music.onestream.R;
import music.onestream.song.Song;

public class LocalMusicGetter implements MusicGetter {
    ArrayList<Song> songs;
    String directory = "N/A";
    public LocalMusicGetter(String directory)
    {
        this.directory = directory;
    }

    public ArrayList<Song> getSongs() {
        return this.songs;
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
                else if (file.isDirectory())
                {
                    fileNames(file.getPath());
                }
            }
            String artist;
            String album;
            String albumArtString;
            for (int i = 0; i < files.size(); i++) {
                artist = artists.get(i);
                album = albums.get(i);
                if (artist == null ||artist.equals(""))
                {
                    artist = "<Unknown>";
                }
                if (album == null || album.equals(""))
                {
                    album = "<Unknown>";
                }
                Song song = new Song(filess.get(i), files.get(i).toURI().toString(),
                        artist, album, "Local", i, files.get(i).getPath());
                songs.add(song);
            }
        }
        return this.songs;
    }

    @Override
    public void init() {
        this.songs = new ArrayList<Song>();
        if (!directory.equals("N/A")) {
            this.songs = fileNames(directory);
        }
    }
}
