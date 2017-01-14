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

public class MessageEventModel {

	private Object Msg;
    private String User;
	
	public Object getMsg() {
		return Msg;
	}

	public void setMsg(Object msg) {
		Msg = msg;
	}

	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		User = user;
	}

}