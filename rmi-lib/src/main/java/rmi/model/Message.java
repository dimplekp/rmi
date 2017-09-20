package rmi.model;

import java.io.Serializable;

import rmi.util.Util.MessageType;

public class Message<T> implements Serializable {

	private static final long serialVersionUID = -5893673278796744579L;
	private T messageData;
	private MessageType messageType;

	public Message(MessageType messageType, T messageData) {
		this.messageType = messageType;
		this.messageData = messageData;
	}

	public Object getMessageData() {
		return messageData;
	}

	public void setMessageData(T messageData) {
		this.messageData = messageData;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

}
