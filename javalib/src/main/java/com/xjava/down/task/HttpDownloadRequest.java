package com.xjava.down.task;

import com.xjava.down.made.AutoRetryRecorder;
import com.xjava.down.tool.XDownUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class HttpDownloadRequest extends BaseHttpRequest{

    public HttpDownloadRequest(AutoRetryRecorder autoRetryRecorder){
        super(autoRetryRecorder);
    }

    protected boolean readInputStream(InputStream is,OutputStream os) throws IOException{
        try{
            byte[] bytes=new byte[1024*10];
            int length;
            while((length=is.read(bytes))>0){
                if(isCancel){
                    onCancel();
                    return false;
                }
                os.write(bytes,0,length);
                os.flush();
                onProgress(length);
            }
            return true;
        } finally{
            XDownUtils.closeIo(is);
            XDownUtils.closeIo(os);
        }
    }

    protected void onProgress(int length){ }

    protected boolean copyFile(File inputFile,File outputFile,boolean isDelect) throws IOException{
        if(inputFile==null||outputFile==null){
            return false;
        }
        if(!inputFile.exists()){
            return false;
        }
        if(inputFile==outputFile){
            return true;
        }
        if(inputFile.getAbsolutePath().equals(outputFile.getAbsolutePath())){
            return true;
        }
        if(outputFile.exists()&&outputFile.length()==inputFile.length()){
            if(isDelect){inputFile.delete();}
            return true;
        } else{
            inputFile.delete();
            FileOutputStream output=null;
            FileInputStream input=null;
            try{
                input=new FileInputStream(inputFile);
                output=new FileOutputStream(outputFile);

                byte[] bytes=new byte[1024*8];
                int length;
                while((length=input.read(bytes))>0){
                    output.write(bytes,0,length);
                    output.flush();
                }
                return true;
            } finally{
                XDownUtils.closeIo(input);
                XDownUtils.closeIo(output);
            }
        }
    }

}
