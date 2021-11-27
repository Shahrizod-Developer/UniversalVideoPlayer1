package uz.android.universalvideoplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.android.universalvideoplayer.adapters.VideoAdapter
import uz.android.universalvideoplayer.databinding.ActivityPlayVideoBinding
import uz.android.universalvideoplayer.models.PlayVideo
import uz.android.universalvideoplayer.models.VideoModel
import java.io.File
import kotlin.math.abs





//https://gist.github.com/anry200/8455113

class PlayVideoActivity : AppCompatActivity(), PlayVideo {

    var video_index = 0
    var current_pos = 0.0
    var total_duration = 0.0
    var mHandler: Handler? = null
    var handler: Handler? = null
    var videoView: VideoView? = null
    var audioManager: AudioManager? = null

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
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setVideo() {

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
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun playVideo(pos: Int) {
        var oldX = 0F
        var oldY = 0F
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            binding.videoview.setVideoURI(videoArrayList[pos].videoUri)
            binding.videoview.start()
            binding.pause.setImageResource(R.drawable.ic_baseline_pause_circle_24)
            video_index = pos
            binding.name.text = videoArrayList[pos].videoTitle
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val adapter = VideoAdapter(this, videoArrayList)
        binding.menu.setOnClickListener {

            val dialog1 = BottomSheetDialog(this)
            val dialogView1 = LayoutInflater.from(this).inflate(R.layout.menu_diaolog, null)
            val share = dialogView1.findViewById<LinearLayout>(R.id.share)
            val delete = dialogView1.findViewById<LinearLayout>(R.id.delete)
            val details = dialogView1.findViewById<LinearLayout>(R.id.details)

            share.setOnClickListener {

                val uri = videoArrayList[pos].videoUri
                val file = File(uri.path)
                adapter.shareVideo(videoArrayList[pos].videoTitle, file.toString(), pos)
                dialog1.cancel()
            }
            delete.setOnClickListener {
                val uri = videoArrayList[pos].videoUri
                val file = File(uri.path)

                val dialogdelete = AlertDialog.Builder(this).create()
                val dialogViewdelete = LayoutInflater.from(this).inflate(R.layout.dialod_delete, null)
                val yes = dialogViewdelete.findViewById<TextView>(R.id.yes)
                val no = dialogViewdelete.findViewById<TextView>(R.id.no)

                yes.setOnClickListener {
                    adapter.scanDeletedMedia(this, file)
                    adapter.removeAt(pos)
                    Toast.makeText(this, "Deleted" + videoArrayList[pos].videoTitle, Toast.LENGTH_SHORT).show()
                    val intent = Intent(
                        this,
                        MainActivity::class.java)
                    startActivity(intent)
                    dialogdelete.cancel()
                    dialog1.cancel()
                }
                no.setOnClickListener {
                    dialogdelete.cancel()
                    dialog1.cancel()
                }
                dialogdelete.setView(dialogViewdelete)
                dialogdelete.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialogdelete.show()
            }
            details.setOnClickListener {
                val dialog = BottomSheetDialog(this)
                val dialogView = LayoutInflater.from(this).inflate(
                    R.layout.details_diaolog,
                    null
                )
                val pixel = dialogView.findViewById<TextView>(R.id.pixel)
                val time = dialogView.findViewById<TextView>(R.id.add_time)
                val adress = dialogView.findViewById<TextView>(R.id.adress)
                val name = dialogView.findViewById<TextView>(R.id.name)
                val size = dialogView.findViewById<TextView>(R.id.size)
                val duration = dialogView.findViewById<TextView>(R.id.duration)
                val ok = dialogView.findViewById<CardView>(R.id.ok)
                name.text = "Name:  " + videoArrayList[pos].videoTitle
                time.text = "Edit time:  " + videoArrayList[pos].videoAddedTime
                adress.text = "Video adress:  " + videoArrayList[pos].adress
                size.text = "Size:  " + videoArrayList[pos].videoSize
                duration.text = "Duration:  " + videoArrayList[pos].videoDuration
                pixel.text = "Pixel capacity:  " + videoArrayList[pos].videoPxile

                ok.setOnClickListener {
                    dialog.cancel()
                }
                dialog.setContentView(dialogView)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.show()
                dialog1.cancel()
            }
            dialog1.setContentView(dialogView1)
            dialog1.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog1.show()
        }

        binding.videoview.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                oldY = event.y
                oldX = event.x
            }
                if ((event.y < oldY && oldX > 360) && ((oldY - event.y).toInt() % 10 == 0)) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                }
                if ((event.y > oldY && oldX > 360) && ((oldY - event.y).toInt() % 10 == 0)) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            }
//            binding.name.text = binding.videoview.width.toString() + " / " + oldX + " / " + oldY

            true
        }
    }

    // display video progress
    override fun setVideoProgress() {
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
    override fun prevVideo() {
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
    override fun nextVideo() {
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
    override fun setPause() {
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
    override fun hideLayout() {
        val runnable = Runnable {
            binding.showProgress.visibility = View.GONE
            binding.back.visibility = View.GONE
            binding.menu.visibility = View.GONE
            binding.name.visibility = View.GONE
            inVisible = false
        }
        handler!!.postDelayed(runnable, 5000)
        binding.relative.setOnClickListener {
            mHandler!!.removeCallbacks(runnable)
            if (inVisible) {
                binding.showProgress.visibility = View.GONE
                binding.back.visibility = View.GONE
                binding.menu.visibility = View.GONE
                binding.name.visibility = View.GONE
                // binding.seekBar1.visibility = View.GONE
                inVisible = false
            } else {
                binding.showProgress.visibility = View.VISIBLE
                binding.back.visibility = View.VISIBLE
                binding.menu.visibility = View.VISIBLE
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



