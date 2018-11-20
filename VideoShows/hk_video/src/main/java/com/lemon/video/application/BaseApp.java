package com.lemon.video.application;

import android.app.Activity;
import android.content.Context;

import com.lemon.video.model.UserBean;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import share.ShareConfigKt;

//import share.ShareConfigKt;

/**
 * BaseApp
 */

public class BaseApp extends MobApplication {
    private static BaseApp sInstance;
    private static List<Activity> activities;
    public static boolean isLogin = false;
    public static String macAddr = null;
    public static String token = null;
    public static String createTime = null;

    public static boolean canRefresh = false;

    public static boolean isCanRefresh() {
        return canRefresh;
    }

    //是否是第三方登录
    public static boolean isThirdLogin = false;

    public static Boolean isChinaRegion;

    public static Boolean netIsAvailable;

    public static Boolean isFavor = false;

    public static UserBean user;

    private static String USER_AGENT;
    public static String getUserAgent() {
        return USER_AGENT;
    }

    public static void setCanRefresh(boolean canRefresh) {
        BaseApp.canRefresh = canRefresh;
    }

    public interface MsgDisplayListener {
        void handle(String msg);
    }

    public static MsgDisplayListener msgDisplayListener = null;
    public static StringBuilder cacheMsg = new StringBuilder();

    public BaseApp() {
    }

    public void onCreate() {
        super.onCreate();
        initHotfix();
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        //初始化百度地图
//		SDKInitializer.initialize(BaseApp.this);

        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//		Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(this);
        ShareConfigKt.initShare(this);

        //xutils 初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        //mobSDK
        MobSDK.init(this);

//		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//		JPushInterface.init(this);     		// 初始化 JPush

//		ExceptionCatchUtil exception = ExceptionCatchUtil.getInstance();  
//		exception.init(getApplicationContext());


        activities = new ArrayList<Activity>();

        initImageLoader(this);

        sInstance = this;


    }

    private void initHotfix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0";
        }

        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                //.setAesKey("0123456789123456")
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        if (msgDisplayListener != null) {
                            msgDisplayListener.handle(msg);
                        } else {
                            cacheMsg.append("\n").append(msg);
                        }
                    }
                }).initialize();
    }


    {
        //设置linkedin  和 google+
//		PlatformConfig.setTwitter("7yb93CK9HkiNz7oq8xy5cJ2SB", "wYCSh3B0eX7uhuPtujbqWGGSqw9XoDGaLm9izvuhzFSRrQAGGn");
    }

    public static BaseApp getInstance() {
        return sInstance;
    }

    // 初始化图片处理
    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
//			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//					.threadPriority(Thread.NORM_PRIORITY - 2)
//					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
//					.tasksProcessingOrder(QueueProcessingType.LIFO)
//					.defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
//					.build();
//			// Initialize ImageLoader with configuration.
//			ImageLoader.getInstance().init(config);
    }


    /**
     * add
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * remove
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * close
     */
    public static void exit() {
        try {
            for (Activity activity : activities) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean getNetIsAvailable() {
        return netIsAvailable;
    }

    public static void setNetIsAvailable(Boolean netIsAvailable) {
        BaseApp.netIsAvailable = netIsAvailable;
    }

    public static String getCreateTime() {
        return createTime;
    }

    public static void setCreateTime(String createTime) {
        BaseApp.createTime = createTime;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        BaseApp.token = token;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static Boolean getIsFavor() {
        return isFavor;
    }

    public static void setIsFavor(Boolean isFavor) {
        BaseApp.isFavor = isFavor;
    }

    public static void setLogin(boolean isLogin) {
        BaseApp.isLogin = isLogin;
    }

    public static UserBean getUserBean() {
        return user;
    }

    public static void setUserBean(UserBean userBean) {
        BaseApp.user = userBean;
    }

    public static boolean isThirdLogin() {
        return isThirdLogin;
    }

    public static void setThirdLogin(boolean isThirdLogin) {
        BaseApp.isThirdLogin = isThirdLogin;
    }

    public static Boolean getIsChinaRegion() {
        return isChinaRegion;
    }

    public static void setIsChinaRegion(Boolean isChinaRegion) {
        BaseApp.isChinaRegion = isChinaRegion;
    }

    public static void exit(Activity currentActivity) {
        try {
            for (Activity activity : activities) {
                if (currentActivity == activity || currentActivity.equals(activity)) {
                    continue;
                } else {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
