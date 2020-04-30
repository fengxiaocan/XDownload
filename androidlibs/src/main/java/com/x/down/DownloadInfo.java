package com.x.down;

 class DownloadInfo{
    private int id;
    private String tag;
    private String url;
    private String save;
    private String header;
    private String params;
    private String filename;
    private String md5;
    private int status;
    private int block;
    private long total;
    private long sofar;
    private int[] progress;

    public long getTotal(){
        return total;
    }

    public void setTotal(long total){
        this.total=total;
    }

    public long getSofar(){
        return sofar;
    }

    public void setSofar(long sofar){
        this.sofar=sofar;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag=tag;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public String getSave(){
        return save;
    }

    public void setSave(String save){
        this.save=save;
    }

    public String getHeader(){
        return header;
    }

    public void setHeader(String header){
        this.header=header;
    }

    public String getParams(){
        return params;
    }

    public void setParams(String params){
        this.params=params;
    }

    public String getFilename(){
        return filename;
    }

    public void setFilename(String filename){
        this.filename=filename;
    }

    public String getMd5(){
        return md5;
    }

    public void setMd5(String md5){
        this.md5=md5;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status=status;
    }

    public int getBlock(){
        return block;
    }

    public void setBlock(int block){
        this.block=block;
    }

    public int[] getProgress(){
        return progress;
    }

    public void setProgress(int[] progress){
        this.progress=progress;
    }
}
