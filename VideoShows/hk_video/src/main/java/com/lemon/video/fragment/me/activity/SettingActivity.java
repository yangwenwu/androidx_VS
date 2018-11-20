package com.lemon.video.fragment.me.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.application.BaseApp;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.common.Commrequest;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.fragment.me.MineFragment;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.VersionBean;
import com.lemon.video.update.TaskService;
import com.lemon.video.update.UpdateDialog;
import com.lemon.video.utils.DataCleanManager;
import com.lemon.video.utils.NetWorkUtil;
import com.lemon.video.utils.QuickClick2TimesUtil;
import com.lemon.video.utils.SPUtils;
import com.lemon.video.utils.StringUrlUtil;


public class SettingActivity extends BaseAppActivity implements View.OnClickListener {

    private RelativeLayout clear_cache_layout, version_update_layout, about_us_layout, terms,accessibility_statement;
    private RelativeLayout privacy;
    private CheckBox push_msg_cb;
    private TextView cache_size, log_out;
    private ImageView nav_back;
    private String size;
    private String localVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        //由于6.0主题添加 name="android:windowTranslucentStatus">true属性的原因，这里需要垫上状态栏的高度
        initView();

        //保存系统版本，供apk升级更新后第一次安装使用
        try {
            // 系统版本号
            localVersionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setLogoutView();
        getCacheSize();
    }

