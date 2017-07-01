package com.never.data.jung.chat.server;

public class Com implements Runnable {

	ChatServer server;
	ServerThread serverThread;
	
	public Com(ChatServer server, ServerThread serverThread) {
		
		this.server = server;
		this.serverThread = serverThread;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
