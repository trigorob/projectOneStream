package OneStream.Controller.Util;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongSorter {

    ArrayList<Song> Array1;

    public SongSorter(ArrayList<Song> listContent) {
        this.Array1 = listContent;

        if (this.Array1 != null) {
            sort();
        }
    }

    public ArrayList<Song> getSortedArray() {
        return Array1;
    }
    public void sort() {

        ArrayList<compString> metaData = new ArrayList<compString>();
        for (int i = 0; i < Array1.size(); i++) {
            metaData.add(new compString(Array1.get(i).getName(), Array1.get(i).getUri(),
                    Array1.get(i).getArtist(),
                    Array1.get(i).getAlbum(), Array1.get(i).getType(), Array1.get(i).getPosition(),
                    Array1.get(i).getAlbumArt(), Array1.get(i).getGenre()));
        }

        Collections.sort(metaData, new ResultComparatorString());
        for (int i = 0; i < metaData.size(); i++) {
            compString comp = metaData.get(i);
            Array1.set(i, new Song(comp.name, comp.uri, comp.artist, comp.album,
                    comp.type, comp.position, comp.albArt, comp.genre));
        }
    }

}

class ResultComparatorString implements Comparator<compString> {
    @Override
    public int compare(compString a, compString b) {
            return b.position - a.position < 0 ? -1 : b.position == a.position ? 0 : 1;
    }
}

class compString{
    String name;
    String uri;
    String artist;
    String album;
    String type;
    int position;
    String albArt;
    String genre;

    compString(String n, String u, String a, String a2, String t, int pos, String art, String gnre) {
        name = n;
        uri = u;
        artist = a;
        album = a2;
        type = t;
        position = pos;
        albArt = art;
        genre = gnre;
    }
}
