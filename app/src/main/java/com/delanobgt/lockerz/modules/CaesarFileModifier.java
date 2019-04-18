package com.delanobgt.lockerz.modules;

public class CaesarFileModifier implements FileModifier {

    private String key;
    private int rot;
    private boolean decryptMode;

    public CaesarFileModifier(String key, boolean decryptMode) {
        this.key = key;
        int rot = 0;
        for (char ch : key.toCharArray()) {
            rot += (rot + (int) (ch)) % 256;
        }
        this.rot = rot;
        this.decryptMode = decryptMode;
    }

    @Override
    public void modify(byte[] data, long pos, long len, long total) {
        if (!decryptMode) {
            for (int i = 0; i < len; i++) {
                data[i] = (byte) (((int) (data[i]) + rot) % 256);
            }
        } else {
            for (int i = 0; i < len; i++) {
                data[i] = (byte) (((int) (data[i]) - rot + 256) % 256);
            }
        }
    }

    @Override
    public void onError(Exception ex) {
    }

}