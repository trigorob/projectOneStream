package music.onestream.song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import music.onestream.R;
import music.onestream.song.Song;

/**
 * Created by ruspe_000 on 2017-03-04.
 */

public class SongAdapter extends ArrayAdapter<Song> implements Filterable {

    private ArrayList<Song> songs;
    private ArrayList<Song> filteredSongs;

        public SongAdapter(Context context, int textViewResourceId, List<Song> songs) {
            super(context, textViewResourceId, songs);
            this.songs = (ArrayList<Song>) songs;
            this.filteredSongs = (ArrayList<Song>) songs;
        }

    public int getCount() {
        return filteredSongs.size();
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Song> getFilteredSongs() {
        return filteredSongs;
    }


    public Song getItem(int position) {
        return filteredSongs.get(position);
    }

    public long getItemId(int position) {
        return position;
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

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredSongs = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Song> FilteredSongNames = new ArrayList<Song>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < songs.size(); i++) {
                    String dataNames = songs.get(i).getName();
                    String dataArtists = songs.get(i).getArtist();
                    String dataAlbums = songs.get(i).getAlbum();
                    if (dataNames.toLowerCase().contains(constraint.toString()) ||
                        dataArtists.toLowerCase().contains(constraint.toString()) ||
                        dataAlbums.toLowerCase().contains(constraint.toString()))
                        {
                            FilteredSongNames.add(songs.get(i));
                        }
                }

                results.count = FilteredSongNames.size();
                results.values = FilteredSongNames;

                return results;
            }
        };

        return filter;
    }

}
