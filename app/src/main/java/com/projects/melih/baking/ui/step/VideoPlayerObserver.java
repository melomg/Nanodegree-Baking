package com.projects.melih.baking.ui.step;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.projects.melih.baking.R;

import java.util.ArrayList;

/**
 * Created by Melih Gültekin on 09.05.2018
 */
public class VideoPlayerObserver implements LifecycleObserver, Player.EventListener {
    private static final String TAG = VideoPlayerObserver.class.getSimpleName();
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static MediaSessionCompat mediaSession;
    private Context context;
    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private ArrayList<String> uris;

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart() {
        if ((Util.SDK_INT > Build.VERSION_CODES.M) && (exoPlayer != null)) {
            initializeExoPlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        if ((Util.SDK_INT <= Build.VERSION_CODES.M) && (exoPlayer != null)) {
            initializeExoPlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        if (Util.SDK_INT <= Build.VERSION_CODES.M) {
            releaseMediaPlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            releaseMediaPlayer();
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void setPlayerView(@NonNull Context context, @NonNull PlayerView playerView) {
        this.context = context;
        this.playerView = playerView;
        initializeMediaSession();
    }

    public void setUris(@NonNull ArrayList<String> uris) {
        this.uris = uris;
    }

    public void setSelectedVideoIndex(int indexOfSelectedVideo, long videoPosition, boolean isVideoPlaying) {
        if (exoPlayer == null) {
            initializeExoPlayer();
        }
        exoPlayer.seekTo(indexOfSelectedVideo, videoPosition);
        exoPlayer.setPlayWhenReady(isVideoPlaying);
    }

    // ExoPlayer Event Listeners
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        //no-op
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        if (exoPlayer.getCurrentWindowIndex() != 0) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        //no-op
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        final long currentPosition = exoPlayer.getCurrentPosition();
        final float playbackSpeed = 1f;
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, currentPosition, playbackSpeed);
        } else if ((playbackState == Player.STATE_READY)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, currentPosition, playbackSpeed);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        //no-op
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        //no-op
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        //no-op
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        //no-op
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        //no-op
    }

    @Override
    public void onSeekProcessed() {
        //no-op
    }

    private void releaseMediaPlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mediaSession = new MediaSessionCompat(context, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mediaSession.setActive(true);
    }

    private void initializeExoPlayer() {
        // Create an instance of the ExoPlayer.
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        playerView.setPlayer(exoPlayer);

        // Set the ExoPlayer.EventListener to this activity.
        exoPlayer.addListener(this);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (String uriString : uris) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent, BANDWIDTH_METER);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(uriString));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        exoPlayer.prepare(concatenatingMediaSource);
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0L);
        }
    }
}