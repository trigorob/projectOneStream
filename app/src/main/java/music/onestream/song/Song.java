package music.onestream.song;

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

    public Song(String name, String uri, String artist, String album,
                String type, int position, String albumArt)
    {
        this.name = name;
        this.uri = uri;
        this.artist = artist;
        this.album = album;
        this.type = type;
        this.position = position;
        this.albumArt = albumArt;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return this.position;
    }

    public String getAlbumArt() {
        return this.albumArt;
    }

    public void setAlbumArt(String art) {
        this.albumArt = art;
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
