package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    public static final int ACTIVITY_CHANGE_DIR = 6;
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
                    Intent settings = new Intent(v.getContext(), MainActivity.class);
                    startActivityForResult(settings, 0);
                }
            });

        SharedPreferences settings = getSharedPreferences("dirInfo", 0);
        String directory = settings.getString("dir", "Default");
        TextView directoryTxt = (TextView) findViewById(R.id.dirName);
        directoryTxt.setText(directory);

        final DialogProperties properties=new DialogProperties();

        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root=new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=null;


        final Button resetDir = (Button) findViewById(R.id.resetDir);
        resetDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView eTxt = (TextView) findViewById(R.id.dirName);
                eTxt.setText("Default");
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
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