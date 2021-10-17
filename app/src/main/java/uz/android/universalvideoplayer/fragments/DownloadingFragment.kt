package uz.android.universalvideoplayer.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import uz.android.universalvideoplayer.databinding.FragmentDownloadingBinding
import uz.android.universalvideoplayer.models.InstaModel

class DownloadingFragment : Fragment() {


    private lateinit var binding: FragmentDownloadingBinding
    val videoUrl: String? = null
    var video: String? = null
    private lateinit var diaolog: ProgressDialog

    @SuppressLint("ResourceType")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDownloadingBinding.inflate(inflater, container, false)

//        diaolog = ProgressDialog(requireContext())
//        diaolog.setMessage("Please wait...")
//        diaolog.setCancelable(false)
        binding.download.setOnClickListener(View.OnClickListener {
            val URL: String = binding.url.text.toString()
            val downloader = InstaModel(requireContext(), URL)
            downloader.DownloadVideo()
        })
//        binding.download.setOnClickListener {
//            val URL: String = inputURl.getText().toString()
//            val downloader = InstagramVideoDownloader(this@Instagram, URL)
//            downloader.DownloadVideo()
//        }

        return binding.root

    }

    private fun getVideo() {

        if(TextUtils.isEmpty(binding.url.text.toString()))
        {
            Toast.makeText(requireContext(), "Please paste the link", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if(binding.url.text.toString().contains("instagram"))
            {
                val replace: String

                if(binding.url.text.toString().contains("?utm_source=ig_web_copy_link"))
                {
                    replace = "?utm_source=ig_web_copy_link"
                    video = binding.url.text.toString().replace(replace, "")

                }
                else if(binding.url.text.toString().contains("?utm_medium=copy_link"))
                {
                    replace = "?utm_medium=copy_link"
                    video = binding.url.text.toString().replace(replace, "")
                }
                else
                {
                    video = binding.url.text.toString()
                }


            }
            else
            {
                Toast.makeText(requireContext(), "Please provide instagram url", Toast.LENGTH_SHORT).show()
            }
        }
        
    }

}



