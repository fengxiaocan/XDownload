package com.x.down.net;

import com.x.down.interfaces.HttpUrl;

public class Request{
    private Object tag;
    private String method;
    private HttpUrl url;
    private Headers headers;
    private RequestBody requestBody;

    private Request(Builder builder){
        this.tag = builder.tag;
        this.method = builder.method;
        this.url = builder.url;
        if(builder.headerBuilder != null){
            this.headers = builder.headerBuilder.build();
        }
        this.requestBody = builder.requestBody;
    }

    public Object tag(){
        return tag;
    }

    public String method(){
        return method;
    }

    public HttpUrl httpUrl(){
        return url;
    }

    public Headers headers(){
        return headers;
    }

    public RequestBody requestBody(){
        return requestBody;
    }

    public Builder newBuilder(){
        return new Builder(this);
    }

    public static final class Builder{
        private Object tag;
        private String method;
        private HttpUrl url;
        private Headers.Builder headerBuilder;
        private RequestBody requestBody;

        public Builder(){
        }

        Builder(Request request){
            tag = request.tag;
            method = request.method;
            url = request.url;
            if(request.headers != null){
                headerBuilder = request.headers.newBuilder();
            }
            requestBody = request.requestBody;
        }

        public Builder method(String method,RequestBody requestBody){
            this.method = method;
            this.requestBody = requestBody;
            return this;
        }

        public Builder get(){
            this.method = "GET";
            this.requestBody = null;
            return this;
        }

        public Builder put(RequestBody requestBody){
            this.method = "PUT";
            this.requestBody = requestBody;
            return this;
        }

        public Builder head(){
            this.method = "HEAD";
            this.requestBody = null;
            return this;
        }

        public Builder delete(){
            this.method = "DELETE";
            this.requestBody = null;
            return this;
        }

        public Builder delete(RequestBody requestBody){
            this.method = "DELETE";
            this.requestBody = requestBody;
            return this;
        }

        public Builder trace(){
            this.method = "TRACE";
            this.requestBody = null;
            return this;
        }

        public Builder options(){
            this.method = "OPTIONS";
            this.requestBody = null;
            return this;
        }

        public Builder post(RequestBody requestBody){
            this.method = "POST";
            this.requestBody = requestBody;
            return this;
        }

        public Builder url(HttpUrl url){
            this.url = url;
            return this;
        }

        public Builder headers(Headers headers){
            if(headers != null){
                if(headerBuilder == null){
                    headerBuilder = headers.newBuilder();
                } else{
                    headerBuilder.addAll(headers);
                }
            }
            return this;
        }

        public Builder addHeader(String name,String value){
            if(headerBuilder == null){
                headerBuilder = new Headers.Builder();
            }
            headerBuilder.add(name,value);
            return this;
        }

        public Request build(){
            return new Request(this);
        }
    }
}
