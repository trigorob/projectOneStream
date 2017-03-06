package music.onestream;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import android.icu.util.RangeValueIterator;
import android.net.Uri;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicSorter {

    ArrayList<Song> Array1;
    String type;
    Object[] retArr = null;

    public MusicSorter(ArrayList<Song> listContent, String type) {
        this.Array1 = listContent;
        this.type = type;

        if (this.Array1 != null) {
            sort();
            retArr = new Object[]{Array1};
        }
         else {
            return;
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
                    Array1.get(i).getAlbum(), Array1.get(i).getType(),
                    Array1.get(i).getAlbumArt()));
        }

        Collections.sort(metaData, new ResultComparatorString(type));
        for (int i = 0; i < metaData.size(); i++) {
            compString comp = metaData.get(i);
            Array1.set(i, new Song(comp.name, comp.uri, comp.artist, comp.album, comp.type, i, comp.albArt));
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
            if (type.equals("ALPH-ASC")) {
                return a.name.compareTo(b.name) < 0 ? -1 : a.name == b.name ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.name.compareTo(a.name) < 0 ? -1 : b.name == a.name ? 0 : 1;
            }
            else if (type.equals("ALPH-ASC-ARTIST")) {
                return a.artist.compareTo(b.artist) < 0 ? -1 : a.artist == b.artist ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC-ARTIST")) {
                return b.artist.compareTo(a.artist) < 0 ? -1 : b.artist == a.artist ? 0 : 1;
            }
            else if (type.equals("ALPH-ASC-ALBUM")) {
                return a.album.compareTo(b.album) < 0 ? -1 : a.album == b.album ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC-ALBUM")) {
                return b.album.compareTo(a.album) < 0 ? -1 : b.album == a.album ? 0 : 1;
            }
            else {
                return 0;
            }
        }
    }

    class compString{
        String name;
        String uri;
        String artist;
        String album;
        String type;
        String albArt;

        compString(String n, String u, String a, String a2, String t, String art) {
            name = n;
            uri = u;
            artist = a;
            album = a2;
            type = t;
            albArt = art;
        }
    }
