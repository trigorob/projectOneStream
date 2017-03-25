package OneStream.Controller.Util;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongSorter {

    ArrayList<Song> Array1;
    String type;
    Object[] retArr = null;

    public SongSorter(ArrayList<Song> listContent, String type) {
        this.Array1 = listContent;
        this.type = type;

        if (this.Array1 != null) {
            sort();
            retArr = new Object[]{Array1};
        }
    }

    public Object[] getRetArr() {
        return retArr;
    }


    public void sort() {

        ArrayList<compString> metaData = new ArrayList<compString>();
        for (int i = 0; i < Array1.size(); i++) {
            metaData.add(new compString(Array1.get(i).getName(), Array1.get(i).getUri(),
                    Array1.get(i).getArtist(),
                    Array1.get(i).getAlbum(), Array1.get(i).getType(), Array1.get(i).getPosition(),
                    Array1.get(i).getAlbumArt()));
        }

        Collections.sort(metaData, new ResultComparatorString(type));
        for (int i = 0; i < metaData.size(); i++) {
            compString comp = metaData.get(i);
            Array1.set(i, new Song(comp.name, comp.uri, comp.artist, comp.album,
                    comp.type, comp.position, comp.albArt));
        }
    }

}

class ResultComparatorString implements Comparator<compString> {
    String type;

    public ResultComparatorString(String type) {
        this.type = type;
    }
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

    compString(String n, String u, String a, String a2, String t, int pos, String art) {
        name = n;
        uri = u;
        artist = a;
        album = a2;
        type = t;
        position = pos;
        albArt = art;
    }
}
