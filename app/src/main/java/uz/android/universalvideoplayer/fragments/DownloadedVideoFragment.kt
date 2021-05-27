package uz.android.universalvideoplayer.fragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import uz.android.universalvideoplayer.PlayVideoActivity
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.adapters.VideoAdapter
import uz.android.universalvideoplayer.databinding.FragmentDownloadedVideoBinding
import uz.android.universalvideoplayer.databinding.FragmentPhoneMemoryBinding
import uz.android.universalvideoplayer.models.VideoModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class   DownloadedVideoFragment : Fragment() {

    private lateinit var binding: FragmentDownloadedVideoBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloadedVideoBinding.inflate(inflater, container, false)

        videoList()

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun videoList() {
        binding.rv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rv.itemAnimator = DefaultItemAnimator()
        getVideos()
    }
    //get video files from storage
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("Recycle")
    fun getVideos() {
        val contentResolver: ContentResolver? = context?.contentResolver
        val selection = MediaStore.Video.Media.DATA + " like?"
        val selectionArgs = arrayOf("%/storage/emulated/0/Telegram/Telegram Video%")
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver?.query(uri, null, selection,
                selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC")
        val videoArrayList = arrayListOf<VideoModel>()

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {
                    val title: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val duration: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                    val data: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val adress: String = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val size: String = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val time: String = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                    val pixel: String = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
                    val videoModel = VideoModel(
                        title, timeConversion(duration.toLong()), size(
                            size.toLong()
                        ),
                        adress, Uri.parse(data), convertLongToDate(time.toLong()), pixel
                    )
                    videoArrayList.add(videoModel)
                } while (cursor.moveToNext())
            } catch (e: Exception) {
            }
            PlayVideoActivity.videoArrayList = videoArrayList
        }
        val adapter = VideoAdapter(requireActivity(), PlayVideoActivity.videoArrayList)
        binding.rv.adapter = adapter
        adapter.onItemClickListener = object : VideoAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                val intent = Intent(
                    requireContext(),
                    PlayVideoActivity::class.java
                )
                intent.putExtra("pos", pos)
                startActivity(intent)
            }
        }
    }
    //time conversion
    fun timeConversion(value: Long): String{
        val videoTime: String
        val dur = value.toInt()
        val a = dur/1000
        val hrs = a / 3600
        val mns = (a - hrs * 3600)/60
        val scs = a - mns * 60 - hrs * 3600
        videoTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return videoTime
    }
//Just multiply it by 1000 to get correct date

    fun convertLongToDate(time: Long): String =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd MMMM yyyy").format(
                Instant.ofEpochMilli(time * 1000)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )
        } else {
            SimpleDateFormat("dd MMMM yyyy").format(
                Date(time * 1000)
            )
        }
    fun size(value: Long): String {
        val a = value.toDouble()
        val b: Double = a/1048576
        val decimal = BigDecimal(b).setScale(2, RoundingMode.HALF_EVEN)
        return "$decimal MB"
    }
}

