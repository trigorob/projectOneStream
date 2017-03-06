package music.onestream;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-20.
 */

public class DatabaseActionsHandler extends AsyncTask {
    AsyncResponse SAR;

    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = "DatabaseActionsHandler";
        retObject[1] = result;
        SAR.processFinish(retObject);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String action = (String) params[0];

        if (action.equals("GetPlaylists"))
        {
            String owner = (String) params[1];
            return getPlaylists(owner);
        }
        else if (action.equals("CreatePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            createPlaylist(playlist, false);
        }
        else if (action.equals("DeletePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            deletePlaylist(playlist);
        }
        //We do this with a delete/create rather than an update mostly for batch loading/speed reasons
        else if (action.equals("UpdatePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            createPlaylist(playlist, true);
        }
        return null;
    }

    public static ArrayList<Playlist> getPlaylists(String owner) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://api-7328501912465276845-942591.appspot.com/OneStream/Playlists?owner=" + owner;
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = JSONExtractor.extractJSON(conn);
        if (json != null) {
            return JSONExtractor.processPlaylistJSON(json);
        }
        return null;
    }

    private static void createPlaylist(Playlist playlist, boolean update) {
        DataOutputStream dataOutputStream;
        HttpURLConnection conn = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(playlist);
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            String urlString = "http://api-7328501912465276845-942591.appspot.com/OneStream/Playlists";
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches( false );
            conn.setRequestProperty( "charset", "utf-8");
            if (update) {
                conn.setRequestMethod("PUT");
            }
            else {
                conn.setRequestMethod("POST");
            }
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty( "Content-Length", Integer.toString(data.length));
            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write(data);
            dataOutputStream.close();
            conn.getResponseCode();
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void deletePlaylist(Playlist playlist) {
        HttpURLConnection conn = null;
        try {
            String urlString = "http://api-7328501912465276845-942591.appspot.com" +
                    "/OneStream/Playlists?owner=" + URLEncoder.encode(playlist.getOwner(), "UTF-8")
                    + "&name=" + URLEncoder.encode(playlist.getName(), "UTF-8");
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            conn.getResponseCode();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
