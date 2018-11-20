package com.lemon.video.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.widget.RemoteViews;
import com.lemon.video.BuildConfig;
import com.lemon.video.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;


public class TaskService extends Service {
    /****
     * 发送广播的请求码
     */
    private final int REQUEST_CODE_BROADCAST = 0X0001;
    /****
     * 发送广播的action
     */
    private final String BROADCAST_ACTION_CLICK = "servicetask";
    /**
     * 通知
     */
    private Notification notification;
    /**
     * 通知的Id
     */
    private final int NOTIFICATION_ID = 1;
    /**
     * 通知管理器
     */
    private NotificationManager notificationManager;
    /**
     * 通知栏的远程View
     */
    private RemoteViews mRemoteViews;
    /**
     * 下载是否可取消
     */
    private Callback.Cancelable cancelable;
    /**
     * 自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
     */
    private String filePath = Environment.getExternalStorageDirectory() + "/video_shows_download/";
    private File file;
    private String download_url;
    /**
     * 通知栏操作的四种状态
     */
    private enum Status {
        DOWNLOADING, PAUSE, FAIL, SUCCESS
    }

    /**
     * 当前在状态 默认正在下载中
     */
    private Status status = Status.DOWNLOADING;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            download_url = intent.getStringExtra("download_url");
        }

        registerBroadCast();
        download();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 注册按钮点击广播*
     */
    private void registerBroadCast() {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_CLICK);
        registerReceiver(myBroadcastReceiver, filter);
    }


    /**
     * 更新通知界面的按钮的广播
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(BROADCAST_ACTION_CLICK)) {
                return;
            }
            switch (status) {
                case DOWNLOADING:
                    /**当在下载中点击暂停按钮**/
                    cancelable.cancel();
//                    mRemoteViews.setTextViewText(R.id.bt, "下载");
                    mRemoteViews.setTextViewText(R.id.bt, "Download");
//                    mRemoteViews.setTextViewText(R.id.tv_message, "暂停中...");
                    mRemoteViews.setTextViewText(R.id.tv_message, "Pause");
                    status = Status.PAUSE;
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    break;
                case SUCCESS:
                    /**当下载完成点击完成按钮时关闭通知栏**/
                    notificationManager.cancel(NOTIFICATION_ID);
                    break;
                case FAIL:
                    break;
                case PAUSE:
                    /**当在暂停时点击下载按钮**/
                    download();
//                    mRemoteViews.setTextViewText(R.id.bt, "暂停");
                    mRemoteViews.setTextViewText(R.id.bt, "Pause");
//                    mRemoteViews.setTextViewText(R.id.tv_message, "下载中...");
                    mRemoteViews.setTextViewText(R.id.tv_message, "Download");
                    status = Status.DOWNLOADING;
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    break;
            }
        }
    }

    /**
     * 下载文件
     */
    private void download() {
//        final String url = "https://github.com/linglongxin24/DylanStepCount/raw/master/app-debug.apk";
//        final String url = "http://gdown.baidu.com/data/wisegame/bd47bd249440eb5f/shenmiaotaowang2.apk";
//        final String download_url = "http://www.vdoenglish.com/apk/ch/vdoenglish-release.apk";

//        RequestParams requestParams = new RequestParams(url);
        RequestParams requestParams = new RequestParams(download_url);
        String fileName = download_url.substring(download_url.lastIndexOf("/") + 1);
//        String fileName = "VDO English";
        file = new File(filePath, fileName);
        showNotificationProgress(TaskService.this);
        showFileName(fileName);
        requestParams.setSaveFilePath(file.getPath());
        /**自动为文件命名**/
        requestParams.setAutoRename(true);
        /**自动为文件断点续传**/
        requestParams.setAutoResume(true);

        cancelable = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
//                Logger.d("下载完成");
//                Logger.d("result=" + result.getPath());
                downloadSuccess();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                Logger.d("下载异常");
                downloadFail();
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Logger.d("下载已取消");
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                Logger.d("total=" + total + "--" + "current=" + current);
                updateNotification(total, current);
            }
        });
    }

    /**
     * 显示一个下载带进度条的通知
     *
     * @param context 上下文
     */
    NotificationCompat.Builder builderProgress;
    public void showNotificationProgress(Context context) {
        /**进度条通知构建**/
         builderProgress = new NotificationCompat.Builder(context);

        /**设置为一个正在进行的通知**/
        builderProgress.setOngoing(true);
        /**设置小图标**/
        builderProgress.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        builderProgress.setLargeIcon(largeIcon);

        /**新建通知自定义布局**/
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);

        /**进度条ProgressBar**/
        mRemoteViews.setProgressBar(R.id.pb, 100, 0, false);
        /**提示信息的TextView**/
