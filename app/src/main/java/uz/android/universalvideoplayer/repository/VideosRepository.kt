package uz.android.universalvideoplayer.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import uz.android.universalvideoplayer.models.FirebaseVideo

class VideosRepository {
        private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        private lateinit var firebaseReference: DatabaseReference
        private var mutableList = MutableLiveData<List<FirebaseVideo>>()
        private lateinit var videos: List<FirebaseVideo>
        fun getMutableLiveData(): MutableLiveData<List<FirebaseVideo>> {
            firebaseReference = firebaseDatabase.getReference("maruzalar/0000")
            firebaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    videos = ArrayList<FirebaseVideo>()
                    snapshot.children.forEach {
                        var value = it.getValue(FirebaseVideo::class.java)
                        if (value != null) {
                            (videos as ArrayList<FirebaseVideo>).add(value)
                        }
                    }
                    mutableList.value = videos
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
            return mutableList
        }
    }