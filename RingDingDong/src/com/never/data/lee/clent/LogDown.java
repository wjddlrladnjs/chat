package com.never.data.lee.clent;

import com.never.data.lee.server.ChatServer;

public class LogDown extends Thread{

	ChatServer server ;
	ChatCilent cli ; 
	public LogDown(ChatCilent cli) {
		this.cli = cli ;
		
	}

	@Override
	public void run() {
		server.addChatList(cli.name+"님이 서버로그 다운로드 ");
		
		
		
		
		
	}

	
}
