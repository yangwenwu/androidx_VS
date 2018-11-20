package com.lemon.video.fragment.me;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.lemon.video.R;
import com.lemon.video.application.BaseApp;
import com.lemon.video.application.GlideApp;
import com.lemon.video.common.Commrequest;
import com.lemon.video.common.HttpConstants;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.fragment.me.activity.ChangeNicknameActivity;
import com.lemon.video.fragment.me.activity.FeedBackActivity;
import com.lemon.video.fragment.me.activity.LoginAndRegisterActivity;
import com.lemon.video.fragment.me.activity.ProfileActivity;
import com.lemon.video.fragment.me.activity.SettingActivity;
import com.lemon.video.fragment.me.collection.CollectionListActivity;
import com.lemon.video.httpModel.UserImageModel;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.UserBean;
import com.lemon.video.utils.ChoseImageDialog;
import com.lemon.video.utils.CircleImageView;
import com.lemon.video.utils.ImageUtils;
import com.lemon.video.utils.SPUtils;
import com.lemon.video.utils.StringUrlUtil;

import java.io.File;
import java.util.List;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MineFragment extends TakePhotoFragment implements View.OnClickListener{

    private View view;
    private TextView me_login,tv_profile,tv_bookmark,tv_share,tv_feedback,tv_settings;
    private TextView me_nickname;
    private CircleImageView me_image;
    private LinearLayout me_profile,me_bookmark,me_share,me_feedback,me_settings;


    //takePhoto
    private RadioGroup rgCrop,rgCompress,rgFrom,rgCropSize,rgCropTool,rgShowProgressBar,rgPickTool,rgCompressTool,rgCorrectTool,rgRawFile;
    private EditText etCropHeight,etCropWidth,etLimit,etSize,etHeightPx,etWidthPx;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.mine_fragment, null);
            initView();
            loginBroadcastReceiver();
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initView(){
        AssetManager mgr = getActivity().getAssets();
        //根据路径得到Typeface
        Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf");

//        me_image = (ImageView) view.findViewById(R.id.me_image);
        me_image = (CircleImageView) view.findViewById(R.id.me_image);
        me_login = (TextView) view.findViewById(R.id.me_login);
        me_nickname = (TextView) view.findViewById(R.id.me_nickname);

        me_profile = (LinearLayout) view.findViewById(R.id.me_profile);
        me_bookmark = (LinearLayout) view.findViewById(R.id.me_bookmark);
        me_share = (LinearLayout) view.findViewById(R.id.me_share);
        me_feedback = (LinearLayout) view.findViewById(R.id.me_feedback);
        me_settings = (LinearLayout) view.findViewById(R.id.me_settings);

        tv_profile = (TextView) view.findViewById(R.id.tv_profile);
        tv_bookmark = (TextView) view.findViewById(R.id.tv_bookmark);
        tv_share = (TextView) view.findViewById(R.id.tv_share);
        tv_feedback = (TextView) view.findViewById(R.id.tv_feedback);
        tv_settings = (TextView) view.findViewById(R.id.tv_settings);
        tv_profile.setTypeface(tf2);
        tv_bookmark.setTypeface(tf2);
        tv_share.setTypeface(tf2);
        tv_feedback.setTypeface(tf2);
        tv_settings.setTypeface(tf2);

        me_profile.setOnClickListener(this);
        me_bookmark.setOnClickListener(this);
        me_share.setOnClickListener(this);
        me_feedback.setOnClickListener(this);
        me_settings.setOnClickListener(this);
        me_image.setOnClickListener(this);

        //takePhoto 的视图
        rgCrop= (RadioGroup) view.findViewById(R.id.rgCrop);
        rgCompress= (RadioGroup) view.findViewById(R.id.rgCompress);
        rgCompressTool= (RadioGroup) view.findViewById(R.id.rgCompressTool);
        rgCropSize= (RadioGroup) view.findViewById(R.id.rgCropSize);
        rgFrom= (RadioGroup) view.findViewById(R.id.rgFrom);
        rgPickTool= (RadioGroup) view.findViewById(R.id.rgPickTool);
        rgRawFile = (RadioGroup) view.findViewById(R.id.rgRawFile);
        rgCorrectTool= (RadioGroup) view.findViewById(R.id.rgCorrectTool);
        rgShowProgressBar= (RadioGroup) view.findViewById(R.id.rgShowProgressBar);
        rgCropTool= (RadioGroup) view.findViewById(R.id.rgCropTool);
        etCropHeight= (EditText) view.findViewById(R.id.etCropHeight);
        etCropWidth= (EditText) view.findViewById(R.id.etCropWidth);
        etLimit= (EditText) view.findViewById(R.id.etLimit);
        etSize= (EditText) view.findViewById(R.id.etSize);
        etHeightPx= (EditText) view.findViewById(R.id.etHeightPx);
        etWidthPx= (EditText) view.findViewById(R.id.etWidthPx);

        me_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginAndRegisterActivity.class);
                startActivity(intent);
            }
        });
        setUserInfo();
    }

    private void setUserInfo(){
        if (BaseApp.getUserBean() != null ){
            UserBean userBean = BaseApp.getUserBean();
            me_login.setVisibility(View.GONE);
            me_nickname.setVisibility(View.VISIBLE);
            me_nickname.setText(userBean.nickName);
            if (userBean.headImage != null && !userBean.headImage.equals("")){
                String imgUrl = userBean.headImage;
                if (imgUrl.startsWith("http")){
//                    GlideImageUtils.LoadCircleImage(getActivity(),imgUrl,me_image);
                    GlideApp.with(getActivity())
                    .load(imgUrl)
                    .placeholder(R.mipmap.avatar)
                    .error(R.mipmap.avatar)
                    .centerCrop()
                    .dontAnimate()
                    .into(me_image);
                }else{
                    GlideApp.with(getActivity())
                            .load(HttpConstants.IMAGEURL+ StringUrlUtil.checkSeparator(imgUrl))
                            .placeholder(R.mipmap.avatar)
                            .error(R.mipmap.avatar)
                            .centerCrop()
                            .dontAnimate()
                            .into(me_image);
//                    GlideImageUtils.LoadCircleImage(getActivity(), HttpConstants.IMAGEURL+ StringUrlUtil.checkSeparator(imgUrl),me_image);
                }
            }else {
                me_image.setImageResource(R.mipmap.avatar);
            }
        }else{
            me_login.setVisibility(View.VISIBLE);
            me_nickname.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_profile:   //修改信息
                if (BaseApp.isLogin){
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    getActivity().startActivity(intent);
                    setRefreshLister();
                }else{
                    Intent intent = new Intent(getActivity(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.me_bookmark:   //收藏
                if (BaseApp.isLogin){
//                    Intent intent1 = new Intent(getActivity(), BookMarkActivity.class);
                    Intent intent1 = new Intent(getActivity(), CollectionListActivity.class);
                    getActivity().startActivity(intent1);
                }else{
                    Intent intent = new Intent(getActivity(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.me_feedback:
                int2Activity(getActivity(), FeedBackActivity.class);
                break;
            case R.id.me_settings:
                int2Activity(getActivity(), SettingActivity.class);
                break;
            case R.id.me_image:
                if (BaseApp.isLogin){
                    choseImage();
                }
                break;
        }
    }

    private void setRefreshLister(){
        ChangeNicknameActivity.readingRefreshListen(new ChangeNicknameActivity.ReadingTypeRefreshListen() {
            @Override
            public void reFresh() {
                setNickname();
            }
        });
    }

    private void setNickname(){
        String userStr = (String) SPUtils.get(getActivity(),"user","");
        UserBean userBean = JSON.parseObject(userStr,UserBean.class);
        me_nickname.setText(userBean.nickName);
    }


    private void setInfo(){
        String userStr = (String) SPUtils.get(getActivity(),"user","");
        UserBean userBean = JSON.parseObject(userStr,UserBean.class);
        me_login.setVisibility(View.GONE);
        me_nickname.setVisibility(View.VISIBLE);
        me_nickname.setText(userBean.nickName);
        if (userBean.headImage != null && !userBean.headImage.equals("")){
           String imgUrl = userBean.headImage;
            if (imgUrl.startsWith("http")){
//                GlideImageUtils.LoadCircleImage(getActivity(),imgUrl,me_image);
                GlideApp.with(getActivity())
                        .load(imgUrl)
                        .placeholder(R.mipmap.avatar)
                        .error(R.mipmap.avatar)
                        .centerCrop()
                        .dontAnimate()
                        .into(me_image);
            }else{
                GlideApp.with(getActivity())
                        .load(HttpConstants.IMAGEURL+ StringUrlUtil.checkSeparator(imgUrl))
                        .placeholder(R.mipmap.avatar)
                        .error(R.mipmap.avatar)
                        .centerCrop()
                        .dontAnimate()
                        .into(me_image);
//                GlideImageUtils.LoadCircleImage(getActivity(), HttpConstants.IMAGEURL+ StringUrlUtil.checkSeparator(imgUrl),me_image);
            }

        }else {
            me_image.setImageResource(R.mipmap.avatar);
        }
    }

    private void int2Activity(FragmentActivity activity, Class<? extends Activity> tarActivity){
        Intent intent = new Intent(getActivity(), tarActivity);
        startActivity(intent);
    }

    public static final String LOGIN_NOTICE = "com.video.broadcasttest.LOGIN_NOTICE";
    public static final String NICKNAME_NOTICE = "com.video.broadcasttest.NICKNAME";
    private LoginBroadcastReceiver receiver;
    private NickNameBroadcastReceiver nickNamereceiver;
    //登录之后收到广播（注册）
    private void loginBroadcastReceiver(){
        //注册广播  (登录成功)
        IntentFilter counterActionFilter = new IntentFilter(MineFragment.LOGIN_NOTICE);
        receiver = new LoginBroadcastReceiver();
        getActivity().registerReceiver(receiver, counterActionFilter);

        IntentFilter nickNameFilter = new IntentFilter(MineFragment.NICKNAME_NOTICE);
        nickNamereceiver = new NickNameBroadcastReceiver();
        getActivity().registerReceiver(nickNamereceiver, nickNameFilter);
    }

    //登录成功的广播
    class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BaseApp.getToken() != null && !BaseApp.getToken().equals("")){
                setInfo();
            }else{
                me_image.setImageResource(R.mipmap.avatar);
                me_login.setEnabled(true);
                me_login.setVisibility(View.VISIBLE);
                me_nickname.setVisibility(View.GONE);
            }
        }
    }

    class NickNameBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNickname();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(nickNamereceiver);

    }

    private String img = null;
    private ChoseImageDialog imageDialog;
    private void  choseImage(){
        imageDialog = new ChoseImageDialog(getActivity());
        imageDialog.choseCamera(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (toCheckPermission()){
                    //照相
                    click(getTakePhoto(), 2);
                    imageDialog.dismiss();
                }
            }
        });
        imageDialog.chosePhoto(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (toCheckPermission2())
                    click(getTakePhoto(), 1);
                imageDialog.dismiss();
            }
        });
        imageDialog.choseCancle(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });
        imageDialog.show();
    }

    private void upLoadpic(String img){
        String userStr = (String) SPUtils.get(getActivity(),"user","");
        UserBean userBean = JSON.parseObject(userStr,UserBean.class);
        UserImageModel model = new UserImageModel();
        model.userId = userBean.id;
        model.imgStr = img;
        model.imageName = String.valueOf(System.currentTimeMillis())+".png";
        Commrequest.modifyImage(getActivity(), model, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                if (jsonObject.getString("resMsg").equals("success")){
                    JSONObject jsonObject1 =  jsonObject.getJSONObject("resObject");
                    jsonObject1.getString("headImage");

                    UserBean userBean = BaseApp.getUserBean();
                    userBean.headImage =jsonObject1.getString("headImage");
                    BaseApp.setUserBean(userBean);
                    JSONObject userObj = (JSONObject) JSON.toJSON(userBean);
                    SPUtils.put(getActivity(),"user",userObj.toJSONString());
                }else{
                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean toCheckPermission(){
        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if(PERMISSION_GRANTED != result){

//			ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RP_WRITE);
            requestPermissions(new String[]{Manifest.permission.CAMERA},RP_CAMERA);
            return false;
        }
        return true;
    }

    private boolean toCheckPermission2(){
        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(PERMISSION_GRANTED != result){
//			ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RP_WRITE);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},RP_WRITE);
            return false;
        }
        return true;
    }

    private void showDialog(boolean isReTry){
        //翻译
        //SD卡读写权限缺少
        //应用的基础数据本地初始化时，需要SD卡的读写权限，否则将无法正常使用本应用。
        //可通过'设置' -> '应用程序'->'权限设置'，重新设置应用权限。
        //退出应用
        //重新授权
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity())
                .setTitle("Don't have Camera permissions")
                .setMessage("The Camera permissions is needed. Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
                .setNegativeButton("Exit ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if(isReTry){
            builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    toCheckPermission();
                }
            });
        }
        builder.create().show();
    }

    private void showDialog2(boolean isReTry){
        //翻译
        //SD卡读写权限缺少
        //应用的基础数据本地初始化时，需要SD卡的读写权限，否则将无法正常使用本应用。
        //可通过'设置' -> '应用程序'->'权限设置'，重新设置应用权限。
        //退出应用
        //重新授权
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity())
                .setTitle("Don't have read and write permissions to SD card.")
                .setMessage("When initializing the application's basic data, the read and write permissions to SD card are needed. Otherwise, it would cause abnormal usage. Please reset the permission via 'Settings'->'Applications'->'Permission Settings'.")
                .setNegativeButton("Exit ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if(isReTry){
            builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    toCheckPermission2();
                }
            });
        }
        builder.create().show();
    }
    private static final int RP_WRITE = 2;
    private static final int RP_CAMERA = 3;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(RP_CAMERA == requestCode){
            if (grantResults[0] == PERMISSION_GRANTED) {
                click(getTakePhoto(),2);
            } else {
                //判断用户是否勾选 不再询问的选项，未勾选可以 说明权限作用，重新授权。
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA);
                if(shouldShow){
                    showDialog(true);
                }else{
                    showDialog(false);
                }
            }
        }else if ( RP_WRITE ==requestCode){
            if (grantResults[0] == PERMISSION_GRANTED) {
                click(getTakePhoto(),1);
            } else {
                //判断用户是否勾选 不再询问的选项，未勾选可以 说明权限作用，重新授权。
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(shouldShow){
                    showDialog2(true);
                }else{
                    showDialog2(false);
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //*************   takePhoto    *********
    private void click(TakePhoto takePhoto , int type){
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

        switch (type){
            case 1:
                int limit= Integer.parseInt(etLimit.getText().toString());
                if(limit>1){
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){
                        takePhoto.onPickMultipleWithCrop(limit,getCropOptions());
                    }else {
                        takePhoto.onPickMultiple(limit);
                    }
                    return;
                }
                if(rgFrom.getCheckedRadioButtonId()==R.id.rbFile){
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){
                        takePhoto.onPickFromDocumentsWithCrop(imageUri,getCropOptions());
                    }else {
                        takePhoto.onPickFromDocuments();
                    }
                    return;
                }else {
                    if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){
                        takePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());
                    }else {
                        takePhoto.onPickFromGallery();
                    }
                }
                break;
            case 2:
                if(rgCrop.getCheckedRadioButtonId()==R.id.rbCropYes){
                    takePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());
                }else {
                    takePhoto.onPickFromCapture(imageUri);
                }
                break;
        }

    }

    private void configTakePhotoOption(TakePhoto takePhoto){
        TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
        if(rgPickTool.getCheckedRadioButtonId()==R.id.rbPickWithOwn){
            builder.setWithOwnGallery(true);
        }
        if(rgCorrectTool.getCheckedRadioButtonId()==R.id.rbCorrectYes){
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

    }
    private void configCompress(TakePhoto takePhoto){
        if(rgCompress.getCheckedRadioButtonId()!=R.id.rbCompressYes){
            takePhoto.onEnableCompress(null,false);
            return ;
        }
        int maxSize= Integer.parseInt(etSize.getText().toString());
        int width= Integer.parseInt(etCropWidth.getText().toString());
        int height= Integer.parseInt(etHeightPx.getText().toString());
        boolean showProgressBar=rgShowProgressBar.getCheckedRadioButtonId()==R.id.rbShowYes? true:false;
        boolean enableRawFile = rgRawFile.getCheckedRadioButtonId() == R.id.rbRawYes ? true : false;
        CompressConfig config;
        if(rgCompressTool.getCheckedRadioButtonId()==R.id.rbCompressWithOwn){
            config=new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width>=height? width:height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        }else {
            LubanOptions option=new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxSize(maxSize)
                    .setMaxWidth(width)
                    .create();
            config=CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config,showProgressBar);


    }

    private CropOptions getCropOptions(){
        if(rgCrop.getCheckedRadioButtonId()!=R.id.rbCropYes)return null;
        int height= Integer.parseInt(etCropHeight.getText().toString());
        int width= Integer.parseInt(etCropWidth.getText().toString());
        boolean withWonCrop=rgCropTool.getCheckedRadioButtonId()==R.id.rbCropOwn? true:false;

        CropOptions.Builder builder=new CropOptions.Builder();

        if(rgCropSize.getCheckedRadioButtonId()==R.id.rbAspect){
            builder.setAspectX(width).setAspectY(height);
        }else {
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        List<TImage> images = result.getImages();
        if (images.size() >0){

            //设置图片
            Glide.with(getActivity()).load(new File(images.get(0).getCompressPath())).into(me_image);
            //在这里进行图片上传
//            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0).getCompressPath());
            img = ImageUtils.convertIconToString(ImageUtils.lessenUriImage(images.get(0).getCompressPath()));
//            upLoadPic(img,null,null);
            upLoadpic(img);
        }


    }


}
