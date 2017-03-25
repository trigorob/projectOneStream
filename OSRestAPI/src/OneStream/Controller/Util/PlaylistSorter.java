package OneStream.Controller.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ruspe_000 on 2017-03-22.
 */
public class PlaylistSorter {
    ArrayList<Playlist> sortedArray;
    String type;

    public PlaylistSorter(ArrayList<Playlist> playlists, String type) {
        this.sortedArray = playlists;
        this.type = type;

        if (this.sortedArray != null) {
            sort();
        }
    }

    public ArrayList<Playlist> getSortedArray() {
        return sortedArray;
    }


    public void sort() {

        ArrayList<compPL> metaData = new ArrayList<compPL>();
        for (int i = 0; i < sortedArray.size(); i++) {
            metaData.add(new compPL(sortedArray.get(i).getName(), sortedArray.get(i).getOwner(),
                    sortedArray.get(i).getSongInfo(), sortedArray.get(i).get_id(), sortedArray.get(i).getSongInfo().size()));
        }

        Collections.sort(metaData, new ResultComparatorPlaylist(type));
        for (int i = 0; i < metaData.size(); i++) {
            compPL comp = metaData.get(i);
            sortedArray.set(i, new Playlist(comp.name, comp.owner, comp.songInfo));
        }
    }

}

class ResultComparatorPlaylist implements Comparator<compPL> {
    String type;

    public ResultComparatorPlaylist(String type) {
        this.type = type;
    }

    @Override
    public int compare(compPL a, compPL b) {
            return b.size - a.size < 0 ? -1 : b.size == a.size ? 0 : 1;
    }
}

class compPL{
    String name;
    String owner;
    ArrayList<Song> songInfo;
    String id;
    int size;

    compPL(String n, String o, ArrayList<Song> sinfo, String i, int sz) {
        name = n;
        owner = o;
        songInfo = sinfo;
        id = i;
        size = sz;
    }
}
