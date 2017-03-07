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

import music.onestream.activity.PlaylistActivity;
import music.onestream.R;
import music.onestream.activity.SongActivity;
import music.onestream.activity.OneStreamActivity;
import music.onestream.util.PlayerActionsHandler;

/**
 * Created by ruspe_000 on 2017-02-27.
 */

public class OneStreamPlayerService extends Service {

    public static final String ACTION_INIT = "action_init";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_STOP = "action_stop";


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

        if (action.equalsIgnoreCase(ACTION_INIT))
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

        else if (action.equalsIgnoreCase(ACTION_PLAY))
        {
            mediaController.getTransportControls().play();
            playerHandler.resumeSong(playerHandler.getCurrentSongListPosition());

        }
        else if (action.equalsIgnoreCase(ACTION_PAUSE))
        {
            mediaController.getTransportControls().pause();
            playerHandler.stopSong();
        }
        else if (action.equalsIgnoreCase(ACTION_NEXT))
        {
            mediaController.getTransportControls().skipToNext();
            mediaController.getTransportControls().play();
            if (!playerHandler.isRandomNext()) {
                playerHandler.playRandomSong();
            }
            else {
                playerHandler.nextSong();
            }
        }
        else if (action.equalsIgnoreCase(ACTION_REWIND))
        {
            mediaController.getTransportControls().rewind();
            playerHandler.playSong(playerHandler.getCurrentSongListPosition());
        }
        else if (action.equalsIgnoreCase(ACTION_PREVIOUS))
        {
            mediaController.getTransportControls().skipToPrevious();
            mediaController.getTransportControls().play();
            playerHandler.previousSong();
        }
        else if (action.equalsIgnoreCase(ACTION_STOP))
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
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
        //Play/Pause
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setShowWhen(false)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.logo))
                .setDeleteIntent(pendingIntent)
                .setStyle(style)
                .setContentTitle("OneStream");

        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        if (action.title.equals("Pause")) {
            builder.addAction(action);
        }
        else {
            builder.addAction(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
        }
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
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
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                buildNotification(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
            }

            @Override
            public void onRewind() {
                super.onRewind();
                buildNotification(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                buildNotification(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
            }

            @Override
            public void onStop() {
                super.onStop();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), OneStreamPlayerService.class);
                stopService(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
    }

}