//        mRemoteViews.setTextViewText(R.id.tv_message, "下载中...");
        mRemoteViews.setTextViewText(R.id.tv_message, "Download");
        /**操作按钮的Button**/
//        mRemoteViews.setTextViewText(R.id.bt, "暂停");
        mRemoteViews.setTextViewText(R.id.bt, "Pause");
        /**设置左侧小图标*/
        mRemoteViews.setImageViewResource(R.id.iv, R.mipmap.ic_launcher);
        /**设置通过广播形式的PendingIntent**/
        Intent intent = new Intent(BROADCAST_ACTION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_BROADCAST, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.bt, pendingIntent);
        /**设置自定义布局**/
        builderProgress.setContent(mRemoteViews);
        /**设置滚动提示**/
        builderProgress.setTicker(null);
        notification = builderProgress.build();
        if(Build.VERSION.SDK_INT >= 16) {
            notification = builderProgress.build();
            notification.bigContentView = mRemoteViews;
        }
        /**设置不可手动清除**/
//        notification.flags = Notification.FLAG_NO_CLEAR;
        /**设置可手动清除**/
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        /**获取通知管理器**/
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        /**发送一个通知**/
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    /**
     * 在通知栏显示文件名
     *
     * @param url 下载地址
     */
    private void showFileName(String url) {
//        mRemoteViews.setTextViewText(R.id.tv_name, url.substring(url.lastIndexOf("/") + 1));
        mRemoteViews.setTextViewText(R.id.tv_name, "Video Shows");
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 下载更改进度
     *
     * @param total   总大小
     * @param current 当前已下载大小
     */
    private void updateNotification(long total, long current) {
        mRemoteViews.setTextViewText(R.id.tv_size, formatSize(current) + "/" + formatSize(total));
        int result = Math.round((float) current / (float) total * 100);
        mRemoteViews.setTextViewText(R.id.tv_progress, result + "%");
        mRemoteViews.setProgressBar(R.id.pb, 100, result, false);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 下载失败
     */
    private void downloadFail() {
        status = Status.FAIL;
        if (!cancelable.isCancelled()) {
            cancelable.cancel();
        }
//        mRemoteViews.setTextViewText(R.id.bt, "重试");
        mRemoteViews.setTextViewText(R.id.bt, "Retry");
//        mRemoteViews.setTextViewText(R.id.tv_message, "下载失败");
        mRemoteViews.setTextViewText(R.id.tv_message, "Download failed");
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 下载成功
     */
    private void downloadSuccess() {
        status = Status.SUCCESS;
        mRemoteViews.setTextViewText(R.id.bt, "Done");
        mRemoteViews.setTextViewText(R.id.tv_message, "Download success");
        if (Build.VERSION.SDK_INT >= 26){
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
            startActivity(install);// 下载完成之后自动弹出安装界面
            stopSelf();
            notificationManager.cancel(NOTIFICATION_ID);
        }else
        if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            stopSelf();
            startActivity(install);
            notificationManager.cancel(NOTIFICATION_ID);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stopSelf();
            startActivity(install);
            notificationManager.cancel(NOTIFICATION_ID);
        }

    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return
     */
    private String formatSize(long size) {
        String format;
        if (size >= 1024 * 1024) {
            format = byteToMB(size) + "M";
        } else if (size >= 1024) {
            format = byteToKB(size) + "k";
        } else {
            format = size + "b";
        }
        return format;
    }

    /**
     * byte转换为MB
     *
     * @param bt 大小
     * @return MB
     */
    private float byteToMB(long bt) {
        int mb = 1024 * 1024;
        float f = (float) bt / (float) mb;
        float temp = (float) Math.round(f * 100.0F);
        return temp / 100.0F;
    }

    /**
     * byte转换为KB
     *
     * @param bt 大小
     * @return K
     */
    private int byteToKB(long bt) {
        return Math.round((bt / 1024));
    }

    /**
     * 销毁时取消下载，并取消注册广播，防止内存溢出
     */
    @Override
    public void onDestroy() {
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }
//        stopSelf();
//        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

}
