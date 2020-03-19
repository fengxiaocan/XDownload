package com.x.down.interfaces;

import com.x.down.net.Request;
import com.x.down.net.Response;

public interface XHttpInterceptor{
    Request request(Request request);

    Response response(Request request,Response response);
}
