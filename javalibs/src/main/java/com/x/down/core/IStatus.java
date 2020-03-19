package com.x.down.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.x.down.core.IStatus.COMPLETE;
import static com.x.down.core.IStatus.CONTECT;
import static com.x.down.core.IStatus.DOWNING;
import static com.x.down.core.IStatus.ERROR;
import static com.x.down.core.IStatus.INVALID;
import static com.x.down.core.IStatus.PAUSE;
import static com.x.down.core.IStatus.PENGING;
import static com.x.down.core.IStatus.RETRY;
import static com.x.down.core.IStatus.START;

@IntDef({INVALID,PENGING,START,CONTECT,DOWNING,COMPLETE,PAUSE,ERROR,RETRY})
@Retention(RetentionPolicy.SOURCE)
public @interface IStatus{
    int INVALID = 0;
    int PENGING = 1;
    int START = 2;
    int CONTECT = 3;
    int DOWNING = 4;
    int COMPLETE = 5;
    int PAUSE = 6;
    int ERROR = 7;
    int RETRY = 8;
}
