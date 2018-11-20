package com.lemon.video.fragment.me

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoFragment
import com.jph.takephoto.compress.CompressConfig
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.LubanOptions
import com.jph.takephoto.model.TResult
import com.jph.takephoto.model.TakePhotoOptions
import com.lemon.video.R
import com.lemon.video.application.BaseApp
import com.lemon.video.application.GlideApp
import com.lemon.video.common.Commrequest
import com.lemon.video.common.HttpConstants
import com.lemon.video.entrys.BaseJsonBean
import com.lemon.video.fragment.me.activity.FeedBackActivity
import com.lemon.video.fragment.me.activity.LoginAndRegisterActivity
import com.lemon.video.fragment.me.activity.ProfileActivity
import com.lemon.video.fragment.me.activity.SettingActivity
import com.lemon.video.fragment.me.collection.BookMarkActivity
import com.lemon.video.httpModel.UserImageModel
import com.lemon.video.https.ResponseListener
import com.lemon.video.model.UserBean
import com.lemon.video.utils.ChoseImageDialog
import com.lemon.video.utils.ImageUtils
import com.lemon.video.utils.SPUtils
import com.lemon.video.utils.StringUrlUtil
import kotlinx.android.synthetic.main.me_fragment.*
import java.io.File

class MeFragment :TakePhotoFragment(),View.OnClickListener{

    private val RP_CAMERA = 1
    private val RP_WRITE = 2
    //takePhoto
    private var rgCrop: RadioGroup? = null
    private var rgCompress:RadioGroup? = null
    private var rgFrom:RadioGroup? = null
    private var rgCropSize:RadioGroup? = null
    private var rgCropTool:RadioGroup? = null
    private var rgShowProgressBar:RadioGroup? = null
    private var rgPickTool:RadioGroup? = null
    private var rgCompressTool:RadioGroup? = null
    private var rgCorrectTool:RadioGroup? = null
    private var rgRawFile:RadioGroup? = null
    private var etCropHeight: EditText? = null
    private var etCropWidth:EditText? = null
    private var etLimit:EditText? = null
    private var etSize:EditText? = null
    private var etHeightPx:EditText? = null
    private var etWidthPx:EditText? = null

