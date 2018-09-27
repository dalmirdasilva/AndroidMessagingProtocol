package com.dalmirdasilva.androidmessagingprotocol.device.message;

public class MessageFactory {

  public static Message newMessageFromType(byte type) {
    Message message = null;
    switch (type) {
      case Message.TYPE_DATA:
        message = newDataMessage();
        break;
      case Message.TYPE_ACK:
        message = newAckMessage();
        break;
      case Message.TYPE_CONNECT:
        message = newConnectMessage();
        break;
      case Message.TYPE_SYNC:
        message = newSyncMessage();
        break;
      case Message.TYPE_EPOCH:
        message = newEpochMessage();
        break;
      case Message.TYPE_SETTINGS:
        message = newSettingsMessage();
        break;
      case Message.TYPE_PING:
        message = newPingMessage();
        break;
      case Message.TYPE_PONG:
        message = newPongMessage();
        break;
    }
    return message;
  }

  public static Message newDataMessage() {
    return new DataMessage();
  }

  public static Message newPingMessage() {
    return new PingMessage();
  }

  public static Message newPongMessage() {
    return new PongMessage();
  }

  public static Message newSyncMessage() {
    return new SyncMessage();
  }

  public static Message newAckMessage() {
    return new AckMessage();
  }

  public static Message newConnectMessage() {
    return new ConnectMessage();
  }

  public static Message newSettingsMessage() {
    return new SettingsMessage();
  }

  public static Message newSettingsMessage(byte type, int setting) {
    return new SettingsMessage(type, setting);
  }

  public static Message newEpochMessage(long epoch) {
    return new EpochMessage(epoch);
  }

  public static Message newEpochMessage() {
    return new EpochMessage();
  }
}
