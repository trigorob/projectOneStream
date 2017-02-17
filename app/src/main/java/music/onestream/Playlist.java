package music.onestream;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-13.
 */

public class Playlist implements Serializable {
    private String name;
    private ArrayList<Song> songInfo;

    public String getName() {
        return name;
    }

    public Playlist() {
        name = "";
        songInfo = new ArrayList<Song>();
    }

    /* Format of song info should be:
    songInfo[0] = name
    songInfo[1] = URI/link
    songInfo[2] = artist
    songInfo[3] = album
    songInfo[4] = Type: One of "Spotify", "GooglePlay", "Local"
    songInfo[5] = Position: "0" indicates 0th position in list
     */
    public ArrayList<Song> getSongInfo() {
        return songInfo;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void addSong(Song song)
    {
        songInfo.add(song);
    }
    public void removeSong(int position)
    {
        songInfo.remove(position);
    }
}
