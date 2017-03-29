package music.onestream.util;

/**
 * Created by ruspe_000 on 2017-03-17.
 */

public class Constants {
    /* UTIL CONSTANTS */
    public static final String SPOTIFY_TOKEN_NAME = "spotify.credentials.access_token";
    public static final String SOUNDCLOUD_TOKEN_NAME = "soundcloud.credentials.access_token";
    public static final String SPOTIFY_ACCESS_TOKEN = "spotify_access_token";
    public static final String SOUNDCLOUD_ACCESS_TOKEN = "soundcloud_access_token";
    public static final String EXPIRES_AT = "expires_at";
    public static final String CLIENT_ID = "0785a1e619c34d11b2f50cb717c27da0";
    @SuppressWarnings("SpellCheckingInspection")
    public static final String SPOTIFY_ID = "0785a1e619c34d11b2f50cb717c27da0";
    @SuppressWarnings("SpellCheckingInspection")
    public static final String SPOTIFY_REDIRECT_URI = "testschema://callback";
    public static final String SOUNDCLOUD_REDIRECT_URI = "http://onestream.local/dashboard/";
    public static final String SOUNDCLOUD_CLIENT_ID = "asNLcGe4DAQ1YHSRKNyCo15sfFnXDbvS";
    public static final String SOUNDCLOUD_CLIENT_SECRET = "2W7E1oEwhXuAMVx8dL2KAArXw1Kv1NK6";
    public static final int REQUEST_CODE = 1337;

    /* Service Actions */
    public static final String ACTION_INIT = "action_init";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_SHUFFLE = "action_shuffle";
    public static final String ACTION_STOP = "action_stop";

    public static final String ACTION_ICON_PAUSE = "action_icon_pause";
    public static final String ACTION_ICON_PLAY = "action_icon_play";
    public static final String ACTION_ICON_SHUFFLE = "action_icon_play";


    /* Music Caps */
    public static final int spotifySongCap = 1000;

    /*Loading step size*/
    public static final int spotifyLoadStepSize = 50;
    public static final int soundCloudLoadStepSize = 50;

    /*Playlist default name */
    public static String defaultPlaylistName = "Playlist Title";

    /*Song Default values */
    public static String defaultArtistsAlbumGenreName = "N/A";

    /*Shared pref files*/
    public static String oneStreamDomainLoc = "ONESTREAM_ACCOUNT";
    public static String playlistChangeLoc = "PLAYLIST-CHANGE";
    public static String sortTypeLoc = "SORT_TYPE";
    public static String dirInfoLoc = "dirInfo";
    public static String songViewLoc = "SongView";
    public static String cacheSongsLoc = "CacheSongs";
    public static String cachePlaylistsLoc = "CachePlaylists";

    /*Shared pref value locations*/
    public static String playlistChanged = "PlaylistsChanged";
    public static String domain = "domain";
    public static String spotifyLoginChanged = "spotifyLoginChanged";
    public static String soundCloudLoginChanged = "soundCloudLoginChanged";
    public static String sortOnLoad = "sortOnLoad";
    public static String sortType = "sortType";
    public static String directory = "dir";
    public static String directoryChanged = "directoryChanged";
    public static String useExternalStorage = "useExternalStorage";
    public static String songViewOn = "SongView";
    public static String cacheSongOn = "CacheSongs";
    public static String cachePlaylistsOn = "CachePlaylists";

    /*Default sortType*/
    public static String defaultSortType = "Default";

    /*Default directory*/
    public static String defaultDirectory = "N/A";

    /*Default href*/
    public static String defaultHref = "N/A";

    /*Default domain*/
    public static String defaultDomain = "Admin";

    /*Service Names*/
    public static String googleMusic = "GoogleMusic";
    public static String soundCloud = "SoundCloud";
    public static String spotify = "Spotify";
    public static String local = "Local";
    public static String library = "Library";
    public static String playlists = "Playlists";
    public static String artists = "Artists";
    public static String albums = "Albums";
    public static String genres = "Genres";

    /*Tokens*/
    public static String spotifyToken = "SPOTIFY_TOKEN";
    public static String googleToken = "GOOGLE_TOKEN";

    /*OneStreamActivity Tabs */
    public static int OneStream_Library_Pos = 0;
    public static String OneStream_Library = "Library";
    public static int OneStream_Spotify_Pos = 2;
    public static String OneStream_Spotify = "Spotify";
    public static int OneStream_SoundCloud_Pos = 3;
    public static String OneStream_SoundCloud = "SoundCloud";
    public static int OneStream_Playlists_Pos = 4;
    public static String OneStream_Playlists = "Playlists";
    public static int OneStream_Artists_Pos = 5;
    public static String OneStream_Artists = "Artists";
    public static int OneStream_Albums_Pos = 6;
    public static String OneStream_Albums = "Album";
    public static int OneStream_Local_Pos = 1;
    public static String OneStream_Local = "Local";
    public static int OneStream_Genres_Pos = 7;
    public static String OneStream_Genre = "Genre";

    /*OneStreamActivity Tabs */
    public static int Recommendations_Songs_Pos = 0;
    public static String Recommendations_Songs = "Songs";
    public static int Recommendations_Playlists_Pos = 1;
    public static String Recommendations_Playlists = "Recommendations";
    public static int Recommendations_TopSongs_Pos = 2;
    public static String Recommendations_TopSongs = "Top Songs";
    public static int Recommendations_TopArtists_Pos = 3;
    public static String Recommendations_TopArtists = "Top Artists";
    public static int Recommendations_TopAlbums_Pos = 4;
    public static String Recommendations_TopAlbums = "Top Albums";


    /*Activity parent names*/
    public static String oneStreamActivity = "OneStreamActivity";
    public static String playlistActivity = "PlaylistActivity";
    public static String songActivity = "SongActivity";

    /*Recommendations Descriptions*/
    public static String recommendationsSongsText = "Click a song to recommend a playlist";
    public static String playlistRecommendationsText = "Click to open recommended playlist";
    public static String recommendationsTopSongsText = "Click to open OneStream's top songs";
    public static String recommendationsArtists = "Click to open OneStream users top 50 artists playlists";
    public static String recommendationsAlbums = "Click to open OneStream users top 50 albums playlists";

    /*Recommendations string getters */
    public static String getRecommendations = "GetRecommendations";
    public static String getTopSongs = "GetTopSongs";
    public static String getTopArtists = "GetTopArtists";
    public static String getTopAlbums = "GetTopAlbums";
    public static String getRecommendationsFailedText = "Nothing found, try another song.";

    /*RestServiceActionsCodes */
    public static String getPlaylists = "GetPlaylists";
    public static String createPlaylist = "CreatePlaylist";
    public static String updatePlaylist = "UpdatePlaylist";
    public static String deletePlaylist = "DeletePlaylist";
    public static String restServiceActionsHandler = "RestServiceActionsHandler";

    /*Album/Color constants */
    public static int minColorDifference = 4000000;
    public static int maxColor = 255;

    /*Spotify Error*/
    public static String spotifyPlaybackFailed = "kSpErrorFailed";

    /*Getters and loaders*/
    public static String artistsAlbumsGenresLoader = "AlbumArtistGenreLoader";
    public static String soundCloudMusicGetter = "SoundCloudMusicGetter";
    public static String musicLoaderService = "MusicLoaderService";
    public static String spotifyMusicGetter = "SpotifyMusicGetter";
}
