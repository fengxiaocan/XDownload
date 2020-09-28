package com.x.down.task;

import com.x.down.tool.XDownUtils;
import com.x.down.base.IoSink;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

class HttpIoSink implements IoSink {
    private final OutputStream outputStream;

    public HttpIoSink(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public IoSink writeByte(int source) throws IOException {
        outputStream.write(source);
        return this;
    }

    @Override
    public IoSink writeByte(byte source) throws IOException {
        outputStream.write(source);
        return this;
    }

    @Override
    public IoSink writeBytes(byte[] source) throws IOException {
        outputStream.write(source);
        return this;
    }

    @Override
    public IoSink writeBytes(byte[] source, int offset, int byteCount) throws IOException {
        outputStream.write(source, offset, byteCount);
        return this;
    }

    @Override
    public IoSink writeUtf8(String string) throws IOException {
        if (string != null) {
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
        return this;
    }

    @Override
    public IoSink writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
        if (string != null) {
            outputStream.write(string.substring(beginIndex, endIndex).getBytes(StandardCharsets.UTF_8));
        }
        return this;
    }

    public void close() {
        XDownUtils.closeIo(outputStream);
    }
}
