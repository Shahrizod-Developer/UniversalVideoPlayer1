package uz.android.universalvideoplayer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import uz.android.universalvideoplayer.R
import uz.android.universalvideoplayer.adapters.FirebaseVideosAdapter
import uz.android.universalvideoplayer.databinding.FragmentYouTubeBinding
import uz.android.universalvideoplayer.models.FirebaseVideo
import uz.android.universalvideoplayer.viewModel.FirebaseViewModel
import java.util.jar.Manifest

class YouTubeFragment : Fragment() {

    private lateinit var binding: FragmentYouTubeBinding
    private lateinit var adapter: FirebaseVideosAdapter
    private lateinit var viewModel:FirebaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        binding = FragmentYouTubeBinding.inflate(inflater, container, false)

        adapter = FirebaseVideosAdapter()
        viewModel = ViewModelProvider(this)[FirebaseViewModel::class.java]
        viewModel.getMutableList().observe(requireActivity(), {
            adapter.differ.submitList(it)
        })
        binding.rv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rv.adapter = adapter
        return binding.root
    }
}