package com.lemon.video.fragment.top.adapter

import android.content.Context
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lemon.video.R
import com.lemon.video.application.GlideApp
import com.lemon.video.common.HttpConstants
import com.lemon.video.model.HomeNewsBean
import com.lemon.video.utils.StringUrlUtil

class SearchAdapter(private val context: Context, data: MutableList<HomeNewsBean>?) : BaseMultiItemQuickAdapter<HomeNewsBean, BaseViewHolder>(data) {

    init {
        addItemType(0, R.layout.video_search_collection_item2)
    }
    override fun convert(helper: BaseViewHolder, item: HomeNewsBean?) {
        val mgr = context.assets
        //根据路径得到Typeface
        val tf = Typeface.createFromAsset(mgr, "fonts/Lato-Bold.ttf")
        val tf2 = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf")
        when(helper.itemViewType){
            0 ->{
                val tvTag = helper.getView<TextView>(R.id.video_tag)
                tvTag.typeface = tf2
                helper.setText(R.id.video_tag,item!!.subjectName)

                val tvTitle = helper.getView<TextView>(R.id.video_title)
                tvTitle.typeface = tf
                helper.setText(R.id.video_title,item.title)

                var img: String? = item.bigTitleImage
//        String img = "http://www.chinadailyhk.com/attachments/image/0/133/32/56159_53265/56159_53265_620_356_jpg.jpg";

                val video_image = helper.getView<ImageView>(R.id.video_image)
                if (img != null && img != "") {
                    val imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img)
                    GlideApp.with(context)
                            .load(imgUrl)
                            .placeholder(R.mipmap.news_big_default)
                            .error(R.mipmap.news_big_default)
                            .centerCrop()
                            .dontAnimate()
                            .into(video_image)
                } else {
                    img = item.titleImage
                    if (img != null && img != "") {
                        val imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img!!)
                        GlideApp.with(context)
                                .load(imgUrl)
                                .placeholder(R.mipmap.news_big_default)
                                .error(R.mipmap.news_big_default)
                                .centerCrop()
                                .dontAnimate()
                                .into(video_image)
                    } else {
                        video_image.setImageResource(R.mipmap.news_big_default)
                    }
                }
            }
            else ->{

            }
        }
    }


}
