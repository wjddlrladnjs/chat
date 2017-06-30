package com.never.data.won;

import java.io.IOException;


public class ChatReadThread extends Thread{
	private ChatClient client;
	private String input;
	private char type;

	public void run(){
		try{
			while(true){
				type = client.dis.readChar();
				switch(type){
				case 'A':
					input = client.dis.readUTF();
					client.addLog(input);
					input = client.dis.readUTF();
					client.addUser(input);
					break;
				case 'M':
					input = client.dis.readUTF();
					client.addLog(input);
					break;
				case 'N':
					input = client.dis.readUTF();
					client.addLog(input);
					input = client.dis.readUTF();
					String[] userNames = input.split(",");
					client.updataUser(userNames[0], userNames[1]);
					break;
				case 'X':
					input = client.dis.readUTF();
					client.addLog(input);
					input = client.dis.readUTF();
					client.deleteUser(input);
					break;		
				case 'L':
					input = client.dis.readUTF();
					String[] nameList = input.split(",");
					StringBuilder sb = new StringBuilder();
					client.showUserList(nameList);
					for(String name : nameList){
						sb.append(name).append("\n");
					}
					client.addLog("========Now User List======");
					client.addLog(sb.toString());
					client.addLog("===========================");
					break;
				}
			}			
		}catch(IOException e){
			client.addLog("ChatReadThread client.dis.readUTF IOException");
		}
	}

	/*
	 * case 'L':
	 * msg = dis.readUTF();
	 * String[] nameList = msg.split(",");
	 * StringBuilder sb = new Stringbuilder();
	 * for(String name : nameList){
	 * 	sb.append(name).append("/n");
	 * }
	 * client.addLog(=====Now accepting..=========);
	 * client.addLog(sb.toString());
	 * client.addLog(=======================);
	 */

	public ChatReadThread(ChatClient chatClient) {
		this.client = chatClient;
	}

}
