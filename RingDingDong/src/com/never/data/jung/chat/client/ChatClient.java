package com.never.data.jung.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

	JFrame f;
	JPanel mainPanel, nPanel, nPanelCenter, nPanelCenterLeft, nPanelCenterRight,
	cPanel, cPanelCenter, cPanelCenterSouth, cPanelSouth, cPanelEast, cPanelEastCenter, cPanelEastSouth, 
	cPanelEastSouthNorth,  cPanelEastSouthCenter, cPanelEastSouthSouth,
	sPanel;
	JTextField tfIP, tfPort, tfNickname, tfMessage;
	
	JTextArea taClientLog;
	// JEditorPane epChatLog;
	JScrollPane spUserList, spClientLog;
	JList<String> userList;
	JButton btnConnect, btnNickname, btnFuntion1, btnFuntion2, btnFuntion3, btnFuntion4, btnNameChange, btnSend;

	Socket s;
	
	String taImgPath;
	Image img;
	
	ActionListener listener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			String cmd = e.getActionCommand();
			
			switch( cmd ) {
			case "connection":
				connectServer();
				break;
				
			default:
				break;
			}
		}
	};
	
	public ChatClient() {
		initGUI();
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
					throw new NumberFormatException();
				}
				
				s = new Socket(inputIP, port);
			} catch( IOException e ) {
				appendClientLog("소켓 생성에 실패했습니다." + e.toString());
				tfIP.selectAll();
				tfPort.selectAll();
				tfIP.grabFocus();
				
				return;
			} catch( NumberFormatException e ) {
				appendClientLog("잘못된 입력입니다.");
				tfIP.selectAll();
				tfPort.selectAll();
				tfIP.grabFocus();

				return;
			} 
		} // if end.
		appendClientLog("소켓 연결 성공!");
	}
	
	
	private void initGUI() {

		f = new JFrame("Chatting Client");
		f.setBounds(0, 600, 500, 400);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 클라이언트 종료.
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
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
		nPanel.add(btnConnect, "East");
		nPanel.add(nPanelCenter, "Center");
		nPanelCenter.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server info", TitledBorder.LEFT,TitledBorder.CENTER)));

		nPanelCenter.add(nPanelCenterLeft);
		nPanelCenterLeft.add(new JLabel("IP : "), "West");
		tfIP = new JTextField("1.1.1.6", 10);
		nPanelCenterLeft.add(tfIP, "Center");
		nPanelCenter.add(nPanelCenterRight);
		nPanelCenterRight.add(new JLabel("Port : "), "West");
		nPanelCenter.add(nPanelCenterRight);
		tfPort = new JTextField("12345", 10);
		nPanelCenterRight.add(tfPort, "Center");
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
		

		taImgPath = "./src/com/never/data/jung/chat/file/a.jpg";
		taClientLog = new JTextArea();
		spClientLog = new JScrollPane(taClientLog);
		// taChatLog = createTextArea(taImgPath);
		// editer 실험중
		// epChatLog = new JEditorPane();
		// spChatLog = new JScrollPane(epChatLog);
		// 가로 스크롤 나오지 않게 함.
		spClientLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cPanelCenter.add(spClientLog, "Center");
		cPanelCenter.add(cPanelCenterSouth, "South");
		tfMessage = new JTextField();
		cPanelCenterSouth.add(tfMessage, "Center");
		btnSend = new JButton("send");
		btnSend.addActionListener(listener);
		btnSend.setActionCommand("send");
		cPanelCenterSouth.add(btnSend, "East");

		// main.center.east
		// main.center.east.center
		cPanel.add(cPanelEast, "East");
		cPanelEast.add(cPanelEastCenter, "Center");
		userList = new JList<String>();
		userList.setFixedCellWidth(130);
		spUserList = new JScrollPane(userList);
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
		cPanelEastSouth.add(cPanelEastSouthSouth, "South");
		btnNickname = new JButton("nick");
		btnNickname.addActionListener(listener);
		btnNickname.setActionCommand("nick");
		cPanelEastSouthSouth.add(btnNickname, "East");
		tfNickname = new JTextField(5);
		cPanelEastSouthSouth.add(tfNickname, "Center");

		// main.south
		// 뭔가 붙여서 추가하면 좋겠음.
		// 지금은 빈 공간임.
		//		sPanel = new JPanel(new BorderLayout());
		//		mainPanel.add(sPanel, "South");
		//		JLabel bLabel = new JLabel("이미지를 띄어 볼까?");
		//		bLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		//		sPanel.add(bLabel);

		f.setVisible(true);

	}
	
	// 이미지 변경을  실험중.
//	private JTextArea createTextArea(String taImgPath) {
//		img = new ImageIcon(taImgPath).getImage();
//		taChatLog = new JTextArea(){
//			{ setOpaque( false ) ; }
//			public void paintComponent(Graphics g){
//				g.drawImage(img,0,0,null);
//				super.paintComponent(g);
//			}
//		};
//		return taChatLog;
//	}
//
	public static void main(String[] args) {

		new ChatClient();

	}

}
