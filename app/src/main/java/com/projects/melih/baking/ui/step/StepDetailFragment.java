package com.projects.melih.baking.ui.step;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.projects.melih.baking.R;
import com.projects.melih.baking.common.CollectionUtils;
import com.projects.melih.baking.common.Utils;
import com.projects.melih.baking.databinding.FragmentStepDetailBinding;
import com.projects.melih.baking.model.Step;
import com.projects.melih.baking.ui.base.BaseFragment;
import com.projects.melih.baking.ui.recipe.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Melih Gültekin on 06.05.2018
 * <p>
 * Detail Fragment(Second Pane) of RecipeDetailFragment
 */
public class StepDetailFragment extends BaseFragment implements View.OnClickListener, Player.EventListener {
    private static final String EXTRA_VIDEO_POSITION = "extra_video_position";
    private static final String EXTRA_IS_VIDEO_PLAYING = "extra_is_video_playing";
    private static final long INITIAL_POSITION = 0;
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private FragmentStepDetailBinding binding;
    private RecipeViewModel recipeViewModel;
    private StepPagerAdapter adapter;
    private long videoPosition = INITIAL_POSITION;
    private boolean isVideoPlaying = true;

    // for Video
    private static final String TAG = StepDetailFragment.class.getSimpleName();
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static MediaSessionCompat mediaSession;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private ArrayList<String> uris;

    public static StepDetailFragment newInstance() {
        return new StepDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_detail, container, false);
        recipeViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipeViewModel.class);

        recipeViewModel.getStepListLiveData().observe(this, steps -> {
            if (CollectionUtils.isNotEmpty(steps)) {
                ArrayList<String> uriList = new ArrayList<>();
                for (Step step : steps) {
                    uriList.add(step.getVideoUrl());
                }
                uris = uriList;
            }
            if (!context.getResources().getBoolean(R.bool.is_phone_and_land)) {
                adapter.setStepList(steps);
            }

            Integer selectedPosition = recipeViewModel.getSelectedStepPositionLiveData().getValue();
            if (selectedPosition == null) {
                updateUI(steps, 0);
            }
        });
        recipeViewModel.getSelectedStepPositionLiveData().observe(this, position -> {
            if (position != null) {
                updateUI(recipeViewModel.getStepListLiveData().getValue(), position);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (context.getResources().getBoolean(R.bool.is_phone_and_land)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            showToolbarAndStatusBar(false);
            binding.footerStep.previous.setOnClickListener(this);
            binding.footerStep.next.setOnClickListener(this);
        } else {
            // phone portrait or tablet
            adapter = new StepPagerAdapter(context);
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.setCanScroll(false);
            if (context.getResources().getBoolean(R.bool.is_phone)) {
                // only phone portrait
                binding.footerStep.previous.setOnClickListener(this);
                binding.footerStep.next.setOnClickListener(this);
            }
        }

        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getLong(EXTRA_VIDEO_POSITION, INITIAL_POSITION);
            isVideoPlaying = savedInstanceState.getBoolean(EXTRA_IS_VIDEO_PLAYING, true);
        }

        initializeMediaSession();
    }

    @Override
    public void onStart() {
        super.onStart();
        if ((Util.SDK_INT > Build.VERSION_CODES.M) && (exoPlayer != null)) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= Build.VERSION_CODES.M) && (exoPlayer != null)) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (exoPlayer != null) {
            outState.putLong(EXTRA_VIDEO_POSITION, exoPlayer.getCurrentPosition());
            outState.putBoolean(EXTRA_IS_VIDEO_PLAYING, exoPlayer.getPlayWhenReady());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= Build.VERSION_CODES.M) {
            releaseMediaPlayer();
            saveVideoData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            releaseMediaPlayer();
            saveVideoData();
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    @Override
    public void onClick(View v) {
        Utils.await(v);
        switch (v.getId()) {
            case R.id.previous:
                isVideoPlaying = true;
                videoPosition = INITIAL_POSITION;
                recipeViewModel.goToPreviousStep();
                break;
            case R.id.next:
                isVideoPlaying = true;
                videoPosition = INITIAL_POSITION;
                recipeViewModel.goToNextStep();
                break;
        }
    }

    private void updateUI(@Nullable List<Step> steps, Integer position) {
        final int stepListSize = CollectionUtils.size(steps);
        if (stepListSize > 0) {
            @SuppressWarnings("ConstantConditions")
            Step selectedStep = steps.get(position);
            // if the video url of this step is empty show an image
            if (TextUtils.isEmpty(selectedStep.getVideoUrl())) {
                String thumbnailUrl = selectedStep.getThumbnailUrl();
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .placeholder(R.drawable.ic_recipe_placeholder)
                        .error(R.drawable.ic_recipe_placeholder);
                Glide.with(context)
                        .asBitmap()
                        .apply(options)
                        .load(thumbnailUrl)
                        .thumbnail(0.1f)
                        .into(binding.stepImage);
                saveVideoData();

                binding.stepImage.setVisibility(View.VISIBLE);
                binding.playerView.setVisibility(View.GONE);
            } else {
                binding.stepImage.setVisibility(View.GONE);
                binding.playerView.setVisibility(View.VISIBLE);
                setSelectedVideoIndex(position, videoPosition, isVideoPlaying);
            }
        }

        if (context.getResources().getBoolean(R.bool.is_phone)) {
            binding.footerStep.next.setVisibility(((stepListSize - 1) == position) ? View.GONE : View.VISIBLE);
            if (position == 0) {
                binding.footerStep.previous.setVisibility(View.GONE);
                binding.footerStep.stepCount.setText("");
            } else {
                binding.footerStep.previous.setVisibility(View.VISIBLE);
                binding.footerStep.stepCount.setText(context.getString(R.string.step_count, position, (stepListSize - 1)));
            }
        }

        if (!context.getResources().getBoolean(R.bool.is_phone_and_land)) {
            binding.viewPager.setCurrentItem(position);
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
        mediaSession.setCallback(new MediaSessionCallback());

        // Start the Media Session since the activity is active.
        mediaSession.setActive(true);
    }

    private void initializeExoPlayer() {
        // Create an instance of the ExoPlayer.
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        binding.playerView.setPlayer(exoPlayer);

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

        Integer selectedPosition = recipeViewModel.getSelectedStepPositionLiveData().getValue();
        exoPlayer.seekTo((selectedPosition == null) ? 0 : selectedPosition, videoPosition);
        exoPlayer.setPlayWhenReady(isVideoPlaying);
    }

    private void setSelectedVideoIndex(int indexOfSelectedVideo, long videoPosition, boolean isVideoPlaying) {
        if (exoPlayer == null) {
            initializeExoPlayer();
        }
        exoPlayer.seekTo(indexOfSelectedVideo, videoPosition);
        exoPlayer.setPlayWhenReady(isVideoPlaying);
    }

    private void saveVideoData() {
        if (exoPlayer != null) {
            videoPosition = exoPlayer.getCurrentPosition();
            isVideoPlaying = exoPlayer.getPlayWhenReady();
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void releaseMediaPlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
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
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
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