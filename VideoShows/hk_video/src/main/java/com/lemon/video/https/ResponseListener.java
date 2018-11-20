package com.lemon.video.https;


import com.lemon.video.entrys.BaseJsonBean;

public interface ResponseListener {

    void onResponse(BaseJsonBean t, int code);

    /**
    判断t 不为null 并且 t.date 也不不为空，就表示是取的缓存数据
    */
    void onFailure(BaseJsonBean t, String errMessage);
}
