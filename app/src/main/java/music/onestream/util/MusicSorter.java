package music.onestream.util;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import music.onestream.song.Song;

public class MusicSorter {

    ArrayList<Song> Array1;
    String type;

    public MusicSorter(ArrayList<Song> listContent, String type) {
        this.Array1 = listContent;
        this.type = type;

        if (this.Array1 != null) {
            sort();
        }
         else {
            return;
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
                    Array1.get(i).getAlbum(), Array1.get(i).getType(),
                    Array1.get(i).getAlbumArt(),
                    Array1.get(i).getGenre()));
        }

        try {
            Collections.sort(metaData, new ResultComparatorString(type));
        }
        catch (IllegalArgumentException e) {

        }
        for (int i = 0; i < metaData.size(); i++) {
            compString comp = metaData.get(i);
            Array1.set(i, new Song(comp.name, comp.uri, comp.artist, comp.album,
                    comp.type, 1, comp.albArt, comp.genre));
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
                return a.name.compareTo(b.name) < 0 ? -1 : a.name.equals(b.name) ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.name.compareTo(a.name) < 0 ? -1 : b.name.equals(a.name) ? 0 : 1;
            }
            else if (type.equals("ALPH-ASC-ARTIST")) {
                return a.artist.compareTo(b.artist) < 0 ? -1 : a.artist.equals(b.artist) ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC-ARTIST")) {
                return b.artist.compareTo(a.artist) < 0 ? -1 : b.artist.equals(a.artist) ? 0 : 1;
            }
            else if (type.equals("ALPH-ASC-ALBUM")) {
                return a.album.compareTo(b.album) < 0 ? -1 : a.album.equals(b.album) ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC-ALBUM")) {
                return b.album.compareTo(a.album) < 0 ? -1 : b.album.equals(a.album) ? 0 : 1;
            }
            else if (type.equals("ALPH-ASC-GENRE")) {
                return b.genre.compareTo(a.genre) < 0 ? -1 : b.genre.equals(a.genre) ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC-GENRE")) {
                return b.genre.compareTo(a.genre) < 0 ? -1 : b.genre.equals(a.genre) ? 0 : 1;
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
        String genre;

        compString(String n, String u, String a, String a2, String t, String art, String gnre) {
            name = n;
            uri = u;
            artist = a;
            album = a2;
            type = t;
            albArt = art;
            genre = gnre;
        }
    }
