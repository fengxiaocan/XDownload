package com.x.down.interfaces;

import com.x.down.impl.HttpUrlImpl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface HttpUrl{
    String getQuery();

    String getPath();

    String getUserInfo();

    String getAuthority();

    int getPort();

    int getDefaultPort();

    int setDefaultPort();

    String getProtocol();

    String getHost();

    String getFile();

    String getRef();

    String toExternalForm();

    URI URI() throws URISyntaxException;

    URL URL();

    String url();

    Builder newBuilder();

    class Builder{
        private String url;
        private URL URL;
        private Map<String,Object> query;

        public Builder(){
        }

        protected Builder(HttpUrl httpUrl){
            URL = httpUrl.URL();
            url = httpUrl.url();
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder URL(URL url){
            URL = url;
            return this;
        }

        private Builder query(String key,Object value){
            if(query == null){
                query = new LinkedHashMap<>();
            }
            query.put(key,value);
            return this;
        }

        public Builder addQuery(String key,String value){
            return query(key,value);
        }

        public Builder addQuery(String key,long value){
            return query(key,value);
        }

        public Builder addQuery(String key,int value){
            return query(key,value);
        }

        public Builder addQuery(String key,boolean value){
            return query(key,value);
        }

        public Builder addQuery(String key,float value){
            return query(key,value);
        }

        public Builder addQuery(String key,double value){
            return query(key,value);
        }

        public HttpUrl builder() throws MalformedURLException{
            if(url != null && query != null && query.size() > 0){
                StringBuilder builder = new StringBuilder(url);
                Set<String> keySet = query.keySet();
                if(! url.contains("?")){
                    builder.append("?");
                }
                for(String key: keySet){
                    builder.append(key);
                    builder.append("=");
                    builder.append(query.get(key));
                    builder.append("&");
                }
                builder.deleteCharAt(builder.length() - 1);
                String spec = builder.toString();
                return new HttpUrlImpl(new URL(URL,spec),spec);
            } else{
                return new HttpUrlImpl(new URL(URL,url),url);
            }
        }
    }
}
