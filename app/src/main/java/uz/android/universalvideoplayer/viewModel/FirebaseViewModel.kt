package uz.android.universalvideoplayer.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.android.universalvideoplayer.models.FirebaseVideo
import uz.android.universalvideoplayer.repository.VideosRepository

class FirebaseViewModel: ViewModel() {
    private val repository = VideosRepository()
    fun getMutableList(): MutableLiveData<List<FirebaseVideo>> {
        return  repository.getMutableLiveData()
    }
}