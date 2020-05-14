package com.xjava.down.tool;

import com.xjava.down.XDownload;
import com.xjava.down.config.XConfig;
import com.xjava.down.core.XDownloadRequest;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;

public class XDownUtils{
    private static String getMd5(boolean lowerCase,byte[] btInput){
        char[] hexDigits;
        if(lowerCase){
            hexDigits=new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        } else{
            hexDigits=new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        }

        try{
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst=MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md=mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j=md.length;
            char[] str=new char[j*2];
            int k=0;
            for(int i=0;i<j;i++){
                byte byte0=md[i];
                str[k++]=hexDigits[byte0 >>> 4&0xf];
                str[k++]=hexDigits[byte0&0xf];
            }
            return new String(str);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getMd5(String msg){
        byte[] bytes=msg.getBytes();
        return getMd5(true,bytes);
    }

    public static void closeIo(Closeable closeable){
        try{
            if(closeable!=null){
                closeable.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void sleep(long ms)
    {
        long start=System.currentTimeMillis();
        long duration=ms;
        boolean interrupted=false;
        do{
            try{
                Thread.sleep(duration);
            } catch(InterruptedException e){
                interrupted=true;
            }
            duration=start+ms-System.currentTimeMillis();
        } while(duration>0);

        if(interrupted){
            Thread.currentThread().interrupt();
        }
    }


    public static void disconnectHttp(HttpURLConnection connection){
        if(connection!=null){
            try{
                connection.disconnect();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public static String getFileName(XDownloadRequest xDownloadRequest){
        if(!isStringEmpty(xDownloadRequest.getSaveFile())){
            return xDownloadRequest.getSaveFile();
        } else{
            int defaultName=XDownload.get().config().getDefaultName();
            switch(defaultName){
                default:
                case XConfig.DefaultName.MD5:
                    return xDownloadRequest.getIdentifier()+getFileSuffix(xDownloadRequest);
                case XConfig.DefaultName.TIME:
                    return System.currentTimeMillis()+getFileSuffix(xDownloadRequest);
                case XConfig.DefaultName.ORIGINAL:
                    final String url=xDownloadRequest.getConnectUrl();
                    final int index1=url.indexOf("?");
                    final String subUrl;
                    if(index1>0){
                        subUrl=url.substring(0,index1);
                    } else{
                        subUrl=url;
                    }
                    int index2=subUrl.lastIndexOf("/");
                    if(index2>0){
                        return subUrl.substring(index2+1);
                    } else{
                        return "";
                    }
            }
        }
    }

    public static String getFileSuffix(XDownloadRequest xDownloadRequest){
        final String url=xDownloadRequest.getConnectUrl();
        final int index1=url.indexOf("?");
        final String subUrl;
        if(index1>0){
            subUrl=url.substring(0,index1);
        } else{
            subUrl=url;
        }
        int index2=subUrl.lastIndexOf(".");
        int index3=subUrl.lastIndexOf("/");
        if(index3>index2){
            return ".unknown";
        }
        if(index2>0){
            return subUrl.substring(index2);
        } else{
            return ".unknown";
        }
    }

    public static boolean isStringEmpty(String content){
        return content==null||content.equals("");
    }


    public static boolean writeObject(File file,Object obj){
        ObjectOutputStream stream=null;
        try{
            stream=new ObjectOutputStream(new FileOutputStream(file,false));
            stream.writeObject(obj);
            stream.flush();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        } finally{
            XDownUtils.closeIo(stream);
        }
    }

    public static <T> T readObject(File file){
        ObjectInputStream stream=null;
        try{
            stream=new ObjectInputStream(new FileInputStream(file));
            Object object=stream.readObject();
            return (T)object;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        } finally{
            XDownUtils.closeIo(stream);
        }
    }

    /**
     * 获取最终保存的文件
     *
     * @return
     */
    public static File getSaveFile(XDownloadRequest request){
        if(XDownUtils.isStringEmpty(request.getSaveFile())){
            return new File(request.getCacheDir(),XDownUtils.getFileName(request));
        } else{
            return new File(request.getSaveFile());
        }
    }

    /**
     * 保存临时文件的文件夹
     *
     * @return
     */
    public static File getTempCacheDir(XDownloadRequest request){
        //保存路径
        String saveDir=request.getCacheDir();
        //获取MD5
        String md5=request.getIdentifier();
        File dir=new File(saveDir,md5);
        dir.mkdirs();
        return dir;
    }

    /**
     * 获得临时文件的文件名
     *
     * @return
     */
    public static File getTempFile(XDownloadRequest request){
        //保存路径
        if(XDownUtils.isStringEmpty(request.getSaveFile())){
            //没有设置保存文件名
            return new File(request.getCacheDir(),XDownUtils.getFileName(request));
        } else{
            return new File(request.getCacheDir(),request.getIdentifier()+"_temp");
        }
    }


    public static void delectDir(File dir){
        if(dir==null){
            return;
        }
        File[] files=dir.listFiles();
        if(files!=null){
            for(File file1: files){
                if(file1.isDirectory()){
                    delectDir(file1);
                }
                file1.delete();
            }
        }
        dir.delete();
    }

    public static long getContentLength(HttpURLConnection http){
        try{
            return Long.parseLong(http.getHeaderField("Content-Length"));
        } catch(Exception e){}
        return 0;
    }
}
