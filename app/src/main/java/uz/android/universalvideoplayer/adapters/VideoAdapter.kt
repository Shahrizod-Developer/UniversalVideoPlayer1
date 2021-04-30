package uz.android.universalvideoplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.adapters.VideoAdapter.viewHolder
import uz.android.universalvideoplayer.models.VideoModel
import java.io.File

class VideoAdapter(var context: Context, var videoArrayList: ArrayList<VideoModel>) :
    RecyclerView.Adapter<viewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, i: Int) {
        holder.title.text = videoArrayList[i].videoTitle
        holder.duration.text = videoArrayList[i].videoDuration
        holder.size.text = videoArrayList[i].videoSize

        Glide.with(context)
            .load(videoArrayList[i].videoUri.toString())
            .into(holder.image)

        holder.menu.setOnClickListener {

            val dialog1 = BottomSheetDialog(this.context)
            val dialogView1 = LayoutInflater.from(context).inflate(R.layout.menu_diaolog, null)
            val share = dialogView1.findViewById<LinearLayout>(R.id.share)
            val delete = dialogView1.findViewById<LinearLayout>(R.id.delete)
            val details = dialogView1.findViewById<LinearLayout>(R.id.details)

            share.setOnClickListener {
                dialog1.cancel()
            }
            delete.setOnClickListener {
                dialog1.cancel()
            }
            details.setOnClickListener {
                val dialog = BottomSheetDialog(this.context)
                val dialogView = LayoutInflater.from(context).inflate(
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
                name.text = "Name:  " + videoArrayList[i].videoTitle
                time.text = "Edit time:  " + videoArrayList[i].videoAddedTime
                adress.text = "Video adress:  " + videoArrayList[i].adress
                size.text = "Size:  " + videoArrayList[i].videoSize
                duration.text = "Duration:  " + videoArrayList[i].videoDuration
                pixel.text = "Pixel capacity:  " + videoArrayList[i].videoPxile

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
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(i) }
    }
    override fun getItemCount(): Int {
        return videoArrayList.size
    }
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.findViewById(R.id.title)
        var duration: TextView = itemView.findViewById(R.id.duration)
        var size: TextView = itemView.findViewById(R.id.size)
        var image: ImageView = itemView.findViewById(R.id.image)
        var menu: ImageView = itemView.findViewById(R.id.menu)
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
                        R.layout.list_videos,
                        parent,
                        false
                )
        )
    }
}