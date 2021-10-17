package uz.android.universalvideoplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import uz.android.universalvideoplayer.databinding.ActivityPlayVideoBinding
import uz.android.universalvideoplayer.models.VideoModel


//https://gist.github.com/anry200/8455113

class PlayVideoActivity : AppCompatActivity(){

    var video_index = 0
    var current_pos = 0.0
    var total_duration = 0.0
    var mHandler: Handler? = null
    var handler: Handler? = null
    var videoView: VideoView? = null
    var audioManager: AudioManager? = null
    private var defaultVideoViewParams: RelativeLayout.LayoutParams? = null
    private var defaultScreenOrientationMode = 0


    // private lateinit var audioManager: AudioManager
    private lateinit var binding: ActivityPlayVideoBinding

    companion object {
        var videoArrayList = arrayListOf<VideoModel>()
        const val PERMISSION_READ = 0
    }



    var inVisible = true
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (checkPermission()) {
            setVideo()
            audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            videoView = binding.videoview
            binding.back.setOnClickListener {
                finish()
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }
//        binding.videoview.setOnTouchListener { v, event ->
//            var moving = false
//
//            when (event!!.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    moving = true
//                }
//
//                MotionEvent.ACTION_MOVE -> {
////                    if (moving) {
//                    binding.name.text = event.pointerCount.toString()
////                    }
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    moving = false
//                }
//            }
//
//            true
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setVideo() {

        video_index = intent.getIntExtra("pos", 0)
        mHandler = Handler()
        handler = Handler()
        binding.videoview.setOnCompletionListener {
            video_index++
            if (video_index < videoArrayList.size) {
                playVideo(video_index)
            } else {
                video_index = 0
                playVideo(video_index)
            }
        }
        binding.videoview.setOnPreparedListener { setVideoProgress() }
        playVideo(video_index)
        prevVideo()
        nextVideo()
        setPause()
        hideLayout()
    }

    // play video file
    fun playVideo(pos: Int) {

        try {
            binding.videoview.setVideoURI(videoArrayList[pos].videoUri)
            binding.videoview.start()
            binding.pause.setImageResource(R.drawable.ic_baseline_pause_circle_24)
            video_index = pos
            binding.name.text = videoArrayList[pos].videoTitle
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // display video progress
    fun setVideoProgress() {
        //get the video duration
        current_pos = binding.videoview.currentPosition.toDouble()
        total_duration = binding.videoview.duration.toDouble()

        //display video duration
        binding.total.text = timeConversion(total_duration.toLong())
        binding.current.text = timeConversion(current_pos.toLong())
        binding.seekbar.max = total_duration.toInt()
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    current_pos = binding.videoview.currentPosition.toDouble()
                    binding.current.text = timeConversion(current_pos.toLong())
                    binding.seekbar.progress = current_pos.toInt()
                    handler.postDelayed(this, 1000)
                } catch (ed: IllegalStateException) {
                    ed.printStackTrace()
                }
            }
        }
        handler.postDelayed(runnable, 1000)
        //seekbar change listner
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                current_pos = seekBar.progress.toDouble()
                binding.videoview.seekTo(current_pos.toInt())
            }
        })
    }

    //play previous video
    fun prevVideo() {
        binding.prev.setOnClickListener {
            if (video_index > 0) {
                video_index--
                playVideo(video_index)
            } else {
                video_index = videoArrayList.size - 1
                playVideo(video_index)
            }
        }
    }

    //play next video
    fun nextVideo() {
        binding.next.setOnClickListener {
            if (video_index < videoArrayList.size - 1) {
                video_index++
                playVideo(video_index)
            } else {
                video_index = 0
                playVideo(video_index)
            }
        }
    }

    //pause video
    fun setPause() {
        binding.pause.setOnClickListener {
            if (binding.videoview.isPlaying) {
                binding.videoview.pause()
                binding.pause.setImageResource(R.drawable.ic_baseline_play_circle_24)
            } else {
                binding.videoview.start()
                binding.pause.setImageResource(R.drawable.ic_baseline_pause_circle_24)
            }
        }
    }

    //time conversion
    fun timeConversion(value: Long): String {
        val videoTime: String
        val dur = value.toInt()
        val a = dur / 1000
        val hrs = a / 3600
        val mns = (a - hrs * 3600) / 60
        val scs = a - mns * 60 - hrs * 3600
        videoTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return videoTime
    }

    // hide progress when the video is playing
    fun hideLayout() {
        val runnable = Runnable {
            binding.showProgress.visibility = View.GONE
            binding.back.visibility = View.GONE
            binding.name.visibility = View.GONE
           // binding.seekBar1.visibility = View.GONE
            inVisible = false
        }
        handler!!.postDelayed(runnable, 5000)
        binding.relative.setOnClickListener {
            mHandler!!.removeCallbacks(runnable)
            if (inVisible) {
                binding.showProgress.visibility = View.GONE
                binding.back.visibility = View.GONE
                binding.name.visibility = View.GONE
               // binding.seekBar1.visibility = View.GONE
                inVisible = false
            } else {
                binding.showProgress.visibility = View.VISIBLE
                binding.back.visibility = View.VISIBLE
                binding.name.visibility = View.VISIBLE
             //   binding.seekBar1.visibility = View.VISIBLE
                mHandler!!.postDelayed(runnable, 8000)
                inVisible = true
            }
        }
    }


    // runtime storage permission
    fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ
            )
            return false
        }
        return true
    }
}



