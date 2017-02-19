package music.onestream.AmazonWebServices;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Set;

/**
 * Created by ruspe_000 on 2017-02-19.
 */


@DynamoDBTable(tableName = "onestream-mobilehub-2042413541-Playlists")

public class PlaylistsDO {
    private String _username;
    private String _playlistName;
    private Set<String> _services;
    private List<String> _songs;

    @DynamoDBHashKey(attributeName = "Username")
    @DynamoDBIndexRangeKey(attributeName = "Username", globalSecondaryIndexName = "UserN")
    public String getUsername() {
        return _username;
    }

    public void setUsername(final String _username) {
        this._username = _username;
    }
    @DynamoDBIndexHashKey(attributeName = "PlaylistName", globalSecondaryIndexName = "UserN")
    public String getPlaylistName() {
        return _playlistName;
    }

    public void setPlaylistName(final String _playlistName) {
        this._playlistName = _playlistName;
    }
    @DynamoDBAttribute(attributeName = "Services")
    public Set<String> getServices() {
        return _services;
    }

    public void setServices(final Set<String> _services) {
        this._services = _services;
    }
    @DynamoDBAttribute(attributeName = "Songs")
    public List<String> getSongs() {
        return _songs;
    }

    public void setSongs(final List<String> _songs) {
        this._songs = _songs;
    }
}
