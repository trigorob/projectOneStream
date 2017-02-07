package music.onestream;

/**
 * Created by ruspe_000 on 2017-02-07.
 */


public class Constants {
    public interface ACTION {
        public static String SPOTIFY_PACKAGE = "com.spotify.music";
        public static String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        public static String STARTFOREGROUND_ACTION = "music.onestream.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "music.onestream.action.stopforeground";
        public static String MAIN_ACTION = "music.onestream.action.main";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
