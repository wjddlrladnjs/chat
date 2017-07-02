package com.never.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class ServerThread implements Runnable {

	private ChatServer server;
	private ServerSocket ss;
	private Socket s;
	private ServerCom serverCom;
	private boolean onAir;
	private ArrayList<ServerCom> serverComList = new ArrayList<ServerCom>();
	private HashMap<String, String> chatCommand = new HashMap<String, String>();

	// 매개변수로 넘겨 받은 server를 클래스 내에서 공유한다.
	public ServerThread(ChatServer chatServer) {
		this.server = chatServer;
		initChatCommand();
	}
	// 서버에 등록된 명령어 모음을 저장한다.
	private void initChatCommand() {
		HashMap<String, String> initCommand = new HashMap<String, String>();
		initCommand.put("w", "/w (상대) 메시지");
		initCommand.put("t", "/t 서버 시간");
		initCommand.put("c", "/c 화면 지움");
		
		chatCommand.putAll( initCommand );
	}
	// 선택된 대상의 com쓰레드를 찾아서 귓말을 보낸다.
	public void sendWhisperMessage(String tagetClient, String msg) {

		ServerCom com = null;
		for( int i = 0; i < serverComList.size(); i++ ) {
			com = serverComList.get(i);
			if( com.getClientName().equals(tagetClient)) {
				break;
			}
		}
		if( com != null ) {
			char protocol = 'M';
			com.sendMessage(protocol, msg);
		}

	}
	// client들의 이름을 추출해서 각 각 클라이언트에게 보내준다.
	public void sendClientNamelist( ServerCom serverCom ) {

		String userList = "";
		char protocol = 'L';
		StringBuffer sb = new StringBuffer();
		for( ServerCom c : serverComList ) {
			sb.append(c.getClientName() + ",");
		}
		userList = sb.toString().substring(0, sb.length() - 1);
		serverCom.sendMessage( protocol, userList );
	}
	// 쓰레드 종료시 호출 되는 메서드.
	public void stopServer() {

		// 일단 반복문을 종료하고,
		onAir = false;
		try {
			// 서버 소켓을 종료하고,
			ss.close();
			for( ServerCom com : serverComList ) {
				// com 객체도 닫아준 뒤에 삭제한다.
				com.shutDownClient();
				removeCom(com);
			}

		} catch (IOException e) {
			server.appendServerLog("서버 정지 오류 : " + e.toString());
		} catch (ConcurrentModificationException e) {
			server.appendServerLog("서버 정지 오류 : " + e.toString());
		}
		server.controlStopButton(false);

	}

	// 채팅은 연결된 모두에게 보내야 한다.
	// 연결되어진 클라 모두에게 메시지를 보낸다.
	public void sendAllMessage( char protocol, String msg ) {

		for( ServerCom com : serverComList ) {
			com.sendMessage( protocol, msg );
		}

	}
	// 전체 메시지 필터링 오버로드.
	public void sendAllMessage( char protocol,String clientName, String msg ) {

		String temp = "당신";
		String reMsg = "";
		for( ServerCom com : serverComList ) {
			if( com.getClientName().equals(clientName) ){
				reMsg = String.format("%s[%s] : %s", ChatServer.serverTime, temp, msg);
			}else {
				reMsg = String.format("%s[%s] : %s", ChatServer.serverTime, clientName, msg);
			}
			com.sendMessage( protocol, reMsg );
		}

	}
	// 전체 메시지를 보내지만 나를 제외한 나머지에게 보낼 때 쓰는 오버로드된 메서드.
	public void sendAllMessage( char protocol, String msg, ServerCom itsMe ) {

		for( ServerCom com : serverComList ) {
			if(com != itsMe ) {
				com.sendMessage( protocol, msg );
			}
		}

	}
	// 운영자 전용 메시지
	public void sendAdminMessage( char protocol, int command, String msg ) {
		for( ServerCom com : serverComList ) {
			com.sendAdminMessage( protocol, command, msg );
		}

	}
	// 삭제 요청한 serverCom 클라 객체 삭제.
	public void removeCom(ServerCom serverCom) {
		serverComList.remove(serverCom);
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
		server.controlStopButton(true);
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
				serverComList.add(serverCom);
				new Thread(serverCom).start();

			} catch( IOException e ) {
				server.appendServerLog("서버 접속 대기 오류" + e.toString());
			}

		}// while end
		server.appendServerLog(Thread.currentThread().getName() + ": 스레드 종료");
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
		return serverComList;
	}

	public void setComList(ArrayList<ServerCom> comList) {
		this.serverComList = comList;
	}
	public HashMap<String, String> getChatCommand() {
		return chatCommand;
	}
	public void setChatCommand(HashMap<String, String> chatCommand) {
		this.chatCommand = chatCommand;
	}

}
