package com.delanobgt.lockerz.modules;

public interface FileModifier {

    void modify(byte[] data, long pos, long len, long total);

    void onError(Exception ex);
}