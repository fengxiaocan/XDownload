package com.x.down.interfaces;

import com.x.down.net.Request;
import com.x.down.net.Response;

import java.io.IOException;

public interface Repeaters{
    Response proceed(Request request) throws IOException;
}
