package uz.android.universalvideoplayer.adapters

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.models.VideoModel
import java.io.File

class ItemSelectAdapter(var context: Context, var videoArrayList: ArrayList<VideoModel>) :
    RecyclerView.Adapter<ItemSelectAdapter.viewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener
    lateinit var onItemLongClickListener: VideoAdapter.OnItemLongClickListener


    var count = 0
    var list = ArrayList<Int>()
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, i: Int) {

        Glide.with(context)
            .load(videoArrayList[i].videoUri.toString())
            .into(holder.image)

        holder.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                list.add(i)
                Toast.makeText(context, list.size.toString(), Toast.LENGTH_SHORT).show()
                count++
            }
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
        var checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

        return viewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_video_select,
                parent,
                false
            )
        )
    }

}