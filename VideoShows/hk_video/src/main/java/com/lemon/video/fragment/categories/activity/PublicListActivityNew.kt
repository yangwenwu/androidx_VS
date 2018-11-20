package com.lemon.video.fragment.categories.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.lemon.video.R
import com.lemon.video.base.activity.BaseAppActivity
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.categories.adapter.PublicVideoRecycleAdapter
import com.lemon.video.fragment.video.activity.VideoDetailActivity
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.CategoryListBean
import com.lemon.video.model.HomeNewsBean
import com.lemon.video.utils.FileHelper
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.SPUtils
import com.lemon.video.utils.ToastUtils
import kotlinx.android.synthetic.main.public_list_layout.*

class PublicListActivityNew : BaseAppActivity(){
    private var pubList = ArrayList<HomeNewsBean>()
    private var totalCount :Int = 0
    private var page :Int = 1
    var adapter :PublicVideoRecycleAdapter ?= null
    var subjectCode :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.public_list_layout)
        val categoryListBean = intent.getSerializableExtra("bean") as CategoryListBean
        category_name.text = categoryListBean.name
        subjectCode = categoryListBean.code
        setView()
        getData(1,subjectCode,true)
    }

    private fun setView(){
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = PublicVideoRecycleAdapter(this,pubList)
        recyclerview.adapter = adapter
        adapter?.setOnItemClickListener(clickItem)

        classicheader.setEnableLastTime(false)
        refreshLayout.setOnRefreshListener {
            page = 1
            getData(1, subjectCode, true)
        }
        refreshLayout.setOnLoadMoreListener {
            getData(page, subjectCode, false)
        }

        back.setOnClickListener { finish() }

    }

    private fun getData(page: Int, subjectCode: String, isRefresh: Boolean) {
        Commrequest.getVideoTypeList(this, subjectCode, page, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {
                val jsonObject = JSON.parseObject(t.`object`)
                val resMsg = jsonObject.getString("resMsg")
                if (resMsg == "success") {
                    //取第一页作为缓存数据
                    if (page == 1) {
                        SPUtils.put(this@PublicListActivityNew, subjectCode, jsonObject.toJSONString())
                        FileHelper.writeConfigToSDCard("video", subjectCode, jsonObject.toJSONString())
                    }
                    val resObject = jsonObject.getJSONObject("resObject")
                    val jsonArray = resObject.getJSONArray("dateList")
                    totalCount = resObject.getIntValue("totalCount")
                    val msg = Message()
                    msg.obj = jsonArray
                    //如果是刷新就清空
                    if (isRefresh) {
                        pubList.clear()
                        msg.what = 1
                    } else {
                        msg.what = 5
                    }
                    handler.sendMessage(msg)

                }

                if (isRefresh){
                    refreshLayout.finishRefresh()
                }else{
                    refreshLayout.finishLoadMore()
                }
            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                if (page == 1) {
//                    val homeCache = SPUtils.get(this@PublicListActivityNew, subjectCode, "") as String
//                    if (homeCache != null && homeCache != "") {
//                        pubList.clear()
//                        val jsonObject = JSON.parseObject(homeCache)
//                        val resObject = jsonObject.getJSONObject("resObject")
//                        val jsonArray = resObject.getJSONArray("dateList")
//                        val msg = Message()
//                        msg.obj = jsonArray
//                        msg.what = 3
//                        handler.sendMessage(msg)
//                    } else {
//                        //当第一页没有缓存的时候，出现刷新
//                        handler.sendEmptyMessage(2)
//                    }

                    handler.sendEmptyMessage(2)
                } else {
                    //提示加载失败
                    handler.sendEmptyMessage(4)
                }

                if (isRefresh){
                    refreshLayout.finishRefresh()
                }else{
                    refreshLayout.finishLoadMore()
                }
            }

        })

    }

    private val handler = object : Handler(Looper.getMainLooper()){
        override fun dispatchMessage(msg: Message?) {
            super.dispatchMessage(msg)
            when (msg?.what) {
                1 -> {
                    page++
                    val jsonArray = msg.obj as JSONArray
                    if (jsonArray.size > 0) {
                        for (i in jsonArray.indices) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val homeNewsBean = JSON.parseObject(jsonObject.toJSONString(), HomeNewsBean::class.java)
                            pubList.add(homeNewsBean)
                        }
                        adapter?.notifyDataSetChanged()
                        loading.visibility = View.GONE
                        if (pubList.size == totalCount) {

                        }

                    } else {
                        //显示没有数据的图
//                        mRecyclerView.setEmptyView(empty_view)
                        loading.visibility = View.GONE
                        adapter?.notifyDataSetChanged()
                    }
                }
                2 -> {
                    if (NetWorkUtil.isNetworkAvailable(this@PublicListActivityNew)) {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.load_fail))
                    } else {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.net_error))
                    }
                    setLoadingStatus()
                }
                3 -> {
                    page++
                    val jsonArray3 = msg.obj as JSONArray
                    if (jsonArray3.size > 0) {

                        for (i in 4 until jsonArray3.size) {
                            val jsonObject = jsonArray3.getJSONObject(i)
                            val homeNewsBean = JSON.parseObject(jsonObject.toJSONString(), HomeNewsBean::class.java)
                            pubList.add(homeNewsBean)
                        }
                        adapter?.notifyDataSetChanged()
                        loading.visibility = View.GONE
                    }
                    if (NetWorkUtil.isNetworkAvailable(this@PublicListActivityNew)) {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.load_fail))
                    } else {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.net_error))
                    }
                }
                4 -> {
                    if (NetWorkUtil.isNetworkAvailable(this@PublicListActivityNew)) {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.load_fail))
                    } else {
                        ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.net_error))
                    }
                }
                5 -> {
                    page++
                    val jsonArray2 = msg.obj as JSONArray
                    if (jsonArray2.size > 0) {
                        for (i in jsonArray2.indices) {
                            val jsonObject = jsonArray2.getJSONObject(i)
                            val homeNewsBean = JSON.parseObject(jsonObject.toJSONString(), HomeNewsBean::class.java)
                            pubList.add(homeNewsBean)
                        }
                    }
                    adapter?.notifyDataSetChanged()
                    if (pubList.size == totalCount) {
//                        noMore = true
                    }
                }
            }
        }
    }


    private val clickItem = object : PublicVideoRecycleAdapter.OnItemClickListener {
        override fun OnItemClick(view: View?, position: Int) {
            val ttb = pubList.get(position)//经过具体测试，这里应该是添加了一头和一尾，所以position需要-2
            val subjectCode = ttb.subjectCode
            Goto(subjectCode, ttb, VideoDetailActivity::class.java)
        }

        override fun OnItemLongClick(view: View?, position: Int) {
        }

    }

    private fun Goto(code: String, bean: HomeNewsBean, activity: Class<*>) {
        val i = Intent(this, activity)
        i.putExtra("code", code)
        i.putExtra("bean", bean)
        startActivity(i)
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
            if (NetWorkUtil.isNetworkAvailable(this@PublicListActivityNew)) {
                getData(1,subjectCode,true)
            } else {
                loading_progress.visibility = View.GONE
                result_view.visibility = View.VISIBLE
                ToastUtils.showShort(this@PublicListActivityNew, resources.getString(R.string.net_error))
            }
        }
    }
}