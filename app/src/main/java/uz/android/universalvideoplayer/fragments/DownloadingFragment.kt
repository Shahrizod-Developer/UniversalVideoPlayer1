package uz.android.universalvideoplayer.fragments


import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.android.universalvideoplayer.databinding.FragmentDownloadingBinding
import java.io.File


class DownloadingFragment : Fragment() {

    private var enqueue: Long = 0
    private var dm: DownloadManager? = null
    private lateinit var binding: FragmentDownloadingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDownloadingBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
            binding.download.setOnClickListener {
                if (binding.url.text.isNotEmpty() && binding.url.text.length>=5) {
                    if (binding.url.text.toString().substring(binding.url.text.toString().length - 4) == ".mp4") {
                        onClick()
                    } else {
                        Toast.makeText(requireContext(), "Wrong url", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }
            binding.showDownload.setOnClickListener {
                showDownload()
            }
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0
                    )
                    val query = DownloadManager.Query()
                    query.setFilterById(enqueue)
                    val c = dm!!.query(query)
                    if (c.moveToFirst()) {
                        val columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)
                        ) {
                            val view = binding.imageview
                            val uriString = c
                                .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            val a = Uri.parse(uriString)
                            val d = File(a.path)
                            view.setVideoURI(a)
                        }
                    }
                }
            }
        }
        requireContext().registerReceiver(
            receiver, IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE
            )
        )
        return binding.root
    }
    fun onClick() {

        dm = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(
            Uri.parse(binding.url.text.toString().trim())
        ).setDestinationInExternalPublicDir("/Video Player", "test.mp4")

        enqueue = dm!!.enqueue(request)
    }
    fun showDownload() {
        val i = Intent()
        i.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        startActivity(i)
    }
}



