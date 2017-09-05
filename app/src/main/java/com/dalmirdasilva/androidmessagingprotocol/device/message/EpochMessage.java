package com.dalmirdasilva.androidmessagingprotocol.device.message;

public class EpochMessage extends Message {

    private long epoch;

    public EpochMessage() {
        this(System.currentTimeMillis());
    }

    public EpochMessage(long epoch) {
        super(TYPE_EPOCH);
        setEpoch(epoch);
    }

    private long getEpoch() {
        return  epoch;
    }

    private void setEpoch(long epoch) {
        Byte[] payload = new Byte[4];
        payload[0] = (byte) (epoch & 0xff);
        payload[1] = (byte) ((epoch >> 8) & 0xff);
        payload[2] = (byte) ((epoch >> 16) & 0xff);
        payload[3] = (byte) ((epoch >> 24) & 0xff);
        setPayload(payload);
    }

    protected static long makeSystemEpoch(long shortEpoch) {
        long now = System.currentTimeMillis();
        long msb = now & 0xffffffff00000000L;
        long lsb = now & 0x00000000ffffffffL;
        if (lsb < shortEpoch) {
            msb += 0x100000000L;
        }
        return msb | (shortEpoch & 0x00000000ffffffffL);
    }
}
