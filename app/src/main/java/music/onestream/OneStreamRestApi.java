package music.onestream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruspe_000 on 2017-02-22.
 */

public class OneStreamRestApi {

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

    public List<Playlist> deletePlaylists(String sql) {
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

    public List<Playlist> editPlaylists(String sql) {
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

    public List<Playlist> addPlaylists(String sql) {
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


    public List<Playlist> getSongs(String sql) {
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
}
