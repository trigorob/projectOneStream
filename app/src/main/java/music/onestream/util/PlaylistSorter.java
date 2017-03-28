package music.onestream.util;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;

public class PlaylistSorter {

    ArrayList<Playlist> Array1;
    String type;

    public PlaylistSorter(ArrayList<Playlist> listContent, String type) {
        this.Array1 = listContent;
        this.type = type;

        if (this.Array1 != null) {
            sort();
        }
        else {
            return;
        }
    }

    public ArrayList<Playlist> getSortedArray() {
        return Array1;
    }


    public void sort() {

        ArrayList<playlistComp> metaData = new ArrayList<playlistComp>();
        for (int i = 0; i < Array1.size(); i++) {
            metaData.add(new playlistComp(Array1.get(i).getName(), Array1.get(i).getOwner(),
                    Array1.get(i).getSongInfo()));
        }

        Collections.sort(metaData, new PlaylistComparator(type));
        for (int i = 0; i < metaData.size(); i++) {
            playlistComp playlist = metaData.get(i);
            Array1.set(i, new Playlist(playlist.name, playlist.owner, playlist.songlist));
        }
    }

}

class PlaylistComparator implements Comparator<playlistComp> {
    String type;

    public PlaylistComparator(String type) {
        this.type = type;
    }
    @Override
    public int compare(playlistComp a, playlistComp b) {
        if (type.equals("ALPH-ASC")) {
            return a.name.compareTo(b.name) < 0 ? -1 : a.name == b.name ? 0 : 1;
        }
        else if (type.equals("ALPH-DESC")) {
            return b.name.compareTo(a.name) < 0 ? -1 : b.name == a.name ? 0 : 1;
        }
        else {
            return 0;
        }
    }
}

class playlistComp{
    String name;
    String owner;
    ArrayList<Song> songlist;

    playlistComp(String n, String o, ArrayList<Song> list) {
        name = n;
        owner = o;
        songlist = list;
    }
}