    private void getCacheSize() {
        try {
            size = DataCleanManager.getTotalCacheSize(SettingActivity.this);
            cache_size.setText(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLogoutView() {
        if (BaseApp.isLogin) {
            log_out.setVisibility(View.VISIBLE);
        } else {
            log_out.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {
        nav_back = (ImageView) findViewById(R.id.nav_back);

        privacy = (RelativeLayout) findViewById(R.id.privacy);

        clear_cache_layout = (RelativeLayout) findViewById(R.id.clear_cache_layout);
        version_update_layout = (RelativeLayout) findViewById(R.id.version_update_layout);
        about_us_layout = (RelativeLayout) findViewById(R.id.about_us_layout);
        terms = (RelativeLayout) findViewById(R.id.terms);
        accessibility_statement = (RelativeLayout) findViewById(R.id.accessibility_statement);

        log_out = (TextView) findViewById(R.id.log_out);
        cache_size = (TextView) findViewById(R.id.cache_size);
        nav_back.setOnClickListener(this);

        privacy.setOnClickListener(this);
        clear_cache_layout.setOnClickListener(this);
        version_update_layout.setOnClickListener(this);
        about_us_layout.setOnClickListener(this);
        terms.setOnClickListener(this);
        accessibility_statement.setOnClickListener(this);
        log_out.setOnClickListener(this);

        push_msg_cb = (CheckBox) findViewById(R.id.push_msg_cb);

        final boolean isPush = (boolean) SPUtils.get(SettingActivity.this, "isPush", true);
        if (isPush) {
            push_msg_cb.setChecked(true);
        } else {
            push_msg_cb.setChecked(false);
        }
        push_msg_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked){
//					//开启
//					JPushInterface.resumePush(getApplicationContext());
//					SPUtils.remove(MeSettingActivity.this,"isPush");
//					SPUtils.put(MeSettingActivity.this,"isPush",true);
//				}else{
                //关闭
//					JPushInterface.stopPush(getApplicationContext());
//					SPUtils.remove(MeSettingActivity.this,"isPush");
//					SPUtils.put(MeSettingActivity.this,"isPush",false);
//				}
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_back:
                finish();
                break;
            case R.id.about_us_layout:
                if (QuickClick2TimesUtil.isFastClick()) {
                    intentTo(AboutUsActivity.class);
                }
                break;
            case R.id.clear_cache_layout:
                if (QuickClick2TimesUtil.isFastClick()) {
                    clearCache();
                }
                break;
            case R.id.privacy:
                if (QuickClick2TimesUtil.isFastClick()) {
                    WebActivity.openWebWindowByContent(SettingActivity.this,
                            "http://api.cdeclips.com/ui/videoShowsPrivacy.html",
                            getResources().getString(R.string.control_terms_of_use), "");
                    //@string/control_about   control_terms_of_use
                }
                break;
            case R.id.terms:

                break;
            case R.id.version_update_layout:
                if (QuickClick2TimesUtil.isFastClick()) {
                    //版本更新
                    if (NetWorkUtil.isNetworkAvailable(SettingActivity.this)) {
                        getNewVersion();
                    } else {
                        intentTo(UpdateActivity.class);
                    }
                }
                break;
            case R.id.accessibility_statement:
//                file:///android_asset/accessibility_statement.html
                if (QuickClick2TimesUtil.isFastClick()) {
                    WebActivity.openWebWindowByContent(SettingActivity.this,
                            "file:///android_asset/accessibility_statement.html",
                            getResources().getString(R.string.accessibility_statement), "");
                    //@string/control_about   control_terms_of_use
                }
                break;
            case R.id.log_out:
                SPUtils.remove(SettingActivity.this, "user");
                BaseApp.setLogin(false);
                BaseApp.setUserBean(null);
                BaseApp.setToken(null);
                sendLogoutBroad();
                finish();
                break;
        }

    }

    private void sendLogoutBroad() {
        Intent broadIntent = new Intent(MineFragment.LOGIN_NOTICE);
        sendBroadcast(broadIntent);
    }


    private void clearCache() {
        DataCleanManager.clearnCache(SettingActivity.this);
        cache_size.setText("0k");
        Toast.makeText(SettingActivity.this, "Cache cleared ", Toast.LENGTH_SHORT).show();
    }


    private void intentTo(Class activity) {
        Intent intent = new Intent(SettingActivity.this, activity);
        startActivity(intent);
    }

    private void getNewVersion() {
        Commrequest.queryVersion(SettingActivity.this, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMeg = jsonObject.getString("resMsg");
                if (resMeg.equals("success")) {
                    JSONArray array = jsonObject.getJSONArray("resObject");
                    if (array.size() > 0) {
                        JSONObject jsonObject1 = array.getJSONObject(0);
                        VersionBean versionBean = JSON.parseObject(jsonObject1.toJSONString(), VersionBean.class);
                        compareVersion(versionBean);
                    }
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                intentTo(UpdateActivity.class);
            }
        });
    }


    private void compareVersion(VersionBean versionBean) {
        String versionName = versionBean.versionName;  //版本号名称
        String versionDesc = versionBean.versionDesc; //英文描述
        //1强制更新，0非强制更新
        final String force = versionBean.updateType;//强制更新
        //版本更新下载地址
        final String downloadUrl = versionBean.apkPath;

        if (StringUrlUtil.strNoToInt(versionName) > StringUrlUtil.strNoToInt(localVersionName)) {
            final UpdateDialog dialog = new UpdateDialog(SettingActivity.this);
            dialog.setContent(versionDesc);
            if (force.equals("1")) { //1强制更新，0非强制更新
                dialog.setLeftBtnText("Update");
            } else {
                dialog.setLeftBtnText("Later");
            }

            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //取消
                    if (force.equals("1")) {//1强制更新，0非强制更新
                        startServiceTask(downloadUrl);
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }

                }
            });

            dialog.setRightBtnText("Update Now");
            dialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    startServiceTask(downloadUrl);
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            // China Daily News is up to date
            intentTo(UpdateActivity.class);
            Toast.makeText(SettingActivity.this, "Video Shows is up to date", Toast.LENGTH_SHORT).show();
        }
    }

    public void startServiceTask(String mVersion_path) {
        Intent intent = new Intent(SettingActivity.this, TaskService.class);
        intent.putExtra("download_url", mVersion_path);
        startService(intent);
    }

}
