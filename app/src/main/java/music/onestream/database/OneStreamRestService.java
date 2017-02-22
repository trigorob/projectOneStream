package music.onestream.database;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import music.onestream.Playlist;
import music.onestream.Song;

/**
 * Created by ruspe_000 on 2017-02-20.
 */

public class OneStreamRestService {

    public void getDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Playlist> getPlaylists(String sql) {
        List<Playlist> PlaylistList = new ArrayList<>();
        Connection con; //retrieve your database connection
        Statement stmt;
        ResultSet rs = null;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Playlist");
            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void addSongsToPlaylists(ArrayList<String> queries) {
        Connection con; //retrieve your database connection
        Statement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.createStatement();
            for (String query : queries) {
                stmt.addBatch(query);
            }
            stmt.executeBatch();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlaylistToPlaylists(String sql) {
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate(sql);;
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlaylistInPlaylists(String sql) {
        List<Playlist> PlaylistList = new ArrayList<>();
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate(sql);;
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getPlaylistNames(String sql) {
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);;
            ArrayList<String[]> playlists = new ArrayList<String[]>();
            try {
                while (rs.next()){
                    String[] vals = new String[2];
                    vals[0] = (rs.getString("Name"));
                    vals[1] = (rs.getString("SongsTable"));
                    playlists.add(vals);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            stmt.close();
            con.close();
            rs.close();
            return playlists;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Playlist getPlaylist(String sql) {
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            Playlist p = new Playlist();
            try {
                while (rs.next()){
                    Song s = new Song(rs.getString("SongName"),rs.getString("uri"),rs.getString("artist"),
                            rs.getString("album"),rs.getString("type"), rs.getInt("ListPosition"));
                    p.addSong(s);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs.close();
            stmt.close();
            con.close();
            return p;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Delete, insert, create
    public void performUpdateQuery(String sql) {
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        getDriver();
        try {
            con = DriverManager.getConnection("jdbc:mysql://104.155.180.191/onestream", "root",
                    null);
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
