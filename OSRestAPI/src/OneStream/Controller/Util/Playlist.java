package OneStream.Controller.Util;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-13.
 */

public class Playlist implements Serializable {
    private String name;
    private String owner;
    private ArrayList<Song> songInfo;

    @Id
    private String _id;

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Playlist() {
        name = "";
        owner = "";
        songInfo = new ArrayList<Song>();
    }

    public Playlist(String name, String owner, ArrayList<Song> songInfo)
    {
        this.name = name;
        this.owner = owner;
        setSongInfo(songInfo);
    }


    public String getOwner() {
        return this.owner;
    }

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

    public void setSongInfo(ArrayList<Song> songs) {
        this.songInfo = songs;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o)
    {
        return  (((Playlist) o).getName().equals(this.getName()) &&
                ((Playlist) o).getOwner().equals(this.getOwner()));
    }

}
