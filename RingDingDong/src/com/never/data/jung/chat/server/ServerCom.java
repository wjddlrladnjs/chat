package com.never.data.jung.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerCom extends Thread {

	ChatServer server;
	ServerThread serverThread;
	DataInputStream dis;
	DataOutputStream dos;
	Socket s;
	String clientIP;
	
	boolean onAir;

	// 인자로 전달받은 객체를 맴버 변수에 저장.
	public ServerCom(ChatServer server, ServerThread serverThread, Socket s) {
		
		this.server = server;
		this.serverThread = serverThread;
		this.s = s;
		clientIP = s.getInetAddress().getHostAddress();
		onAir = true;
	}
	
	// 클라이언트에게 메시지를 보낼 때 호출된다.
	public void sendMesagee( char protocol, String msg ) {
		
		try {
			dos.writeChar( protocol );
			dos.flush();
			dos.writeUTF( msg );
			dos.flush();
		} catch (IOException e) {
			server.appendServerLog("메시지  전송 애러 : " + e.toString());
		}
		
	}

	@Override
	public void run() {
		
		try {
			
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			server.appendServerLog("서버 I/O 준비 완료.");
			
			String msg = "";
			char protocol = ' ';
			
			protocol = 'M';
			serverThread.sendAllMessage( protocol , String.format("# 안녕. - admin - "));
			
			// 끊임없이 연결을 유지한다.
			while( onAir ) {

				// 예외가 발생하더라도 끊임 없이 반복한다.
				try {
					protocol = dis.readChar();
					
					switch( protocol ) {
					
					case 'M' :
						
						msg = dis.readUTF();
						server.appendServerLog(clientIP + " - " + msg);
						
						serverThread.sendAllMessage(protocol, msg);
					
					}
					
				} catch( IOException e ) {
					server.appendServerLog("서버 I/O 애러" +e.toString());
				}
				
			}
			
		} catch( Exception e ) {
			server.appendServerLog("서버 I/O 애러" + e.toString());
			if( s != null ) {
				try {s.close();} catch (IOException e1) {server.appendServerLog("" + e1.toString());
				}
			}
			if( dis != null ) {
				try {dis.close();} catch (IOException e1) {server.appendServerLog("" + e1.toString());
				}
			}
			if( dos != null ) {
				try {dos.close();} catch (IOException e1) {server.appendServerLog("" + e1.toString());
				}
			}
			// 문제가 생기면 열려있는 객체를 닫고 쓰레드를 종료한다.
			return;
		}
		
	}

}
