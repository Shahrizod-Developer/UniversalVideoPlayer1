package uz.android.universalvideoplayer.models
import android.net.Uri

data class VideoModel(var videoTitle: String,
                      var videoDuration: String,
                      var videoSize: String,
                      var adress: String,
                      var videoUri: Uri,
                      var videoAddedTime: String,
                      var videoPxile: String)