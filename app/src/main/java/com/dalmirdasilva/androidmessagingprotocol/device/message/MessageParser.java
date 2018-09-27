package com.dalmirdasilva.androidmessagingprotocol.device.message;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Responsible to parse incoming bytes and to buikd a @See{Message}
 */
public class MessageParser {

  private static final String TAG = "MessageParser";

  public enum State {
    INITIAL,
    START_OF_MESSAGE_MARK_PARSED,
    ID_PARSED,
    TYPE_PARSED,
    FLAGS_PARSED,
    PAYLOAD_LENGTH_PARSED,
    PAYLOAD_PARSED,
    END_OF_MESSAGE_MARK_PARSED
  }

  private List<Byte> raw;
  private State state;
  private int payloadLength;
  private Queue<Message> messageQueue;

  public MessageParser() {
    this.messageQueue = new ArrayDeque<>();
    resetState();
  }

  public void resetState() {
    state = State.INITIAL;
  }

  public State getState() {
    return state;
  }

  /**
   * Parses a array of bytes.
   * <p>
   * It return the number of parsed bytes. After a message is decoded, it stores the message
   * in the message queue and keeps parsing towards the next message.
   * <p>
   * If the number of parsed bytes is less than the received array, it means an error occurred
   * while parsing the array of bytes.
   *
   * @param bytes
   * @return
   */
  public int parse(byte[] bytes) {
    int i;
    for (i = 0; i < bytes.length; i++) {
      if (!parse(bytes[i])) {
        break;
      }
    }
    return i;
  }

  /**
   * The state machine will stop and @See{resetState} needs to be called to reset the machine.
   *
   * @param b
   * @return
   */
  private boolean parse(byte b) {
    Log.d(TAG, "Parsing: 0x" + Integer.toHexString(b & 0xff));
    boolean ingested = true;
    switch (state) {
      case INITIAL:
        if (b == Message.START_MESSAGE_MARK) {
          raw = new ArrayList<>();
          state = State.START_OF_MESSAGE_MARK_PARSED;
        } else {
          Log.d(TAG, "State is INITIAL but START_MESSAGE_MARK mismatches.");
          ingested = false;
        }
        break;
      case START_OF_MESSAGE_MARK_PARSED:
        state = State.ID_PARSED;
        break;
      case ID_PARSED:
        state = State.TYPE_PARSED;
        break;
      case TYPE_PARSED:
        state = State.FLAGS_PARSED;
        break;
      case FLAGS_PARSED:
        state = State.PAYLOAD_LENGTH_PARSED;
        payloadLength = b;

        // If the message has no payload, jump straight to PAYLOAD_PARSED state.
        if (payloadLength <= 0) {
          state = State.PAYLOAD_PARSED;
        }
        break;
      case PAYLOAD_LENGTH_PARSED:
        if (--payloadLength == 0) {
          state = State.PAYLOAD_PARSED;
        }
        break;
      case PAYLOAD_PARSED:
        if (b == Message.END_MESSAGE_MARK) {
          state = State.END_OF_MESSAGE_MARK_PARSED;
          enqueueDecodedMessage();
        } else {
          Log.d(TAG, "State is PAYLOAD_PARSED but END_MESSAGE_MARK mismatches.");
          ingested = false;
        }
        break;
      case END_OF_MESSAGE_MARK_PARSED:
        Log.d(TAG, "Message was fully parsed and wasn't yet retrieved, but more data is coming.");
        ingested = false;
        break;
    }
    if (ingested) {
      raw.add(b);
    }
    return ingested;
  }

  public boolean wasMessageDecoded() {
    return this.messageQueue.size() > 0;
  }

  public Message collectDecodedMessage() {
    Message message = null;
    if (messageQueue.size() > 0) {
      message = messageQueue.poll();
    }
    return message;
  }

  private void enqueueDecodedMessage() {
    Message message = instantiateDecodedMessage();
    messageQueue.offer(message);
    resetState();
  }

  private Message instantiateDecodedMessage() {
    Message message = MessageFactory.newMessageFromType(raw.get(Message.TYPE_POS));
    message.setId(raw.get(Message.ID_POS));
    message.setFlags(raw.get(Message.FLAGS_POS));
    List<Byte> payload = raw.subList(Message.PAYLOAD_POS, Message.PAYLOAD_POS + payloadLength);
    Byte[] bytes = payload.toArray(new Byte[payloadLength]);
    message.setPayload(bytes);
    return message;
  }
}
