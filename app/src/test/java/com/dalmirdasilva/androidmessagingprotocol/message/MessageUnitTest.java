package com.dalmirdasilva.androidmessagingprotocol.message;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        Message m = MessageFactory.newAckMessage();
        assertEquals((byte) m.getType(), Message.TYPE_ACK);
    }
}