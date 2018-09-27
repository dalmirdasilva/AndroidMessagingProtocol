package com.dalmirdasilva.androidmessagingprotocol.device.message;

import java.util.ArrayList;
import java.util.List;

public class DataMessage extends Message {

  public DataMessage() {
    super(Message.TYPE_DATA);
  }

  public DataMessage(Byte[] payload) {
    this();
    this.payload = payload;
  }
}
