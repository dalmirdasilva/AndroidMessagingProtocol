package com.dalmirdasilva.androidmessagingprotocol.device.message;

import java.util.Arrays;

/**
 * Message raw components:
 * [
 * START_OF_MESSAGE_MARK {1}
 * ID {1}
 * TYPE {1}
 * FLAGS {1}
 * PAYLOAD_LENGTH {1}
 * PAYLOAD {PAYLOAD_LENGTH}
 * END_OF_MESSAGE_MARK {1}
 * ]
 */
public abstract class Message {

    // Message types
    public static final byte TYPE_DATA = 0x00;
    public static final byte TYPE_ACK = 0x01;
    public static final byte TYPE_CONNECT = 0x02;
    public static final byte TYPE_SYNC = 0x03;
    public static final byte TYPE_EPOCH = 0x04;
    public static final byte TYPE_SETTINGS = 0x05;
    public static final byte TYPE_PING = 0x06;
    public static final byte TYPE_PONG = 0x07;

    // Marks
    public static final byte START_MESSAGE_MARK = (byte) 0xaa;
    public static final byte END_MESSAGE_MARK = (byte) 0xbb;

    // Positions
    public static final int ID_POS = 0x01;
    public static final int TYPE_POS = 0x02;
    public static final int FLAGS_POS = 0x03;
    public static final int PAYLOAD_LENGTH_POS = 0x04;
    public static final int PAYLOAD_POS = 0x05;

    // Sizes
    public static final int STATIC_PART_SIZE = 0x06;

    // Flag bits
    public static final byte REQUIRED_ACK = 0x01;
    public static final byte IS_LAST_MESSAGE = 0x02;

    public static final byte EPOCH_SIZE = 0x04;
    public static final byte FLAG_SIZE = 0x01;
    public static final byte SAMPLE_SIZE = 0x04;

    protected static byte NEXT_ID = 0x00;

    protected Byte id;
    protected Byte type;
    protected Byte flags;
    protected Byte[] payload;

    protected Message(Byte type) {
        this(NEXT_ID++, type, new Byte((byte) 0x00), new Byte[]{});
    }

    public Message(Byte id, Byte type, Byte flags, Byte[] payload) {
        this.id = id;
        this.type = type;
        this.flags = flags;
        this.payload = payload;
    }

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getFlags() {
        return flags;
    }

    public void setFlags(Byte flags) {
        this.flags = flags;
    }

    public byte getPayloadLength() {
        return (byte) payload.length;
    }

    public Byte[] getPayload() {
        return payload;
    }

    public void setPayload(Byte[] payload) {
        this.payload = payload;
    }

    public byte[] toArray() {
        int i = 0, rawSize = STATIC_PART_SIZE + payload.length;
        byte[] raw = new byte[rawSize];
        raw[i++] = START_MESSAGE_MARK;
        raw[i++] = getId();
        raw[i++] = getType();
        raw[i++] = getFlags();
        raw[i++] = getPayloadLength();
        for (Byte b : getPayload()) {
            raw[i++] = b;
        }
        raw[i++] = END_MESSAGE_MARK;
        return raw;
    }

    public boolean isLastMessage() {
        return (payload[FLAGS_POS] & IS_LAST_MESSAGE) > 0;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type=" + type +
                ", flags=" + flags +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
