package com.xjava.down.listener;

import com.xjava.down.base.IRequest;
import com.xjava.down.data.Response;

public interface OnResponseListener{
    void onResponse(IRequest request,Response response);

    void onError(IRequest request,Exception exception);
}
