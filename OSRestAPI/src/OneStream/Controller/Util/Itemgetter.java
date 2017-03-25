package OneStream.Controller.Util;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-03-07.
 */
public class Itemgetter {

    public ArrayList<Playlist> getPlaylists(String name, String owner, int minLength, int maxLength, boolean excludeLocal,
                                            boolean excludeSpotify, boolean excludeSoundCloud, ArrayList<Playlist> allPlaylists)
    {
        ArrayList<Playlist> returnList = new ArrayList<Playlist>();
        Boolean valid;
        for (int i = 0; i < allPlaylists.size(); i++) {
            valid = true;
            Playlist candidate = allPlaylists.get(i);

            if (!candidate.getName().contains(name) || !candidate.getOwner().contains(owner))
            {
                valid = false;
            }
            ArrayList<Song> songs = candidate.getSongInfo();

            if (songs.size() < minLength || songs.size() > maxLength) {
                valid = false;
            }

            //Only loop through if candidate has a chance
            if (valid) {
                for (int d = 0; d < songs.size(); d++) {
                    String type = songs.get(d).getType();
                    if (type.equals("Local") && excludeLocal ||
                            (type.equals("Spotify") && excludeSpotify) ||
                            (type.equals("Spotify") && excludeSoundCloud)) {
                        valid = false;
                        d+= songs.size();
                    }
                }
            }
            if (valid) {
                returnList.add(candidate);
            }
        }
        return returnList;
    }

    public ArrayList<Song> getSongs(String name, String artist, String album,
                                    boolean excludeLocal, boolean excludeSpotify, boolean excludeSoundCloud,
            ArrayList<Song> allSongs) {
        ArrayList<Song> returnList = new ArrayList<Song>();
        Boolean valid;
        for (int i = 0; i < allSongs.size(); i++) {
            valid = true;
            Song candidate = allSongs.get(i);

            if (!candidate.getName().contains(name) && !candidate.getArtist().contains(artist)
                    && !candidate.getAlbum().contains(album))
            {
                valid = false;
            }
            //Only loop through if candidate has a chance
            if (valid) {
                String type = candidate.getType();
                if (type.equals("Local") && excludeLocal ||
                        (type.equals("Spotify") && excludeSpotify) ||
                        (type.equals("SoundCloud") && excludeSoundCloud)) {
                    valid = false;
                }
            }
            if (valid) {
                returnList.add(candidate);
            }
        }
        return returnList;
    }
}
