package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import music.onestream.R;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

public class SortingActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorting_activity);

    final Button sortA = (Button) findViewById(R.id.sortAlphAscend);
    sortA.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sortType", "ALPH-ASC");
            editor.putBoolean("sortOnLoad", true);
            editor.commit();

            Intent back = new Intent(v.getContext(), OneStreamActivity.class);
            startActivityForResult(back, 0);
        }
    });
    final Button sortD = (Button) findViewById(R.id.sortAlphDescend);
        sortD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortAARTIST = (Button) findViewById(R.id.sortAlphAscendArtist);
        sortAARTIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-ASC-ARTIST");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortDARTIST = (Button) findViewById(R.id.sortAlphDescendArtist);
        sortDARTIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC-ARTIST");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortAAlbum = (Button) findViewById(R.id.sortAlphAscendAlbum);
        sortAAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-ASC-ALBUM");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortDAlbum = (Button) findViewById(R.id.sortAlphDescendAlbum);
        sortDAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC-ALBUM");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });

        final Button sortAGenre = (Button) findViewById(R.id.sortAlphAscendGenre);
        sortAGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-ASC-GENRE");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });
        final Button sortDGenre = (Button) findViewById(R.id.sortAlphDescendGenre);
        sortDGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortType", "ALPH-DESC-GENRE");
                editor.putBoolean("sortOnLoad", true);
                editor.commit();

                Intent back = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(back, 0);
            }
        });


    final Button reset = (Button) findViewById(R.id.resetSortingType);
    reset.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences(Constants.sortTypeLoc, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sortType", "Default");
            editor.putBoolean("sortOnLoad", false);
            editor.commit();

            Intent back = new Intent(v.getContext(), OneStreamActivity.class);
            startActivityForResult(back, 0);
        }
    });
    }
}
