package music.onestream;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class PlaylistActivity {

    final Button addtracks = (Button) findViewById(R.id.addtracks);
    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent addtracks = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(addtracks, 0);
        }
    });

}
