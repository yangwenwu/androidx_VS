package com.lemon.video.fragment.categories

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.lemon.video.R
import com.lemon.video.base.fragment.BaseFragment
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.categories.activity.PublicListActivityNew
import com.lemon.video.fragment.categories.adapter.CategoryAdapter
import com.lemon.video.fragment.top.search.SearchActivityNew
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.CategoryListBean
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.SPUtils
import com.lemon.video.utils.ToastUtil
import com.lemon.video.utils.ToastUtils
import kotlinx.android.synthetic.main.category_fragment_new.*
import java.util.ArrayList

class CategoriesFragmentNew : BaseFragment() {
    private var adapter: CategoryAdapter? = null
    private val categoriesList = ArrayList<CategoryListBean>()
    private var isLoadedView = false

    override fun getLayoutResources(): Int {
        return R.layout.category_fragment_new
    }

    override fun initView() {
        if (isLoadedView){

        }else{
            loading.visibility = View.GONE
            setView()
        }
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
//        return inflater.inflate(R.layout.category_fragment_new, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//    }

    private fun setView() {
        isLoadedView = true
        recyclerview.layoutManager = GridLayoutManager(activity, 2)
        adapter = CategoryAdapter(activity, categoriesList)
        recyclerview.adapter = adapter
        adapter!!.setOnItemClickListener(itemClickListener)

        classicheader.setEnableLastTime(false)
        refreshLayout.isEnableLoadMore = false
        refreshLayout.setOnRefreshListener { queryCategoryBean() }
        goToSearch()

        getCache()
    }

    private fun getCache(){
        var cache = SPUtils.get(activity,"videocategory","") as String
        if (cache != null && !cache.equals("")){
            val jsonObject = JSON.parseObject(cache.toString())
            val jsonArray = jsonObject.getJSONArray("resObject")
//            makeData(jsonArray)
            makeNewData(jsonArray)

            //现在改成动态接口了之后，还是需要在刷新一遍
            refreshLayout.autoRefresh()
        }else{
            //没有缓存的时候就刷新
            refreshLayout.autoRefresh()
        }
    }

    private fun goToSearch() {
        search_img.setOnClickListener {
            val toGoSearch = Intent(activity, SearchActivityNew::class.java)
            startActivity(toGoSearch)
        }
    }

    private fun queryCategoryBean() {
        Commrequest.getVideoCategory(activity, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {
                if (loading != null){
                    loading.visibility = View.GONE
                }

                val jsonObject = JSON.parseObject(t.`object`)
                val resMsg = jsonObject.getString("resMsg")
                categoriesList.clear()
                if (resMsg == "success") {
                    SPUtils.put(activity, "videocategory", jsonObject.toJSONString())
                    val jsonArray = jsonObject.getJSONArray("resObject")
//                    makeData(jsonArray)
                    makeNewData(jsonArray)
                } else {
                    ToastUtil.show(resources.getString(R.string.load_fail))
                }
                refreshLayout.finishRefresh()
            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                if (loading != null){
                    loading.visibility = View.GONE
                }
                refreshLayout.finishRefresh()
                setFailView()
            }
        })
    }

    private fun makeData(jsonArray: JSONArray) {
        if (jsonArray.size > 0) {
            categoriesList.clear()
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                val categoryListBean = JSON.parseObject(`object`.toJSONString(), CategoryListBean::class.java)
                if (categoryListBean.code == "hong_hong_by_night") {
                    categoryListBean.image = R.mipmap.cd_weekend
                } else if (categoryListBean.code == "tech_china") {
                    categoryListBean.image = R.mipmap.tech_china
                } else if (categoryListBean.code == "hong_kong_enquirer") {
                    categoryListBean.image = R.mipmap.hong_kong_enquirer
                } else if (categoryListBean.code == "life_and_art") {
                    categoryListBean.code = "life_and_art"
                    categoryListBean.image = R.mipmap.life_and_art
                } else if (categoryListBean.code == "girl_city") {
                    categoryListBean.image = R.mipmap.girl_city
                } else if (categoryListBean.code == "urban_tales") {
                    categoryListBean.image = R.mipmap.urban_tales
                } else if (categoryListBean.code == "endangered_species") {
                    categoryListBean.image = R.mipmap.protected_species
                } else if (categoryListBean.code == "flavor_of_the_east") {
                    categoryListBean.image = R.mipmap.asia360
                    continue
                } else if (categoryListBean.code == "biz_leader") {
                    categoryListBean.image = R.mipmap.biz_leader
                } else if (categoryListBean.code == "design_asia") {
                    categoryListBean.image = R.mipmap.design_asia
                } else if (categoryListBean.code == "drone_and_phone") {
                    categoryListBean.image = R.mipmap.drone_and_phone
                }
                categoriesList.add(categoryListBean)
            }
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun makeNewData(jsonArray: JSONArray) {
        if (jsonArray.size > 0) {
            categoriesList.clear()
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                val categoryListBean = JSON.parseObject(`object`.toJSONString(), CategoryListBean::class.java)
                categoriesList.add(categoryListBean)
            }
            adapter!!.notifyDataSetChanged()
        }
    }


    internal var itemClickListener = object : CategoryAdapter.OnItemClickListener {
        override fun OnItemClick(view: View, position: Int) {
            val bean = categoriesList.get(position)
            goTo(bean, PublicListActivityNew::class.java)
        }
    }

    private fun goTo(bean: CategoryListBean, activity: Class<*>) {
        val i = Intent(getActivity(), activity)
        i.putExtra("bean", bean)
        getActivity()!!.startActivity(i)
    }


    private fun setFailView(){
        var cache = SPUtils.get(activity,"videocategory","") as String
        if (cache != null && !cache.equals("")){
            val jsonObject = JSON.parseObject(cache.toString())
            val jsonArray = jsonObject.getJSONArray("resObject")
//            makeData(jsonArray)
            makeNewData(jsonArray)
        }else{
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