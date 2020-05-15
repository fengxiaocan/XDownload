package com.xjava.down.listener;

import javax.net.ssl.SSLSocketFactory;

public interface SSLCertificateFactory{
    SSLSocketFactory createCertificate();
}
