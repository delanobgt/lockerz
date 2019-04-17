package com.delanobgt.lockerz.modules;

public class VigenereFileModifier implements FileModifier {

    private String key;
    private boolean decryptMode;

    public VigenereFileModifier(String key, boolean decryptMode) {
        this.key = key;
        this.decryptMode = decryptMode;
    }

    @Override
    public void modify(byte[] data, long pos, long len, long total) {
        if (decryptMode) {
            for (int i = 0; i < len; i++) {
                data[i] = (byte) (((int) (data[i]) - 5 + 256) % 256);
            }
        } else {
            for (int i = 0; i < len; i++) {
                data[i] = (byte) (((int) (data[i]) + 5) % 256);
            }
        }
    }

    @Override
    public void onError(Exception ex) {
    }

}