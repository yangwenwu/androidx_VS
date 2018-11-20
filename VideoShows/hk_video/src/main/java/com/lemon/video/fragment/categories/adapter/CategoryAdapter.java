package com.lemon.video.fragment.categories.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemon.video.R;
import com.lemon.video.application.GlideApp;
import com.lemon.video.model.CategoryListBean;
import com.lemon.video.utils.DeviceConfig;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<CategoryListBean> mData;
    protected Context context;
    protected OnItemClickListener onItemClickListener;

    public CategoryAdapter(Context context, List<CategoryListBean> mData) {
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
        view = LayoutInflater.from(context).inflate(R.layout.category_item_layout, parent, false);
        holder = new ViewHolderOne(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolderOne holderOne = (ViewHolderOne) holder;
        CategoryListBean categoryListBean = mData.get(position);
        AssetManager mgr = context.getAssets();
        //根据路径得到Typeface
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Lato-Heavy.ttf");
        holderOne.category_title.setTypeface(tf);
        holderOne.category_title.setText(categoryListBean.name);
        DeviceConfig.reinstallScreenSize(context);
        int width = DeviceConfig.getDeviceWidth() - (int) context.getResources().getDimension(R.dimen.padding_medium2) * 2;
        //int hight = (int) context.getResources().getDimension(R.dimen.height_all_img);

        ViewGroup.LayoutParams laParams = (ViewGroup.LayoutParams) holderOne.category_image.getLayoutParams();
//                laParams.height = width * 2 / 3;
        laParams.height = width/2 * 4/5;
        laParams.width = width/2;
        holderOne.category_image.setLayoutParams(laParams);
//        holderOne.category_image.setImageResource(CategoryListBean.image);
        GlideApp.with(context)
                .load(categoryListBean.imageUrl)
                .placeholder(R.mipmap.news_big_default)
                .error(R.mipmap.news_big_default)
                .centerCrop()
                .into(holderOne.category_image);

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
        private ImageView category_image;
        private TextView category_title;

        public ViewHolderOne(View itemView) {
            super(itemView);
            category_image = (ImageView) itemView.findViewById(R.id.category_image);
            category_title = (TextView) itemView.findViewById(R.id.category_title);
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
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

    }


}
