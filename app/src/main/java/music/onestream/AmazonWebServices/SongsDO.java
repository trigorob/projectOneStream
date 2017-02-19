package music.onestream.AmazonWebServices;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ruspe_000 on 2017-02-19.
 */

@DynamoDBTable(tableName = "onestream-mobilehub-2042413541-Songs")

public class SongsDO {
    private String _userId;
    private String _album;
    private String _artist;
    private Double _position;
    private String _songName;
    private String _type;
    private String _uri;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "Album")
    public String getAlbum() {
        return _album;
    }

    public void setAlbum(final String _album) {
        this._album = _album;
    }
    @DynamoDBAttribute(attributeName = "Artist")
    public String getArtist() {
        return _artist;
    }

    public void setArtist(final String _artist) {
        this._artist = _artist;
    }
    @DynamoDBAttribute(attributeName = "Position")
    public Double getPosition() {
        return _position;
    }

    public void setPosition(final Double _position) {
        this._position = _position;
    }
    @DynamoDBAttribute(attributeName = "SongName")
    public String getSongName() {
        return _songName;
    }

    public void setSongName(final String _songName) {
        this._songName = _songName;
    }
    @DynamoDBAttribute(attributeName = "Type")
    public String getType() {
        return _type;
    }

    public void setType(final String _type) {
        this._type = _type;
    }
    @DynamoDBAttribute(attributeName = "Uri")
    public String getUri() {
        return _uri;
    }

    public void setUri(final String _uri) {
        this._uri = _uri;
    }

}
