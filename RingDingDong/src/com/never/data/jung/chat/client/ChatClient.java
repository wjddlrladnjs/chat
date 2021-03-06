package com.never.data.jung.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

public class ChatClient {

	private JFrame f;
	private JPanel mainPanel, nPanel, nPanelCenter, nPanelCenterLeft, nPanelCenterRight,
	cPanel, cPanelCenter, cPanelCenterSouth, cPanelSouth, cPanelEast, cPanelEastCenter, cPanelEastSouth, 
	cPanelEastSouthNorth,  cPanelEastSouthCenter, cPanelEastSouthSouth,
	sPanel;
	private JTextField tfIP, tfPort, tfNickname, tfMessage;
	private JTextArea taClientLog;
	// JEditorPane epChatLog;
	private JScrollPane spUserList, spClientLog;
	private JButton btnConnect, btnNickname, btnFuntion1, btnFuntion2, btnFuntion3, btnFuntion4, btnNameChange, btnSend;

	private Socket s;
	private DataInputStream dis;
	private DataOutputStream dos;

	private String nickname;
	private DefaultListModel<String> model;
	private JList<String> clientList;
	private HashMap<String, String> chatCommand;

	private ActionListener listener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {

			String cmd = e.getActionCommand();

			switch( cmd ) {

			case "connection":
				connectServer();
				break;
			case "disconnection":
				disconnectServer();
				break;
			case "send" :
				sendMessage();
				tfMessage.grabFocus();
				break;
			case "nick" :
				changeNickname();
				break;
			}
		}
	};

	public ChatClient() {
		initGUI();
		getClientIP();
	}
	// 임시 아이피 사용을 쉽게 하기 위한 메서드.
	private void getClientIP() {

		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			tfIP.setText(ip);
		} catch (UnknownHostException e) {
			appendClientLog("클라 IP를 읽어올 수 없습니다. :" + e);
		}  

	}
	// 접속할 때 서버에서 명령어를 받아온다.
	public void getChatCommand(ClientReadThread clientReadThread) {

		FileOutputStream fos = null;
		ObjectInputStream ois = null;
		HashMap<String, String> temp = null;

		String filePath = "./src/com/never/data/jung/chat/client/file/";
		String fileName = "";
		File f = null;
		try {
			fileName = dis.readUTF();
			int size = dis.readInt();
			byte[] data = new byte[size];
			dis.readFully(data, 0, size);
			f = new File(filePath, fileName);
			fos = new FileOutputStream(f);
			fos.write(data, 0, size);
			fos.flush();

			ois = new ObjectInputStream(new FileInputStream(f));
			Object obj = ois.readObject();
			if( obj instanceof HashMap ) {
				temp = (HashMap) obj;
				chatCommand = temp;
			}
			appendClientLog("받은 명령어 : " + chatCommand.size() + "개");

		} catch( IOException e ) {
			appendClientLog("객체 받기 애러" + e.toString());
		} catch (ClassNotFoundException e) {
			appendClientLog("받은 객체가 받을 객체와 다름" + e.toString());
		} finally {
			if( fos != null ) {
				try { fos.close(); fos = null; } catch (IOException e) {appendClientLog("객체 생성 애러" + e.toString());}
			}
			if( ois != null ) {
				try { ois.close(); ois = null; } catch (IOException e) {appendClientLog("객체 생성 애러" + e.toString());}
			}
		}

	}
	// 귓속말 처리 메서드
	private void sendWhisperMessage(String tagetClient, String msg) {

		if( msg.length() == 0 ) {
			return;
		}
		try {
			// 선택한 대상에게 귓말을 보낸다.
			dos.writeChar('W');	// 귓속말 프로토콜
			dos.flush();
			dos.writeUTF(tagetClient);
			dos.flush();
			dos.writeUTF(msg);
			dos.flush();
		} catch( IOException e ) {
			appendClientLog("클라 메시지 전송 애러 : " + e.toString());
		}

	}
	// 클라가 서버에서 나가면 해당 클라를 리스트에서 삭제한다.
	public void deleteClient(String clientName) {
		// removeElement는 Object까지 삭제가 가능하다.
		model.removeElement(clientName);

	}
	// 접속 중에 추가되는 클라 이름을 리스트에 추가한다.
	public void addClientName(String clientName) {
		model.addElement( clientName );
	}
	// 서버 접속시 서버가 보내온 clientName을 list에 추가한다.
	public void showUserList( String[] clientNameList ) {

		for( String clientName : clientNameList ) {
			model.addElement( clientName );
		}

	}
	// 변경된 클라이언트 이름을 리스트에 적용한다.
	public void updateClient(String oldClientName, String newClientName) {

		// 예전 이름을 쓰던 클라이언트의 위치를 찾아서,
		int index = model.indexOf(oldClientName);
		// 새로운 이름으로 변경해준다.
		model.setElementAt(newClientName, index);

	}
	// 서버와 접속을 종료하는 메서드.
	private void disconnectServer() {

		doExitEvent(5);
		appendClientLog(">> 접속을 종료하였습니다. <<");
		model.clear();
		changeButton(false);

	}
	// 내게 보이는 닉네임을 변경한다.
	public void setNickName(String nickname) {

		this.nickname = nickname;
		f.setTitle(String.format("채팅 클라이언트  - %s", nickname));
	}
	// 닉네임 바꿀 때 처리를 해보자.
	private void changeNickname() {

		String newNickname = tfNickname.getText();
		// 아무값도 들어오지 않았다면 메서드를 종료한다.
		if( newNickname == null || newNickname.length() == 0 ) {
			return;
		}
		setNickName(newNickname);
		sendNickname(newNickname);
		tfNickname.setText("");
		tfNickname.grabFocus();
	}
	// 닉네임 변경을 서버에 알린다.
	private void sendNickname(String nickname) {

		String msg = tfNickname.getText();

		if( msg.length() == 0 ) {
			return;
		}
		if(  s == null || dis == null || dos == null ) {
			appendClientLog("서버와 연결되어 있지 않습니다.");
			return;
		}

		try {
			dos.writeChar('R');
			dos.flush();
			dos.writeUTF(nickname);
			dos.flush();
		} catch( IOException e ) {
			appendClientLog("닉네임 변경 애러" + e.toString());
		}

	}
	// 명령어 전송 메소드.
	private void chatCommandEvent(String command) {

		String result = "";
		String inputKey = "";
		int strEnd = -1;
		
		if( command.length() >= 2 ) {
			strEnd = command.length();
			 if( strEnd != -1 ) {
				 inputKey = command.substring(1, strEnd);
				 if( (chatCommand.get(inputKey) != null) ) {
					 appendClientLog(chatCommand.get(inputKey));
				 } else {
					 appendClientLog("잘못된 입력입니다.");
				 }
				 
			 }
		}
	}


	// 메시지 전송을 위한 메서드.
	private void sendMessage() {

		String msg = tfMessage.getText();
		tfMessage.setText("");

		String tagetClient = clientList.getSelectedValue();

		if( msg.length() == 0 ) {
			return;
		}
		// 연결 안 된 상태에서 귓속말 처리.
		if( s == null || dis == null || dos == null ) {
			appendClientLog("서버와 연결되어 있지 않습니다.");
			return;
		}
		// 만약에 리스트에 선택된 사람이 있다면 귓속말을 한다.
		if( tagetClient != null ) {
			sendWhisperMessage(tagetClient, msg);
			return;
		}
		if( msg.charAt(0) == '/' ) {
			chatCommandEvent(msg);
			return;
		}

		try {
			dos.writeChar('M');
			dos.flush();
			dos.writeUTF(msg);
			dos.flush();
		} catch( IOException e ) {
			appendClientLog("클라 메시지 전송 애러 : " + e.toString());
		}

	}
	// client log 메시지 띄우는 메서드.
	public void appendClientLog( String msg ) {

		int length = taClientLog.getText().length();
		taClientLog.append(msg + "\n");
		// 자동 스크롤링
		taClientLog.setCaretPosition( length );

	}
	// 접속 버튼 눌렀을 때 해당 정보로 서버에 접속시도.
	private void connectServer() {

		// 입력된 포트 정보를 가져온다.
		int port = 12345;
		String inputIP = tfIP.getText().trim();
		String inputPort = tfPort.getText().trim();

		// 입력된 port가 잘못되었을 때 처리.
		if( (inputPort != null && !"".equals(inputPort)) && (inputIP != null && !"".equals(inputIP)) ) {
			try {
				port = Integer.parseInt(inputPort);
				if( port < 1 || port > 65535) {
					throw new NumberFormatException("");
				}
			} catch( NumberFormatException e ) {
				appendClientLog("잘못된 입력입니다." + e.toString());
				tfIP.selectAll();
				tfPort.selectAll();
				tfIP.grabFocus();

				return;
			} 
		} // if end.
		// IP와 Port가 준비되었으니 연결을 준비한다.
		s = null;
		try {
			s = new Socket(inputIP, port);
			appendClientLog("소켓 연결 완료");

			// 소켓이 연결되었으면, I/O 준비를 하자.
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			appendClientLog("I/O 연결 완료");
			// 여기까지 진행되면 서버와 통신이 가능하다.
			// 버튼 상태를 변경한다.
			changeButton(true);

			// 보내는 것은 이 객채로 가능 하지만,
			// 받는 작업은 언제 올지 모르기 때문에 무한 반복으로 기다려야 한다.
			// 이럴 때 쓰라고 쓰래드를 만들어서 쓴다.
			new Thread(new ClientReadThread(this)).start();;

		} catch( IOException e ) {
			appendClientLog("클라 I/O 애러" + e.toString());
		}

	}
	// 서버와 연결되었을 때 버튼의 상태 변경 메서드.
	public void changeButton( boolean state ) {

		btnSend.setEnabled(state);
		btnNickname.setEnabled(state);
		btnFuntion1.setEnabled(state);
		btnFuntion2.setEnabled(state);
		btnFuntion3.setEnabled(state);
		btnFuntion4.setEnabled(state);

		// 현재 상태에 따라 분기.
		if( state ) {
			btnConnect.setText("끊기");
			btnConnect.setBackground(Color.RED);
			btnConnect.setActionCommand("disconnection");
			tfPort.setEnabled(!state);
			tfIP.setEnabled(!state);
			tfMessage.grabFocus();

		} else {
			btnConnect.setText("접속");
			btnConnect.setBackground(Color.GREEN);
			btnConnect.setActionCommand("connection");
			tfPort.setEnabled(!state);
			tfIP.setEnabled(!state);
			tfIP.grabFocus();
		}
	}
	// 클라 종료시 호출된 메서드.
	public void doExitEvent(int state) {

		try {
			// 서버에게도 클라가 종료되었음을 알림.
			if( dos != null ) {
				dos.writeChar('X');
				dos.flush();
			}
		} catch( IOException e ) {
			appendClientLog("클라 종료 애러" + e.toString());
		} finally {
			if( s != null ) {
				try {s.close(); s = null; } catch (IOException e) {appendClientLog("클라 I/O 닫기 애러" + e.toString());
				}
			}
			if( dis != null ) {
				try {dis.close(); dis = null;} catch (IOException e) {appendClientLog("클라 I/O 닫기 애러" + e.toString());
				}
			}
			if( dos != null ) {
				try {dos.close(); dos = null; } catch (IOException e) {appendClientLog("클라 I/O 닫기 애러" + e.toString());
				}
			}
		}

		// 종료인지 접속 종료인지 판단.
		if( state == 0 ) {
			// X 버튼이면 다 닫고 종료.
			System.exit(0);
		}
	}
	private void initGUI() {

		f = new JFrame("Chatting Client");
		f.setBounds(0, 600, 500, 400);
		// 클라이언트 종료.
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 클라 종료 메서드 호출. 인자 0은 종료.
				doExitEvent(0);
				;}
		});

		// main
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		f.add(mainPanel);

		// main.north
		nPanel = new JPanel(new BorderLayout());
		nPanelCenter = new JPanel(new GridLayout(1, 2));
		nPanelCenterLeft = new JPanel(new BorderLayout());
		nPanelCenterRight = new JPanel(new BorderLayout());

		btnConnect = new JButton("접속");
		btnConnect.addActionListener(listener);
		btnConnect.setActionCommand("connection");
		btnConnect.setFocusable(false);
		btnConnect.setBackground(Color.GREEN);
		nPanel.add(btnConnect, "East");
		nPanel.add(nPanelCenter, "Center");
		nPanelCenter.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server info", TitledBorder.LEFT,TitledBorder.CENTER)));

		nPanelCenter.add(nPanelCenterLeft);
		nPanelCenterLeft.add(new JLabel("IP : "), "West");
		tfIP = new JTextField("1.1.1.6", 10);
		tfIP.setActionCommand("connection");
		tfIP.addActionListener(listener);
		nPanelCenterLeft.add(tfIP, "Center");
		nPanelCenter.add(nPanelCenterRight);
		nPanelCenterRight.add(new JLabel("Port : "), "West");
		nPanelCenter.add(nPanelCenterRight);
		tfPort = new JTextField("12345", 10);
		nPanelCenterRight.add(tfPort, "Center");
		tfPort.setActionCommand("connection");
		tfPort.addActionListener(listener);
		nPanelCenter.add(nPanelCenterRight);
		mainPanel.add(nPanel, "North");

		// main.center
		cPanel = new JPanel(new BorderLayout());
		cPanelCenter = new JPanel(new BorderLayout());
		cPanelCenterSouth = new JPanel(new BorderLayout());
		cPanelSouth = new JPanel(new BorderLayout());
		cPanelEast = new JPanel(new BorderLayout());
		cPanelEastCenter = new JPanel(new BorderLayout());
		cPanelEastSouth = new JPanel(new BorderLayout());
		cPanelEastSouthNorth = new JPanel(new GridLayout(2, 2));
		cPanelEastSouthSouth = new JPanel(new BorderLayout());

		// main.center.center
		mainPanel.add(cPanel, "Center");
		cPanel.add(cPanelCenter);
		cPanelCenter.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"chat log", TitledBorder.LEFT,TitledBorder.CENTER)));

		taClientLog = new JTextArea();
		taClientLog.setEditable(false);
		spClientLog = new JScrollPane(taClientLog);
		spClientLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cPanelCenter.add(spClientLog, "Center");
		cPanelCenter.add(cPanelCenterSouth, "South");
		tfMessage = new JTextField();
		tfMessage.setActionCommand("send");
		tfMessage.addActionListener(listener);
		cPanelCenterSouth.add(tfMessage, "Center");
		btnSend = new JButton("send");
		btnSend.addActionListener(listener);
		btnSend.setActionCommand("send");
		btnSend.setEnabled(false);
		cPanelCenterSouth.add(btnSend, "East");

		// main.center.east
		// main.center.east.center
		cPanel.add(cPanelEast, "East");
		cPanelEast.add(cPanelEastCenter, "Center");
		// list 부분 - unselected 구현이라 쓰고 복사라고 읽는다.
		clientList = new JList<String>();
		clientList.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = -1282953634250437799L;
			public void setSelectionInterval(int index0, int index1) {
				if (index0 == index1) {
					if (isSelectedIndex(index0)) {
						removeSelectionInterval(index0, index0);
						return;
					}
				}
				super.setSelectionInterval(index0, index1);
			}
			public void addSelectionInterval(int index0, int index1) {
				if (index0 == index1) {
					if (isSelectedIndex(index0)) {
						removeSelectionInterval(index0, index0);
						return;
					}
					super.addSelectionInterval(index0, index1);
				}
			}

		});

		model = new DefaultListModel<String>();
		clientList.setModel(model);
		clientList.setFixedCellWidth(130);
		spUserList = new JScrollPane(clientList);
		spUserList.setBorder(new TitledBorder("user list"));
		spUserList.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"user list", TitledBorder.CENTER,TitledBorder.CENTER)));
		cPanelEastCenter.add(spUserList, "Center");

		// main.center.east.south
		cPanelEast.add(cPanelEastSouth, "South");
		cPanelEastSouth.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"function", TitledBorder.CENTER,TitledBorder.CENTER)));
		cPanelEastSouth.add(cPanelEastSouthNorth, "North");
		btnFuntion1 = new JButton("Fun1");
		btnFuntion2 = new JButton("Fun2");
		btnFuntion3 = new JButton("Fun3");
		btnFuntion4 = new JButton("Fun4");
		cPanelEastSouthNorth.add(btnFuntion1);
		cPanelEastSouthNorth.add(btnFuntion2);
		cPanelEastSouthNorth.add(btnFuntion3);
		cPanelEastSouthNorth.add(btnFuntion4);
		btnFuntion1.addActionListener(listener);
		btnFuntion2.addActionListener(listener);
		btnFuntion3.addActionListener(listener);
		btnFuntion4.addActionListener(listener);
		btnFuntion1.setActionCommand("F1");
		btnFuntion2.setActionCommand("F2");
		btnFuntion3.setActionCommand("F3");
		btnFuntion4.setActionCommand("F4");
		btnFuntion1.setEnabled(false);
		btnFuntion2.setEnabled(false);
		btnFuntion3.setEnabled(false);
		btnFuntion4.setEnabled(false);
		cPanelEastSouth.add(cPanelEastSouthSouth, "South");
		btnNickname = new JButton("nick");
		btnNickname.addActionListener(listener);
		btnNickname.setActionCommand("nick");
		btnNickname.setEnabled(false);
		cPanelEastSouthSouth.add(btnNickname, "East");
		tfNickname = new JTextField(5);
		tfNickname.addActionListener(listener);
		tfNickname.setActionCommand("nick");
		cPanelEastSouthSouth.add(tfNickname, "Center");

		// main.south
		// 뭔가 붙여서 추가하면 좋겠음.
		// 지금은 빈 공간임.
		// sPanel = new JPanel(new BorderLayout());
		// mainPanel.add(sPanel, "South");
		// JLabel bLabel = new JLabel("이미지를 띄어 볼까?");
		// bLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		// sPanel.add(bLabel);

		f.setVisible(true);

	}

	public static void main(String[] args) {

		new ChatClient();

	}

	// getter and setter
	public JFrame getF() {
		return f;
	}
	public void setF(JFrame f) {
		this.f = f;
	}
	public JPanel getMainPanel() {
		return mainPanel;
	}
	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}
	public JPanel getnPanel() {
		return nPanel;
	}
	public void setnPanel(JPanel nPanel) {
		this.nPanel = nPanel;
	}
	public JPanel getnPanelCenter() {
		return nPanelCenter;
	}
	public void setnPanelCenter(JPanel nPanelCenter) {
		this.nPanelCenter = nPanelCenter;
	}
	public JPanel getnPanelCenterLeft() {
		return nPanelCenterLeft;
	}
	public void setnPanelCenterLeft(JPanel nPanelCenterLeft) {
		this.nPanelCenterLeft = nPanelCenterLeft;
	}
	public JPanel getnPanelCenterRight() {
		return nPanelCenterRight;
	}
	public void setnPanelCenterRight(JPanel nPanelCenterRight) {
		this.nPanelCenterRight = nPanelCenterRight;
	}
	public JPanel getcPanel() {
		return cPanel;
	}
	public void setcPanel(JPanel cPanel) {
		this.cPanel = cPanel;
	}
	public JPanel getcPanelCenter() {
		return cPanelCenter;
	}
	public void setcPanelCenter(JPanel cPanelCenter) {
		this.cPanelCenter = cPanelCenter;
	}
	public JPanel getcPanelCenterSouth() {
		return cPanelCenterSouth;
	}
	public void setcPanelCenterSouth(JPanel cPanelCenterSouth) {
		this.cPanelCenterSouth = cPanelCenterSouth;
	}
	public JPanel getcPanelSouth() {
		return cPanelSouth;
	}
	public void setcPanelSouth(JPanel cPanelSouth) {
		this.cPanelSouth = cPanelSouth;
	}
	public JPanel getcPanelEast() {
		return cPanelEast;
	}
	public void setcPanelEast(JPanel cPanelEast) {
		this.cPanelEast = cPanelEast;
	}
	public JPanel getcPanelEastCenter() {
		return cPanelEastCenter;
	}
	public void setcPanelEastCenter(JPanel cPanelEastCenter) {
		this.cPanelEastCenter = cPanelEastCenter;
	}
	public JPanel getcPanelEastSouth() {
		return cPanelEastSouth;
	}
	public void setcPanelEastSouth(JPanel cPanelEastSouth) {
		this.cPanelEastSouth = cPanelEastSouth;
	}
	public JPanel getcPanelEastSouthNorth() {
		return cPanelEastSouthNorth;
	}
	public void setcPanelEastSouthNorth(JPanel cPanelEastSouthNorth) {
		this.cPanelEastSouthNorth = cPanelEastSouthNorth;
	}
	public JPanel getcPanelEastSouthCenter() {
		return cPanelEastSouthCenter;
	}
	public void setcPanelEastSouthCenter(JPanel cPanelEastSouthCenter) {
		this.cPanelEastSouthCenter = cPanelEastSouthCenter;
	}
	public JPanel getcPanelEastSouthSouth() {
		return cPanelEastSouthSouth;
	}
	public void setcPanelEastSouthSouth(JPanel cPanelEastSouthSouth) {
		this.cPanelEastSouthSouth = cPanelEastSouthSouth;
	}
	public JPanel getsPanel() {
		return sPanel;
	}
	public void setsPanel(JPanel sPanel) {
		this.sPanel = sPanel;
	}
	public JTextField getTfIP() {
		return tfIP;
	}
	public void setTfIP(JTextField tfIP) {
		this.tfIP = tfIP;
	}
	public JTextField getTfPort() {
		return tfPort;
	}
	public void setTfPort(JTextField tfPort) {
		this.tfPort = tfPort;
	}
	public JTextField getTfNickname() {
		return tfNickname;
	}
	public void setTfNickname(JTextField tfNickname) {
		this.tfNickname = tfNickname;
	}
	public JTextField getTfMessage() {
		return tfMessage;
	}
	public void setTfMessage(JTextField tfMessage) {
		this.tfMessage = tfMessage;
	}
	public JTextArea getTaClientLog() {
		return taClientLog;
	}
	public void setTaClientLog(JTextArea taClientLog) {
		this.taClientLog = taClientLog;
	}
	public JScrollPane getSpUserList() {
		return spUserList;
	}
	public void setSpUserList(JScrollPane spUserList) {
		this.spUserList = spUserList;
	}
	public JScrollPane getSpClientLog() {
		return spClientLog;
	}
	public void setSpClientLog(JScrollPane spClientLog) {
		this.spClientLog = spClientLog;
	}
	public JList<String> getUserList() {
		return clientList;
	}
	public void setUserList(JList<String> userList) {
		this.clientList = userList;
	}
	public JButton getBtnConnect() {
		return btnConnect;
	}
	public void setBtnConnect(JButton btnConnect) {
		this.btnConnect = btnConnect;
	}
	public JButton getBtnNickname() {
		return btnNickname;
	}
	public void setBtnNickname(JButton btnNickname) {
		this.btnNickname = btnNickname;
	}
	public JButton getBtnFuntion1() {
		return btnFuntion1;
	}
	public void setBtnFuntion1(JButton btnFuntion1) {
		this.btnFuntion1 = btnFuntion1;
	}
	public JButton getBtnFuntion2() {
		return btnFuntion2;
	}
	public void setBtnFuntion2(JButton btnFuntion2) {
		this.btnFuntion2 = btnFuntion2;
	}
	public JButton getBtnFuntion3() {
		return btnFuntion3;
	}
	public void setBtnFuntion3(JButton btnFuntion3) {
		this.btnFuntion3 = btnFuntion3;
	}
	public JButton getBtnFuntion4() {
		return btnFuntion4;
	}
	public void setBtnFuntion4(JButton btnFuntion4) {
		this.btnFuntion4 = btnFuntion4;
	}
	public JButton getBtnNameChange() {
		return btnNameChange;
	}
	public void setBtnNameChange(JButton btnNameChange) {
		this.btnNameChange = btnNameChange;
	}
	public JButton getBtnSend() {
		return btnSend;
	}
	public void setBtnSend(JButton btnSend) {
		this.btnSend = btnSend;
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
	public ActionListener getListener() {
		return listener;
	}
	public void setListener(ActionListener listener) {
		this.listener = listener;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public DefaultListModel<String> getModel() {
		return model;
	}
	public void setModel(DefaultListModel<String> model) {
		this.model = model;
	}

}