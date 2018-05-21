package com.dalmirdasilva.androidmessagingprotocol.message;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageFactory;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageParserUnitTest {

    @Test
    public void parse_decodesMessageProperly() throws Exception {
        MessageParser parser = new MessageParser();

        Message connectMessage = MessageFactory.newConnectMessage();
        Message dataMessage = MessageFactory.newDataMessage();
        Message ackMessage = MessageFactory.newAckMessage();

        byte[] connectMessageRaw = connectMessage.toArray();
        byte[] dataMessageRaw = dataMessage.toArray();
        byte[] ackMessageRaw = ackMessage.toArray();

        parser.parse(connectMessageRaw);
        parser.parse(dataMessageRaw);
        parser.parse(ackMessageRaw);

        assertEquals(parser.wasMessageDecoded(), true);
        assertEquals(parser.collectDecodedMessage().getType(), connectMessage.getType());
        assertEquals(parser.collectDecodedMessage().getType(), dataMessage.getType());
        assertEquals(parser.collectDecodedMessage().getType(), ackMessage.getType());
    }
}