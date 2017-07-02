package com.never.project.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerCom implements Runnable {

	private ChatServer server;
	private ServerThread serverThread;
	private Socket s;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String clientIP;
	private String clientName;
	private String adminComment;
	private static int clientNum;
	private boolean onAir;

	// 인자로 전달받은 객체를 맴버 변수에 저장.
	public ServerCom(ChatServer server, ServerThread serverThread) {
		
		this.server = server;
		this.serverThread = serverThread;
		this.s = serverThread.getS();
		clientIP = s.getInetAddress().getHostAddress();
		clientName = "guest" + ++clientNum;
		adminComment = "안녕.";
		onAir = true;
	}
	
	// 클라이언트에게 메시지를 보낼 때 호출된다.
	public void sendMessage( char protocol, String msg ) {
		
		try {
			dos.writeChar( protocol );
			dos.flush();
			dos.writeUTF( msg );
			dos.flush();
		} catch (IOException e) {
			server.appendServerLog("메시지  전송 애러 : " + e.toString());
		}
		
	}
	// 관리자가 메시지를 명령어와 함께 보낸다.
	public void sendAdminMessage( char protocol, int command, String msg ) {
		try {
			
			dos.writeChar( protocol );
			dos.flush();
			dos.writeInt( command );
			dos.flush();
			dos.writeUTF( msg );
			dos.flush();
		} catch (IOException e) {
			server.appendServerLog("관리자 메시지  전송 애러 : " + e.toString());
		}
		
	}
	
	// 클라이언트가 닉네임 변경을 요청하면 호출되는 메서드.
	private void sendNickname(char protocol, String msg ) {
		
		try {
			dos.writeChar(protocol);
			dos.flush();
			dos.writeUTF(msg);
			dos.flush();
		} catch( IOException e ) {
			server.appendServerLog("클라 이름 변경 오류 : " + e.toString());
		}
		
	}
	// 클라이언트 종료시 호출되는 매서드.
	public void shutDownClient() {

		onAir = false;
		
		if( s != null ) {
			try {s.close();} catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
			}
		}
		if( dis != null ) {
			try {dis.close();} catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
			}
		}
		if( dos != null ) {
			try {dos.close();} catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
			}
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
			
			// 서버에 접속한 클라가 받게 되는 데이터.
			protocol = 'M'; // 전체 메시지
			serverThread.sendAllMessage( protocol , String.format("%s# %s님 입장 ~ ! %s -admin-", ChatServer.serverTime, clientName, adminComment));
			protocol = 'N'; // 클라 이름
			sendNickname( protocol, clientName );
			serverThread.sendClientNamelist(this);
			protocol = 'A'; // 신규 클라 접속 알림.
			serverThread.sendAllMessage(protocol, clientName, this);
			
			// 끊임없이 연결을 유지한다.
			while( onAir ) {

				// 예외가 발생하더라도 끊임 없이 반복한다.
				try {
					protocol = dis.readChar();
					
					switch( protocol ) {
					// 일반적인 메시지 처리.
					case 'M' :
						msg = dis.readUTF();
						server.appendServerLog(String.format( "/%s/[%s] : %s",clientIP, clientName, msg ));
						serverThread.sendAllMessage(protocol, clientName, msg);
						break;
					// 클라이언트 종료.
					case 'X' :
						server.appendServerLog(clientName + "님 종료.");
						// 모두에게 이 쓰레드의 클라가 종료되었음을 알림.
						protocol = 'M';
						serverThread.sendAllMessage(protocol, String.format( "%s# %s님 퇴장 !", ChatServer.serverTime, clientName ));
						// 이 쓰레드를 쓰던 클라를 list에서 삭제요청.
						serverThread.removeCom(this);
						// 클라에게 어떤 클라가 나갔는지 그 이름을 알린다.
						protocol = 'D';
						serverThread.sendAllMessage(protocol, clientName);
						// 이 쓰레드를 사용하던 클라가 종료되었으므로 반복을 멈춘다.
						onAir = false;
						break;
					// 클라 이름 변경 프로토콜.
					case 'R' :
						String newClientName = dis.readUTF();
						server.appendServerLog( clientName + "님 닉네임 변경 -> " + newClientName );
						protocol = 'M';
						serverThread.sendAllMessage( protocol, String.format("%s#%s님의 이름이 %s로 변경되었습니다.", ChatServer.serverTime, clientName, newClientName));
						// 클라명 변경 프로토콜.
						protocol = 'U';
						serverThread.sendAllMessage( protocol, clientName + "," + newClientName ) ;
						clientName = newClientName;
						break;
					// 클라, 클라 귓말 프로토콜.
					case 'W' :
						String tagetClient = dis.readUTF();
						msg = dis.readUTF();
						server.appendServerLog(String.format("귓속말 : %s -> %s : %s", clientName, tagetClient, msg));
						serverThread.sendWhisperMessage(tagetClient, String.format("%s(%s님 귓속말) : %s" ,ChatServer.serverTime, clientName, msg));
						break;
						
					}
					
				} catch( IOException e ) {
					server.appendServerLog("서버 I/O 애러" +e.toString());
				}
				
			} // while end
			// 열려있던 객체들을 다 닫아주자.
			if( s != null ) {
				try {s.close(); s = null ;} catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			if( dis != null ) {
				try {dis.close(); dis = null; } catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			if( dos != null ) {
				try {dos.close(); dos = null; } catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			onAir = false;
			
		} catch( Exception e ) {
			server.appendServerLog("서버 Com 애러" + e.toString());
			if( s != null ) {
				try {s.close(); s = null; } catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			if( dis != null ) {
				try {dis.close(); dis = null; } catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			if( dos != null ) {
				try {dos.close(); dos = null; } catch (IOException e1) {server.appendServerLog("서버 I/O 애러" + e1.toString());
				}
			}
			// 문제가 생기면 열려있는 객체를 닫고 쓰레드를 종료한다.
			return;
		}
		
	}// run() end

	// getter and setter
	public ChatServer getServer() {
		return server;
	}

	public void setServer(ChatServer server) {
		this.server = server;
	}

	public ServerThread getServerThread() {
		return serverThread;
	}

	public void setServerThread(ServerThread serverThread) {
		this.serverThread = serverThread;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAdminComment() {
		return adminComment;
	}

	public void setAdminComment(String adminComment) {
		this.adminComment = adminComment;
	}

	public static int getClientNum() {
		return clientNum;
	}

	public static void setClientNum(int clientNum) {
		ServerCom.clientNum = clientNum;
	}

	public boolean isOnAir() {
		return onAir;
	}

	public void setOnAir(boolean onAir) {
		this.onAir = onAir;
	}

}
