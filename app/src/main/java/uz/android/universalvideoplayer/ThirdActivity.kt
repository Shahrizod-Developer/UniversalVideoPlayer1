package uz.android.universalvideoplayer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.media2.exoplayer.external.SimpleExoPlayer
import androidx.media2.exoplayer.external.source.MediaSource
import androidx.media2.exoplayer.external.source.hls.HlsMediaSource
import androidx.media2.exoplayer.external.upstream.DefaultHttpDataSourceFactory
import androidx.media2.exoplayer.external.util.Util
import uz.android.universalvideoplayer.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {


    private var mPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0;
    private lateinit var binding: ActivityThirdBinding
    private val hlsUrl =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

    @SuppressLint("RestrictedApi")
    private fun initPlayer() {
      //  mPlayer = SimpleExoPlayer.Builder(this).build()
        // Bind the player to the view.
    //    binding.playerView.player = mPlayer
        mPlayer!!.playWhenReady = true
        mPlayer!!.seekTo(playbackPosition)
        mPlayer!!.prepare(buildMediaSource(), false, false)

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    @SuppressLint("RestrictedApi")
    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        playWhenReady = mPlayer!!.playWhenReady
        playbackPosition = mPlayer!!.currentPosition
        currentWindow = mPlayer!!.currentWindowIndex
        mPlayer!!.release()
        mPlayer = null
    }

    @SuppressLint("RestrictedApi")
    private fun buildMediaSource(): MediaSource {
        val userAgent =
                Util.getUserAgent(binding.playerView.context, binding.playerView.context.getString(R.string.app_name))

        val dataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
        val hlsMediaSource =
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(hlsUrl))

        return hlsMediaSource
    }

}