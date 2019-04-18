package com.delanobgt.lockerz.modules;

public class XORFileModifier implements FileModifier {

    private String key;
    private byte[] rots;
    private boolean decryptMode;

    public XORFileModifier(String key, boolean decryptMode) {
        this.key = key;
        int[] rots = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            rots[i] = (byte) ((int) (key.charAt(i)) % 256);
        }
        this.decryptMode = decryptMode;
    }

    @Override
    public void modify(byte[] data, long pos, long len, long total) {
        for (int i = 0; i < len; i++) {
            data[i] = (byte) (data[i] ^ rots[(int) ((pos + i) % rots.length)]);
        }
    }

    @Override
    public void onError(Exception ex) {
    }

}