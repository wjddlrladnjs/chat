package com.never.data.jung.chat.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

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

	JFrame f;
	JPanel mainPanel, nPanel, cPanel, sPanel, sPanelCenter, sPanelSouth;
	JTextArea taServerLog;
	JTextField tfPort;
	JScrollPane spServerLog;
	JButton btnStart, btnStop, btnFunction1, btnFunction2, btnFunction3, btnFunction4 ;

	ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			String cmd = e.getActionCommand();
			
			switch( cmd ) {
			case "A":
				StartServer();
				break;
			case "F1":
				getServerIP();
				break;
			}
			
		}
	};
	
	// 생성자
	public ChatServer() {
		initGUI();
	}
	// server Log에 메시지를 띄웁니다.
	public void appendServerLog( String msg ) {
		int length = taServerLog.getText().length();
		taServerLog.append(msg + "\n");
		// 자동 스크롤링
		taServerLog.setCaretPosition( length );
		
	}
	
	// server의 IP를 알아옵니다.
	private void getServerIP() {

		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			appendServerLog(ip);
		} catch (UnknownHostException e) {
		}  
		
	}

	// 서버를 시작합니다.
	private void StartServer() {
		
		
		
		
	}

	private void initGUI() {
		
		f = new JFrame("Chat Server");
		f.setBounds(0, 0, 500, 600);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});
		
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
		nPanel.add(tfPort, "Center");
		// main.center
		mainPanel.add(cPanel, "Center");
		cPanel.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server log", TitledBorder.CENTER,TitledBorder.CENTER)));
		taServerLog = new JTextArea();
		spServerLog = new JScrollPane(taServerLog);
		cPanel.add(spServerLog, "Center");
		// main.south
		mainPanel.add(sPanel, "South");
		sPanel.add(sPanelCenter, "Center");
		btnFunction1 = new JButton("fun1");
		btnFunction2 = new JButton("fun2");
		btnFunction3 = new JButton("fun3");
		btnFunction4 = new JButton("fun4");
		btnFunction1.setActionCommand("F1");
		btnFunction2.setActionCommand("F2");
		btnFunction3.setActionCommand("F3");
		btnFunction4.setActionCommand("F4");
		btnFunction1.addActionListener(listener);
		btnFunction2.addActionListener(listener);
		btnFunction3.addActionListener(listener);
		btnFunction4.addActionListener(listener);
		btnFunction2.setEnabled(false);
		btnFunction3.setEnabled(false);
		btnFunction4.setEnabled(false);
		sPanelCenter.add(btnFunction1);
		sPanelCenter.add(btnFunction2);
		sPanelCenter.add(btnFunction3);
		sPanelCenter.add(btnFunction4);
		btnStart = new JButton("start");
		btnStop = new JButton("stop");
		btnStart.setActionCommand("A");
		btnStop.setActionCommand("B");
		btnStart.addActionListener(listener);
		btnStop.addActionListener(listener);
		btnStop.setEnabled(false);
		sPanel.add(sPanelSouth, "South");
		sPanelSouth.add(btnStart);
		sPanelSouth.add(btnStop);
		
		f.setVisible(true);
		
	}
	
	public static void main(String[] args) {

		new ChatServer();
		
	}

}
