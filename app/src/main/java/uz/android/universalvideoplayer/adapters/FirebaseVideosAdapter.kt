package uz.android.universalvideoplayer.adapters

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.android.universalvideoplayer.databinding.ItemViewVideosBinding
import uz.android.universalvideoplayer.models.FirebaseVideo


class FirebaseVideosAdapter(var context: Context) : RecyclerView.Adapter<FirebaseVideosAdapter.ViewHolder>() {

    private lateinit var video: FirebaseVideo
    private val itemCallBack = object : DiffUtil.ItemCallback<FirebaseVideo>() {
        override fun areItemsTheSame(oldItem: FirebaseVideo, newItem: FirebaseVideo): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: FirebaseVideo, newItem: FirebaseVideo): Boolean {
            return oldItem.url == newItem.url
        }
    }
    val differ = AsyncListDiffer(this, itemCallBack)
    inner class ViewHolder(private val binding: ItemViewVideosBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        @SuppressLint("NewApi")
        fun onBind(videos: FirebaseVideo) {

            binding.total.text = videos.duration
            binding.text.text = videos.title
          //  binding.videoview.setVideoPath(videos.videoUrl)
            Picasso.get().load(videos.image).into(binding.imageview)


            binding.play.setOnClickListener {
//

//                binding.videoview.setVideoPath(videos.url);
//                binding.videoview.setOnCompletionListener {
//                    binding.videoview.start();
//                }

                val uri = Uri.parse(videos.url)
                binding.videoview.visibility = View.VISIBLE
                binding.imageview.visibility = View.GONE
                val video = binding.videoview
                video.setVideoURI(uri)
                video.start()
            }
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
