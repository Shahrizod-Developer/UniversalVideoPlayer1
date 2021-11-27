package uz.android.universalvideoplayer.adapters

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.adapters.VideoAdapter.viewHolder
import uz.android.universalvideoplayer.models.VideoModel
import java.io.File

class VideoAdapter(var context: Context, var videoArrayList: ArrayList<VideoModel>) :
    RecyclerView.Adapter<viewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener
    lateinit var onItemLongClickListener: OnItemLongClickListener

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, i: Int) {

        Glide.with(context)
            .load(videoArrayList[i].videoUri.toString())
            .into(holder.image)

        holder.time.text = videoArrayList[i].videoDuration

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener.onItemLongClick(i)
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(i)

        }
    }

    override fun getItemCount(): Int {
        return videoArrayList.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var image: ImageView = itemView.findViewById(R.id.image)
        var time : TextView = itemView.findViewById(R.id.time)


    }

    @JvmName("setOnItemClickListener1")
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        if (onItemClickListener != null) {
            this.onItemClickListener = onItemClickListener
        }
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }
    interface OnItemLongClickListener {
        fun onItemLongClick(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

        return viewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_videos,
                parent,
                false
            )
        )
    }

    fun scanDeletedMedia(context: Context, file: File) {
        if (SDK_INT >= 19) {
            context.contentResolver.delete(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media.DATA + "= ?",
                arrayOf(file.absolutePath)
            )
        } else {
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)))
        }
    }

    fun removeAt(position: Int) {
        videoArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, videoArrayList.size)
    }

    fun shareVideo(title: String?, path: String, pos: Int) {

        val contentResolver: ContentResolver? = context?.contentResolver

        MediaScannerConnection.scanFile(
            context, arrayOf(path),
            null
        ) { path, uri ->
            val shareIntent = Intent(
                Intent.ACTION_SEND
            )
            shareIntent.type = "video/*"
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT, title
            )
            shareIntent.putExtra(
                Intent.EXTRA_TITLE, title
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(contentResolver, videoArrayList[pos].videoTitle)
                )
            )
        }
    }
}