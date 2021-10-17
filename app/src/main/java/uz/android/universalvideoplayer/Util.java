package uz.android.universalvideoplayer;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

import uz.android.universalvideoplayer.fragments.DownloadingFragment;

public class Util {

    public static String RootDirectoryFaceBook = "/My Story Saver /facebook/";
    public static String RootDirectoryShareChat = "/My Story Saver /sharechat/";
    public static String RootDirectoryInstagram = "/My Story Saver /instagram/";

    public static File RootDirectoryWhatsapp =
            new File(Environment.getExternalStorageDirectory()
            + "/Download/MyStorySaver/Whatsapp");

    public static void createFileFolder(){
        if(!RootDirectoryWhatsapp.exists())
        {
            RootDirectoryWhatsapp.mkdirs();
        }
    }

    public static void download(String downloadPath, String destinationPath, Context context)
    {
        Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("salom");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "salom");
        ((DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }
}
