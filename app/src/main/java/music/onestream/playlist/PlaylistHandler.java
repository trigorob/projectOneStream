package music.onestream.playlist;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;

import com.google.gson.Gson;
import com.spotify.sdk.android.player.Connectivity;

import music.onestream.musicgetter.ArtistAlbumMusicLoader;
import music.onestream.musicgetter.SoundCloudMusicGetter;
import music.onestream.song.Song;
import music.onestream.activity.OneStreamActivity;
import music.onestream.musicgetter.LocalMusicGetter;
import music.onestream.musicgetter.MusicGetterHandler;
import music.onestream.musicgetter.SpotifyMusicGetter;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;
import music.onestream.util.CredentialsHandler;
import music.onestream.util.JSONExtractor;
import music.onestream.util.RestServiceActionsHandler;
import music.onestream.util.MusicSorter;
import music.onestream.util.PlayerActionsHandler;
import music.onestream.util.PlaylistSorter;
import music.onestream.util.TinyDB;

public class PlaylistHandler implements AsyncResponse {

    private String domain;
    private String directory;
    private Boolean directoryChanged = false;
    private String sortType;
    private Boolean spotifyLoginChanged;
    private Boolean soundCloudLoginChanged;

    private static RestServiceActionsHandler restActionHandler;

    private PlayerActionsHandler playerHandler;
    private MusicGetterHandler musicGetterHandler;

    private Playlist listContent;
    private Playlist spotifyListContent;
    private Playlist soundCloudListContent;
    private Playlist combinedList;
    private static ArrayList<Playlist> playlists;
    private static ArrayList<Playlist> artists;
    private static ArrayList<Playlist> albums;

    private boolean playlistCachingOn;

    private ArrayList<Song> currentSongs;

    private Context context;
    private static PlaylistHandler instance;
    private int totalDirectories = -1;


    protected PlaylistHandler() {

    }

    public static PlaylistHandler initPlaylistHandler(Context appContext, PlayerActionsHandler playerHandler,
                           String type, String directory, boolean directoryChanged, String domain,
                                      boolean spotifyLoginChanged, boolean soundCloudLoginChanged, boolean cachePlaylists) {

        if (instance == null)
        {
            instance = new PlaylistHandler();
            instance.musicGetterHandler = new MusicGetterHandler(instance);
        }

        instance.playlistCachingOn = cachePlaylists;
        instance.directory = directory;
        instance.context = appContext;
        instance.playerHandler = playerHandler;
        instance.sortType = type;
        instance.directory = directory;
        instance.directoryChanged = directoryChanged;
        instance.domain = domain;
        instance.spotifyLoginChanged = spotifyLoginChanged;
        instance.soundCloudLoginChanged = soundCloudLoginChanged;

        instance.initSongLists();
        return instance;
    }

    public Playlist getList(String type) {
        if (type.equals(Constants.local))
        {
            return listContent;
        }
        else if (type.equals(Constants.spotify))
        {
            return spotifyListContent;
        }
        else if (type.equals(Constants.library))
        {
            return combinedList;
        }
        else if (type.equals(Constants.soundCloud))
        {
            return soundCloudListContent;
        }
        else
        {
            return null;
        }
    }

    public void setCurrentSongs(ArrayList<Song> list) {
        this.currentSongs = list;
    }

    public ArrayList<Song> getCurrentSongs()
    {
        return currentSongs;
    }

    public void getMusicFromDirectory(String dir)
    {
        if (totalDirectories == -1)
        {
            totalDirectories = 1;
        }
        musicGetterHandler.addLocalMusicGetter(new LocalMusicGetter(dir), dir);
        musicGetterHandler.initLocalMusicGetter(dir);
    }

    public void sortAllLists(String type)
    {
        sortLists(type, Constants.local);
        sortLists(type, Constants.spotify);
        sortLists(type, Constants.playlists);
        sortLists(type, Constants.library);
        sortLists(type, Constants.albums);
        sortLists(type, Constants.artists);
    }

