package music.onestream;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruspe_000 on 2017-03-04.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    private ArrayList<Song> songs;
    private Activity activity;

        public SongAdapter(Context context, int textViewResourceId, List<Song> songs) {
            super(context, textViewResourceId, songs);
            this.songs = (ArrayList<Song>) songs;
            this.activity = (Activity) context;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = convertView;
        Song song = getItem(position);
        if (customView == null) {
          final LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          customView = vi.inflate(R.layout.songlayout, null);
        }

        TextView t1 = (TextView) customView.findViewById(R.id.listSongName);
        t1.setText(song.getName());
        TextView t2= (TextView) customView.findViewById(R.id.listSongArtistAlbum);
        t2.setText(song.getArtist() + " ---- " + song.getAlbum());

        return customView;
    }

}
