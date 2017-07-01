package com.never.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

	ChatServer server;
	ServerSocket ss;
	boolean onAir;
	Com com;
	
	// 매개변수로 넘겨 받은 server를 클래스 내에서 공유한다.
	public ServerThread(ChatServer chatServer) {
		this.server = chatServer;
	}

	@Override
	public void run() {
		
		int port = server.getPort();
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			server.appendServerLog("서버 소켓 생성 실패 : " + e.toString());
			// 서버 소켓 생성에 실패했으므로 쓰레드를 종료한다.
			return;
		}
		// 서버 소켓이 정상적으로 생성되었다는 것을 알림.
		server.controlStopBtton();
		server.appendServerLog("서버 소켓 생성 성공!");
		onAir = true;
		
		// 서버는 죽지 않아요.
		while( onAir ) {
			// 클라이언트와 연결될 소켓을 만든다.
			Socket s = null;
			String remoteIP = "";

			try {
				server.appendServerLog("서버가 연결을 기다립니다. . . . .");
				
				// 클라의 연결을 기다리다가 연결 요청이 생기면 소켓을 연결한다.
				s = ss.accept();
				remoteIP = s.getInetAddress().getHostAddress();
				server.appendServerLog(remoteIP + " - 접속 . . . . .");
				
				com = new Com( server, this );
				
			} catch( IOException e ) {
				server.appendServerLog(e.toString());
			}
			
		}
	}

}
