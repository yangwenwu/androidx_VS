package com.lemon.video.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.lemon.video.MainPageActivity
import com.lemon.video.R
import com.lemon.video.base.activity.BaseAppActivity

class SplashActivity : BaseAppActivity() {
    private val RP = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        if (toCheckPermission()){
            init()
        }
    }

    //	初次进来
    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            val goTo = Intent(this@SplashActivity, MainPageActivity::class.java)
            startActivity(goTo)
            finish()
        }, 2000)
    }

    private fun showDialog(isReTry: Boolean) {
        //翻译
        //SD卡读写权限缺少
        //应用的基础数据本地初始化时，需要SD卡的读写权限，否则将无法正常使用本应用。
        //可通过'设置' -> '应用程序'->'权限设置'，重新设置应用权限。
        //退出应用
        //重新授权
        val builder = AlertDialog.Builder(this)
                .setTitle("Don't have read and write permissions to SD card.")
                .setMessage("When initializing the application's basic data, the read and write permissions to SD card are needed. Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
                .setNegativeButton("Exit ") { dialog, which ->
                    dialog.dismiss()
                    finish()
                }
        if (isReTry) {
            builder.setPositiveButton("Reset") { dialog, which ->
                dialog.dismiss()
                toCheckPermission()
            }
        }
        builder.create().show()
    }

//    相机权限不是必须
//    private fun showDialog1(isReTry: Boolean) {
//        val builder = AlertDialog.Builder(this)
//                .setTitle("Don't have CAMERA permissions")
//                .setMessage("When initializing the application's basic data, the READ_PHONE_STATE permissions is needed.，Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
//                .setNegativeButton("Exit") { dialog, which ->
//                    dialog.dismiss()
//                    finish()
//                }
//        if (isReTry) {
//            builder.setPositiveButton("Reset") { dialog, which ->
//                dialog.dismiss()
//                toCheckPermission()
//            }
//        }
//        builder.create().show()
//    }

    private fun toCheckPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val camera = ActivityCompat.checkSelfPermission(this@SplashActivity, android.Manifest.permission.CAMERA)
//        if (PackageManager.PERMISSION_GRANTED != result || PackageManager.PERMISSION_GRANTED != camera) {
        if (PackageManager.PERMISSION_GRANTED != result ) {
//            ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA), RP)
            ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), RP)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (RP == requestCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                init()
            } else {
                //判断用户是否勾选 不再询问的选项，未勾选可以 说明权限作用，重新授权。
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (shouldShow) {
                        showDialog(true)
                    } else {
                        showDialog(false)
                    }
                }

                //相机权限不是唯一
//                else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
//                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
//                    if (shouldShow) {
//                        showDialog1(true)
//                    } else {
//                        showDialog1(false)
//                    }
//                }


            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    //返回键，finish
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.ACTION_DOWN && event?.action == KeyEvent.ACTION_DOWN){
            finish()
            System.exit(0)
           return true
        }
        return super.onKeyDown(keyCode, event)
    }

}