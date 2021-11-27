package uz.android.universalvideoplayer.models

interface PlayVideo {

    fun setVideo()
    fun playVideo(pos: Int)
    fun setVideoProgress()
    fun prevVideo()
    fun nextVideo()
    fun setPause()
    fun hideLayout()
}