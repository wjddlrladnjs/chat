package com.never.share;

import java.io.File;

public class LogDown extends Thread{
	String logdata  ;
	ChatCilent Cilent;
	public LogDown(ChatCilent Cilent, String logdata) {
		this.logdata = logdata;
		this.Cilent = Cilent;
	}

	@Override
	public void run() {
		
		
		
	}

	
}
