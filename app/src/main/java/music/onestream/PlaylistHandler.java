package music.onestream;

import java.util.ArrayList;
import android.content.Context;

import com.spotify.sdk.android.player.Connectivity;

public class PlaylistHandler implements AsyncResponse {

    //Increment this after getting spotify songs. TODO: Implement this functionality
    private static int spotifySongOffset= 0;
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
    private static ArrayList<String> playlistNames;
    private static Playlist combinedList;

    private Context context;


    public PlaylistHandler(Context appContext, PlayerActionsHandler playerHandler,
                           String type, String directory, String domain) {
        this.context = appContext;
        this.playerHandler = playerHandler;
        this.sortType = type;
        this.directory = directory;
        this.domain = domain;

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

        ArrayList<Song> totalListContent = musicGetterHandler.getLocalMusicGetter().songs;
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
        }
        else if (spotifyListContent != null && spotifyListContent.size() > 0 && list.equals("Spotify"))
        {
            ms = new MusicSorter(spotifyListContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            spotifyListContent.setSongInfo((ArrayList<Song>) retVal[0]);
        }

        else if (playlists != null && list.equals("Playlists"))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(playlists, type);
            retVal = ps.getRetArr();
            playlists = ((ArrayList<Playlist>) retVal[0]);
            playlistNames = new ArrayList<String>();
            for (int i = 0; i < playlists.size(); i++)
            {
                playlistNames.add(playlists.get(i).getName());
            }
        }
        OneStreamActivity.notifyAdapters();
    }

    public void initSongLists() {
        if (listContent == null)
        {
            listContent = new Playlist();
        }

        if (spotifyListContent == null)
        {
            spotifyListContent = new Playlist();
        }

        if (combinedList == null)
        {
            combinedList = new Playlist();
        }

        if (listContent.size() == 0 || isDirectoryChanged()) {
            setMusicDir(directory);
            directoryChanged();
        }
        getSpotifyLibrary();

        if (playlists == null || playlists.size() == 0)
        {
            playlists = new ArrayList<Playlist>();
            playlistNames = new ArrayList<String>();
            getRemotePlaylists(getDomain());
        }
    }

    //Only call this when you change domains
    public static void resetPlaylists()
    {
        playlists = null;
        playlistNames = null;
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

        if (accessToken != null && isConnected()) {
            musicGetterHandler.setSpotifyMusicGetter(new SpotifyMusicGetter(accessToken, this));
            musicGetterHandler.initSpotifyMusicGetter();
            playerHandler.initSpotifyPlayer(accessToken);
        }

    }

    //Called when threads return
    //Todo: We really really need to refactor to send return value AND what to do with it
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
            for (int i = 0; i < playlists.size(); i++)
            {
                playlistNames.add(playlists.get(i).getName());
            }
            sortLists(sortType, "Playlists");
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
                spotifySongOffset = 1000;
                sortLists(sortType, "Spotify");
            }

            spotifyListContent.addSongs(tempList);
            combinedList.addSongs(tempList);
            OneStreamActivity.notifyAdapters();

        }
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
    public ArrayList<String> getPlaylistNames() {return playlistNames;}

}


