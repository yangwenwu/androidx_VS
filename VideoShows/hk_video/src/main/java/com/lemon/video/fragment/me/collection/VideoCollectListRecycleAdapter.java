package com.lemon.video.fragment.me.collection;

import android.content.Context;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.lemon.video.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Staff on 2017/6/29.
 */

public class VideoCollectListRecycleAdapter extends VideoCollectRecycleAdapter {

    /**
     * RecycleViewAdapter 要素点：
     * <p/>
     * 1，ViewHolder必须继承RecyclerView.ViewHolder
     * 2，RecycleView.Adapter的泛型为自定义ViewHolder
     */
    private boolean isEditModel;
    private IselectedListener iselectedListener;

    public void setEditModel(boolean editModel) {
        isEditModel = editModel;
        notifyDataSetChanged();
    }

    public void setIselectedListener(IselectedListener iselectedListener) {
        this.iselectedListener = iselectedListener;

    }

    public VideoCollectListRecycleAdapter(Context context, List<TouTiaoListBean> mData, boolean isEditModel) {
        super(context, mData);
        this.isEditModel = isEditModel;
        setIselectedListener(iselectedListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_list, parent, false);
        ViewStub vs = (ViewStub) view.findViewById(R.id.vs_content);
        RecyclerView.ViewHolder holder = null;

        vs.setLayoutResource(R.layout.video_search_collection_item2);
        vs.inflate();
//                view = LayoutInflater.from(context).inflate(R.layout.item_recycleview_type1, parent, false);
        holder = new ViewHolderOne(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderSelected) {
            ViewHolderSelected selected = (ViewHolderSelected) holder;
            selected.ivSelected.setVisibility(isEditModel ? View.VISIBLE : View.GONE);
            if (isEditModel) {
                final TouTiaoListBean ttlb = mData.get(position);
                selected.ivSelected.setSelected(ttlb.isSelected());
            }else{
                final TouTiaoListBean ttlb = mData.get(position);
                ttlb.setSelected(false);
                selected.ivSelected.setSelected(false);
            }
        }
        super.onBindViewHolder(holder, position);
    }

    protected void onItemEventClick(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      如果是可编辑的模式
                if (isEditModel) {
                    final TouTiaoListBean ttlb = mData.get(position);
                    ttlb.setSelected(!ttlb.isSelected());
                    notifyDataSetChanged();
                    calculateSelected();
                } else {
                    if (onItemClickListener != null) onItemClickListener.OnItemClick(v, position);
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //      如果是可编辑的模式
                if (isEditModel) {
                    final TouTiaoListBean ttlb = mData.get(position);
                    ttlb.setSelected(!ttlb.isSelected());
                    notifyDataSetChanged();
                    calculateSelected();
                } else {
                    if (onItemClickListener != null)
                        onItemClickListener.OnItemLongClick(v, position);
                }
                return true;
            }
        });
    }

    private void calculateSelected() {
        int count = 0;
        for (TouTiaoListBean touTiaoListBean : mData) {
            if (touTiaoListBean.isSelected()) {
                count++;
            }
        }
        if (iselectedListener != null) {
            iselectedListener.seleced(count);
        }

//        setIselectedListener(new IselectedListener() {
//            @Override
//            public void seleced(int count) {
//
//            }
//        });
    }

    public String getSelectIds() {
        StringBuilder sb = new StringBuilder();
        for (TouTiaoListBean touTiaoListBean : mData) {
            if (touTiaoListBean.isSelected()) {
//                sb.append(touTiaoListBean.getCollectionId()).append(",");
//                sb.append(touTiaoListBean.getId()).append(",");
                sb.append(touTiaoListBean.getDataId()).append(",");
            }
        }
        int length = sb.length();
        if (length > 0) sb.deleteCharAt(length - 1);
        return sb.toString();
    }

    public List<String> getSelectIdsList() {
        List<String> ids = new ArrayList<>();
        for (TouTiaoListBean touTiaoListBean : mData) {
            if (touTiaoListBean.isSelected()) {
//                ids.add(touTiaoListBean.getId());
                ids.add(touTiaoListBean.getDataId());
            }
        }
        return ids;
    }


    public interface IselectedListener {

        void seleced(int count);


    }



}
