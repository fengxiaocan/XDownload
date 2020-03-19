package com.x.down.net;

import java.io.InputStream;

public abstract class RequestBody{

    public void close(){}

    public long contentLength(){
        return 0;
    }

    public byte[] bytes(){
        return null;
    }

    public MediaType contentType(){
        return null;
    }

    public String string(){
        return null;
    }

    public String byteString(){
        return null;
    }

    public InputStream stream(){
        return null;
    }

}
