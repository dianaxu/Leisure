package com.example.leisure.test;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import androidx.annotation.Nullable;

public class TestActivity extends Activity implements Player.EventListener, CacheListener {
    private PlayerView playerView;
    private ExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    private String mUrl = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
    private SimpleExoPlayerView simpleExoPlayerView;
    private ProgressBar progressBar;
    private SimpleExoPlayer simpleExoPlayer;
    private ImageView cacheStatusImageView;

//    private final VideoProgressUpdater updater = new VideoProgressUpdater();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        playerView = findViewById(R.id.player_view);
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);
        progressBar = findViewById(R.id.progressBar);
        cacheStatusImageView = findViewById(R.id.cacheStatusImageView);

//        initializePlayer();
        initPlay();
//        player.addListener(this);

    }


    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            playerView.setPlayer(player);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        String url = "http://audio04.dmhmusic.com/71_53_T10041176570_128_4_1_0_sdk-cpm/cn/0206/M00/2C/89/ChR47FpkiHqASUcwADdm2vb1TNk011.mp3?xcode=05d5f42c71f29fef59c78388c595369dbb58f79";
//        String url = "http://res.lgdsunday.club/Champ%20de%20tournesol.mp3";
        HttpProxyCacheServer proxy = MainApplication.getProxy(this);
        String proxyUrl = proxy.getProxyUrl(url);
        Uri uri = Uri.parse(proxyUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, false, true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Toast.makeText(TestActivity.this, timeline.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(TestActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    //---------------------------------------------------------
    private void initPlay() {
        checkCachedState();
        simpleExoPlayer = setupPlayer();
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private SimpleExoPlayer setupPlayer() {
        simpleExoPlayerView.setUseController(false);
        HttpProxyCacheServer proxy = MainApplication.getProxy(this);
        proxy.registerCacheListener(this, mUrl);
        String proxyUrl = proxy.getProxyUrl(mUrl);
        Log.d("TestActivity", "Use proxy url " + proxyUrl + " instead of original url " + mUrl);

        SimpleExoPlayer exoPlayer = newSimpleExoPlayer();
        simpleExoPlayerView.setPlayer(exoPlayer);

        MediaSource videoSource = newVideoSource(proxyUrl);
        exoPlayer.prepare(videoSource);

        return exoPlayer;
    }

    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
    }

    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        String userAgent = Util.getUserAgent(this, "AndroidVideoCache sample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = MainApplication.getProxy(this);
        boolean fullyCached = proxy.isCached(mUrl);
        setCachedState(fullyCached);
        if (fullyCached) {
            progressBar.setSecondaryProgress(100);
        }
    }


    private void setCachedState(boolean cached) {
        int statusIconId = cached ? R.drawable.ic_cloud_done : R.drawable.ic_cloud_download;
        cacheStatusImageView.setImageResource(statusIconId);
    }


    //----------------------------------------
    private MediaSource buildMediaSource(Uri uri) {
        DefaultHttpDataSourceFactory dataSourceFactory =
                new DefaultHttpDataSourceFactory("user-agent");

        ExtractorMediaSource videoSource =
                new ExtractorMediaSource.Factory(dataSourceFactory).
                        createMediaSource(uri);

        ExtractorMediaSource audioSource =
                new ExtractorMediaSource.Factory(dataSourceFactory).
                        createMediaSource(uri);

        return new ConcatenatingMediaSource(audioSource, videoSource);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady) {

        }
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
        progressBar.setSecondaryProgress(percentsAvailable);
        setCachedState(percentsAvailable == 100);
        Log.d("TestActivity", String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, file, url));
    }
}