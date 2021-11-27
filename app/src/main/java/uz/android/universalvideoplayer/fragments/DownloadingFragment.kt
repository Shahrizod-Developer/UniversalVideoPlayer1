package uz.android.universalvideoplayer.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import uz.android.universalvideoplayer.databinding.FragmentDownloadingBinding


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

        binding.download.setOnClickListener {
            Toast.makeText(requireContext(), binding.url.text.toString(), Toast.LENGTH_SHORT).show()
        }

        return binding.root

    }


}



