package com.x.down.impl;

import com.x.down.base.IRequest;
import com.x.down.data.Response;
import com.x.down.dispatch.Schedulers;
import com.x.down.listener.OnConnectListener;
import com.x.down.listener.OnResponseListener;

public class RequestListenerDisposer implements OnConnectListener, OnResponseListener {
    private final Schedulers schedulers;
    private final OnResponseListener onResponseListener;
    private final OnConnectListener onConnectListener;

    public RequestListenerDisposer(
            Schedulers schedulers, OnConnectListener onConnectListener, OnResponseListener onResponseListener) {
        this.schedulers = schedulers;
        this.onResponseListener = onResponseListener;
        this.onConnectListener = onConnectListener;
    }

    @Override
    public void onPending(final IRequest request) {
        if (onConnectListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onConnectListener.onConnecting(request);
                }
            });
        } else {
            onConnectListener.onConnecting(request);
        }
    }

    @Override
    public void onStart(final IRequest request) {
        if (onConnectListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onConnectListener.onStart(request);
                }
            });
        } else {
            onConnectListener.onStart(request);
        }
    }

    @Override
    public void onConnecting(final IRequest request) {
        if (onConnectListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onConnectListener.onConnecting(request);
                }
            });
        } else {
            onConnectListener.onConnecting(request);
        }
    }

    @Override
    public void onCancel(final IRequest request) {
        if (onConnectListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onConnectListener.onCancel(request);
                }
            });
        } else {
            onConnectListener.onCancel(request);
        }
    }

    @Override
    public void onRetry(final IRequest request) {
        if (onConnectListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onConnectListener.onRetry(request);
                }
            });
        } else {
            onConnectListener.onRetry(request);
        }
    }

    @Override
    public void onResponse(final IRequest request, final Response response) {
        if (onResponseListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onResponseListener.onResponse(request, response);
                }
            });
        } else {
            onResponseListener.onResponse(request, response);
        }
    }

    @Override
    public void onError(final IRequest request, final Exception exception) {
        if (onResponseListener == null) {
            return;
        }
        if (schedulers != null) {
            schedulers.schedule(new Runnable() {
                @Override
                public void run() {
                    onResponseListener.onError(request, exception);
                }
            });
        } else {
            onResponseListener.onError(request, exception);
        }
    }
}
