package com.lemon.video.fragment.me.collection;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lemon.video.R;
import com.lemon.video.application.GlideApp;
import com.lemon.video.common.Commrequest;
import com.lemon.video.common.HttpConstants;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.fragment.top.adapter.SearchVideoRecycleAdapter;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.HomeNewsBean;
import com.lemon.video.utils.DeviceConfig;
import com.lemon.video.utils.StringUrlUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Staff on 2017/6/29.
 */

public class VideoCollectRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * RecycleViewAdapter 要素点：
     * <p/>
     * 1，ViewHolder必须继承RecyclerView.ViewHolder
     * 2，RecycleView.Adapter的泛型为自定义ViewHolder
     */

    public List<TouTiaoListBean> mData;
    protected Context context;
    protected OnItemClickListener onItemClickListener;

    //列表类型，1 普通稿件 2 oPinion 3 视频
    public final static int TYPE_ONE = 1;
    public final static int TYPE_TWO = 2;
    public final static int TYPE_THREE = 3;


    public VideoCollectRecycleAdapter(Context context, List<TouTiaoListBean> mData) {
        this.mData = mData;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        view = LayoutInflater.from(context).inflate(R.layout.video_search_collection_item2, parent, false);
        holder = new ViewHolderOne(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolderOne holderOne = (ViewHolderOne) holder;
        TouTiaoListBean nb = mData.get(position);
        AssetManager mgr = context.getAssets();
        //根据路径得到Typeface
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Lato-Bold.ttf");
        Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf");
        holderOne.video_tag.setTypeface(tf2);
        if (nb.subjectName != null && !nb.subjectName.equals("")) {
            holderOne.video_tag.setText(nb.subjectName);
        }
        holderOne.video_title.setTypeface(tf);
        if (nb.title != null && !nb.title.equals("")){
            holderOne.video_title.setText(nb.title);
//                    holderOne.video_title.setText("holderOne holderOne holderOne holderOne holderOne holderOne holderOne holderOne holderOne" );
        }
        String img = nb.bigTitleImage;
//        String img = "http://www.chinadailyhk.com/attachments/image/0/133/32/56159_53265/56159_53265_620_356_jpg.jpg";
        if (img != null && !img.equals("")){
            String imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img);
            GlideApp.with(context)
                    .load(imgUrl)
                    .placeholder(R.mipmap.news_big_default)
                    .error(R.mipmap.news_big_default)
                    .centerCrop()
//                    .skipMemoryCache( true )
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(holderOne.video_image);


        }else{
            img = nb.titleImage;
            if (img != null && !img.equals("")){
                String imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img);
                GlideApp.with(context)
                        .load(imgUrl)
                        .placeholder(R.mipmap.news_big_default)
                        .error(R.mipmap.news_big_default)
                        .centerCrop()
//                        .skipMemoryCache( true )
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate()
                        .into(holderOne.video_image);
            }else{
                holderOne.video_image.setImageResource(R.mipmap.news_big_default);
            }
        }

        onItemEventClick(holderOne,position);

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolderSelected extends RecyclerView.ViewHolder {
        @Nullable
        public ImageView ivSelected;  //该属性在我的收藏对象中使用，其他的地方可能为空

        public ViewHolderSelected(View itemView) {
            super(itemView);
            View view = itemView.findViewById(R.id.iv_select);
            if (view != null) {
                ivSelected = (ImageView) view;
            }
        }
    }

    class ViewHolderOne extends ViewHolderSelected {
        private ImageView video_image;
        private TextView video_tag,video_title;



        public ViewHolderOne(View itemView) {
            super(itemView);
            video_image = (ImageView) itemView.findViewById(R.id.video_image);
            video_title = (TextView) itemView.findViewById(R.id.video_title);
            video_tag = (TextView) itemView.findViewById(R.id.video_tag);
        }
    }

    protected void onItemEventClick(RecyclerView.ViewHolder holder,final int position) {
//        final int position = holder.getLayoutPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.OnItemLongClick(v, position);
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }

    private void getGood(final TextView good, String newsId ){
        Commrequest.getGoodCount(context,newsId , new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                JSONObject idInfo = jsonObject.getJSONObject("resObject");
                int goodCount = idInfo.getIntValue("count");
                if (goodCount == 0){
                    good.setText("");
                }else{
                    if (goodCount == 1){
                        good.setText(String.valueOf(goodCount)+ " like");
                    }else{
                        good.setText(String.valueOf(goodCount)+ " likes");
                    }
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {

            }
        });

    }

    //视频分类like
    private void getVideoGood(final TextView good, String newsId ){
        Commrequest.getGoodCount(context,newsId , new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                JSONObject idInfo = jsonObject.getJSONObject("resObject");
                int goodCount = idInfo.getIntValue("count");
                if (goodCount == 0){
                    good.setText("");
                }else{
                    if (goodCount == 1){
                        good.setText(String.valueOf(goodCount));
                    }else{
                        good.setText(String.valueOf(goodCount));
                    }
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {

            }
        });

    }
}
