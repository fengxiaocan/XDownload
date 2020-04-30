package com.xjava.down.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Headers{
    private HashMap<String,String> headerMap;

    public Headers(){
        headerMap=new HashMap<>();
    }

    public Headers(Headers other){
        this.headerMap=other.headerMap;
    }

    public Headers clear(){
        headerMap.clear();
        return this;
    }

    public Headers addHeader(String name,String value){
        headerMap.put(name,value);
        return this;
    }

    public Headers removeHeader(String name){
        headerMap.remove(name);
        return this;
    }

    public String getValue(String name){
        return headerMap.get(name);
    }

    public Set<String> keySet(){
        return headerMap.keySet();
    }

    public Collection<String> values(){
        return headerMap.values();
    }

    public int size(){
        return headerMap.size();
    }

    public boolean containsKey(String name){
        return headerMap.containsKey(name);
    }

    public boolean containsValue(String value){
        return headerMap.containsValue(value);
    }

    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        for(String key: headerMap.keySet()){
            builder.append(key);
            builder.append("=");
            builder.append(headerMap.get(key));
            builder.append("&");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