    private var viewRoot: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewRoot = inflater.inflate(R.layout.me_fragment,container,false)
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){

        //takePhoto 的视图
//        rgCrop = viewRoot?.findViewById<View>(R.id.rgCrop) as RadioGroup
//        rgCompress = viewRoot?.findViewById<View>(R.id.rgCompress) as RadioGroup
//        rgCompressTool = viewRoot?.findViewById<View>(R.id.rgCompressTool) as RadioGroup
//        rgCropSize = viewRoot?.findViewById<View>(R.id.rgCropSize) as RadioGroup
//        rgFrom = viewRoot?.findViewById<View>(R.id.rgFrom) as RadioGroup
//        rgPickTool = viewRoot?.findViewById<View>(R.id.rgPickTool) as RadioGroup
//        rgRawFile = viewRoot?.findViewById<View>(R.id.rgRawFile) as RadioGroup
//        rgCorrectTool = viewRoot?.findViewById<View>(R.id.rgCorrectTool) as RadioGroup
//        rgShowProgressBar = viewRoot?.findViewById<View>(R.id.rgShowProgressBar) as RadioGroup
//        rgCropTool = viewRoot?.findViewById<View>(R.id.rgCropTool) as RadioGroup
//        etCropHeight = viewRoot?.findViewById<View>(R.id.etCropHeight) as EditText
//        etCropWidth = viewRoot?.findViewById<View>(R.id.etCropWidth) as EditText
//        etLimit = viewRoot?.findViewById<View>(R.id.etLimit) as EditText
//        etSize = viewRoot?.findViewById<View>(R.id.etSize) as EditText
//        etHeightPx = viewRoot?.findViewById<View>(R.id.etHeightPx) as EditText
//        etWidthPx = viewRoot?.findViewById<View>(R.id.etWidthPx) as EditText

        val typeface = Typeface.createFromAsset(activity?.assets, "fonts/Lato-Regular.ttf")
        tv_profile.typeface = typeface
        tv_bookmark.typeface = typeface
        tv_feedback.typeface = typeface
        tv_settings.typeface = typeface
        me_image.setOnClickListener(this)
        me_login.setOnClickListener(this)
        me_profile.setOnClickListener(this)
        me_bookmark.setOnClickListener(this)
        me_feedback.setOnClickListener(this)
        me_settings.setOnClickListener(this)

        loginBroadcastReceiver()

        setUserInfo()
    }


    private fun setNickname() {
        val userStr = SPUtils.get(activity!!, "user", "") as String
        val userBean = JSON.parseObject(userStr, UserBean::class.java)
        me_nickname.text = userBean.nickName
    }

    private fun setUserInfo() {
        if (BaseApp.getUserBean() != null) {
            val userBean = BaseApp.getUserBean()
            me_login.visibility = View.GONE
            me_nickname.visibility = View.VISIBLE
            me_nickname.text = userBean.nickName
            if (userBean.headImage != null && userBean.headImage != "") {
                val imgUrl = userBean.headImage
                if (imgUrl.startsWith("http")) {
                    GlideApp.with(activity!!)
                            .load(imgUrl)
                            .placeholder(R.mipmap.avatar)
                            .error(R.mipmap.avatar)
                            .centerCrop()
                            .dontAnimate()
                            .into(me_image)
                } else {
                    GlideApp.with(activity!!)
                            .load(HttpConstants.IMAGEURL + StringUrlUtil.checkSeparator(imgUrl))
                            .placeholder(R.mipmap.avatar)
                            .error(R.mipmap.avatar)
                            .centerCrop()
                            .dontAnimate()
                            .into(me_image)
                }
            } else {
                me_image.setImageResource(R.mipmap.avatar)
            }
        } else {
            me_login.visibility = View.VISIBLE
            me_nickname.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.me_image ->{
                if (BaseApp.isLogin){
                    choseImage()
                }
            }
            R.id.me_login ->{
                val inten = Intent(activity, LoginAndRegisterActivity::class.java)
                startActivity(inten)
            }
            R.id.me_profile -> {
                if (BaseApp.isLogin){
                    val inten = Intent(activity, ProfileActivity::class.java)
                    startActivity(inten)
                }else{
                    val inten = Intent(activity, LoginAndRegisterActivity::class.java)
                    startActivity(inten)
                }
            }
            R.id.me_bookmark ->{
//                val inten = Intent(activity, CollectionListActivity::class.java)
                val inten = Intent(activity, BookMarkActivity::class.java)
                startActivity(inten)
            }
            R.id.me_feedback ->{
                val inten = Intent(activity, FeedBackActivity::class.java)
                startActivity(inten)
            }
            R.id.me_settings->{
                val inten = Intent(activity, SettingActivity::class.java)
                startActivity(inten)
            }
        }
    }

    private var imageDialog: ChoseImageDialog? = null
    private fun choseImage() {
        imageDialog = ChoseImageDialog(activity)
        imageDialog!!.choseCamera(View.OnClickListener {
            imageDialog!!.dismiss()
            if (toCheckPermission()) {
                //					takePhoto();
                //照相
                click(takePhoto, 2)
            }
        })
        imageDialog!!.chosePhoto(View.OnClickListener {
            imageDialog!!.dismiss()
            if (toCheckPermission2())
            //				pickPhoto();
                click(takePhoto, 1)
        })
        imageDialog!!.choseCancle(View.OnClickListener { imageDialog!!.dismiss() })
        imageDialog!!.show()
    }


    private fun click(takePhoto: TakePhoto, type: Int) {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        val imageUri = Uri.fromFile(file)

        configCompress(takePhoto)
        configTakePhotoOption(takePhoto)

        when (type) {
            1 -> {
                val limit = Integer.parseInt(etLimit?.text.toString())
                if (limit > 1) {
                    if (rgCrop?.checkedRadioButtonId == R.id.rbCropYes) {
                        takePhoto.onPickMultipleWithCrop(limit, getCropOptions())
                    } else {
                        takePhoto.onPickMultiple(limit)
                    }
                    return
                }
                if (rgFrom?.checkedRadioButtonId == R.id.rbFile) {
                    if (rgCrop?.checkedRadioButtonId  == (R.id.rbCropYes)) {
                        takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions())
                    } else {
                        takePhoto.onPickFromDocuments()
                    }
                    return
                } else {
                    if (rgCrop?.checkedRadioButtonId== (R.id.rbCropYes)) {
                        takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions())
                    } else {
                        takePhoto.onPickFromGallery()
                    }
                }
            }
            2 -> if (rgCrop?.checkedRadioButtonId  == (R.id.rbCropYes)) {
                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions())
            } else {
                takePhoto.onPickFromCapture(imageUri)
            }
        }

    }

    private fun configTakePhotoOption(takePhoto: TakePhoto) {
        val builder = TakePhotoOptions.Builder()
        if (rgPickTool?.checkedRadioButtonId == R.id.rbPickWithOwn) {
            builder.setWithOwnGallery(true)
        }
        if (rgCorrectTool?.checkedRadioButtonId == R.id.rbCorrectYes) {
            builder.setCorrectImage(true)
        }
        takePhoto.setTakePhotoOptions(builder.create())

    }

    private fun configCompress(takePhoto: TakePhoto) {
        if (rgCompress?.checkedRadioButtonId  != R.id.rbCompressYes) {
            takePhoto.onEnableCompress(null, false)
            return
        }
        val maxSize = Integer.parseInt(etSize?.text.toString())
        val width = Integer.parseInt(etCropWidth?.text.toString())
        val height = Integer.parseInt(etHeightPx?.text.toString())
        val showProgressBar = if (rgShowProgressBar?.checkedRadioButtonId  == R.id.rbShowYes) true else false
        val enableRawFile = if (rgRawFile?.checkedRadioButtonId == R.id.rbRawYes) true else false
        val config: CompressConfig
        if (rgCompressTool?.checkedRadioButtonId == R.id.rbCompressWithOwn) {
            config = CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(if (width >= height) width else height)
                    .enableReserveRaw(enableRawFile)
                    .create()
        } else {
            val option = LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxSize(maxSize)
                    .setMaxWidth(width)
                    .create()
            config = CompressConfig.ofLuban(option)
            config.enableReserveRaw(enableRawFile)
        }
        takePhoto.onEnableCompress(config, showProgressBar)


    }

    private fun getCropOptions(): CropOptions? {
        if (rgCrop?.checkedRadioButtonId != R.id.rbCropYes) return null
        val height = Integer.parseInt(etCropHeight?.text.toString())
        val width = Integer.parseInt(etCropWidth?.text.toString())
        val withWonCrop = if (rgCropTool?.checkedRadioButtonId == R.id.rbCropOwn) true else false

        val builder = CropOptions.Builder()

        if (rgCropSize?.checkedRadioButtonId == R.id.rbAspect) {
            builder.setAspectX(width).setAspectY(height)
        } else {
            builder.setOutputX(width).setOutputY(height)
        }
        builder.setWithOwnCrop(withWonCrop)
        return builder.create()
    }

    override fun takeCancel() {
        super.takeCancel()
    }

    override fun takeFail(result: TResult, msg: String) {
        super.takeFail(result, msg)
    }

    override fun takeSuccess(result: TResult) {
        super.takeSuccess(result)

        val images = result.images
        if (images.size > 0) {

            //设置图片
            Glide.with(this).load(File(images[0].compressPath)).into(me_image)
            //在这里进行图片上传
            //            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0).getCompressPath());
           var img = ImageUtils.convertIconToString(ImageUtils.lessenUriImage(images[0].compressPath))
            upLoadpic(img)
        }

    }

    private fun upLoadpic(img: String) {
        val userStr = SPUtils.get(activity!!, "user", "") as String
        val userBean = JSON.parseObject(userStr, UserBean::class.java)
        val model = UserImageModel()
        model.userId = userBean.id
        model.imgStr = img
        model.imageName = System.currentTimeMillis().toString() + ".png"
        Commrequest.modifyImage(activity, model, object : ResponseListener {
            override fun onResponse(t: BaseJsonBean, code: Int) {
                val jsonObject = JSON.parseObject(t.`object`)
                if (jsonObject.getString("resMsg") == "success") {
                    val jsonObject1 = jsonObject.getJSONObject("resObject")
                    jsonObject1.getString("headImage")

                    val userBean = BaseApp.getUserBean()
                    userBean.headImage = jsonObject1.getString("headImage")
                    BaseApp.setUserBean(userBean)
                    val userObj = JSON.toJSON(userBean) as JSONObject
                    SPUtils.put(activity, "user", userObj.toJSONString())
                } else {
                    Toast.makeText(activity, "Upload failed", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(t: BaseJsonBean?, errMessage: String) {
                Toast.makeText(activity, "Upload failed", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun toCheckPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        if (PERMISSION_GRANTED != result) {
            //			ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RP_WRITE);
            requestPermissions(arrayOf(Manifest.permission.CAMERA), RP_CAMERA)
            return false
        }
        return true
    }

    private fun toCheckPermission2(): Boolean {
        val result = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (PERMISSION_GRANTED != result) {
            //			ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RP_WRITE);
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RP_WRITE)
            return false
        }
        return true
    }

    private fun showDialog(isReTry: Boolean) {
        //翻译
        //SD卡读写权限缺少
        //应用的基础数据本地初始化时，需要SD卡的读写权限，否则将无法正常使用本应用。
        //可通过'设置' -> '应用程序'->'权限设置'，重新设置应用权限。
        //退出应用
        //重新授权
        val builder = AlertDialog.Builder(activity!!)
                .setTitle("Don't have Camera permissions")
                .setMessage("The Camera permissions is needed. Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
                .setNegativeButton("Exit ") { dialog, which -> dialog.dismiss() }
        if (isReTry) {
            builder.setPositiveButton("Reset") { dialog, which ->
                dialog.dismiss()
                toCheckPermission()
            }
        }
        builder.create().show()
    }

    private fun showDialog2(isReTry: Boolean) {
        //翻译
        //SD卡读写权限缺少
        //应用的基础数据本地初始化时，需要SD卡的读写权限，否则将无法正常使用本应用。
        //可通过'设置' -> '应用程序'->'权限设置'，重新设置应用权限。
        //退出应用
        //重新授权
        val builder = AlertDialog.Builder(activity!!)
                .setTitle("Don't have read and write permissions to SD card.")
                .setMessage("When initializing the application's basic data, the read and write permissions to SD card are needed. Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
                .setNegativeButton("Exit ") { dialog, which -> dialog.dismiss() }
        if (isReTry) {
            builder.setPositiveButton("Reset") { dialog, which ->
                dialog.dismiss()
                toCheckPermission2()
            }
        }
        builder.create().show()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            1 ->{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    click(takePhoto, 2)
                }else{
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.CAMERA)
                    if (shouldShow) {
                        showDialog(true)
                    } else {
                        showDialog(false)
                    }
                }
            }
            2 ->{
                if (grantResults[0] == PERMISSION_GRANTED) {
                    click(takePhoto, 1)
                    //				pickPhoto();
                } else {
                    //判断用户是否勾选 不再询问的选项，未勾选可以 说明权限作用，重新授权。
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (shouldShow) {
                        showDialog2(true)
                    } else {
                        showDialog2(false)
                    }
                }
            }
        }
    }


    val LOGIN_NOTICE = "com.video.broadcasttest.LOGIN_NOTICE"
    val NICKNAME_NOTICE = "com.video.broadcasttest.NICKNAME"
    private var receiver: LoginBroadcastReceiver? = null
    private var nickNamereceiver: NickNameBroadcastReceiver? = null
    //登录之后收到广播（注册）
    private fun loginBroadcastReceiver() {
        //注册广播  (登录成功)
        val counterActionFilter = IntentFilter(MineFragment.LOGIN_NOTICE)
        receiver = LoginBroadcastReceiver()
        activity!!.registerReceiver(receiver, counterActionFilter)

        val nickNameFilter = IntentFilter(MineFragment.NICKNAME_NOTICE)
        nickNamereceiver = NickNameBroadcastReceiver()
        activity!!.registerReceiver(nickNamereceiver, nickNameFilter)
    }

    //登录成功的广播
    internal inner class LoginBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (BaseApp.getToken() != null && BaseApp.getToken() != "") {
                setUserInfo()
            } else {
                me_image.setImageResource(R.mipmap.avatar)
                me_login.isEnabled = true
                me_login.visibility = View.VISIBLE
                me_nickname.visibility = View.GONE
            }
        }
    }

    internal inner class NickNameBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            setNickname()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销广播
        activity!!.unregisterReceiver(receiver)
        activity!!.unregisterReceiver(nickNamereceiver)
    }

}