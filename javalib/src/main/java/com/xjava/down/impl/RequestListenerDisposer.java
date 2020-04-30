package com.xjava.down.impl;

import com.xjava.down.base.IRequest;
import com.xjava.down.data.Response;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnResponseListener;

import java.util.List;

public class RequestListenerDisposer implements OnConnectListener, OnResponseListener{
    private List<OnConnectListener> connectListeners;
    private List<OnResponseListener> onResponseListeners;

    public RequestListenerDisposer(
            List<OnConnectListener> connectListeners,List<OnResponseListener> onResponseListeners)
    {
        this.connectListeners=connectListeners;
        this.onResponseListeners=onResponseListeners;
    }

    public synchronized void addOnConnectListener(OnConnectListener listener){
        if(connectListeners!=null){
            connectListeners.add(listener);
        }
    }

    public synchronized void removeOnConnectListener(OnConnectListener listener){
        if(connectListeners!=null){
            connectListeners.remove(listener);
        }
    }

    public synchronized void addOnResponseListener(OnResponseListener listener){
        if(onResponseListeners!=null){
            onResponseListeners.add(listener);
        }
    }

    public synchronized void removeOnResponseListener(OnResponseListener listener){
        if(onResponseListeners!=null){
            onResponseListeners.remove(listener);
        }
    }

    @Override
    public void onPending(IRequest request){
        if(connectListeners!=null){
            for(OnConnectListener listener: connectListeners){
                listener.onPending(request);
            }
        }
    }

    @Override
    public void onStart(IRequest request){
        if(connectListeners!=null){
            for(OnConnectListener listener: connectListeners){
                listener.onStart(request);
            }
        }
    }

    @Override
    public void onConnecting(IRequest request){
        if(connectListeners!=null){
            for(OnConnectListener listener: connectListeners){
                listener.onConnecting(request);
            }
        }
    }

    @Override
    public void onRetry(IRequest request){
        if(connectListeners!=null){
            for(OnConnectListener listener: connectListeners){
                listener.onRetry(request);
            }
        }
    }

    @Override
    public void onResponse(IRequest request,Response response){
        if(onResponseListeners!=null){
            for(OnResponseListener listener: onResponseListeners){
                listener.onResponse(request,response);
            }
        }
    }

    @Override
    public void onError(IRequest request,Exception exception){
        if(onResponseListeners!=null){
            for(OnResponseListener listener: onResponseListeners){
                listener.onError(request,exception);
            }
        }
    }
}
