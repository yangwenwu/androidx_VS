package share

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.GridLayoutManager
//import android.support.v7.widget.LinearLayoutCompat
//import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.share.R
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.dialog_share.*
import java.io.File

class ShareDialog(var activity: AppCompatActivity, var title: String = "", var description: String = "", var imageFile: File, var webUrl: String = "") : Dialog(activity, R.style.AppTheme_Alert_Fullscreen) {

//    private val icons = arrayListOf(R.drawable.share_facebook, R.drawable.share_twitter, R.drawable.share_linkedin, R.drawable.share_google, R.drawable.share_instagram, R.drawable.share_wechat, R.drawable.share_wxcircle)
    private val icons = arrayListOf(R.drawable.share_facebook, R.drawable.share_twitter, R.drawable.share_linkedin,  R.drawable.share_instagram, R.drawable.share_wechat, R.drawable.share_wxcircle)
//    private val names = arrayListOf(R.string.share_facebook, R.string.share_twitter, R.string.share_linked_in, R.string.share_google_plus, R.string.share_instagram, R.string.share_wechat, R.string.share_wechat_moment)
    private val names = arrayListOf(R.string.share_facebook, R.string.share_twitter, R.string.share_linked_in,R.string.share_instagram, R.string.share_wechat, R.string.share_wechat_moment)
//    private val data = arrayListOf(SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER, SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
    private val data = arrayListOf(SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER, SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_share)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission_group.STORAGE) != 0) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        Log.e("zuiweng",webUrl)
        window.setLayout(activity.resources.displayMetrics.widthPixels, -2)
        window.decorView.setPadding(0, 0, 0, 0)
        window.setGravity(Gravity.BOTTOM)
        cancel.setOnClickListener { dismiss() }
        recyclerView.layoutManager = GridLayoutManager(activity, 4)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = object : RecyclerView.ViewHolder(LinearLayoutCompat(activity).apply {
                orientation = LinearLayoutCompat.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(-1, -2)
                setPadding(0, 16f.dip2px(), 0, 0)
                addView(ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(48f.dip2px(), 48f.dip2px())
                })
                addView(TextView(context).apply {
                    setPadding(16f.dip2px(), 0, 0, 0)
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setSingleLine()
                })
            }) {}

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val item = holder.itemView as LinearLayoutCompat
                val iconView = item.getChildAt(0) as ImageView
                val nameView = item.getChildAt(1) as TextView
                iconView.setImageResource(icons[position])
                nameView.setText(names[position])
                item.setOnClickListener {
                    dismiss()
                    activity.share(data[position], this@ShareDialog.title, this@ShareDialog.description, this@ShareDialog.imageFile, this@ShareDialog.webUrl)
                }
            }

            override fun getItemCount(): Int = data.size
        }
    }

    fun Float.dip2px(): Int {
        val scale = activity.resources.displayMetrics.density
        return (this * scale).toInt()
    }
}