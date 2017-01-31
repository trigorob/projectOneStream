package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

public class Settings extends Activity {

    private static final int ACTIVITY_CHOOSE_FILE = 5;

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

        final DialogProperties properties=new DialogProperties();

        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root=new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=null;


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
                        final EditText eTxt = (EditText) findViewById(R.id.dirName);
                        if (files.length > 0) {
                            eTxt.setText(files[0].toString());
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