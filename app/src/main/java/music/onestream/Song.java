package music.onestream;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public Song(String name, String uri, String artist, String album, String type, int position)
    {
        this.name = name;
        this.uri = uri;
        this.artist = artist;
        this.album = album;
        this.type = type;
        this.position = position;

    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return this.position;
    }

    public String getUri() {
        return uri;
    }

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

}