    public void sortLists(String type, String list) {

        MusicSorter ms;
        Object[] retVal;
        if (list.equals(Constants.local) && listContent != null)
        {
            ms = new MusicSorter(listContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            listContent.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifyLocalAdapter();
        }
        else if (spotifyListContent != null && spotifyListContent.size() > 0
                && list.equals(Constants.spotify))
        {
            ms = new MusicSorter(spotifyListContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            spotifyListContent.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifySpotifyAdapter();
        }

        else if (soundCloudListContent != null && soundCloudListContent.size() > 0
                && list.equals(Constants.soundCloud))
        {
            ms = new MusicSorter(soundCloudListContent.getSongInfo(), type);
            retVal = ms.getRetArr();
            soundCloudListContent.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifySoundCloudAdapter();
        }

        else if (playlists != null && list.equals(Constants.playlists))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(playlists, type);
            retVal = ps.getRetArr();
            playlists = ((ArrayList<Playlist>) retVal[0]);
        }

        else if (combinedList != null && list.equals(Constants.library))
        {

            ms = new MusicSorter(combinedList.getSongInfo(), type);
            retVal = ms.getRetArr();
            combinedList.setSongInfo((ArrayList<Song>) retVal[0]);
            OneStreamActivity.notifyLibraryAdapter();
        }

        else if (artists != null && list.equals(Constants.artists))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(artists, type);
            retVal = ps.getRetArr();
            artists = ((ArrayList<Playlist>) retVal[0]);
            OneStreamActivity.notifyArtistsAdapter();
        }

        else if (albums != null && list.equals(Constants.albums))
        {

            PlaylistSorter ps;
            ps = new PlaylistSorter(albums, type);
            retVal = ps.getRetArr();
            albums = ((ArrayList<Playlist>) retVal[0]);
            OneStreamActivity.notifyAlbumsAdapter();
        }

    }

    public void getCachedLists() {
        TinyDB tinyDB = new TinyDB(context);
        ArrayList<Object> cachedLists = (tinyDB.getListObject(Constants.library, Object.class));
        Gson gson = new Gson();
        if (cachedLists != null && cachedLists.size() > 0)
        {
            combinedList = JSONExtractor.processPlaylistJSON(gson.toJson(cachedLists)).get(0);
        }
        cachedLists = (tinyDB.getListObject(Constants.spotify, Object.class));
        if (cachedLists != null && cachedLists.size() > 0)
        {
            spotifyListContent = JSONExtractor.processPlaylistJSON(gson.toJson(cachedLists)).get(0);
        }
        cachedLists = (tinyDB.getListObject(Constants.soundCloud, Object.class));
        if (cachedLists != null && cachedLists.size() > 0)
        {
            soundCloudListContent = JSONExtractor.processPlaylistJSON(gson.toJson(cachedLists)).get(0);
        }
        cachedLists = (tinyDB.getListObject(Constants.local, Object.class));
        if (cachedLists != null && cachedLists.size() > 0)
        {
            listContent = JSONExtractor.processPlaylistJSON(gson.toJson(cachedLists)).get(0);
        }
        cachedLists =(tinyDB.getListObject(Constants.playlists, Object.class));
        if (cachedLists != null && cachedLists.size() > 0)
        {
            playlists = JSONExtractor.processPlaylistJSON(gson.toJson(cachedLists));
        }
    }

    public void initSongLists() {

        //Only do this on first run!
        if (playlistCachingOn && combinedList == null) {
            getCachedLists();
        }

        if (listContent == null)
        {
            listContent = new Playlist();
        }

        if (combinedList == null)
        {
            combinedList = new Playlist();
        }

        if (listContent.size() == 0 || isDirectoryChanged()) {
            for (Song song: listContent.getSongInfo()) {
                combinedList.removeSongItem(song);
            }
            listContent.setSongInfo(new ArrayList<Song>());
            getMusicFromDirectory(directory);
            directoryChanged();
        }

        if (spotifyListContent == null)
        {
            spotifyListContent = new Playlist();
        }
        if (soundCloudListContent == null)
        {
            soundCloudListContent = new Playlist();
        }
        if (artists == null || albums == null)
        {
            artists = new ArrayList<Playlist>();
            albums = new ArrayList<Playlist>();
        }
        getSpotifyLibrary();
        getSoundCloudLibrary();

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
        restActionHandler = null;
    }

    public Boolean isDirectoryChanged() {
        return directoryChanged;
    }

    public void directoryChanged() {
        directoryChanged = !directoryChanged;
    }

