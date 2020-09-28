package com.x.down.listener;

import com.x.down.base.IRequest;
import com.x.down.data.Response;

public interface OnResponseListener {
    void onResponse(IRequest request, Response response);

    void onError(IRequest request, Exception exception);
}
