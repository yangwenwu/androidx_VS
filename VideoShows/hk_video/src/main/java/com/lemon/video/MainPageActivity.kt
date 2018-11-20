package com.lemon.video

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
//import androidx.core.app.Fragment
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.lemon.video.application.BaseApp
import com.lemon.video.base.activity.BaseAppActivity
import com.lemon.video.common.Commrequest
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.categories.CategoriesFragmentNew
import com.lemon.video.fragment.me.MineFragment
import com.lemon.video.fragment.top.HomeNewFragment
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.UserBean
import com.lemon.video.model.VersionBean
import com.lemon.video.update.DataException
import com.lemon.video.update.TaskService
import com.lemon.video.update.UpdateDialog
import com.lemon.video.utils.NetWorkUtil
import com.lemon.video.utils.SPUtils
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*

class MainPageActivity : BaseAppActivity(), TabHost.OnTabChangeListener {

    private var tabItemList: MutableList<TabItem>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //没有使用Guide也面，不需要做这个处理
//        SPUtils.put(this@MainPageActivity,"versionCode",packageManager.getPackageInfo(this.packageName,0).versionCode)
        initTabData()
        initTabHost()
        autoLogin()

        if (NetWorkUtil.isNetworkAvailable(this@MainPageActivity)){
            checkVersion()
        }
    }

    private fun autoLogin() {
        var userStr = SPUtils.get(this@MainPageActivity, "user", "")
        if (userStr != null && userStr != "") {
            val userBean = JSON.parseObject(userStr.toString(), UserBean::class.java)
            BaseApp.setToken(userBean.token)
            BaseApp.setLogin(true)
            BaseApp.setUserBean(userBean)
        }
    }

    @Throws(DataException::class)
    private fun checkVersion() {
        Commrequest.queryVersion(this@MainPageActivity, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {
                val jsonObject = JSON.parseObject(t.`object`)
                val resMeg = jsonObject.getString("resMsg")
                if (resMeg == "success") {
                    val array = jsonObject.getJSONArray("resObject")
                    if (array.size > 0) {
                        val jsonObject1 = array.getJSONObject(0)
                        val versionBean = JSON.parseObject(jsonObject1.toJSONString(), VersionBean::class.java)
                        compareVersion(versionBean)
                    }
                }
            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {

            }
        })
    }

    private fun compareVersion(versionBean: VersionBean) {
        try {
            val versionName = versionBean.versionName //3.2版本
            val serverName = versionName.replace(".", "")
            val versionDesc = versionBean.versionDesc //英文描述
            //1强制更新，0非强制更新
            val force = versionBean.updateType//强制更新
            //版本更新下载地址
            val downloadUrl = versionBean.apkPath
            //系统版本号
            val code = this.packageManager.getPackageInfo(this.packageName, 0).versionName
            val localName = code.replace(".", "")
            val serverNo = Integer.parseInt(serverName)
            val localNo = Integer.parseInt(localName)
            if (serverNo > localNo) {
                val dialog = UpdateDialog(this@MainPageActivity)
                dialog.setContent(versionDesc)
                if (force == "1") { //1强制更新，0非强制更新
                    dialog.setLeftBtnText("Update")
                } else {
                    dialog.setLeftBtnText("Later")
                }

                dialog.setOnNegativeListener {
                    //取消
                    if (force == "1") {//1强制更新，0非强制更新
                        startServiceTask(downloadUrl)
                        dialog.dismiss()
                    } else {
                        dialog.dismiss()
                    }
                }

                dialog.setRightBtnText("Update Now")
                dialog.setOnPositiveListener {
                    startServiceTask(downloadUrl)
                    dialog.dismiss()
                }
                dialog.show()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun startServiceTask(mVersion_path: String) {
        val intent = Intent(this@MainPageActivity, TaskService::class.java)
        intent.putExtra("download_url", mVersion_path)
        startService(intent)
    }

    //初始化Tab数据
    private fun initTabData() {
//        tabItemList!!.add(TabItem(R.mipmap.top, R.mipmap.top_selected, R.string.page1, VideoHomeFragment::class.java))
        tabItemList!!.add(TabItem(R.mipmap.top, R.mipmap.top_selected, R.string.page1, HomeNewFragment::class.java))
//        tabItemList!!.add(TabItem(R.mipmap.top, R.mipmap.top_selected, R.string.page1, VideoHomeFragmentNew::class.java))
//        tabItemList!!.add(TabItem(R.mipmap.category, R.mipmap.category_selected, R.string.page2, CategoriesFragment::class.java))
        tabItemList!!.add(TabItem(R.mipmap.category, R.mipmap.category_selected, R.string.page2, CategoriesFragmentNew::class.java))
        tabItemList!!.add(TabItem(R.mipmap.me, R.mipmap.me_selected, R.string.page3, MineFragment::class.java))
//        tabItemList!!.add(TabItem(R.mipmap.me, R.mipmap.me_selected, R.string.page3, MeFragment::class.java))
    }

    //初始化主页选项卡视图
    private fun initTabHost() {
        tabhost.setup(this, supportFragmentManager, android.R.id.tabcontent)
        //去掉分割线
        tabhost.tabWidget.dividerDrawable = null
        for (i in tabItemList!!.indices) {
            val tabItem = tabItemList!![i]
            val tabSpec = tabhost.newTabSpec(tabItem.getTitleString())
                    .setIndicator(tabItem.getView())
            tabhost.addTab(tabSpec, tabItem.fragmentClass, tabItem.getBundle())
            tabhost.tabWidget.getChildAt(i).setBackgroundColor(resources.getColor(R.color.black))
            tabhost.setOnTabChangedListener(this@MainPageActivity)
            if (i == 0) {
                tabItem.setChecked(true)
            }
        }
    }


    override fun onTabChanged(tabId: String?) {
        //        ToastUtil.showToast(this,tabId);
        //重置Tab样式
        for (i in tabItemList!!.indices) {
            val tabItem = tabItemList!![i]
            if (tabId == tabItem.getTitleString()) {
                //选中设置为选中壮体啊
                tabItem.setChecked(true)
            } else {
                //没有选择Tab样式设置为正常
                tabItem.setChecked(false)
            }
        }
    }


    //代表每一个Tab
    internal inner class TabItem(//正常情况下显示的图片
            val imageNormal: Int, //选中情况下显示的图片
            val imagePress: Int, //tab的名字
            val title: Int,
            val fragmentClass: Class<out Fragment>) {
        private var titleString: String? = null

        private var view: View? = null
        private var imageView: ImageView? = null
        private var textView: TextView? = null
        private var bundle: Bundle? = null

        fun getTitleString(): String {
            if (title == 0) {
                return ""
            }
            if (TextUtils.isEmpty(titleString)) {
                titleString = getString(title)
            }
            return titleString!!
        }

        fun getBundle(): Bundle {
            if (bundle == null) {
                bundle = Bundle()
            }
            bundle!!.putString("title", getTitleString())
            return bundle!!
        }

        //还需要提供一个切换Tab方法---改变Tab样式
        fun setChecked(isChecked: Boolean) {
            if (imageView != null) {
                if (isChecked) {
                    imageView!!.setImageResource(imagePress)
                } else {
                    imageView!!.setImageResource(imageNormal)
                }
            }
            if (textView != null && title != 0) {
                if (isChecked) {
                    textView!!.setTextColor(resources.getColor(R.color.white))
                } else {
                    textView!!.setTextColor(resources.getColor(R.color.white))
                }
            }
        }

        fun getView(): View {
            if (this.view == null) {
                this.view = layoutInflater.inflate(R.layout.view_tab_indicator, null)
                this.imageView = this.view!!.findViewById<View>(R.id.iv_tab) as ImageView
                this.textView = this.view!!.findViewById<View>(R.id.tv_tab) as TextView
                //判断资源是否存在,不再就隐藏
                if (this.title == 0) {
                    this.textView!!.visibility = View.GONE
                } else {
                    this.textView!!.visibility = View.GONE
                    this.textView!!.text = getTitleString()
                }
                //绑定图片默认资源
                this.imageView!!.setImageResource(imageNormal)
            }
            return this.view!!
        }
    }


    override fun onBackPressed() {
        exitApp()
    }

    private var exitTime: Long = 0
    fun exitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            System.exit(0)
        }
    }

}