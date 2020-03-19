package com.x.down.impl;

import com.x.down.interfaces.HttpUrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpUrlImpl implements HttpUrl{
    private URL URL;
    private String url;

    public HttpUrlImpl(java.net.URL URL,String url){
        this.URL = URL;
        this.url = url;
    }

    public String getQuery(){
        return URL.getQuery();
    }

    public String getPath(){
        return URL.getPath();
    }

    public String getUserInfo(){
        return URL.getUserInfo();
    }

    public String getAuthority(){
        return URL.getAuthority();
    }

    public int getPort(){
        return URL.getPort();
    }

    public int getDefaultPort(){
        return URL.getDefaultPort();
    }

    public int setDefaultPort(){
        return URL.getDefaultPort();
    }

    public String getProtocol(){
        return URL.getProtocol();
    }

    public String getHost(){
        return URL.getHost();
    }

    public String getFile(){
        return URL.getFile();
    }

    public String getRef(){
        return URL.getRef();
    }

    public String toExternalForm(){
        return URL.toExternalForm();
    }

    @Override
    public URI URI() throws URISyntaxException{
        return URL.toURI();
    }

    @Override
    public URL URL(){
        return URL;
    }

    @Override
    public String url(){
        return url;
    }

    @Override
    public HttpUrl.Builder newBuilder(){
        return new Builder(this);
    }

    protected static class Builder extends HttpUrl.Builder{
        private Builder(HttpUrl httpUrl){
            super(httpUrl);
        }
    }
}
