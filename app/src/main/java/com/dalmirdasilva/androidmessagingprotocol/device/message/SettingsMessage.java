package com.dalmirdasilva.androidmessagingprotocol.device.message;

public class SettingsMessage extends Message {

  private byte settingType;
  private int settingValue;

  public SettingsMessage() {
    this((byte) 0x00, 0x00);
  }

  public SettingsMessage(byte settingType, int settingValue) {
    super(TYPE_SETTINGS);
    this.settingType = settingType;
    this.settingValue = settingValue;
  }

  public SettingsMessage(Byte id, Byte type, Byte[] payload) {
    super(id, TYPE_SETTINGS, (byte) 0x00, payload);
  }

  public void setSettingType(byte settingType) {
    this.settingType = settingType;
  }

  public byte getSettingType() {
    return this.settingType;
  }

  public void setSettingValue(int settingValue) {
    this.settingValue = settingValue;
  }

  public int getSettingValue() {
    return settingValue;
  }
}
