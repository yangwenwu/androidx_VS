package share

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.share.BuildConfig
import com.share.R
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors


fun Context.initShare() {
    UMConfigure.setLogEnabled(true)
    UMConfigure.init(this, BuildConfig.UMENG_APPKEY, BuildConfig.UMENG_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "")
    UMShareAPI.init(this, BuildConfig.UMENG_APPKEY)
    PlatformConfig.setWeixin(BuildConfig.WECHAT_APPKEY, BuildConfig.WECHAT_APPSCECRET)
}

fun AppCompatActivity.share(share_media: SHARE_MEDIA, title: String, description: String, imageFile: File, webUrl: String) {
    when (share_media) {
        SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN -> {
//            if (UMShareAPI.get(this).isInstall(this, share_media)) {
            if (UMShareAPI.get(this).isInstall(this, share_media)) {
                ShareAction(this)
                        .setPlatform(share_media)
                        .withMedia(UMWeb(webUrl).apply {
                            setTitle(title)
                            setThumb(UMImage(this@share, imageFile))
                            setDescription(description)
                        })
                        .share()
            } else {
                Toast.makeText(this, R.string.share_wx_toast, Toast.LENGTH_SHORT).show()
            }
        }
        SHARE_MEDIA.FACEBOOK -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.facebook.katana"
                })
            } catch (e: java.lang.Exception) {
                shareWeb("https://www.facebook.com/sharer.php?u=${Uri.encode(webUrl)}")
            }
        }
        SHARE_MEDIA.LINKEDIN -> {
            if (UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.LINKEDIN)) {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.linkedin.android"
                })
            }else{
                shareWeb("https://www.linkedin.com/shareArticle?url=${Uri.encode(webUrl)}")
                Log.e("zuiweng", "shareweb")
            }
        }
        SHARE_MEDIA.INSTAGRAM -> {
            runBackground(this.lifecycle) {
                try {
                    val file = File(Environment.getExternalStorageDirectory(), "instagram.jpg")
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    val out = FileOutputStream(file)
                    BitmapFactory.decodeStream(imageFile.inputStream()).compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    this.startActivity(Intent("android.intent.action.SEND").apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        type = "image/*"
                        if (Build.VERSION.SDK_INT >= 24) {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@share, "${this@share.packageName}.fileprovider", file))
                        } else {
                            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                        }
                        `package` = "com.instagram.android"
                    })
                } catch (e: java.lang.Exception) {
                    Log.e("zuiweng", "", e)
                    runOnUiThread {
                        Toast.makeText(this@share, R.string.share_instagram_toast, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        SHARE_MEDIA.TWITTER -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "$title \n $webUrl")
                    `package` = "com.twitter.android"
//                    component = android.content.ComponentName("com.twitter.android", "com.twitter.composer.ComposerActivity")
                })
            } catch (e: java.lang.Exception) {
                shareWeb("https://twitter.com/intent/tweet?url=${Uri.encode(webUrl)}")
            }
        }
        SHARE_MEDIA.GOOGLEPLUS -> {
            if (UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.GOOGLEPLUS)){
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.google.android.apps.plus"
                })
            }else{
                shareWeb("https://plus.google.com/share?url=${Uri.encode(webUrl)}")
            }
        }
    }
}

fun Activity.onShareActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
}


fun Activity.release() {
    UMShareAPI.get(this).release()
}

private val background by lazy { Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()) }

fun runBackground(lifecycle: Lifecycle, body: () -> Unit) {
    val future = background.submit {
        body.invoke()
    }
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            if (future.isDone) {
                future.cancel(true)
            }
        }
    })
}

fun Activity.shareWeb(url: String) {
    try {
        startActivity(Intent().apply {
            Log.e("zuiweng", url)
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_BROWSABLE)
            type = "text/plain"
            data = Uri.parse(url)
        })
    } catch (e: Exception) {
    }
}