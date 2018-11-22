package com.lemon.video.fragment.me.collection

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.lemon.video.R
import com.lemon.video.application.BaseApp
import com.lemon.video.base.activity.BaseAppActivity
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.video.activity.VideoDetailActivity
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.HomeNewsBean
import com.lemon.video.model.UserBean
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.SPUtils
import com.lemon.video.utils.ToastUtils
import kotlinx.android.synthetic.main.bookmark_layout.*
import kotlinx.android.synthetic.main.include_bar_left_right_text.*
import java.util.ArrayList

class BookMarkActivity :BaseAppActivity(),VideoCollectListRecycleAdapter.IselectedListener, View.OnClickListener {
    private var adapter :BookMarkAdapter ?= null
//    private var tbList: MutableList<TouTiaoListBean>? = null
    private var tbList = ArrayList<TouTiaoListBean>()
    private var isEditModel: Boolean = false
    internal var deleteOK = false
    private var totalCount = 0
    private var page = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmark_layout)
        initView()
    }

    private fun initView(){
        bar_iv_left.setOnClickListener(this)
        bar_tv_right.setOnClickListener(this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = BookMarkAdapter(this, tbList, isEditModel)
        recyclerview.adapter = adapter
        adapter?.setOnItemClickListener(itemClickListener)

        queryDate(1, true, false)
    }

    //查询收藏列表内容
    private fun queryDate(page: Int, isDown: Boolean, delete: Boolean) {
        val userStr = SPUtils.get(this@BookMarkActivity, "user", "") as String
        val userBean = JSON.parseObject(userStr, UserBean::class.java)
        val userId = userBean.id   //  d63d251b-b7b1-11e7-9872-00155d03d036
        Commrequest.queryCollectionList(this@BookMarkActivity, userId, page, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {

                val jsonObject = JSON.parseObject(t.`object`)
                val resMsg = jsonObject.getString("resMsg")
                if (resMsg == "success") {
                    if (delete) {
                        deleteOK = true
                    }

                    val resObject = jsonObject.getJSONObject("resObject")
                    val jsonArray = resObject.getJSONArray("dateList")
                    val SQLlist = ArrayList<TouTiaoListBean>()
                    if (jsonArray.size > 0) {
                        totalCount = resObject.getIntValue("totalCount")
                        for (i in jsonArray.indices) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val homeNewsBean = JSON.parseObject(jsonObject1.toJSONString(), TouTiaoListBean::class.java)
                            SQLlist.add(homeNewsBean)
                        }
                        val message = Message()
                        message.obj = SQLlist
                        if (page == 1) {
                            tbList?.clear()
                            message.what = 2
                        } else {
                            message.what = 5
                        }
                        handler.sendMessage(message)
                    } else {
                        tbList?.clear()
                        adapter?.notifyDataSetChanged()
                        bar_tv_right.setVisibility(View.INVISIBLE)
                        tv_delete.setVisibility(View.GONE)
//                        adapter.setEmptyView(empty_view)
                        loading.visibility = View.GONE

                    }
                } else {
                    ToastUtils.showShort(this@BookMarkActivity, "Load failed")
                }

            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                handler.sendEmptyMessage(1)
            }
        })
    }

    /***
     * 没有缓存，加载失败
     */
    private fun setLoadingStatus() {
        loading_progress.visibility = View.GONE
        result_view.visibility = View.VISIBLE
        refresh.visibility = View.VISIBLE
        refresh.setOnClickListener {
            //状态
            loading_progress.visibility = View.VISIBLE
            result_view.visibility = View.GONE
            if (NetWorkUtil.isNetworkAvailable(this@BookMarkActivity)) {
                queryDate(1, true, false)
            } else {
                loading_progress.visibility = View.GONE
                result_view.visibility = View.VISIBLE
                ToastUtils.showShort(this@BookMarkActivity, resources.getString(R.string.net_error))
            }
        }
    }

    private val handler = object : Handler() {
        override fun dispatchMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    tbList?.clear()

                    adapter?.notifyDataSetChanged()
                    bar_tv_right.setVisibility(View.INVISIBLE)
                    loading.visibility = View.VISIBLE
                    setLoadingStatus()
                }
                2 -> {
                    page++
                    val SQLlist = msg.obj as List<TouTiaoListBean>
                    if (SQLlist.size > 0) {
                        val queryList = ArrayList<TouTiaoListBean>()
                        for (i in SQLlist.indices) {
                            val touTiaoListBean = SQLlist[i]
                            queryList.add(touTiaoListBean)
                        }
                        bar_tv_right.setVisibility(View.VISIBLE)
                        tbList?.addAll(queryList)
                        loading.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                        if (tbList?.size == totalCount) {
//                            xrlList.setLoadingMoreEnabled(false)
//                            noMore = true
                        }
                        if (deleteOK) {
                            deleteOK = false
                            isEditModel = !isEditModel
                            bar_tv_right.setText(if (isEditModel) "Done" else "Edit")
                            adapter?.setEditModel(isEditModel)
                            tv_delete.setVisibility(View.GONE)
                        }

                    } else {
                        bar_tv_right.setVisibility(View.INVISIBLE)
                        tv_delete.setVisibility(View.GONE)
//                        xrlList.setEmptyView(empty_view)
                        loading.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                    }
                }
                5 -> {
                    page++
                    val SQLlist2 = msg.obj as List<TouTiaoListBean>
                    if (SQLlist2.size > 0) {
                        val queryList = ArrayList<TouTiaoListBean>()
                        for (i in SQLlist2.indices) {
                            val touTiaoListBean = SQLlist2[i]
                            queryList.add(touTiaoListBean)
                        }
                        bar_tv_right.setVisibility(View.VISIBLE)
//                        xrlList.loadMoreComplete()
                        tbList?.addAll(queryList)
                        loading.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                        if (tbList?.size == totalCount) {
//                            xrlList.setLoadingMoreEnabled(false)
//                            noMore = true
                        }

                    } else {
                        bar_tv_right.setVisibility(View.INVISIBLE)
                        tv_delete.setVisibility(View.GONE)
//                        xrlList.setEmptyView(empty_view)
                        loading.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun seleced(count: Int) {
        tv_delete.setVisibility(if (count == 0) View.GONE else View.VISIBLE)
        if (count > 0) tv_delete.setText("Delete ( $count )")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.bar_iv_left -> finish()
            R.id.bar_tv_right -> {
                isEditModel = !isEditModel
                bar_tv_right.setText(if (isEditModel) "Done" else "Edit")
                adapter?.setEditModel(isEditModel)
                tv_delete.setVisibility(View.GONE)
            }
            R.id.tv_delete -> clickDeletIds()
        }
    }

    private fun clickDeletIds() {
        //        List<String> ids = adapter.getSelectIdsList();
        //        System.out.print(ids);
        //        deleteItems(ids);
        //删除接口，一个或者多个
        //        deleteId(ids);

        val delIds = adapter?.selectIds
//        var de = adapter?.selectIds
        deleteId(delIds!!)
    }


    private fun deleteId(ids: String) {
        var userId = ""
        if (BaseApp.isLogin) {
            userId = BaseApp.getUserBean().id
        } else {
            return
        }
        Commrequest.deLCollectionList(this@BookMarkActivity, userId, ids, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {

                val jsonObject = JSON.parseObject(t.`object`)
                val resMsg = jsonObject.getString("resMsg")
                if (resMsg == "success") {
                    val resObject = jsonObject.getJSONObject("resObject")
                    tv_delete.setText("Delete( " + 0 + " )")
                    page = 1
//                    if (noMore) {
//                        noMore = false
//                        xrlList.setLoadingMoreEnabled(true)
//                    }
                    queryDate(1, true, true)
                } else {
                    ToastUtils.showShort(this@BookMarkActivity, "Delete failed")
                }

            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                ToastUtils.showShort(this@BookMarkActivity, "Delete failed")
            }
        })
    }


    private var itemClickListener= object : VideoCollectRecycleAdapter.OnItemClickListener {
        override fun OnItemClick(view: View, position: Int) {
            val ttb = adapter?.mData?.get(position)
            val homeNewsBean = HomeNewsBean()
            homeNewsBean.id = ttb?.getId()
            homeNewsBean.title = ttb?.getTitle()
            homeNewsBean.subjectCode = ttb?.getSubjectCode()
            homeNewsBean.bigTitleImage = ttb?.getBigTitleImage()
            homeNewsBean.titleImage = ttb?.getTitleImage()
            homeNewsBean.dataId = ttb?.getDataId()
            homeNewsBean.description = ttb?.getDescription()
            val subjectCode = ttb?.subjectCode
            Goto(subjectCode!!, homeNewsBean, VideoDetailActivity::class.java)

        }

        override fun OnItemLongClick(view: View, position: Int) {

        }
    }


    private fun Goto(code: String, bean: HomeNewsBean, activity: Class<*>) {
        val i = Intent(this@BookMarkActivity, activity)
        i.putExtra("code", code)
        i.putExtra("bean", bean)
        startActivity(i)
    }

}