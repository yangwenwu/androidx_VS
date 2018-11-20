package com.lemon.video.fragment.top.search

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.lemon.video.R
import com.lemon.video.base.activity.BaseAppActivity
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.top.adapter.SearchAdapter
import com.lemon.video.fragment.video.activity.VideoDetailActivity
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.HomeNewsBean
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.StringUtils
import com.lemon.video.utils.ToastUtils
import kotlinx.android.synthetic.main.search_addheardview.view.*
import kotlinx.android.synthetic.main.search_video_layout.*
import java.util.ArrayList

class SearchActivityNew : BaseAppActivity(){
    private var adapter : SearchAdapter ?= null
    private var searchList = ArrayList<HomeNewsBean>()
    private var searchInputKey = ""
    private var page = 1
    private var totalCount = 0
    private var headerView :View ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_video_layout)
        initView()
    }

    private fun initView(){
        val mgr = this@SearchActivityNew.getAssets()
        //根据路径得到Typeface
        val tf = Typeface.createFromAsset(mgr, "fonts/Lato-Bold.ttf")
        val tf2 = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf")
//        empty_view = layoutInflater.inflate(R.layout.search_emptyview, recyclerview.parent as ViewGroup?, false)
        headerView = LayoutInflater.from(this@SearchActivityNew).inflate(R.layout.search_addheardview, findViewById<View>(android.R.id.content) as ViewGroup, false)
        headerView!!.visibility = View.GONE
        headerView!!.total_count.setTypeface(tf)
        headerView!!.tv_head1.setTypeface(tf2)
        headerView!!.tv_head2.setTypeface(tf2)

        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = SearchAdapter(this@SearchActivityNew, searchList)
        recyclerview.adapter = adapter
        adapter!!.setOnItemClickListener { adapter, view, position ->
            val ttb = searchList[position]
            val subjectCode = ttb.subjectCode
            goTo(subjectCode, ttb, VideoDetailActivity::class.java)
        }

        adapter!!.addHeaderView(headerView)
        classicheader.setEnableLastTime(false)
        refreshLayout.isEnableRefresh = false
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false)
        refreshLayout.setOnRefreshListener {
            getSearchRequest(searchInputKey, 1)
        }
        refreshLayout.setOnLoadMoreListener {
            getSearchRequest(searchInputKey, page)
        }

        search_edt.imeOptions = EditorInfo.IME_ACTION_SEARCH// 搜索框获取焦点时，软键盘回车变成搜索
        search_edt.setOnClickListener { search_edt.isCursorVisible = true }
        search_edt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {

                if (StringUtils.isEmpty(search_edt.text.toString())) {
                    Toast.makeText(this@SearchActivityNew, "Keyword cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    searchInputKey = search_edt.text.toString().trim { it <= ' ' }
                    page = 1
                    refreshLayout.isEnableLoadMore = true
                    getSearchRequest(searchInputKey, 1)

                }
                return@OnEditorActionListener true
            }
            false
        })
        // 输入框监听
        search_edt.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (arg0.length > 0) {
                    if (searchInputKey != arg0.toString().trim { it <= ' ' }) {
                        searchInputKey = arg0.toString().trim { it <= ' ' }
                    } else {

                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })

        cancel.setOnClickListener{finish()}
    }

    private fun goTo(code: String, bean: HomeNewsBean, activity: Class<*>) {
        val i = Intent(this@SearchActivityNew, activity)
        i.putExtra("code", code)
        i.putExtra("bean", bean)
        startActivity(i)
    }


    private fun getSearchRequest(key: String, page: Int) {
        search_edt.isCursorVisible = false
        hideKeyBoard()
        if (page == 1) {
            loading.visibility = View.VISIBLE
        }

        if (NetWorkUtil.isNetworkAvailable(this)){

        }else{
            loading.visibility = View.GONE
            ToastUtils.showShort(this@SearchActivityNew, resources.getString(R.string.net_error))
            return
        }

        Commrequest.queryVideoList(this@SearchActivityNew, key, page, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {
                val jsonObject = JSON.parseObject(t.`object`)
                val resMsg = jsonObject.getString("resMsg")
                if (resMsg == "success") {
                    val resObject = jsonObject.getJSONObject("resObject")
                    val jsonArray = resObject.getJSONArray("dateList")
                    if (page == 1){
                        searchList.clear()
                        if (jsonArray.size >0){
                            headerView!!.visibility =View.VISIBLE
                            totalCount = resObject.getIntValue("totalCount")
                            headerView!!.total_count.setText(totalCount.toString())
                            loading.visibility = View.GONE
                            empty_view.visibility = View.GONE
                            val msg = Message()
                            msg.obj = jsonArray
                            msg.what = 1
                            handler.sendMessage(msg)
                        }else{
                            loading.visibility = View.GONE
                            empty_view.visibility = View.VISIBLE
                        }
                    }else{
                        if (jsonArray.size >0){
                            val msg = Message()
                            msg.obj = jsonArray
                            msg.what = 1
                            handler.sendMessage(msg)
                        }
                    }

                } else {
                    //其他的异常比如500
                    ToastUtils.showShort(this@SearchActivityNew, resources.getString(R.string.load_fail))
                }
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()
            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                handler.sendEmptyMessage(2)
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()
            }
        })
    }


    private val handler = object : Handler() {
        override fun dispatchMessage(msg: Message) {

            when (msg.what) {
                1 -> {
                    page++
                    val jsonArray = msg.obj as JSONArray
                    if (jsonArray.size > 0) {
                        for (i in jsonArray.indices) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val homeNewsBean = JSON.parseObject(jsonObject.toJSONString(), HomeNewsBean::class.java)
                            searchList.add(homeNewsBean)
                        }
                        adapter!!.notifyDataSetChanged()

                        if (searchList.size == totalCount) {
                            refreshLayout.isEnableLoadMore = false
                        }
                    }
                }
                2 -> {
                    if (NetWorkUtil.isNetworkAvailable(this@SearchActivityNew)) {
                        ToastUtils.showShort(this@SearchActivityNew, resources.getString(R.string.load_fail))
                    } else {
                        ToastUtils.showShort(this@SearchActivityNew, resources.getString(R.string.net_error))
                    }
                    setLoadingStatus()
                }
            }
        }
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
            if (NetWorkUtil.isNetworkAvailable(this@SearchActivityNew)) {
                getSearchRequest(searchInputKey, 1)
            } else {
                loading_progress.visibility = View.GONE
                result_view.visibility = View.VISIBLE
                ToastUtils.showShort(this@SearchActivityNew, resources.getString(R.string.net_error))
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search_edt.windowToken, 0)
    }

}