package com.xjava.down.base;

public @interface RequestStatus{
    int INVALID=0;
    int PENDING=1;//已创建未开始
    int STARTED=2;//开始
    int CONNECTED=3;//链接中
    int PAUSED=4;//暂停
    int COMPLETED=5;//完成
    int RETRY=6;//重试中
    int WARN=-1;//出错
    int ERROR=-2;//失败
}
