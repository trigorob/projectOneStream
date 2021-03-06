package music.onestream.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import music.onestream.activity.PlaylistActivity;
import music.onestream.R;
import music.onestream.activity.SongActivity;
import music.onestream.activity.OneStreamActivity;
import music.onestream.song.Song;
import music.onestream.util.Constants;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-27.
 */

public class OneStreamPlayerService extends Service {

    private String currentActivity = "";
    private MediaSession session;
    private MediaController mediaController;

    private PlayerActionsHandler playerHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        session.release();
        return super.onUnbind(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();

        if (action.equalsIgnoreCase(Constants.ACTION_INIT))
        {
            if (intent.getExtras().get("currentActivity").equals("OneStreamActivity")) {
                currentActivity = "OneStreamActivity";
            }
            else if (intent.getExtras().get("currentActivity").equals("PlaylistActivity")) {
                currentActivity = "PlaylistActivity";
            }
            initPlayerHandler();
            mediaController.getTransportControls().pause();
        }

        else if (action.equalsIgnoreCase(Constants.ACTION_ICON_PAUSE))
        {
            mediaController.getTransportControls().pause();
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_ICON_PLAY))
        {
            mediaController.getTransportControls().play();
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_ICON_SHUFFLE))
        {
            //This can be anything, Just need to rebuild the notification
            mediaController.getTransportControls().skipToNext();
        }

        else if (action.equalsIgnoreCase(Constants.ACTION_PLAY))
        {
            mediaController.getTransportControls().play();
            playerHandler.resumeSong(playerHandler.getCurrentSongListPosition());

        }
        else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE))
        {
            mediaController.getTransportControls().pause();
            playerHandler.stopSong();
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_REWIND))
        {
            mediaController.getTransportControls().play();
            playerHandler.playSong(playerHandler.getCurrentSongListPosition());
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_SHUFFLE))
        {
            playerHandler.setRandomNext(!playerHandler.isRandomNext());
            if (playerHandler.isPlaying()) {
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
            }
            else {
                buildNotification(generateAction(R.drawable.play, "Play", Constants.ACTION_PLAY));
            }
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_NEXT))
        {
            mediaController.getTransportControls().skipToNext();
            if (playerHandler.isRandomNext()) {
                playerHandler.playRandomSong();
            }
            else {
                playerHandler.nextSong();
            }
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_REWIND))
        {
            mediaController.getTransportControls().rewind();
            playerHandler.playSong(playerHandler.getCurrentSongListPosition());
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_PREVIOUS))
        {
            mediaController.getTransportControls().skipToPrevious();
            playerHandler.previousSong();
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_STOP))
        {
            mediaController.getTransportControls().stop();
            playerHandler.onPause();
        }

    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification(Notification.Action action) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
        intent.setAction(Constants.ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setShowWhen(false)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.logo))
                .setDeleteIntent(pendingIntent)
                .setOngoing(true);

        if (playerHandler != null && playerHandler.getCurrentSongListPosition() != -1) {
            Song currentSong = playerHandler.getCurrentSong();
            builder.setContentTitle(currentSong.getName());
            if (!currentSong.getArtist().equals(Constants.defaultArtistsAlbumGenreName) &&
             !currentSong.getAlbum().equals(Constants.defaultArtistsAlbumGenreName)) {
                builder.setContentText(currentSong.getArtist());
                builder.setSubText(currentSong.getAlbum());
            }
            else if (!currentSong.getGenre().equals(Constants.defaultArtistsAlbumGenreName))
            {
                builder.setContentText(currentSong.getGenre());
            }
        }

        builder.setStyle(style);

        builder.addAction(R.drawable.rewind, "Rewind", generatePendingIntent(Constants.ACTION_REWIND));
        builder.addAction(R.drawable.previous, "Previous", generatePendingIntent(Constants.ACTION_PREVIOUS));
        if (playerHandler != null && playerHandler.isPlaying() || action.title.equals("Pause") ||
                action.title.equals("Previous") || action.title.equals("Next")) {
            builder.addAction(R.drawable.pause, "Pause", generatePendingIntent(Constants.ACTION_PAUSE));
        }
        else {
            builder.addAction(R.drawable.play, "Play", generatePendingIntent(Constants.ACTION_PLAY));
        }
        builder.addAction(R.drawable.skip, "Next", generatePendingIntent(Constants.ACTION_NEXT));
        if (playerHandler != null && playerHandler.isRandomNext()) {
            builder.addAction(R.drawable.shuffleoff, "Shuffle", generatePendingIntent(Constants.ACTION_SHUFFLE));
        }
        else {
            builder.addAction(R.drawable.shuffle, "Shuffle", generatePendingIntent(Constants.ACTION_SHUFFLE));
        }
        style.setShowActionsInCompactView(1, 2, 3);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(1, builder.build());
    }

    private PendingIntent generatePendingIntent(String action) {
        Intent resultIntent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
        resultIntent.setAction(action);
        PendingIntent resultPendingIntent =
                PendingIntent.getService(getApplicationContext(), 0, resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return resultPendingIntent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMediaSession();
        initPlayerHandler();
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initPlayerHandler()
    {
        if (currentActivity.equalsIgnoreCase("PlaylistActivity")) {
            playerHandler = PlaylistActivity.getPlayerHandler();
        } else if (currentActivity.equalsIgnoreCase("OneStreamActivity")) {
            playerHandler = OneStreamActivity.getPlayerHandler();
        } else if (currentActivity.equalsIgnoreCase("SongActivity")) {
            playerHandler = SongActivity.getPlayerHandler();
        }
    }

    private void initMediaSession() {

        session = new MediaSession(getApplicationContext(), "OneStream Session");
        mediaController = new MediaController(getApplicationContext(), session.getSessionToken());
        session.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                buildNotification(generateAction(android.R.drawable.ic_media_next, "Next", Constants.ACTION_NEXT));
            }

            @Override
            public void onRewind() {
                super.onRewind();
                buildNotification(generateAction(android.R.drawable.ic_media_rew, "Rewind", Constants.ACTION_REWIND));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                buildNotification(generateAction(android.R.drawable.ic_media_rew, "Previous", Constants.ACTION_PREVIOUS));
            }

            @Override
            public void onStop() {
                super.onStop();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
                stopForeground(true);
                stopService(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        stopForeground(true);
        stopSelf();
    }

}
