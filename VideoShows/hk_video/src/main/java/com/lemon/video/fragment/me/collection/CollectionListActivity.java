package com.lemon.video.fragment.me.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.application.BaseApp;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.common.Commrequest;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.fragment.video.activity.VideoDetailActivity;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.HomeNewsBean;
import com.lemon.video.model.UserBean;
import com.lemon.video.utils.NetWorkUtil;
import com.lemon.video.utils.SPUtils;
import com.lemon.video.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CollectionListActivity extends BaseAppActivity implements VideoCollectListRecycleAdapter.IselectedListener, View.OnClickListener {

    private ImageView barIvLeft;
    private TextView barTvRight;
    //    private XRecyclerView xrlList;
    private RecyclerView recyclerview;
    private SmartRefreshLayout refreshLayout;


    private TextView tvDelete;

    private VideoCollectListRecycleAdapter mCollectListRecycleAdapter;
    private List<TouTiaoListBean> tbList;
    private boolean isEditModel;
    private View empty_view;

    private FrameLayout loading;
    //菊花
    private ProgressBar loading_progress;
    //加载失败的默认图
    private TextView refresh;
    //没有网络加载失败图
    private LinearLayout result_view;

    private int totalCount = 0;
    private int page = 1;
    private boolean noMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_collection_list);
        setContentView(R.layout.bookmark_layout);
        init();
    }

    private void init() {
        barIvLeft = (ImageView) findViewById(R.id.bar_iv_left);
        barIvLeft.setOnClickListener(this);
        barTvRight = (TextView) findViewById(R.id.bar_tv_right);
        barTvRight.setOnClickListener(this);
//        xrlList =(XRecyclerView) findViewById(R.id.xrl_list);
        recyclerview = findViewById(R.id.recyclerview);

        ClassicsHeader classicsHeader = findViewById(R.id.classicheader);
        classicsHeader.setEnableLastTime(false);
        refreshLayout = findViewById(R.id.refreshLayout);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(this);
        //************* loading布局
        //************* loading布局
        loading = (FrameLayout) findViewById(R.id.loading);
        //加载失败的结果视图
        result_view = (LinearLayout) findViewById(R.id.result_view);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        refresh = (TextView) findViewById(R.id.refresh);
        //************************
        //************************
        empty_view = findViewById(R.id.empty_view);

        tbList = new ArrayList<>();
//        View adview = LayoutInflater.from(this).inflate(R.layout.header_advertisement_view, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerview.setLayoutManager(layoutManager);
        mCollectListRecycleAdapter = new VideoCollectListRecycleAdapter(this, tbList, isEditModel);
        recyclerview.setAdapter(mCollectListRecycleAdapter);

//        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_advertisement_view, findViewById(android.R.id.content),false);
//        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_advertisement_view, container,false);
//        xrlList.addHeaderView(header);
//        xrlList.addHeaderView(adview);
//        xrlList.setLoadingListener(loadingListener);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                queryDate(1, true, false);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                queryDate(page, true, false);
            }
        });

        mCollectListRecycleAdapter.setOnItemClickListener(itemClickListener);
        mCollectListRecycleAdapter.setIselectedListener(this);

        queryDate(1, true, false);
    }

    //    @OnClick({R.id.bar_iv_left, R.id.bar_tv_right, R.id.tv_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_iv_left:
                finish();
                break;
            case R.id.bar_tv_right:
                isEditModel = !isEditModel;
                barTvRight.setText(isEditModel ? "Done" : "Edit");
                mCollectListRecycleAdapter.setEditModel(isEditModel);
                tvDelete.setVisibility(View.GONE);
                break;
            case R.id.tv_delete:
                clickDeletIds();
                break;
        }
    }

    private void clickDeletIds() {
//        List<String> ids = mCollectListRecycleAdapter.getSelectIdsList();
//        System.out.print(ids);
//        deleteItems(ids);
        //删除接口，一个或者多个
//        deleteId(ids);

        String delIds = mCollectListRecycleAdapter.getSelectIds();
        deleteId(delIds);
    }

    private void deleteId(String ids) {
        String userId = "";
        if (BaseApp.isLogin) {
            userId = BaseApp.getUserBean().id;
        } else {
            return;
        }
        Commrequest.deLCollectionList(CollectionListActivity.this, userId, ids, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {

                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")) {
                    JSONObject resObject = jsonObject.getJSONObject("resObject");
                    tvDelete.setText("Delete( " + 0 + " )");
                    page = 1;
//                    if (noMore){
//                        noMore = false;
//                        xrlList.setLoadingMoreEnabled(true);
//                    }
                    queryDate(1, true, true);
                } else {
                    ToastUtils.showShort(CollectionListActivity.this, "Delete failed");
                }

            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                ToastUtils.showShort(CollectionListActivity.this, "Delete failed");
            }
        });
    }


    private void deleteItems(List<String> ids) {
//        if (ids.size() > 0) {
//            for (int i = 0; i < ids.size(); i++) {
//                newsBeanManager.deleteById(ids.get(i));
//            }
//        }
        tvDelete.setText("Delete( " + 0 + " )");
        queryDate(1, true, true);
    }

    boolean deleteOK = false;

    //查询收藏列表内容
    private void queryDate(final int page, final boolean isRefresh, final boolean delete) {
        String userStr = (String) SPUtils.get(CollectionListActivity.this, "user", "");
        UserBean userBean = JSON.parseObject(userStr, UserBean.class);
        String userId = userBean.id;   //  d63d251b-b7b1-11e7-9872-00155d03d036
        Commrequest.queryCollectionList(CollectionListActivity.this, userId, page, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")) {
                    if (delete) {
                        deleteOK = true;
                    }

                    JSONObject resObject = jsonObject.getJSONObject("resObject");
                    JSONArray jsonArray = resObject.getJSONArray("dateList");
                    List<TouTiaoListBean> SQLlist = new ArrayList<TouTiaoListBean>();
                    if (jsonArray.size() > 0) {
                        totalCount = resObject.getIntValue("totalCount");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            TouTiaoListBean homeNewsBean = JSON.parseObject(jsonObject1.toJSONString(), TouTiaoListBean.class);
                            SQLlist.add(homeNewsBean);
                        }
                        Message message = new Message();
                        message.obj = SQLlist;
                        if (page == 1) {
                            tbList.clear();
                            message.what = 2;
                        } else {
                            message.what = 5;
                        }
                        handler.sendMessage(message);
                    } else {
                        if (page == 1){
                            tbList.clear();
                            mCollectListRecycleAdapter.notifyDataSetChanged();
                            barTvRight.setVisibility(View.INVISIBLE);
                            tvDelete.setVisibility(View.GONE);
//                        xrlList.setEmptyView(empty_view);
                            loading.setVisibility(View.GONE);
                        }else{
                            refreshLayout.finishLoadMore();
                        }


                    }
                } else {
                    ToastUtils.showShort(CollectionListActivity.this, "Load failed");
                }

                if (isRefresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                handler.sendEmptyMessage(1);
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tbList.clear();
                    mCollectListRecycleAdapter.notifyDataSetChanged();
                    barTvRight.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    setLoadingStatus();
                    break;
                case 2:
                    page++;
                    List<TouTiaoListBean> SQLlist = (List<TouTiaoListBean>) msg.obj;
                    if (SQLlist.size() > 0) {
                        List<TouTiaoListBean> queryList = new ArrayList<>();
                        for (int i = 0; i < SQLlist.size(); i++) {
                            TouTiaoListBean touTiaoListBean = SQLlist.get(i);
                            queryList.add(touTiaoListBean);
                        }
                        barTvRight.setVisibility(View.VISIBLE);
                        tbList.addAll(queryList);
                        loading.setVisibility(View.GONE);
                        mCollectListRecycleAdapter.notifyDataSetChanged();
//                        if (tbList.size() == totalCount){
//                            xrlList.setLoadingMoreEnabled(false);
//                            noMore = true;
//                        }
                        if (deleteOK) {
                            deleteOK = false;
                            isEditModel = !isEditModel;
                            barTvRight.setText(isEditModel ? "Done" : "Edit");
                            mCollectListRecycleAdapter.setEditModel(isEditModel);
                            tvDelete.setVisibility(View.GONE);
                        }

                    } else {
                        barTvRight.setVisibility(View.INVISIBLE);
                        tvDelete.setVisibility(View.GONE);
//                        xrlList.setEmptyView(empty_view);
                        loading.setVisibility(View.GONE);
                        mCollectListRecycleAdapter.notifyDataSetChanged();
                    }
                    break;
                case 5:
                    page++;
                    List<TouTiaoListBean> SQLlist2 = (List<TouTiaoListBean>) msg.obj;
                    if (SQLlist2.size() > 0) {
                        List<TouTiaoListBean> queryList = new ArrayList<>();
                        for (int i = 0; i < SQLlist2.size(); i++) {
                            TouTiaoListBean touTiaoListBean = SQLlist2.get(i);
                            queryList.add(touTiaoListBean);
                        }
                        barTvRight.setVisibility(View.VISIBLE);
//                        xrlList.loadMoreComplete();
                        tbList.addAll(queryList);
                        loading.setVisibility(View.GONE);
                        mCollectListRecycleAdapter.notifyDataSetChanged();
//                        if (tbList.size() == totalCount){
//                            xrlList.setLoadingMoreEnabled(false);
//                            noMore = true;
//                        }

                    } else {
                        barTvRight.setVisibility(View.INVISIBLE);
                        tvDelete.setVisibility(View.GONE);
//                        xrlList.setEmptyView(empty_view);
                        loading.setVisibility(View.GONE);
                        mCollectListRecycleAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };


    /***
     * 没有缓存，加载失败
     */
    private void setLoadingStatus() {
        loading_progress.setVisibility(View.GONE);
        result_view.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //状态
                loading_progress.setVisibility(View.VISIBLE);
                result_view.setVisibility(View.GONE);
                if (NetWorkUtil.isNetworkAvailable(CollectionListActivity.this)) {
                    queryDate(1, true, false);
                } else {
                    loading_progress.setVisibility(View.GONE);
                    result_view.setVisibility(View.VISIBLE);
                    ToastUtils.showShort(CollectionListActivity.this, getResources().getString(R.string.net_error));
                }
            }
        });
    }


    @Override
    public void seleced(int count) {
        tvDelete.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        if (count > 0) tvDelete.setText("Delete ( " + count + " )");
    }


    //    NewsRecycleAdapter.OnItemClickListener itemClickListener = new NewsRecycleAdapter.OnItemClickListener() {
    VideoCollectListRecycleAdapter.OnItemClickListener itemClickListener = new VideoCollectListRecycleAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            TouTiaoListBean ttb = mCollectListRecycleAdapter.mData.get(position);//经过具体测试，这里应该是添加了一头和一尾，所以position需要-2
            HomeNewsBean homeNewsBean = new HomeNewsBean();
            homeNewsBean.id = ttb.getId();
            homeNewsBean.subjectCode = ttb.getSubjectCode();
            homeNewsBean.bigTitleImage = ttb.getBig_title_image();
            homeNewsBean.titleImage = ttb.getTitle_image();
            homeNewsBean.dataId = ttb.getDataId();
            homeNewsBean.description = ttb.getDescription();
//            HomeNewsBean ttb = videoList.get(position);//经过具体测试，这里应该是添加了一头和一尾，所以position需要-2
            String subjectCode = ttb.subjectCode;
            Goto(subjectCode, homeNewsBean, VideoDetailActivity.class);

        }

        @Override
        public void OnItemLongClick(View view, int position) {

        }
    };

    private void Goto(String code, HomeNewsBean bean, Class activity) {
        Intent i = new Intent(CollectionListActivity.this, activity);
        i.putExtra("code", code);
        i.putExtra("bean", bean);
        startActivity(i);
    }

}
