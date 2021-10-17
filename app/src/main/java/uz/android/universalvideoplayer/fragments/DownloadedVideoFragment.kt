package uz.android.universalvideoplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.android.universalvideoplayer.databinding.FragmentDownloadedVideoBinding

class   DownloadedVideoFragment : Fragment() {

    private lateinit var binding: FragmentDownloadedVideoBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloadedVideoBinding.inflate(inflater, container, false)

        return binding.root
    }
}

