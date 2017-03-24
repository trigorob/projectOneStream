package music.onestream.musicgetter;

/**
 * Created by ruspe_000 on 2017-01-26.
 */

import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;


public class LocalMusicGetter extends AsyncTask implements MusicGetter {
    private ArrayList<Song> songs;
    private String directory = Constants.defaultDirectory;
    public AsyncResponse SAR;
    public LocalMusicGetter(String directory)
    {
        this.directory = directory;
    }
    private ArrayList<File> directories = new ArrayList<File>();

    public ArrayList<Song> getSongs() {
        return this.songs;
    }
    public ArrayList<File> getDirectories() {return this.directories;}

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

    public void loadLocalLibrary(String directoryPath) {

        File dir = new File(directoryPath);
        ArrayList<String> filess = new ArrayList<String>();
        ArrayList<String> artists = new ArrayList<String>();
        ArrayList<String> albums = new ArrayList<String>();
        ArrayList<File> files = new ArrayList<File>();
        if(dir.isDirectory()) {
            try {
            File[] listFiles = dir.listFiles();
            int pos = 0;
                while (pos < listFiles.length) {
                        File file = listFiles[pos];
                        if (file.isFile()) {
                            String fname = file.getName();
                            fname = getFileIfValid(fname);
                            if (fname != null) {
                                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                mmr.setDataSource(file.getPath());
                                String artistName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                                fname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                                files.add(file);
                                filess.add(fname);
                                artists.add(artistName);
                                albums.add(albumName);
                            }
                        } else if (file.isDirectory()) {
                            directories.add(file);
                        }
                        pos++;
                    }
            }
            catch (Exception e)
            {

            }
            String artist;
            String album;
            for (int i = 0; i < files.size(); i++) {
                artist = artists.get(i);
                album = albums.get(i);
                if (artist == null ||artist.equals(""))
                {
                    artist = Constants.defaultArtistsAlbumSongName;
                }
                if (album == null || album.equals(""))
                {
                    album =  Constants.defaultArtistsAlbumSongName;
                }
                Song song = new Song(filess.get(i), files.get(i).toURI().toString(),
                        artist, album, Constants.local, 0, files.get(i).getPath());
                songs.add(song);
            }
        }
    }

    @Override
    public void init() {
        this.songs = new ArrayList<Song>();
        if (!directory.equals(Constants.defaultDirectory)) {
            loadLocalLibrary(directory);
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        init();
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = Constants.musicLoaderService;
        retObject[1] = directory;
        SAR.processFinish(retObject);
    }
}
