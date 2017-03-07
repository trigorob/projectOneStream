package music.onestream.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import music.onestream.R;
import music.onestream.playlist.Playlist;

/**
 * Created by ruspe_000 on 2017-03-04.
 */

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    private ArrayList<Playlist> playlists;
    private ArrayList<Playlist> filteredPlaylists;

    public PlaylistAdapter(Context context, int textViewResourceId, List<Playlist> playlists) {
        super(context, textViewResourceId, playlists);
        this.playlists = (ArrayList<Playlist>) playlists;
        this.filteredPlaylists = (ArrayList<Playlist>) playlists;
    }

    public int getCount() {
        return filteredPlaylists.size();
    }

    public Playlist getItem(int position) {
        return filteredPlaylists.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = convertView;
        Playlist playlist = getItem(position);
        if (customView == null) {
            final LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            customView = vi.inflate(R.layout.songlayout, null);
        }

        TextView t1 = (TextView) customView.findViewById(R.id.listSongName);
        t1.setText(playlist.getName());
        TextView t2 = (TextView) customView.findViewById(R.id.listSongArtistAlbum);
        t2.setText(playlist.getSongInfo().size() + " Songs");

        return customView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredPlaylists = (ArrayList<Playlist>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Playlist> FilteredPlaylistNames = new ArrayList<Playlist>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < playlists.size(); i++) {
                    String dataNames = playlists.get(i).getName();
                    if (dataNames.toLowerCase().contains(constraint.toString()))
                    {
                        FilteredPlaylistNames.add(playlists.get(i));
                    }
                }

                results.count = FilteredPlaylistNames.size();
                results.values = FilteredPlaylistNames;

                return results;
            }
        };

        return filter;
    }

}
