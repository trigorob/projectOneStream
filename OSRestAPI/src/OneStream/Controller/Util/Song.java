package OneStream.Controller.Util;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by ruspe_000 on 2017-02-16.
 */

public class Song implements Serializable {
    String name;
    String uri;
    String artist;
    String album;
    String type;
    int position;
    String albumArt;
    String genre;

    @Id
    private String _id;

    public Song()
    {
        this.name = "";
        this.uri = "";
        this.artist = "";
        this.album = "";
        this.type = "";
        this.position = 0;
        this.albumArt = "";
        this.genre = "";
    }

    public Song(String name, String uri, String artist, String album, String type, int position, String albumArt, String genre)
    {
        this.name = name;
        this.uri = uri;
        this.artist = artist;
        this.album = album;
        this.type = type;
        this.position = position;
        this.albumArt = albumArt;
        this.genre = genre;

    }

    public String get_id() {
        return _id;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUri() { return uri; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public boolean equals(Object other)
    {
        return (((Song) other).getUri().equals(this.getUri()));
    }


    public static boolean compareAllSong(Song a, Song b)
    {
        String aName = a.getName();
        String aArtist = a.getArtist();
        String aAlbum  = a.getAlbum();
        String bName = b.getName();
        String bArtist = b.getArtist();
        String bAlbum = b.getAlbum();
        String aGenre = a.getGenre();
        String bGenre = b.getGenre();
        if (aName == null || aName.equals(""))
        {
            aName = "IGNORE_THIS_FIELD";
        }
        if (aAlbum == null|| aAlbum.equals("") || aAlbum.equals("N/A"))
        {
            aAlbum = "IGNORE_THIS_FIELD";
        }
        if (aArtist == null || aArtist.equals("") || aArtist.equals("N/A"))
        {
            aArtist = "IGNORE_THIS_FIELD";
        }
        if (bName == null || bName.equals(""))
        {
            bName = "IGNORE_THIS_FIELD";
        }
        if (bArtist == null || bArtist.equals("") || bArtist.equals("N/A"))
        {
            bArtist = "IGNORE_THIS_FIELD";
        }
        if (bAlbum == null || bAlbum.equals("") || bAlbum.equals("N/A"))
        {
            bAlbum = "IGNORE_THIS_FIELD";
        }
        if (bGenre == null || bGenre.equals("") || bGenre.equals("N/A"))
        {
            bGenre = "IGNORE_THIS_FIELD";
        }
        if (aGenre == null || aGenre.equals("") || aGenre.equals("N/A"))
        {
            aGenre = "IGNORE_THIS_FIELD";
        }

        boolean nameContains = aName.contains(bName) || bName.contains(aName);
        boolean artistContains = aArtist.contains(bArtist) || bArtist.contains(aArtist);
        boolean albumContains = aAlbum.contains(bAlbum) || bAlbum.contains(aAlbum);
        boolean nameContainsOther = aName.contains(bAlbum) || aName.contains(bArtist);
        boolean nameContainsOtherReverse = bName.contains(aAlbum) || bName.contains(aArtist);
        boolean artistContainsOther = aArtist.contains(bName) || aArtist.contains(bAlbum);
        boolean artistContainsotherReverse = bArtist.contains(aName) || bArtist.contains(aAlbum);
        boolean albumContainsOther = aAlbum.contains(bArtist) || aAlbum.contains(bName);
        boolean albumContainsOtherReverse = bAlbum.contains(aArtist) || bAlbum.contains(aName);
        boolean genreMatch = aGenre.contains(bGenre) || bGenre.contains(aGenre);
        return (nameContains || artistContains || albumContains|| nameContainsOther || nameContainsOtherReverse ||
                artistContainsOther || artistContainsotherReverse || albumContainsOther || albumContainsOtherReverse
                || genreMatch);
    }

}
