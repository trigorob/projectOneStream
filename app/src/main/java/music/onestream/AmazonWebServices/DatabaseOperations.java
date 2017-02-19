package music.onestream.AmazonWebServices;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import music.onestream.AmazonWebServices.mobile.AWSMobileClient;

/**
 * Created by ruspe_000 on 2017-02-19.
 */

public class DatabaseOperations {

    public void insertPlaylist() {
        // Fetch the default configured DynamoDB ObjectMapper
        final DynamoDBMapper dynamoDBMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final PlaylistsDO playlist = new PlaylistsDO(); // Initialize the Notes Object

        // The userId has to be set to user's Cognito Identity Id for private / protected tables.
        // User's Cognito Identity Id can be fetched by using:
        // AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()
        playlist.setUsername("Admin");
        //todo: Get playlistName
        playlist.setPlaylistName("Playlist1");
        //todo: Get songList
        ArrayList SongNames = new ArrayList<String>();
        SongNames.add("Come And Get Your Love");
        playlist.setSongs(SongNames);
        //todo: Get Services
        TreeSet fromServices = new TreeSet<String>();
        fromServices.add("Spotify");
        playlist.setServices(fromServices);
        AmazonClientException lastException = null;

        try {
            dynamoDBMapper.save(playlist);
        } catch (final AmazonClientException ex) {
            Log.e("DBError", "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
    }
}
