package uz.android.universalvideoplayer.models

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.widget.Toast
import uz.android.universalvideoplayer.Interfaces.VideoDownloader
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

//class InstaModel {
//    @SerializedName("info")
//    private val info: ArrayList<InstaInfo>? = null
//}

class InstaModel(private val context: Context, private val VideoURL: String) : VideoDownloader {
    private var VideoTitle: String? = null
    override fun createDirectory(): String {
        val folder = File(Environment.getExternalStorageDirectory().toString() +
                File.separator + "My Video Downloader")
        var subFolder: File? = null
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        } else {
            var success1 = true
            subFolder = File(folder.path + File.separator + "Instagram Videos")
            if (!subFolder.exists()) {
                success1 = subFolder.mkdirs()
            }
        }
        assert(subFolder != null)
        return subFolder!!.path
    }

    override fun getVideoId(link: String): String {
        return link
    }

    override fun DownloadVideo() {
        Data().execute(getVideoId(VideoURL))
    }

    @SuppressLint("StaticFieldLeak")
    private inner class Data : AsyncTask<String?, String?, String>() {
         override fun doInBackground(vararg params: String?): String? {
            val connection: HttpURLConnection
            val reader: BufferedReader
            return try {
                val url = URL(strings[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                var buffer = "No URL"
                var Line: String
                while (reader.readLine().also { Line = it } != null) {
                    if (Line.contains("og:title")) {
                        VideoTitle = Line.substring(Line.indexOf("og:title"))
                        VideoTitle = Line.substring(Line.indexOf("content"))
                        VideoTitle = VideoTitle!!.substring(ordinalIndexOf(VideoTitle!!, "\"", 0) + 1, ordinalIndexOf(VideoTitle!!, "\"", 1))
                        Log.e("Hello", VideoTitle!!)
                    }
                    if (Line.contains("og:video")) {
                        Line = Line.substring(Line.indexOf("og:video"))
                        Line = Line.substring(ordinalIndexOf(Line, "\"", 1) + 1, ordinalIndexOf(Line, "\"", 2))
                        if (Line.contains("amp;")) {
                            Line = Line.replace("amp;", "")
                        }
                        if (!Line.contains("https")) {
                            Line = Line.replace("http", "https")
                        }
                        Log.e("Hello1", Line)
                        buffer = Line
                        break
                    } else {
                        buffer = "No URL"
                    }
                }
                buffer
            } catch (e: IOException) {
                "No URL"
            }
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            if (!s.contains("No URL")) {
                val path = createDirectory()
                VideoTitle = if (VideoTitle == null || VideoTitle == "") {
                    "InstaVideo" + Date().toString() + ".mp4"
                } else {
                    "$VideoTitle.mp4"
                }
                val newFile = File(path, VideoTitle)
                try {
                    val request = DownloadManager.Request(Uri.parse(s))
                    request.allowScanningByMediaScanner()
                    request.setDescription(VideoTitle)
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                            .setDestinationUri(Uri.fromFile(newFile))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setVisibleInDownloadsUi(true)
                            .setTitle("Downloading")
                    val manager = (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                    val downLoadID = manager.enqueue(request)
                } catch (e: Exception) {
                    if (Looper.myLooper() == null) Looper.prepare()
                    Toast.makeText(context, "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
            } else {
                if (Looper.myLooper() == null) Looper.prepare()
                Toast.makeText(context, "Wrong Video URL or Private Video Can't be downloaded", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
    }

    companion object {
        private fun ordinalIndexOf(str: String, substr: String, n: Int): Int {
            var n = n
            var pos = -1
            do {
                pos = str.indexOf(substr, pos + 1)
            } while (n-- > 0 && pos != -1)
            return pos
        }
    }
}
