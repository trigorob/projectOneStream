package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

public class SortingActivity extends Activity {
    private static final String PREFS_NAME = "SORT-TYPE";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorting_activity);

    final Button sortA = (Button) findViewById(R.id.sortAlphAscend);
    sortA.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sortType", "ALPH-ASC");
            editor.commit();

            Intent back = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(back, 0);
        }
    });
    final Button sortD = (Button) findViewById(R.id.sortAlphDescend);
        sortD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC");
                editor.commit();

                Intent back = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortAARTIST = (Button) findViewById(R.id.sortAlphAscendArtist);
        sortAARTIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-ASC-ARTIST");
                editor.commit();

                Intent back = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortDARTIST = (Button) findViewById(R.id.sortAlphDescendArtist);
        sortDARTIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC-ARTIST");
                editor.commit();

                Intent back = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortAAlbum = (Button) findViewById(R.id.sortAlphAscendAlbum);
        sortAAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-ASC-ALBUM");
                editor.commit();

                Intent back = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortDAlbum = (Button) findViewById(R.id.sortAlphDescendAlbum);
        sortDAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC-ALBUM");
                editor.commit();

                Intent back = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(back, 0);
            }
        });

    final Button reset = (Button) findViewById(R.id.resetSortingType);
    reset.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sortType", "Default");
            editor.commit();

            Intent back = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(back, 0);
        }
    });
    }
}
