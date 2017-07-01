package com.never.data.jung.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable {

	private ChatServer server;
	private ServerSocket ss;
	private Socket s;
	private ServerCom serverCom;
	private boolean onAir;
	private ArrayList<ServerCom> comList = new ArrayList<ServerCom>();
	
	// 매개변수로 넘겨 받은 server를 클래스 내에서 공유한다.
	public ServerThread(ChatServer chatServer) {
		this.server = chatServer;
	}

	// 채팅은 연결된 모두에게 보내야 한다.
	// 연결되어진 클라 모두에게 메시지를 보낸다.
	public void sendAllMessage( char protocol, String msg ) {
		for( ServerCom com : comList ) {
			com.sendMesagee( protocol, msg );
		}
		
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
			s = null;
			serverCom = null;
			String clientIP = "";

			try {
				server.appendServerLog("서버가 연결을 기다립니다. . . . .");
			
				// 클라의 연결을 기다리다가 연결 요청이 생기면 소켓을 연결한다.
				s = ss.accept();
				clientIP = s.getInetAddress().getHostAddress();
				server.appendServerLog(clientIP + " - 접속 . . . . .");
			
				// 서버는 여러 클라이언트와 동시 소통을 한다.
				// 소통하고 있는 객체를 순서대로 저장한다.
				serverCom = new ServerCom( server, this );
				comList.add(serverCom);
				new Thread(serverCom).start();
				
			} catch( IOException e ) {
				server.appendServerLog("서버 접속 대기 오류" + e.toString());
			}
			
		}// while end
		
	}

	public ChatServer getServer() {
		return server;
	}

	public void setServer(ChatServer server) {
		this.server = server;
	}

	public ServerSocket getSs() {
		return ss;
	}

	public void setSs(ServerSocket ss) {
		this.ss = ss;
	}

	public boolean isOnAir() {
		return onAir;
	}

	public void setOnAir(boolean onAir) {
		this.onAir = onAir;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public ServerCom getServerCom() {
		return serverCom;
	}

	public void setServerCom(ServerCom serverCom) {
		this.serverCom = serverCom;
	}

	public ArrayList<ServerCom> getComList() {
		return comList;
	}

	public void setComList(ArrayList<ServerCom> comList) {
		this.comList = comList;
	}

}
