package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

public class Settings extends Activity {

    private static final String PREFS_NAME = "dirInfo";

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        final View view = (ViewPager) findViewById(R.id.container);
        final Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent back = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(back, 0);
                }
            });

        final Button playlist = (Button) findViewById(R.id.playlistPage);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playlist = new Intent(v.getContext(), EditPlaylistActivity.class);
                startActivityForResult(playlist, 0);
            }
        });


        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        final String directory = settings.getString("dir", "Default");
        TextView directoryTxt = (TextView) findViewById(R.id.dirName);
        directoryTxt.setText(directory);

        final DialogProperties properties=new DialogProperties();

        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root=new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=null;

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
                eTxt.setText("Default");
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (!settings.getString("dir", "Default").equals("Default"))
                {
                    editor.putBoolean("directoryChanged", true);
                }
                editor.putString("dir", "Default");
                editor.commit();
            }
        });


        final Button change_dir = (Button) findViewById(R.id.change_dir);
        change_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePickerDialog dialog = new FilePickerDialog(v.getContext(), properties);
                dialog.setTitle("Select Music Directory");
                dialog.show();

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        final TextView eTxt = (TextView) findViewById(R.id.dirName);
                        if (files.length > 0) {
                            eTxt.setText(files[0].toString());

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("dir", files[0]);
                            editor.putBoolean("directoryChanged", true);
                            editor.commit();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}