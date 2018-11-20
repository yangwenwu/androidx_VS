package com.lemon.video.fragment.categories.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lemon.video.application.GlideApp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.common.Commrequest;
import com.lemon.video.common.HttpConstants;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.HomeNewsBean;
import com.lemon.video.utils.DeviceConfig;
import com.lemon.video.utils.StringUrlUtil;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PublicVideoRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<HomeNewsBean> mData;
    protected Context context;
    protected OnItemClickListener onItemClickListener;

    public PublicVideoRecycleAdapter(Context context, List<HomeNewsBean> mData) {
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
        view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
//        view = LayoutInflater.from(context).inflate(R.layout.video_home_list_item, parent, false);
        holder = new ViewHolderOne(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolderOne holderOne = (ViewHolderOne) holder;
        HomeNewsBean nb = mData.get(position);
        AssetManager mgr = context.getAssets();
        //根据路径得到Typeface
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Lato-Bold.ttf");
        Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf");

        holderOne.video_title.setTypeface(tf);
        if (nb.title != null && !nb.title.equals("")) {
            holderOne.video_title.setText(nb.title);
//                    holderOne.video_title.setText("holderOne holderOne holderOne holderOne holderOne holderOne holderOne holderOne holderOne" );
        }
        if (nb.publishTime != null && !nb.publishTime.equals("")) {
//                    holderOne.video_time.setText(nb.publishTime.toUpperCase());
            holderOne.video_time.setText("6'30");
        }
        //点赞获取接口
        holderOne.tv_commentcount.setTypeface(tf2);
//        holderOne.tv_commentcount.setText("256");
        holderOne.tv_likecount.setTypeface(tf2);
//        holderOne.tv_likecount.setText("123");
        getGood(holderOne.tv_likecount, nb.dataId);
        getCommentCount(holderOne.tv_commentcount,nb.dataId);
        DeviceConfig.reinstallScreenSize(context);
        int width = DeviceConfig.getDeviceWidth() - (int) context.getResources().getDimension(R.dimen.padding_medium)
                * 2;
        //int hight = (int) context.getResources().getDimension(R.dimen.height_all_img);

        ViewGroup.LayoutParams laParams = (ViewGroup.LayoutParams) holderOne.video_image.getLayoutParams();
//                laParams.height = width * 2 / 3;
        laParams.height = width * 9 / 16;
        laParams.width = width;
        holderOne.video_image.setLayoutParams(laParams);
        String img = nb.bigTitleImage;
//        String img = "http://www.chinadailyhk.com/attachments/image/0/133/32/56159_53265/56159_53265_620_356_jpg.jpg";
        if (img != null && !img.equals("")) {
            String imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img);
            GlideApp.with(context)
                    .load(imgUrl)
                    .placeholder(R.mipmap.news_big_default)
                    .error(R.mipmap.news_big_default)
                    .centerCrop()
                    .into(holderOne.video_image);
        } else {
            img = nb.titleImage;
            if (img != null && !img.equals("")) {
                String imgUrl = HttpConstants.SERVICEURL + StringUrlUtil.checkSeparator(img);
                GlideApp.with(context)
                        .load(imgUrl)
                        .placeholder(R.mipmap.news_big_default)
                        .error(R.mipmap.news_big_default)
                        .centerCrop()
                        .dontAnimate()
                        .into(holderOne.video_image);
            } else {
                holderOne.video_image.setImageResource(R.mipmap.news_big_default);
            }
        }

        onItemEventClick(holderOne, position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }


    class ViewHolderOne extends RecyclerView.ViewHolder {
        private ImageView video_image;
        private TextView video_title, video_time, tv_commentcount, tv_likecount;

        public ViewHolderOne(View itemView) {
            super(itemView);
            video_image = (ImageView) itemView.findViewById(R.id.video_image);
            video_time = (TextView) itemView.findViewById(R.id.video_time);
            video_title = (TextView) itemView.findViewById(R.id.video_title);
            tv_commentcount = (TextView) itemView.findViewById(R.id.tv_commentcount);
            tv_likecount = (TextView) itemView.findViewById(R.id.tv_likecount);
        }
    }

    protected void onItemEventClick(RecyclerView.ViewHolder holder, final int position) {
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


    private void getGood(final TextView good, String newsId) {
        Commrequest.getGoodCount(context, newsId, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                JSONObject idInfo = jsonObject.getJSONObject("resObject");
                int goodCount = idInfo.getIntValue("count");
                if (goodCount == 0) {
                    good.setText("0");
                } else {
                    good.setText(String.valueOf(goodCount));
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {

            }
        });

    }

    /***
     * 查询评论数量
     */
    //查询评论数量
    //http://203.186.80.109/hknews-api/countComment?newsId=12346&type=2
    private void getCommentCount(final TextView comment, String newsId) {
        Commrequest.queryCommentCount(context, newsId, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                //{"resCode":"200","resMsg":"success","resObject":{"newsId":"17647","count":0}}
                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("resObject");
                    int count = jsonObject1.getInteger("count");
                    if (count == 0) {
                        comment.setText("0");
                    } else {
                        comment.setText(String.valueOf(count));
                    }
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {

            }
        });
    }

}
