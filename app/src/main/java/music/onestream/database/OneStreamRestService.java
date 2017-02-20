package music.onestream.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import music.onestream.Playlist;

/**
 * Created by ruspe_000 on 2017-02-20.
 */

public class OneStreamRestService {

    public List<Playlist> getPlaylists() {
        List<Playlist> PlaylistList = new ArrayList<>();
        Connection con; //retrieve your database connection
        Statement stmt;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void addPlaylistToPlaylists(String sql) {
        List<Playlist> PlaylistList = new ArrayList<>();
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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


    public void createPlaylist(String sql) {
        List<Playlist> PlaylistList = new ArrayList<>();
        Connection con; //retrieve your database connection
        PreparedStatement stmt;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
}
