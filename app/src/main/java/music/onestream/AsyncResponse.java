package music.onestream;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-05.
 */

public interface AsyncResponse {
    void processFinish(String output);
    ArrayList<String> processFinish(ArrayList<String> output);
}
