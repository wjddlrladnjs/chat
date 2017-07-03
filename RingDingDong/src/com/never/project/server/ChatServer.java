package com.never.project.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ChatServer {
	//GUI변수
	private JFrame f;
	private JPanel mainPanel, nPanel, cPanel, sPanel, sPanelCenter, sPanelSouth;
	private JTextArea taLog;
	private JTextField tfPort;
	private JScrollPane spServerLog;
	private JButton btnOn, btnOff, btnFunction1, btnFunction2, btnFunction3, btnFunction4 ;

	//객체 변수
	private ChatServerThread serverThread;		//서버스레드 객체. 전달용
	
	public String serverChat = " ";
	ActionListener listener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd = arg0.getActionCommand();
			switch(cmd){
			case "a":
				startServer();
				break;
			case "b":
				stopServer();
				break;
			}
		}
	};

	//listener 'a' cmd에 따른 서버켜기 기능 구현 메소드
	//숫자 이외를 입력하거나  port가 공란일 경우 exception의 초기 port 번호로 server에 port 오픈.
	//숫자를 기존의 port 번호 등에 연결하거나 숫자가 0~65535 범위가 넘어 가는 등 int 내에서의 오류는 따로 잡아주지 않으므로 유의할 것.
	void startServer(){
		try{
			int port = Integer.parseInt(tfPort.getText());
			serverThread = new ChatServerThread(this, port);
			new Thread( serverThread ).start();	//ChatServerThread는 Runnable로 구현
		}catch(NumberFormatException e){
			addLog("You typed wrong format port. It will connect with default value.");
			serverThread = new ChatServerThread(this, 12345);	
			new Thread( serverThread ).start();
		}
	}

	//서버가 켜짐에 따라 프레임에 있는 버튼 변화 메소드
	void buttonOnState(){
		btnOn.setEnabled(false);
		btnOff.setEnabled(true);
	}

	//listener 'b' cmd에 따른 서버 끄기 기능 구현 메소드.
	void stopServer(){
		serverThread.stopServerSocket();
		controlStopButton(false);
	}

	void addLog(String msg){
		taLog.append(msg + "\n");
		if(!msg.equals(null)){  // @요한
			serverChat += String.format("%s \n", msg);
			}
		int length = taLog.getText().length(); 
		taLog.setCaretPosition(length);
	}

	public ChatServer() {
		initGUI();
	}
	
	public String myLog(){//@요한
		
		return serverChat; 
	}

	// 서버가 정상적으로 소켓을 생성하면 버튼 상태를 변경한다.
	public void controlStopButton(boolean state) {
		btnOn.setEnabled(!state);
		btnOff.setEnabled(state);
		tfPort.setEnabled(!state);
		btnFunction1.setEnabled(state);
	}
	private void initGUI() {

		f = new JFrame("Server Screen");
		f.setBounds(0, 0, 600, 600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPanel = new JPanel( new BorderLayout() );
		nPanel = new JPanel( new BorderLayout() );
		cPanel = new JPanel( new BorderLayout() );
		sPanel = new JPanel( new GridLayout(2, 2));
		sPanelCenter = new JPanel( new GridLayout(1, 4) );
		sPanelSouth = new JPanel( new GridLayout(1, 2) );

		// main
		f.add(mainPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// main.north
		mainPanel.add(nPanel, "North");
		nPanel.add(new JLabel("Port : "), "West");
		tfPort = new JTextField("12345");
		tfPort.setActionCommand("port");
		tfPort.addActionListener(listener);
		nPanel.add(tfPort, "Center");
		// main.center
		mainPanel.add(cPanel, "Center");
		cPanel.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server log", TitledBorder.CENTER,TitledBorder.CENTER)));
		taLog = new JTextArea();
		taLog.setEditable(false);
		spServerLog = new JScrollPane(taLog);
		cPanel.add(spServerLog, "Center");
		// main.south
		mainPanel.add(sPanel, "South");
		sPanel.add(sPanelCenter, "Center");
		btnFunction1 = new JButton("Log Save");
		btnFunction2 = new JButton("fun2");
		btnFunction3 = new JButton("fun3");
		btnFunction4 = new JButton("fun4");
		btnFunction1.setActionCommand("c");
		btnFunction2.setActionCommand("F2");
		btnFunction3.setActionCommand("F3");
		btnFunction4.setActionCommand("F4");
		btnFunction1.addActionListener(listener);
		btnFunction2.addActionListener(listener);
		btnFunction3.addActionListener(listener);
		btnFunction4.addActionListener(listener);
		btnFunction1.setEnabled(false);
		btnFunction2.setEnabled(false);
		btnFunction3.setEnabled(false);
		btnFunction4.setEnabled(false);
		sPanelCenter.add(btnFunction1);
		sPanelCenter.add(btnFunction2);
		sPanelCenter.add(btnFunction3);
		sPanelCenter.add(btnFunction4);
		btnOn = new JButton("TurnOn");
		btnOff = new JButton("TurnOff");
		btnOn.setActionCommand("a");
		btnOff.setActionCommand("b");
		btnOn.addActionListener(listener);
		btnOff.addActionListener(listener);
		btnOff.setEnabled(false);
		sPanel.add(sPanelSouth, "South");
		sPanelSouth.add(btnOn);
		sPanelSouth.add(btnOff);

		f.setVisible(true);

	}

	public static void main(String[] args) {
		new ChatServer();
	}

}
