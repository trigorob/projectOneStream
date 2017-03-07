package music.onestream.playlist;

import java.util.ArrayList;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.spotify.sdk.android.player.Connectivity;

import music.onestream.song.Song;
import music.onestream.activity.OneStreamActivity;
import music.onestream.musicgetter.LocalMusicGetter;
import music.onestream.musicgetter.MusicGetterHandler;
import music.onestream.musicgetter.SpotifyMusicGetter;
import music.onestream.util.AsyncResponse;
import music.onestream.util.CredentialsHandler;
import music.onestream.util.DatabaseActionsHandler;
import music.onestream.musicgetter.MusicLoaderService;
import music.onestream.util.MusicSorter;
import music.onestream.util.PlayerActionsHandler;
import music.onestream.util.PlaylistSorter;

public class PlaylistHandler implements AsyncResponse {

    private static int totalLocalSongs = 0;

    private String domain;
    private String directory;
    private Boolean directoryChanged = false;
    private String sortType;
    private static DatabaseActionsHandler dba;

    private PlayerActionsHandler playerHandler;
    private MusicGetterHandler musicGetterHandler;

    private static Playlist listContent;
    private static Playlist spotifyListContent;
    private static ArrayList<Playlist> playlists;
    private static Playlist combinedList;
    private final ArrayList<ArrayAdapter> adapters;

    private Context context;


    public PlaylistHandler(Context appContext, PlayerActionsHandler playerHandler,
                           String type, String directory, boolean directoryChanged, String domain,
    ArrayList<ArrayAdapter> adapters) {
        this.context = appContext;
        this.playerHandler = playerHandler;
        this.sortType = type;
        this.directory = directory;
        this.directoryChanged = directoryChanged;
        this.domain = domain;
        this.adapters = adapters;

        this.musicGetterHandler = new MusicGetterHandler();
        initSongLists();
    }

    public Playlist getList(String type) {
        if (type.equals("Local"))
        {
            return listContent;
        }
        else if (type.equals("Spotify"))
        {
            return spotifyListContent;
        }
        else
        {
            return null;
        }
    }

    public void setMusicDir(String dir)
    {

        musicGetterHandler.setLocalMusicGetter(new LocalMusicGetter(dir));
        musicGetterHandler.initLocalMusicGetter();

        ArrayList<Song> totalListContent = musicGetterHandler.getLocalMusicGetter().getSongs();
        totalLocalSongs = totalListContent.size();
        listContent.setSongInfo(new ArrayList<Song>());
        int localSongOffset= 0;
        while (localSongOffset < totalListContent.size()) {
            Object[] params = new Object[3];
            params[0] = totalListContent;
            params[1] = listContent;
            params[2] = localSongOffset;
            MusicLoaderService mls = new MusicLoaderService();
            mls.SAR = this;
            mls.execute(params);
            localSongOffset+=20;
            OneStreamActivity.notifyAdapters();
        }
    }

    public static Playlist getCombinedList() {
        return combinedList;
    }

    public void sortAllLists(String type)
    {
        sortLists(type, "Local");
        sortLists(type, "Spotify");
        sortLists(type, "Playlists");
    }

    public void sortLists(String type, String list) {

        MusicSorter ms = null;
        Object[] retVal = null;
        if (list.equals("Local") && listContent != null)
        {
            ms = new MusicSorter(listContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            listContent.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifyAdapters();
        }
        else if (spotifyListContent != null && spotifyListContent.size() > 0 && list.equals("Spotify"))
        {
            ms = new MusicSorter(spotifyListContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            spotifyListContent.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifyAdapters();
        }

        else if (playlists != null && list.equals("Playlists"))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(playlists, type);
            retVal = ps.getRetArr();
            playlists = ((ArrayList<Playlist>) retVal[0]);

        }
        OneStreamActivity.notifyAdapters();
    }

    public void initSongLists() {

        if (listContent == null)
        {
            listContent = new Playlist();
        }

        if (listContent.size() == 0 || isDirectoryChanged()) {
            setMusicDir(directory);
            directoryChanged();
        }

        if (spotifyListContent == null)
        {
            spotifyListContent = new Playlist();
        }
        getSpotifyLibrary();

        if (combinedList == null)
        {
            combinedList = new Playlist();
        }

        if (playlists == null || playlists.size() == 0)
        {
            playlists = new ArrayList<Playlist>();
            getRemotePlaylists(getDomain());
        }
    }

    //Only call this when you change domains
    public static void resetPlaylists()
    {
        playlists = null;
        dba = null;
    }

    public Boolean isDirectoryChanged() {
        return directoryChanged;
    }

    public void directoryChanged() {
        if (directoryChanged) {
            directoryChanged = false;
            return;
        }
        directoryChanged = true;
    }

    public void getRemotePlaylists(String domain) {
        if (dba == null && isConnected()) {
            Object[] params = new Object[2];
            params[0] = "GetPlaylists";
            params[1] = domain;
            dba = new DatabaseActionsHandler();
            dba.SAR = this;
            dba.execute(params);
        }
    }

    public Boolean isConnected() {
        return !playerHandler.getNetworkConnectivity(context).equals(Connectivity.OFFLINE);
    }

    public String getDomain() {
        return this.domain;
    }

    public void getSpotifyLibrary() {
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(context, "Spotify");

        if (accessToken != null && isConnected() && spotifyListContent.size() == 0) {
            spotifyListContent = new Playlist();
            musicGetterHandler.setSpotifyMusicGetter(new SpotifyMusicGetter(accessToken, this));
            musicGetterHandler.initSpotifyMusicGetter();
            playerHandler.initSpotifyPlayer(accessToken);
        }

    }

    //Called when threads return
    @Override
    public void processFinish(Object[] result) {
        String type = (String) result[0];
        Object retVal = result[1];
        if (retVal == null || type == null)
        {
            return;
        }
        //Case where retrieved from DB. Only DB lists are playlist array
        else if (type.equals("DatabaseActionsHandler"))
        {
            playlists = (ArrayList<Playlist>) retVal;
            sortLists(sortType, "Playlists");
            OneStreamActivity.initPlaylistAdapter(context);
            OneStreamActivity.notifyAdapters();
        }

        else if (type.equals("MusicLoaderService")) {
            combinedList.addSongs(listContent.getSongInfo());
            if (listContent.size() == totalLocalSongs) {
                sortLists(sortType, "Local");
            }
            OneStreamActivity.notifyAdapters();
        } else if (type.equals("SpotifyMusicGetter")) {
            ArrayList<Song> tempList = (ArrayList<Song>) retVal;
            if (tempList.size() < 20) {
                sortLists(sortType, "Spotify");
            }

            for (Song song: tempList)
            if (!spotifyListContent.getSongInfo().contains(song))
            {
                spotifyListContent.addSong(song);
                combinedList.addSong(song);
            }
            OneStreamActivity.notifyAdapters();

        }
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

}


