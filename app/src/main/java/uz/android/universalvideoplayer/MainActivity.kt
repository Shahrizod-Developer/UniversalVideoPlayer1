package uz.android.universalvideoplayer

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import uz.android.universalvideoplayer.databinding.ActivityMainBinding
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    lateinit var backPress: () -> Unit
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val Permission_Storage_Id = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.fragment3)

        binding.bottomNav.setupWithNavController(navController)
    }

    private var doubleBackToExitPressedOnce = false
    private val mHandler: Handler? = Handler()

    private val mRunnable =
        Runnable { doubleBackToExitPressedOnce = false }

    override fun onDestroy() {
        super.onDestroy()
        mHandler?.removeCallbacks(mRunnable)
    }

    override fun onBackPressed() {

        backPress()
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        mHandler?.postDelayed(mRunnable, 2000)
    }
    protected fun getStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this@MainActivity, "Permission is Already Granted!", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Storage Permission Needed")
                    .setMessage("Storage Permission is Needed to Access External Files")
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Permission_Storage_Id
                        )
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.dismiss() }
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Permission_Storage_Id
                )
            }
        }
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this@MainActivity, "Permission is Already Granted!", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Storage Permission Needed")
                    .setMessage("Storage Permission is Needed to Access External Files")
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Permission_Storage_Id
                        )
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.dismiss() }
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Permission_Storage_Id
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permission_Storage_Id) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission Needed")
                        .setMessage("Storage Permission is Compulsory for this Feature \r\nGrant Now?")
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                packageName, null
                            )
                            intent.data = uri
                            startActivity(intent)
                            Toast.makeText(
                                this@MainActivity,
                                "Grant Permission from Permission Tab",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.dismiss() }
                        .create().show()
                }
            }
        }
    }

}