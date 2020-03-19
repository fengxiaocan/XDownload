package com.x.down.net;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.x.down.XDownload;
import com.x.down.config.XConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class XConnection{
    private HttpURLConnection connection;


    private void init(URL url) throws IOException{
        connection = (HttpURLConnection)url.openConnection();
        XConfig config = XDownload.getInstance().getConfig();
        connection.setConnectTimeout(config.getRequestTimeout());
        connection.setReadTimeout(config.getReadTimeout());
        connection.setDoInput(true);
    }

    public XConnection(Request request) throws IOException{
        init(request.httpUrl().URL());
        connection.setRequestMethod(request.method());
        if("POST".equals(request.method())){
            connection.setDoOutput(true);
            connection.setUseCaches(false);
        }
        Headers headers = request.headers();
        if(headers != null){
            Set<String> names = headers.names();
            for(String name: names){
                connection.setRequestProperty(name,headers.get(name));
            }
        }
    }

    public void disconnect(){
        connection.disconnect();
    }

    public boolean usingProxy(){
        return connection.usingProxy();
    }

    public XConnection connect() throws IOException{
        connection.connect();
        return this;
    }

    public XConnection setFixedLengthStreamingMode(int contentLength){
        connection.setFixedLengthStreamingMode(contentLength);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public XConnection setFixedLengthStreamingMode(long contentLength){
        connection.setFixedLengthStreamingMode(contentLength);
        return this;
    }


    public XConnection setChunkedStreamingMode(int chunklen){
        connection.setChunkedStreamingMode(chunklen);
        return this;
    }

    public XConnection setRequestProperty(String key,String value){
        connection.setRequestProperty(key,value);
        return this;
    }

    public XConnection addRequestProperty(String key,String value){
        connection.addRequestProperty(key,value);
        return this;
    }

    public String getHeaderFieldKey(int n){
        return connection.getHeaderFieldKey(n);
    }

    public String getHeaderField(int n){
        return connection.getHeaderField(n);
    }

    public boolean getInstanceFollowRedirects(){
        return connection.getInstanceFollowRedirects();
    }

    public XConnection setInstanceFollowRedirects(boolean followRedirects){
        connection.setInstanceFollowRedirects(followRedirects);
        return this;
    }

    public String getRequestMethod(){
        return connection.getRequestMethod();
    }

    public XConnection setRequestMethod(String method) throws ProtocolException{
        connection.setRequestMethod(method);
        return this;
    }

    public int getResponseCode() throws IOException{
        return connection.getResponseCode();
    }

    public String getResponseMessage() throws IOException{
        return connection.getResponseMessage();
    }

    public long getHeaderFieldDate(String name,long Default){
        return connection.getHeaderFieldDate(name,Default);
    }

    public Permission getPermission() throws IOException{
        return connection.getPermission();
    }

    public InputStream getErrorStream(){
        return connection.getErrorStream();
    }

    public int getConnectTimeout(){
        return connection.getConnectTimeout();
    }

    public XConnection setConnectTimeout(int timeout){
        connection.setConnectTimeout(timeout);
        return this;
    }

    public int getReadTimeout(){
        return connection.getReadTimeout();
    }

    public XConnection setReadTimeout(int timeout){
        connection.setReadTimeout(timeout);
        return this;
    }

    public URL getURL(){
        return connection.getURL();
    }

    public int getContentLength(){
        return connection.getContentLength();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getContentLengthLong(){
        return connection.getContentLengthLong();
    }

    public String getContentType(){
        return connection.getContentType();
    }

    public String getContentEncoding(){
        return connection.getContentEncoding();
    }

    public long getExpiration(){
        return connection.getExpiration();
    }

    public long getDate(){
        return connection.getDate();
    }

    public long getLastModified(){
        return connection.getLastModified();
    }

    public String getHeaderField(String name){
        return connection.getHeaderField(name);
    }

    public Map<String,List<String>> getHeaderFields(){
        return connection.getHeaderFields();
    }

    public int getHeaderFieldInt(String name,int Default){
        return connection.getHeaderFieldInt(name,Default);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getHeaderFieldLong(String name,long Default){
        return connection.getHeaderFieldLong(name,Default);
    }

    public Object getContent() throws IOException{
        return connection.getContent();
    }

    public Object getContent(Class[] classes) throws IOException{
        return connection.getContent(classes);
    }

    public InputStream getInputStream() throws IOException{
        return connection.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException{
        return connection.getOutputStream();
    }

    public boolean getDoInput(){
        return connection.getDoInput();
    }

    public XConnection setDoInput(boolean doinput){
        connection.setDoInput(doinput);
        return this;
    }

    public boolean getDoOutput(){
        return connection.getDoOutput();
    }

    public XConnection setDoOutput(boolean dooutput){
        connection.setDoOutput(dooutput);
        return this;
    }

    public boolean getAllowUserInteraction(){
        return connection.getAllowUserInteraction();
    }

    public XConnection setAllowUserInteraction(boolean allowuserinteraction){
        connection.setAllowUserInteraction(allowuserinteraction);
        return this;
    }

    public boolean getUseCaches(){
        return connection.getUseCaches();
    }

    public XConnection setUseCaches(boolean usecaches){
        connection.setUseCaches(usecaches);
        return this;
    }

    public long getIfModifiedSince(){
        return connection.getIfModifiedSince();
    }

    public XConnection setIfModifiedSince(long ifmodifiedsince){
        connection.setIfModifiedSince(ifmodifiedsince);
        return this;
    }

    public boolean getDefaultUseCaches(){
        return connection.getDefaultUseCaches();
    }

    public XConnection setDefaultUseCaches(boolean defaultusecaches){
        connection.setDefaultUseCaches(defaultusecaches);
        return this;
    }

    public String getRequestProperty(String key){
        return connection.getRequestProperty(key);
    }


    public Map<String,List<String>> getRequestProperties(){
        return connection.getRequestProperties();
    }
}
