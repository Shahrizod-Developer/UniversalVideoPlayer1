package uz.android.universalvideoplayer.utils

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.FileProvider
import java.io.File


object PlayerUtils {
    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Double {
        var statusBarHeight = 0
        //获取status_bar_height资源的ID
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight.toDouble()
    }

    /**
     * 获取NavigationBar的高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        if (!hasNavigationBar(context)) {
            return 0
        }
        val resources = context.resources
        val resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen", "android"
        )
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 是否存在NavigationBar
     */
    fun hasNavigationBar(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display = getWindowManager(context).defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            realSize.x != size.x || realSize.y != size.y
        } else {
            val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            !(menu || back)
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(context: Context, isIncludeNav: Boolean): Int {
        return if (isIncludeNav) {
            context.resources.displayMetrics.widthPixels + getNavigationBarHeight(context)
        } else {
            context.resources.displayMetrics.widthPixels
        }
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(context: Context, isIncludeNav: Boolean): Int {
        return if (isIncludeNav) {
            context.resources.displayMetrics.heightPixels + getNavigationBarHeight(context)
        } else {
            context.resources.displayMetrics.heightPixels
        }
    }

    /**
     * 隐藏ActionBar
     */
    @SuppressLint("RestrictedApi")
    fun hideActionBar(context: Context?) {
        val appCompatActivity = getAppCompatActivity(context)
        if (appCompatActivity != null) {
            val ab = appCompatActivity.supportActionBar
            if (ab != null && ab.isShowing) {
                ab.setShowHideAnimationEnabled(false)
                ab.hide()
            }
        }
    }

    /**
     * 显示ActionBar
     */
    @SuppressLint("RestrictedApi")
    fun showActionBar(context: Context?) {
        val appCompatActivity = getAppCompatActivity(context)
        if (appCompatActivity != null) {
            val ab = appCompatActivity.supportActionBar
            if (ab != null && !ab.isShowing) {
                ab.setShowHideAnimationEnabled(false)
                ab.show()
            }
        }
    }

    /**
     * 获取Activity
     */
    fun scanForActivity(context: Context?): Activity? {
        return if (context == null) null else if (context is Activity) context else if (context is ContextWrapper) scanForActivity(
            context.baseContext
        ) else null
    }

    /**
     * Get AppCompatActivity from context
     */
    fun getAppCompatActivity(context: Context?): AppCompatActivity? {
        if (context == null) return null
        if (context is AppCompatActivity) {
            return context
        } else if (context is ContextThemeWrapper) {
            return getAppCompatActivity(context.baseContext)
        }
        return null
    }

    /**
     * dp转为px
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        )
            .toInt()
    }

    /**
     * sp转为px
     */
    fun sp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            dpValue,
            context.resources.displayMetrics
        )
            .toInt()
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     */
    fun getWindowManager(context: Context): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * 边缘检测
     */
    fun isEdge(context: Context, e: MotionEvent): Boolean {
        val edgeSize = dp2px(context, 40f)
        return e.rawX < edgeSize || e.rawX > getScreenWidth(
            context,
            true
        ) - edgeSize || e.rawY < edgeSize || e.rawY > getScreenHeight(context, true) - edgeSize
    }

    const val NO_NETWORK = 0
    const val NETWORK_CLOSED = 1
    const val NETWORK_ETHERNET = 2
    const val NETWORK_WIFI = 3
    const val NETWORK_MOBILE = 4
    const val NETWORK_UNKNOWN = -1

    /**
     * 判断当前网络类型-1为未知网络0为没有网络连接1网络断开或关闭2为以太网3为WiFi4为2G5为3G6为4G
     */
    @SuppressLint("MissingPermission")
    fun getNetworkType(context: Context): Int {
        //改为context.getApplicationContext()，防止在Android 6.0上发生内存泄漏
        val connectMgr = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return NO_NETWORK
        val networkInfo = connectMgr.activeNetworkInfo
            ?: // 没有任何网络
            return NO_NETWORK
        if (!networkInfo.isConnected) {
            // 网络断开或关闭
            return NETWORK_CLOSED
        }
        if (networkInfo.type == ConnectivityManager.TYPE_ETHERNET) {
            // 以太网网络
            return NETWORK_ETHERNET
        } else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
            return NETWORK_WIFI
        } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
            when (networkInfo.subtype) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_LTE ->                     // 4G网络
                    return NETWORK_MOBILE
            }
        }
        // 未知网络
        return NETWORK_UNKNOWN
    }

    /**
     * 获取安装应用的Intent
     */
    fun getPlayByIntent(mContext: Context, file: File?): Intent {
        return if (Build.VERSION.SDK_INT >= 24) {
            val uri = FileProvider.getUriForFile(
                mContext, mContext.packageName + ".fileprovider",
                file!!
            )
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(uri, "video/*")
            val chooser = Intent.createChooser(intent, "Play by")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            chooser
        } else {
            val uri = Uri.fromFile(file)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(uri, "video/*")
            val chooser = Intent.createChooser(intent, "Play by")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            chooser
        }
    }

    fun songArt(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.checkbox_off_background)
    }

    fun isAutoRotate(context: Context): Boolean {
        try {
            val autoRotate = Settings.System.getInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION
            )
            return autoRotate == 1
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return false
    }
}
