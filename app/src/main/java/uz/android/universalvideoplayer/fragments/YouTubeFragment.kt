package uz.android.universalvideoplayer.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import uz.android.universalvideoplayer.adapters.FirebaseVideosAdapter
import uz.android.universalvideoplayer.databinding.FragmentYouTubeBinding
import uz.android.universalvideoplayer.models.FirebaseVideo
import uz.android.universalvideoplayer.viewModel.FirebaseViewModel
import java.util.*


class YouTubeFragment : Fragment() {

    private lateinit var binding: FragmentYouTubeBinding
    private lateinit var adapter: FirebaseVideosAdapter
    private lateinit var viewModel:FirebaseViewModel
    val videoList = arrayListOf<FirebaseVideo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        binding = FragmentYouTubeBinding.inflate(inflater, container, false)

        adapter = FirebaseVideosAdapter(requireContext())
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