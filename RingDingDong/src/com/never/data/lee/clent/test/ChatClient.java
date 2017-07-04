package com.never.data.lee.clent.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ChatClient {
	//GUI 변수
	private JFrame f;
	private JPanel mainPanel, nPanel, nPanelCenter, nPanelCenterLeft, nPanelCenterRight,
	cPanel, cPanelCenter, cPanelCenterSouth, cPanelSouth, cPanelEast, cPanelEastCenter, cPanelEastSouth, 
	cPanelEastSouthNorth,  cPanelEastSouthCenter, cPanelEastSouthSouth,
	sPanel;
	private JTextField tfIP, tfPort, tfName, tfText;
	private JTextArea taLog;
	private JScrollPane spUserList, spClientLog;
	private JButton btn1, btn2, btnNickname, btnServerLog, btnImgBg, btnFuntion3, btnFuntion4, btnNameChange;
	private DefaultListModel<String> model;
	private JList<String> list;
	//네트워크 변수
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	//이미지용 변ㅇ수
	JButton btnChangeBgrImg;
	JFileChooser fc;
	Image img;
	File file;
	FileInputStream fis = null;
	FileOutputStream fos = null;
	
	// (임시) 아이피 사용을 쉽게 하기 위한 메서드.
	private void getClientIP() {

		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			tfIP.setText(ip);
		} catch (UnknownHostException e) {
			addLog("클라 IP를 읽어올 수 없습니다. :" + e);
		}  

	}
	//버튼에 따른 이벤트 리스너
	ActionListener listener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd = arg0.getActionCommand();
			switch(cmd){
			case "A":
				startClient();
				break;
			case "B":				
				sendMessage();
				tfText.grabFocus();
				break;
			case "C":				
				changeNickName();
				break;
			case "0" :  //@요한
				serverLogDown();//@요한 
				break; 
			case "i":
				changeBgrImg();
				break;
			case "disconnection":
				disconnectServer();
				break;
			}
		}		
	};
	
	synchronized void  serverLogDown(){ //@요한
		try {
			dos.writeChar('1');
			dos.flush();
//			dos.writeUTF(name); //네임 
//			dos.flush();
			
		} catch (IOException e) {}
	}
	//@김
	void changeBgrImg(){
		
		fc = new JFileChooser(".");
		
		FileFilter ff = new FileNameExtensionFilter("Image files", "jpg", "png");
        fc.addChoosableFileFilter(ff);
		
		int returnVal = fc.showOpenDialog(f);
		
		int tlength = (int) fc.getSelectedFile().length();
		System.out.println("tlength: " + tlength);
		
		System.out.println("returnVal: " + returnVal);
		System.out.println("JFilechooser: " + JFileChooser.APPROVE_OPTION);
		boolean fileChosen = false;
		/////////////////////////////////////////////////////////////
		if(returnVal == JFileChooser.APPROVE_OPTION){ //if approved(yes || select)
			file = fc.getSelectedFile();	//선택된 파일을 얻는다.
			if(file == null) fileChosen = true;
			System.out.println("filechosen: " + fileChosen);
			
			if(file == null){	//null이면 소용없게.
				return;
			}
			System.out.println(tlength);		//왜안되지?
			System.out.println(file.getName());
		}
		
		if(file.getName().isEmpty())System.out.println("응 파일네임 없어~");
		addLog("@@ " +  file.getName() + "을 선택하였습니다. @@");
		
		//서버로 보내자
		sendImageFile(file, tlength);
		
		//나의 배경화면을 바꾼다
		//setBgrImageAs(file);
		
	}
	
	void sendImageFile(File file, int tempLength){
		int data = 0;	//데이타의 크기
		int length = 0;	//파일의 크기
		String fileName = "";
		byte[] brr = null; //데이타가 들어갈 배열
		try {
			fis = new FileInputStream(file);
			addLog("파일이 인풋스트림에 들어왔습니다.");
			System.out.println("이미지파일이 인풋스트림에 들어왓다: " + file.length());
			length = tempLength;
			System.out.println("length:" + length);
			
			if(length == 0){	//크기 0이면 리턴
				addLog("파일 크기가 너무 작거나 파일이 존재하지 않습니다.");
				return;
			}
			
			fileName = file.getName();
			brr = new byte[length];	//length만큼 byte배열 잡아줌
			System.out.println(":brr.length: " + brr.length);
			
			DataInputStream fileDis = new DataInputStream(fis);
			fileDis.readFully(brr, 0, length);
			System.out.println(":brr.length: " + brr.length);
			addLog("DIS가 배열 brr에 파일 길이만큼 저장했습니다.");
			
			//프로토콜 보낸다
			dos.writeChar('i');
			dos.flush();
			dos.writeUTF(fileName);
			dos.flush();
			dos.writeInt(length);
			dos.flush();
			
			dos.writeChar('A');
			
			System.out.println("ma! 이제 brr.length 간다");
			dos.write(brr, 0, length);	//쓴다
			System.out.println("ma! 이제 brr.length 갔다");
			dos.flush();
			
			System.out.println(brr.length);
			
			addLog(String.format("서버로 파일(파일명: %s, 크기: %d)을 보냅니다..",
					fileName, length));
			
		} catch (FileNotFoundException e) {
			addLog("파일 출력 에라: " + e);
		} catch (IOException e) {
			addLog("I/O 에러: " + e);
		}
		
	}
	
	
	
	
	//client 시작. 입력오류에 따른 exception은 port에 숫자가 아닌 다른 입력이 오거나 ip와 port 중 빈 값이 있을 때만 발생한다.
	//ip에 string은 넣지만 ip 주소 이외의 값을 넣거나 port에 숫자는 넣지만 port 규정에 어긋나는 값을 넣을 경우 
	//exception은 발생하지 않고 client와 server 모두(?) 죽을 수 있으니 유의하길.
	//본 메소드는 유효성을 ip와 port에 대한 유효성 검사만 함.
	void startClient(){
		try{
			String ipAddr = tfIP.getText();
			int port = Integer.parseInt(tfPort.getText());
			addLog("Client is ready for connect...");
			commenceClient(ipAddr, port);			
		}catch(NumberFormatException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.14",12345);	//초기값 설정		
		}catch(NullPointerException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.14",12345);			
		}
	}

	//Client 시작을 위한 메소드
	void commenceClient(String ipAddr, int port){
		try {
			socket = new Socket(ipAddr, port);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			String name = nickName();
			changeButton(true);
			dos.writeUTF(name);
			String hello = dis.readUTF();
			addLog(hello);
			new ChatReadThread(this, dis).start();
		} catch (UnknownHostException e) {
			addLog("Socket not connect b/c "+e);
		} catch (IOException e1) {
			addLog("Socket not connect b/c "+e1);
		}
	}

	//서버로 메세지 전송 메소드
	void sendMessage(){
		String rName ; 
		String text = tfText.getText();	//@요한
		if(!text.equals("exit")){
			rName =list.getSelectedValue();
		try{
			if(!tfText.getText().equals(null)){		
				if(rName == null){
				dos.writeChar('M');
				dos.flush();
				}else{
					dos.writeChar('5');
					dos.flush();
					dos.writeUTF(rName);
					dos.flush();
				}
				dos.writeUTF(text);
				dos.flush();

			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfText.setText("");
		}
		}else if(text.equals("exit")){
			list.clearSelection();
		}
		tfText.setText("");				
	}


	//이름 변경 메소드
	void changeNickName(){
		try{
			if(!tfName.getText().equals(null)){		

				String text = tfName.getText();	
				dos.writeChar('N');
				dos.writeUTF(text);
				dos.flush();
				//				text = dis.readUTF();
				//				setChatWindowName(text);
				tfName.setText("");				
			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfName.setText("");
		}
	}

	//참여한 유저 목록
	public void addUserList(String[] name){
		for(String nameInList : name){
			int idx = model.indexOf(nameInList);
			if(idx != -1){
				model.setElementAt(nameInList, idx);
			}else{
			model.addElement(nameInList);
			}
		}
	}

	public void updateUser(String oldName,String newName){
		int idx = model.indexOf(oldName);
		if(idx != -1){
			model.setElementAt(newName, idx);
		}
	}

	//	public void changeUser(String[] names){
	//		model.size()
	//	}


	//user의 window창 종료로 인한 퇴장시 적용 메소드
	void exitClient(){
		if(dos != null){
			try{
				dos.writeChar('X');
				dos.flush();
				if(dos != null)	dos.close();	
				if(dis != null)	dis.close();
				if(socket != null)	socket.close();
			}catch(IOException e){

			}
		}
	}	

	//대화명 변경시 유효성 검사 메소드
	String nickName(){
		String nickName = "";
		nickName = tfName.getText();
		if(nickName.equals(null) || nickName.equals(""))	nickName = "User";
		//		setChatWindowName(String.format("Client[%s] Screen", nickName));
		return nickName;
	}

	//client log창 업데이트
	void addLog(String msg){
		taLog.append(msg+"\n");
	}

	//client 창 제목 업데이트
	void setChatWindowName(String msg){
		f.setTitle(String.format("Chatting Client[ %s ]", msg));
	}

	public ChatClient() {
		initGUI();
		getClientIP();
	}
	// 서버와 연결되었을 때 버튼의 상태 변경 메서드.
	public void changeButton( boolean state ) {

		btn2.setEnabled(state);
		btnNickname.setEnabled(state);
		btnServerLog.setEnabled(state);
		btnImgBg.setEnabled(state);

		// 현재 상태에 따라 분기.
		if( state ) {
			btn1.setText("Disconnect");
			btn1.setBackground(Color.RED);
			btn1.setActionCommand("disconnection");
			tfPort.setEnabled(!state);
			tfIP.setEnabled(!state);
			tfText.grabFocus();

		} else {
			btn1.setText("Connect");
			btn1.setBackground(Color.GREEN);
			btn1.setActionCommand("A");
			tfPort.setEnabled(!state);
			tfIP.setEnabled(!state);
			tfIP.grabFocus();
		}
	}

	// 서버와 접속을 종료하는 메서드.
	private void disconnectServer() {

		doExitEvent(5);
		model.clear();
		changeButton(false);

	}
	public void doExitEvent(int state) {
		// 종료인지 접속 종료인지 판단.
		if( state == 0 ) {
			// X 버튼이면 다 닫고 종료.
			System.exit(0);
		}
	}
	//GUI 구현 메소드
	//GUI 구현 메소드
	void initGUI() {

		f = new JFrame("Client Screen");
		f.setBounds(0, 600, 600, 500);
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

		btn1 = new JButton("Connect");
		btn1.setActionCommand("A");
		btn1.addActionListener(listener);
		btn1.setFocusable(false);
		btn1.setBackground(Color.GREEN);
		nPanel.add(btn1, "East");
		nPanel.add(nPanelCenter, "Center");
		nPanelCenter.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server info", TitledBorder.LEFT,TitledBorder.CENTER)));

		nPanelCenter.add(nPanelCenterLeft);
		nPanelCenterLeft.add(new JLabel("IP : "), "West");
		tfIP = new JTextField("1.1.1.6", 10);
		tfIP.setActionCommand("A");
		tfIP.addActionListener(listener);
		nPanelCenterLeft.add(tfIP, "Center");
		nPanelCenter.add(nPanelCenterRight);
		nPanelCenterRight.add(new JLabel("Port : "), "West");
		nPanelCenter.add(nPanelCenterRight);
		tfPort = new JTextField("12345", 10);
		nPanelCenterRight.add(tfPort, "Center");
		tfPort.setActionCommand("A");
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

		taLog = new JTextArea();
		taLog.setEditable(false);
		spClientLog = new JScrollPane(taLog);
		spClientLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cPanelCenter.add(spClientLog, "Center");
		cPanelCenter.add(cPanelCenterSouth, "South");
		tfText = new JTextField();
		tfText.setActionCommand("B"); // enter로 action command 값 줌
		tfText.addActionListener(listener); // enter로 action command 값 줌
		cPanelCenterSouth.add(tfText, "Center");
		btn2 = new JButton("send");
		btn2.setActionCommand("B");
		btn2.addActionListener(listener);
		btn2.setEnabled(false);
		cPanelCenterSouth.add(btn2, "East");

		// main.center.east
		// main.center.east.center
		cPanel.add(cPanelEast, "East");
		cPanelEast.add(cPanelEastCenter, "Center");
		// list 부분 - unselected 구현이라 쓰고 복사라고 읽는다.
		list = new JList<String>();
		list.setSelectionModel(new DefaultListSelectionModel() {
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
		list.setModel(model);
		list.setFixedCellWidth(130);
		spUserList = new JScrollPane(list);
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
		btnServerLog = new JButton("Get Log");
		btnImgBg = new JButton("Image gb");
		btnFuntion3 = new JButton("Fun3");
		btnFuntion4 = new JButton("Fun4");
		cPanelEastSouthNorth.add(btnServerLog);
		cPanelEastSouthNorth.add(btnImgBg);
		cPanelEastSouthNorth.add(btnFuntion3);
		cPanelEastSouthNorth.add(btnFuntion4);
		btnServerLog.addActionListener(listener);
		btnImgBg.addActionListener(listener);
		btnFuntion3.addActionListener(listener);
		btnFuntion4.addActionListener(listener);
		btnServerLog.setActionCommand("0");
		btnImgBg.setActionCommand("i");
		btnFuntion3.setActionCommand("F3");
		btnFuntion4.setActionCommand("F4");
		btnServerLog.setEnabled(false);
		btnImgBg.setEnabled(false);
		btnFuntion3.setEnabled(false);
		btnFuntion4.setEnabled(false);
		cPanelEastSouth.add(cPanelEastSouthSouth, "South");
		btnNickname = new JButton("nick");
		btnNickname.addActionListener(listener);
		btnNickname.setActionCommand("C");
		btnNickname.setEnabled(false);
		cPanelEastSouthSouth.add(btnNickname, "East");
		tfName = new JTextField(5);
		tfName.addActionListener(listener);
		tfName.setActionCommand("nick");
		cPanelEastSouthSouth.add(tfName, "Center");

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

}