    public void getRemotePlaylists(String domain) {
        if (restActionHandler == null && isConnected()) {
            Object[] params = new Object[2];
            params[0] = Constants.getPlaylists;
            params[1] = domain;
            restActionHandler = new RestServiceActionsHandler();
            restActionHandler.SAR = this;
            restActionHandler.execute(params);
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
        final String accessToken = CH.getToken(context, Constants.spotify);

        if (accessToken != null && isConnected() && spotifyLoginChanged) {
            spotifyLoginChanged = false;
            spotifyListContent = new Playlist();
            musicGetterHandler.addSpotifyMusicGetter(new SpotifyMusicGetter(accessToken, this));
            musicGetterHandler.initSpotifyMusicGetter();
        }

    }

    public void getSoundCloudLibrary() {
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(context, Constants.soundCloud);

        if (accessToken != null && isConnected() && soundCloudLoginChanged) {
            soundCloudLoginChanged = false;
            soundCloudListContent = new Playlist();
            musicGetterHandler.addSoundCloudMusicGetter(new SoundCloudMusicGetter(accessToken, this));
            musicGetterHandler.initSoundCloudMusicGetter();
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
        else if (type.equals(Constants.restServiceActionsHandler))
        {
            playlists = (ArrayList<Playlist>) retVal;
            sortLists(sortType, Constants.playlists);
            OneStreamActivity.initPlaylistAdapter(context);
        }
        else if (type.equals(Constants.artistsAlbumsMusicLoader)) {
            OneStreamActivity.notifyArtistsAdapter();
            OneStreamActivity.notifyAlbumsAdapter();
        }

        else if (type.equals(Constants.musicLoaderService)) {
            String nestedDirectory = (String) retVal;
            LocalMusicGetter directoryContent =
                    ((LocalMusicGetter) musicGetterHandler.getLocalMusicGetter().get(nestedDirectory));
            ArrayList<Song> totalListContent = directoryContent.getSongs();
            listContent.addSongs(totalListContent);
            combinedList.addSongs(totalListContent);
            OneStreamActivity.notifyLocalAdapter();
            OneStreamActivity.notifyLibraryAdapter();

            totalDirectories--;
            for (File file: directoryContent.getDirectories())
            {
                getMusicFromDirectory(file.getPath());
            }
            totalDirectories += directoryContent.getDirectories().size();
            if (totalDirectories == 0) {
                sortLists(sortType, Constants.local);
                sortLists(sortType, Constants.library);
                OneStreamActivity.notifyLocalAdapter();
                OneStreamActivity.notifyLibraryAdapter();
                addToArtistsAlbums(listContent.getSongInfo(), this);
            }
        } else if (type.equals(Constants.soundCloudMusicGetter)) {
            processRemoteSongs((ArrayList<Song>)retVal, soundCloudListContent);
        } else if (type.equals(Constants.spotifyMusicGetter)) {
            processRemoteSongs((ArrayList<Song>)retVal, spotifyListContent);
        }
        if (playlistCachingOn)
        {
            cacheResult(type);
        }
    }

    private void processRemoteSongs(ArrayList<Song> retVal, Playlist songLocation) {
        for (Song song: retVal)
            if (songLocation.getSongInfo().contains(song))
            {
                retVal.remove(song);
            }
        songLocation.addSongs(retVal);
        combinedList.addSongs(retVal);


        if (retVal.size() < 50) {
            sortLists(sortType, Constants.spotify);
            sortLists(sortType, Constants.soundCloud);
            sortLists(sortType, Constants.library);
            addToArtistsAlbums(songLocation.getSongInfo(), this);
        }
        OneStreamActivity.notifySoundCloudAdapter();
        OneStreamActivity.notifyLibraryAdapter();
        OneStreamActivity.notifySpotifyAdapter();
    }

    //Dont calculate artist/albums: Do that at runtime for sanity purposes
    public void cacheResult(String type) {
        ArrayList<Object> listContainer = new ArrayList<Object>();
        if (type.equals(Constants.spotifyMusicGetter)) {
            listContainer.add(spotifyListContent);
            cachePlaylist(listContainer, Constants.spotify);
            listContainer.remove(0);
            listContainer.add(combinedList);
            cachePlaylist(listContainer, Constants.library);
        }
        else if (type.equals(Constants.soundCloudMusicGetter)) {
            listContainer.add(soundCloudListContent);
            cachePlaylist(listContainer, Constants.soundCloud);
            listContainer.remove(0);
            listContainer.add(combinedList);
            cachePlaylist(listContainer, Constants.library);
        }
        else if (type.equals(Constants.musicLoaderService)) {
            listContainer.add(listContent);
            cachePlaylist(listContainer, Constants.local);
            listContainer.remove(0);
            listContainer.add(combinedList);
            cachePlaylist(listContainer, Constants.library);
        }
        if (type.equals(Constants.restServiceActionsHandler)) {
            listContainer.add(playlists);
            cachePlaylist(listContainer, Constants.playlists);
        }
    }

    public void cachePlaylist(ArrayList<Object> playlists, String type) {
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListObject(type, playlists);
    }


    public static void addToArtistsAlbums(ArrayList<Song> songs, AsyncResponse SAR) {
        Object[] params = new Object[3];
        params[0] = artists;
        params[1] = albums;
        params[2] = songs;
        ArtistAlbumMusicLoader aaml = new ArtistAlbumMusicLoader();
        aaml.SAR = SAR;
        aaml.execute(params);
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
    public ArrayList<Playlist> getArtists() {
        return artists;
    }
    public ArrayList<Playlist> getAlbums() {
        return albums;
    }

}


