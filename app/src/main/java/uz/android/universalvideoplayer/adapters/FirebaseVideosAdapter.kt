package uz.android.universalvideoplayer.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import uz.android.universalvideoplayer.PlayVideoActivity
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.databinding.ItemViewVideosBinding
import uz.android.universalvideoplayer.models.FirebaseVideo

class FirebaseVideosAdapter() : RecyclerView.Adapter<FirebaseVideosAdapter.ViewHolder>() {
    var video_index = 0
    var current_pos = 0.0
    var total_duration = 0.0
    var mHandler: Handler? = null
    var handler: Handler? = null
    private val itemCallBack = object : DiffUtil.ItemCallback<FirebaseVideo>() {
        override fun areItemsTheSame(oldItem: FirebaseVideo, newItem: FirebaseVideo): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: FirebaseVideo, newItem: FirebaseVideo): Boolean {
            return oldItem.name == newItem.name
        }
    }
    val differ = AsyncListDiffer(this, itemCallBack)
    inner class ViewHolder(private val binding: ItemViewVideosBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        val videoview = binding.video
        val pause = binding.pause
        val play = binding.play
        fun onBind(videos: FirebaseVideo) {
            binding.name.text = videos.name
            Picasso.get().load(videos.image).into(binding.image)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoModel = differ.currentList[position]
        holder.onBind(videoModel)
    }
}
