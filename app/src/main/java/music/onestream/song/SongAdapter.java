package music.onestream.song;

import android.content.Context;
import android.graphics.Color;
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
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-03-04.
 */

public class SongAdapter extends ArrayAdapter<Song> implements Filterable {

    private ArrayList<Song> songs;
    private ArrayList<Song> filteredSongs;
    private Song selected;
    private CharSequence filterText;

        public SongAdapter(Context context, int textViewResourceId, List<Song> songs) {
            super(context, textViewResourceId, songs);
            this.songs = (ArrayList<Song>) songs;
            this.filteredSongs = (ArrayList<Song>) songs;
            this.filterText = "";
        }

    public int getCount() {
        return filteredSongs.size();
    }

    public void setSelected(Song song) {
        selected = song;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Song> getFilteredSongs() {
        return filteredSongs;
    }

    public void resetFilter() {
        getFilter().filter(filterText);
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
        String name = song.getName();
        if (name.length() > 55) {
            name = name.substring(0, 55) + "...";
        }
        t1.setText(name);
        TextView t2= (TextView) customView.findViewById(R.id.listSongArtistAlbum);
        String bottomText = "";
        if (!song.getArtist().equals(Constants.defaultArtistsAlbumGenreName) &&
                !song.getAlbum().equals(Constants.defaultArtistsAlbumGenreName))
        {
            bottomText = song.getArtist() + " ---- " + song.getAlbum();
        }
        else if (!song.getGenre().equals(Constants.defaultArtistsAlbumGenreName))
        {
            bottomText = song.getGenre();
        }
        t2.setText(bottomText);

        if (selected != null && getItem(position).equals(selected)) {
            customView.setBackgroundColor(Color.parseColor("#E0ECF8"));
        }
        else {
            customView.setBackgroundResource(android.R.color.transparent);
        }

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
                    String dataGenres = songs.get(i).getGenre();
                    String dataAlbums = songs.get(i).getAlbum();
                    if (dataNames.toLowerCase().contains(constraint.toString()) ||
                        dataArtists.toLowerCase().contains(constraint.toString()) ||
                        dataAlbums.toLowerCase().contains(constraint.toString()) ||
                        dataGenres.toLowerCase().contains(constraint.toString()))
                        {
                            FilteredSongNames.add(songs.get(i));
                        }
                }

                results.count = FilteredSongNames.size();
                results.values = FilteredSongNames;
                filterText = constraint;

                return results;
            }
        };

        return filter;
    }

}
