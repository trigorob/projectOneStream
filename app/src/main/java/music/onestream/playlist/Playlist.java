package music.onestream.playlist;

import java.io.Serializable;
import java.util.ArrayList;

import music.onestream.song.Song;

/**
 * Created by ruspe_000 on 2017-02-13.
 */

public class Playlist implements Serializable {
    private String name;
    private String owner;
    private ArrayList<Song> songInfo;

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
    }
    public void addSongs(ArrayList<Song> songs)
    {
        for (int i = 0; i < songs.size(); i++) {
            songInfo.add(songs.get(i));
        }
    }

    public void setSongInfo(ArrayList<Song> songs) {
        this.songInfo = songs;
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
            if (songInfo.get(i).getName().equalsIgnoreCase(name))
            {
                return songInfo.get(i);
            }
        }
        return null;
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
