package com.xjava.down.data;

import com.xjava.down.base.IoSink;
import com.xjava.down.base.RequestBody;
import com.xjava.down.tool.XDownUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultipartBody implements RequestBody{
    private byte[] COLONSPACE=new byte[]{':',' '};
    private byte[] CRLF=new byte[]{'\r','\n'};
    private byte[] DASHDASH=new byte[]{'-','-'};

    private String boundary;
    private MediaType type;
    private List<Part> parts;

    public MultipartBody(String boundary,MediaType type){
        this.boundary=boundary;
        this.type=type;
        this.parts=new ArrayList<>();
    }

    public MultipartBody(MediaType type){
        this.type=type;
        this.boundary=XDownUtils.getMd5(System.currentTimeMillis()+"xDown");
        this.parts=new ArrayList<>();
    }

    public MultipartBody(){
        this.type=MediaType.FORM;
        this.boundary=XDownUtils.getMd5(System.currentTimeMillis()+"xDown");
        this.parts=new ArrayList<>();
    }

    public MultipartBody addPart(Part part){
        parts.add(part);
        return this;
    }

    @Override
    public MediaType contentType(){
        return MediaType.parse(type.getType()+"; boundary="+boundary);
    }

    @Override
    public long contentLength(){
        return -1L;
    }

    @Override
    public void writeTo(IoSink io) throws IOException{

        for(Part part: parts){
            Headers headers=part.headers;
            RequestBody body=part.body;

            io.writeBytes(DASHDASH).writeUtf8(boundary).writeBytes(CRLF);

            if(headers!=null){
                for(String name: headers.keySet()){
//                    System.out.println(name);
//                    System.out.println(headers.getValue(name));
                    io.writeUtf8(name).writeBytes(COLONSPACE).writeUtf8(headers.getValue(name)).writeBytes(CRLF);
                }
            }

            MediaType contentType=body.contentType();
            if(contentType!=null){
//                System.out.println(contentType.toString());
                io.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).writeBytes(CRLF);
            }

            long contentLength=body.contentLength();
            if(contentLength!=-1L){
                io.writeUtf8("Content-Length: ").writeBytes(String.valueOf(contentLength).getBytes()).writeBytes(CRLF);
            }

            io.writeBytes(CRLF);

            body.writeTo(io);

            io.writeBytes(CRLF);
        }

        io.writeBytes(DASHDASH);
        io.writeUtf8(boundary);
        io.writeBytes(DASHDASH);
        io.writeBytes(CRLF);
    }

    public static class Part{
        Headers headers;
        RequestBody body;

        private Part(RequestBody body){
            headers=new Headers();
            headers.addHeader("Unexpected header","Content-Type");
            headers.addHeader("Unexpected header","Content-Length");
            this.body=body;
        }

        private Part(Headers headers,RequestBody body){
            this.headers=headers;
            this.body=body;
            if(!headers.containsKey("Content-Type")){
                headers.addHeader("Unexpected header","Content-Type");
            }
            if(!headers.containsKey("Content-Length")){
                headers.addHeader("Unexpected header","Content-Length");
            }
        }

        public static Part createFormData(String paramsName,String fileName,RequestBody body){
            StringBuilder builder=new StringBuilder("form-data; name=");
            appendQuotedString(builder,paramsName);
            if(fileName!=null){
                builder.append("; filename=");
                appendQuotedString(builder,fileName);
            }
            Headers headers=new Headers();
            headers.addHeader("Content-Disposition",builder.toString());
            return new Part(headers,body);
        }

        public static Part createFormData(String paramsName,String value){
            return createFormData(paramsName,null,RequestBody.Body.create(value));
        }

        private static void appendQuotedString(StringBuilder builder,String value){
            builder.append('"');
            char[] chars=value.toCharArray();
            for(char ch: chars){
                if('\n'==ch){
                    builder.append("%0A");
                } else if('\r'==ch){
                    builder.append("%0D");
                } else if('"'==ch){
                    builder.append("%22");
                } else{
                    builder.append(ch);
                }
            }
            builder.append('"');
        }
    }
}
