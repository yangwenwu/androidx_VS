package com.lemon.video.fragment.me.collection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.recyclerview.widget.RecyclerView

import com.lemon.video.R
import com.lemon.video.fragment.me.collection.VideoCollectRecycleAdapter.ViewHolderOne

import java.util.ArrayList

/**
 * Created by Staff on 2017/6/29.
 */

class BookMarkAdapter(context: Context, mData: List<TouTiaoListBean>,private var isEditModel: Boolean) : VideoCollectRecycleAdapter(context, mData) {
    private var iselectedListener: IselectedListener? = null

    //                sb.append(touTiaoListBean.getCollectionId()).append(",");
    //                sb.append(touTiaoListBean.getId()).append(",");
    val selectIds: String
        get() {
            val sb = StringBuilder()
            for (touTiaoListBean in mData) {
                if (touTiaoListBean.isSelected()) {
                    sb.append(touTiaoListBean.getDataId()).append(",")
                }
            }
            val length = sb.length
            if (length > 0) sb.deleteCharAt(length - 1)
            return sb.toString()
        }

    //                ids.add(touTiaoListBean.getId());
    val selectIdsList: List<String>
        get() {
            val ids = ArrayList<String>()
            for (touTiaoListBean in mData) {
                if (touTiaoListBean.isSelected()) {
                    ids.add(touTiaoListBean.getDataId())
                }
            }
            return ids
        }

    fun setEditModel(editModel: Boolean) {
        isEditModel = editModel
        notifyDataSetChanged()
    }

    fun setIselectedListener(iselectedListener: IselectedListener?) {
        this.iselectedListener = iselectedListener

    }

    init {
//        setIselectedListener(iselectedListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_collect_list, parent, false)
        val vs = view.findViewById<View>(R.id.vs_content) as ViewStub
        var holder: RecyclerView.ViewHolder? = null

        vs.layoutResource = R.layout.video_search_collection_item2
        vs.inflate()
        //                view = LayoutInflater.from(context).inflate(R.layout.item_recycleview_type1, parent, false);
        holder = ViewHolderOne(view)
        return holder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is VideoCollectRecycleAdapter.ViewHolderSelected) {
            holder.ivSelected!!.visibility = if (isEditModel) View.VISIBLE else View.GONE
            if (isEditModel) {
                val ttlb = mData[position]
                holder.ivSelected!!.isSelected = ttlb.isSelected()
            }
        }
        super.onBindViewHolder(holder, position)
    }

    override fun onItemEventClick(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            //      如果是可编辑的模式
            if (isEditModel) {
                val ttlb = mData[position]
                ttlb.setSelected(!ttlb.isSelected())
                notifyDataSetChanged()
                calculateSelected()
            } else {
                if (onItemClickListener != null) onItemClickListener.OnItemClick(v, position)
            }
        }
        holder.itemView.setOnLongClickListener { v ->
            //      如果是可编辑的模式
            if (isEditModel) {
                val ttlb = mData[position]
                ttlb.setSelected(!ttlb.isSelected())
                notifyDataSetChanged()
                calculateSelected()
            } else {
                if (onItemClickListener != null)
                    onItemClickListener.OnItemLongClick(v, position)
            }
            true
        }
    }

    private fun calculateSelected() {
        var count = 0
        for (touTiaoListBean in mData) {
            if (touTiaoListBean.isSelected()) {
                count++
            }
        }
        if (iselectedListener != null) {
            iselectedListener!!.seleced(count)
        }

        //        setIselectedListener(new IselectedListener() {
        //            @Override
        //            public void seleced(int count) {
        //
        //            }
        //        });
    }


    interface IselectedListener {

        fun seleced(count: Int)


    }


}
