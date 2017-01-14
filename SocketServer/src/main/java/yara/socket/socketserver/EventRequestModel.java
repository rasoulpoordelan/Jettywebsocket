/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yara.socket.socketserver;

/**
 *
 * @author rasoul
 */
public class EventRequestModel {

	private String Event;
	private Object Payload;

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public Object getPayload() {
		return Payload;
	}

	public void setPayload(Object payload) {
		Payload = payload;
	}

}
