package music.onestream;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-13.
 */

public class Playlist implements Serializable {
    private String name;
    private String owner;
    private ArrayList<Song> songInfo;
    private ArrayList<String> songAdapter;

    public String getName() {
        return name;
    }

    public Playlist() {
        name = "";
        owner = "";
        songInfo = new ArrayList<Song>();
        songAdapter = new ArrayList<String>();
    }


    public String getOwner() {
        return this.owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
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
        songAdapter.add(song.toString());
    }
    public void addSongs(ArrayList<Song> songs)
    {
        for (int i = 0; i < songs.size(); i++) {
            songInfo.add(songs.get(i));
            songAdapter.add(songs.get(i).toString());
        }
    }

    public ArrayList<String> getAdapterList() {return songAdapter;}
    public void setSongAdapter(ArrayList<String> adapter) {this.songAdapter =adapter;}
    public void setSongInfo(ArrayList<Song> songs) {
        this.songInfo = songs;
        songAdapter = new ArrayList<String>();
        for (int i = 0; i < songs.size(); i++)
        {
            songAdapter.add(songs.get(i).toString());
        }
    }
    public void removeSong(int position)
    {
        songInfo.remove(position);
    }

    public int size() {
        return songInfo.size();
    }

    public Song findSongByName(String name) {
        for (int i = 0; i < songInfo.size(); i++)
        {
            if (songInfo.get(i).getName().equals(name))
            {
                return songInfo.get(i);
            }
        }
        return null;
    }

}
