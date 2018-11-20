package com.lemon.video.fragment.top

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.lemon.video.R
import com.lemon.video.base.fragment.BaseFragment
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.top.search.SearchActivityNew
import com.lemon.video.fragment.video.activity.VideoDetailActivity
import com.lemon.video.fragment.video.adapter.NewVideoRecycleAdapter
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.HomeNewsBean
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.SPUtils
import com.lemon.video.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_latest_layout.*
import java.util.ArrayList

class HomeNewFragment : BaseFragment() {

    private val tbList = ArrayList<HomeNewsBean>()
    private var adapter: NewVideoRecycleAdapter? = null
    private var isLoadedView = false

    override fun getLayoutResources(): Int {
        return R.layout.fragment_latest_layout
    }

    override fun initView() {
        if (isLoadedView) {

        } else {
            loading?.visibility = View.GONE
            setData()
        }
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
////        if (viewRoot == null){
////            viewRoot = inflater.inflate(R.layout.fragment_latest_layout,container,false)
////            setData()
////        }
////        val viewRoot = inflater.inflate(R.layout.fragment_latest_layout,container,false)
//        return  inflater.inflate(R.layout.fragment_latest_layout,container,false)
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setData()
//    }

    private fun setData() {
        isLoadedView = true
        recyclerview!!.layoutManager = LinearLayoutManager(activity)
        adapter = NewVideoRecycleAdapter(activity, tbList)
        recyclerview.adapter = adapter
        adapter!!.setOnItemClickListener(itemClickListener)
        goToSearch()

        classicheader.setEnableLastTime(false)
        refreshLayout.isEnableLoadMore = false
        refreshLayout?.setOnRefreshListener {
            getData(true)
        }
        refreshLayout?.setOnLoadMoreListener {
            getData(false)
        }

        getCacheData()
    }

    private fun getCacheData() {
        var cache = SPUtils.get(activity, "videohome", "") as String
        if (cache != null && !cache.equals("")) {
            val jsonObject = JSON.parseObject(cache.toString())
            val jsonArray = jsonObject.getJSONArray("resObject")
            if (jsonArray.size > 0) {
                tbList.clear()
                for (i in jsonArray.indices) {
                    val homeNewsBean = JSON.parseObject(jsonArray[i].toString(), HomeNewsBean::class.java)
                    if (homeNewsBean.subjectCode == "flavor_of_the_east") {

                    } else {
                        tbList.add(homeNewsBean)
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
        }
        refreshLayout.autoRefresh()
    }

    internal var itemClickListener = object : NewVideoRecycleAdapter.OnItemClickListener {
        override fun OnItemClick(view: View, position: Int) {
            val ttb = tbList[position]//经过具体测试，这里应该是添加了一头和一尾，所以position需要-2
            val subjectCode = ttb.subjectCode
            goTo(subjectCode, ttb, VideoDetailActivity::class.java)
        }

        override fun OnItemLongClick(view: View, position: Int) {

        }
    }

    private fun goTo(code: String, bean: HomeNewsBean, activity: Class<*>) {
        val i = Intent(getActivity(), activity)
        i.putExtra("code", code)
        i.putExtra("bean", bean)
        getActivity()!!.startActivity(i)
    }

    private fun goToSearch() {
        search_img.setOnClickListener {
            val toGoSearch = Intent(activity, SearchActivityNew::class.java)
            startActivity(toGoSearch)
        }
    }

    //首页只有一页数据没有分页
    private fun getData(isRefresh: Boolean) {
        Commrequest.getHomeTopList(activity, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean?, code: Int) {
                val jsonObject = JSON.parseObject(t?.`object`)
                val jsonArray = jsonObject.getJSONArray("resObject")
                if (isRefresh) {
                    tbList.clear()
                }
                loading?.visibility = View.GONE
                SPUtils.put(activity, "videohome", jsonObject.toJSONString())
                if (jsonArray.size > 0) run {
                    for (i in jsonArray.indices) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val homeNewsBean = JSON.parseObject(jsonObject.toJSONString(), HomeNewsBean::class.java)
                        if (homeNewsBean.subjectCode == "flavor_of_the_east") {

                        } else {
                            tbList.add(homeNewsBean)
                        }
                    }
                    recyclerview?.adapter?.notifyDataSetChanged()
//                    handler.sendEmptyMessage(1)
                }
                if (isRefresh) {
                    refreshLayout?.finishRefresh()
                }
            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String?) {
                loading?.visibility = View.GONE
                if (isRefresh) {
                    refreshLayout?.finishRefresh()
                }
                setNoData()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e("HOME", "on Resume")
    }

    private fun setNoData() {
        var cache = SPUtils.get(activity, "videohome", "") as String
        if (cache != null && !cache.equals("")) {
            val jsonObject = JSON.parseObject(cache.toString())
            val jsonArray = jsonObject.getJSONArray("resObject")
            if (jsonArray.size > 0) {
                tbList.clear()
                for (i in jsonArray.indices) {
                    val homeNewsBean = JSON.parseObject(jsonArray[i].toString(), HomeNewsBean::class.java)
                    if (homeNewsBean.subjectCode == "flavor_of_the_east") {

                    } else {
                        tbList.add(homeNewsBean)
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
        } else {
            setNoDataView()
        }
    }


    /***
     * 没有缓存，加载失败view
     */
    private fun setNoDataView() {
        // refresh   text_and_image   load_image_id   result_view  loading_progress  loading
        loading.visibility = View.VISIBLE
        loading_progress.visibility = View.GONE
        result_view.visibility = View.VISIBLE
        refresh.visibility = View.VISIBLE
        refresh.setOnClickListener {
            //状态
            loading_progress.visibility = View.VISIBLE
            result_view.visibility = View.GONE
            if (NetWorkUtil.isNetworkAvailable(activity)) {
                refreshLayout.autoRefresh()
            } else {
                loading_progress.visibility = View.GONE
                result_view.visibility = View.VISIBLE
                ToastUtils.showShort(activity, resources.getString(R.string.net_error))
            }
        }
    }

}


