package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import music.onestream.R;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-21.
 */

public class OneStreamDomainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onestream_domain);

        final EditText oneStreamDomain = (EditText) findViewById(R.id.oneStreamDomain);
        Button confirmDomainButton = (Button) findViewById(R.id.confirmDomainButton);

        final SharedPreferences domainSettings = getSharedPreferences(Constants.oneStreamDomainLoc, 0);
        String oldDomain = domainSettings.getString(Constants.domain, Constants.defaultDomain);
        oneStreamDomain.setText(oldDomain);

        confirmDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDomain = oneStreamDomain.getText().toString();
                SharedPreferences.Editor editor = domainSettings.edit();
                editor.putString(Constants.domain, newDomain);
                editor.commit();

                //Clear out old domain playlists
                OneStreamActivity.getPlaylistHandler().resetPlaylists();

                Intent login = new Intent(v.getContext(), OneStreamActivity.class);
                startActivityForResult(login, 0);
            }
        });
    }

}
