package com.x.down.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Headers{
    private Map<String,String> maps;

    public Headers(){
        this.maps = new LinkedHashMap<>();
    }

    private Headers(Builder builder){
        maps = builder.maps;
    }

    public Set<String> names(){
        return maps.keySet();
    }

    public String get(String name){
        return maps.get(name);
    }

    public boolean hasName(String name){
        return maps.containsKey(name);
    }

    public Collection<String> values(){
        return maps.values();
    }

    public int size(){
        return maps.size();
    }

    public Builder newBuilder(){
        return new Builder(this);
    }

    public static final class Builder{
        private Map<String,String> maps;

        public Builder(){
            maps = new HashMap<>();
        }

        Builder(Headers headers){
            maps = headers.maps;
            if(maps == null){
                maps = new LinkedHashMap<>();
            }
        }

        public Builder add(String name,String value){
            maps.put(name,value);
            return this;
        }

        public Builder addAll(Headers headers){
            if(headers != null){
                maps.putAll(headers.maps);
            }
            return this;
        }

        public Builder remove(String name){
            maps.remove(name);
            return this;
        }

        public Builder clear(){
            maps.clear();
            return this;
        }
        public int size(){
            return maps.size();
        }
        public Headers build(){
            return new Headers(this);
        }
    }
}
