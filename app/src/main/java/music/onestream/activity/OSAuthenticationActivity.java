package music.onestream.activity;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import music.onestream.R;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-03-22.
 */

public class OSAuthenticationActivity extends OSActivity {

    private GoogleApiClient mGoogleApiClient;

    public GoogleApiClient getGoogleApiClient() {

        if (mGoogleApiClient != null)
        {
            return mGoogleApiClient;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.getResources().getString(R.string.server_client_id))
                .requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).enableAutoManage(this,null)
                .build();

        return mGoogleApiClient;
    }

    public void startMainActivity() {
        Intent intent = OneStreamActivity.createIntent(this);
        startActivity(intent);
        finish();
    }
}
