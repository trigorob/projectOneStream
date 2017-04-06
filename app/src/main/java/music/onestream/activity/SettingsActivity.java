package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import music.onestream.R;
import music.onestream.util.Constants;

public class SettingsActivity extends Activity {

    /**
     * Called when the activity is first created.
     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final ToggleButton storageLocation = (ToggleButton) findViewById(R.id.storageLocation);
        SharedPreferences settings = getSharedPreferences(Constants.dirInfoLoc, 0);
        if (settings.getBoolean(Constants.useExternalStorage, false))
        {
            storageLocation.setChecked(true);
        }

        storageLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(Constants.dirInfoLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (storageLocation.isChecked())
                {
                    editor.putBoolean(Constants.useExternalStorage, true);
                }
                else
                {
                    editor.putBoolean(Constants.useExternalStorage, false);
                }
                editor.commit();
            }
        });

        final Button playlist = (Button) findViewById(R.id.playlistPage);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playlist = new Intent(v.getContext(), EditPlaylistActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("Playlist", null);
                b.putSerializable("combinedList", null);
                playlist.putExtras(b);
                startActivityForResult(playlist, 0);
            }
        });

        final Button recommendation = (Button) findViewById(R.id.playlistRecommendationButton);
        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playlistRecommendations = new Intent(v.getContext(), PlaylistRecommendationsActivity.class);
                startActivityForResult(playlistRecommendations, 0);
            }
        });

        final ToggleButton songViewToggle = (ToggleButton) findViewById(R.id.songViewToggleButton);
        settings = getSharedPreferences(Constants.songViewLoc, 0);
        if (settings.getBoolean(Constants.songViewOn, false))
        {
            songViewToggle.setChecked(true);
        }

        songViewToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences settings = getSharedPreferences(Constants.songViewLoc, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    if (songViewToggle.isChecked())
                    {
                        editor.putBoolean(Constants.songViewOn, true);
                    }
                    else
                    {
                        editor.putBoolean(Constants.songViewOn, false);
                    }
                    editor.commit();
                }
            });


        final ToggleButton cacheSongsToggle = (ToggleButton) findViewById(R.id.cacheSongs);
        settings = getSharedPreferences(Constants.cacheSongsLoc, 0);
        if (settings.getBoolean(Constants.cacheSongOn, false))
        {
            cacheSongsToggle.setChecked(true);
        }

        cacheSongsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(Constants.cacheSongsLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (cacheSongsToggle.isChecked())
                {
                    editor.putBoolean(Constants.cacheSongOn, true);
                }
                else
                {
                    editor.putBoolean(Constants.cacheSongOn, false);
                }
                editor.commit();
            }
        });

        final ToggleButton cachePlaylistsToggle = (ToggleButton) findViewById(R.id.cachePlaylists);
        settings = getSharedPreferences(Constants.cachePlaylistsLoc, 0);
        if (settings.getBoolean(Constants.cachePlaylistsOn, false))
        {
            cachePlaylistsToggle.setChecked(true);
        }

        cachePlaylistsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(Constants.cachePlaylistsLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (cachePlaylistsToggle.isChecked())
                {
                    editor.putBoolean(Constants.cachePlaylistsOn, true);
                }
                else
                {
                    editor.putBoolean(Constants.cachePlaylistsOn, false);
                }
                editor.commit();
            }
        });

        settings = getSharedPreferences(Constants.dirInfoLoc, 0);
        final String directory = settings.getString(Constants.directory, Constants.defaultDirectory);
        TextView directoryTxt = (TextView) findViewById(R.id.dirName);
        directoryTxt.setText(directory);

        final Button accountsPage = (Button) findViewById(R.id.accountsPage);
        accountsPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent login = new Intent(v.getContext(), LoginActivity.class);
                startActivityForResult(login, 0);
            }
        });

        final Button sortPage = (Button) findViewById(R.id.sortPage);
        sortPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent sorting = new Intent(v.getContext(), SortingActivity.class);
                startActivityForResult(sorting, 0);
            }
        });


        final Button resetDir = (Button) findViewById(R.id.resetDir);
        resetDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView eTxt = (TextView) findViewById(R.id.dirName);
                eTxt.setText(Constants.defaultDirectory);
                SharedPreferences settings = getSharedPreferences(Constants.dirInfoLoc, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (!settings.getString(Constants.directory, Constants.defaultDirectory)
                        .equals(Constants.defaultDirectory))
                {
                    editor.putBoolean(Constants.directoryChanged, true);
                }
                editor.putString(Constants.directory, Constants.defaultDirectory);
                editor.commit();
            }
        });

        final Button change_dir = (Button) findViewById(R.id.change_dir);
        change_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogProperties properties= getDialogProperties();
                FilePickerDialog dialog = new FilePickerDialog(v.getContext(), properties);
                dialog.setTitle("Select Music Directory");
                dialog.show();

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        final TextView eTxt = (TextView) findViewById(R.id.dirName);
                        if (files.length > 0) {
                            eTxt.setText(files[0].toString());

                            SharedPreferences settings = getSharedPreferences(Constants.dirInfoLoc, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constants.directory, files[0]);
                            editor.putBoolean(Constants.directoryChanged, true);
                            editor.commit();
                        }
                    }
                });
            }
        });
    }

    private DialogProperties getDialogProperties() {
        SharedPreferences settings = getSharedPreferences(Constants.dirInfoLoc, 0);
        DialogProperties properties = new DialogProperties();
        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_AND_DIR_SELECT;
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=null;

        if (settings.getBoolean(Constants.useExternalStorage, false))
        {
            File f = new File ("/storage");
            String[] files = f.list();
            for (String file: files)
            {
                String[] parts = file.toString().split("-");
                if (parts.length == 2 && parts[0].length() == 4 && parts[1].length() == 4)
                {
                    properties.root= new File(f + "/" + file);
                    return properties;
                }
            }
            properties.root = f;

        }
        else
        {
            properties.root = new File((DialogConfigs.DEFAULT_DIR));
        }

        return properties;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}