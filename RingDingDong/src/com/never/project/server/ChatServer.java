package com.never.project.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

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

	// 다른 클래스에서 접근 하기 위해 맴버 변수로 선언.
	private JFrame f;
	private JPanel mainPanel, nPanel, cPanel, sPanel, sPanelCenter, sPanelSouth;
	private JTextArea taServerLog;
	private JTextField tfPort;
	private JScrollPane spServerLog;
	private JButton btnStart, btnStop, btnFunction1, btnFunction2, btnFunction3, btnFunction4 ;
	private ServerThread serverThread;
	private int port;
	public static String serverTime;
	
	ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			String cmd = e.getActionCommand();
			
			switch( cmd ) {
			case "startServer": case "port":
				StartServer();
				btnStop.grabFocus();
				break;
			case "stopServer":
				stopServer();
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
		getServerIP();
	}
	// 서버를 정지하거나 껐을 때 호출.
	private void stopServer() {
		
		String msg = String.format("%s# 서버가 정지되었습니다.", serverTime);
		if( serverThread != null ) {
			char protocol = 'Z';
			int command = -4;
			serverThread.sendAdminMessage( protocol, command, msg );
			serverThread.stopServer();
		} else {
			System.exit(0);
		}
		
	}
	
	// 시간을 남겨보자.
	public void setServerTime(){
	
		GregorianCalendar cal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(cal.getTime());
		serverTime = "["+time+"]";
		
	}
	
	// server log 메시지 띄우는 메서드.
	public void appendServerLog( String msg ) {
		setServerTime();
		int length = taServerLog.getText().length();
		taServerLog.append(serverTime + msg + "\n");
		// 자동 스크롤링
		taServerLog.setCaretPosition( length );
		
	}
	// server의 IP 받아서  출력하는 메서드.
	private void getServerIP() {

		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			appendServerLog("Server IP :" + ip);
		} catch (UnknownHostException e) {
			appendServerLog("서버 IP를 읽어올 수 없습니다. :" + e);
		}  
		
	}

	// 서버가 정상적으로 소켓을 생성하면 버튼 상태를 변경한다.
	public void controlStopButton(boolean state) {
		btnStart.setEnabled(!state);
		btnStop.setEnabled(state);
		tfPort.setEnabled(!state);
	}

	// start 버튼 눌렸을 때 동작할 메서드.
	private void StartServer() {
		
		port = 12345;
		String inputPort = tfPort.getText();
		
		// 입력된 port가 잘못되었을 때 처리.
		if( inputPort != null && !"".equals(inputPort) ) {
			try {
				port = Integer.parseInt(inputPort);
				if( port < 1 || port > 65535) {
					throw new NumberFormatException();
				}
				appendServerLog("서버 Port : " + port);
				
			} catch( NumberFormatException e ) {
				appendServerLog("잘못된 입력입니다.");
				tfPort.selectAll();
				tfPort.grabFocus();

				return;
			} 
		}
		// 입력된 포트가 정상일 때 진행.
		tfPort.setEnabled(false);
		serverThread = new ServerThread( this );
		new Thread(serverThread).start();
		
	}

	private void initGUI() {
		
		f = new JFrame("Chat Server");
		f.setBounds(0, 0, 500, 600);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {stopServer();}
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
		tfPort.setActionCommand("port");
		tfPort.addActionListener(listener);
		nPanel.add(tfPort, "Center");
		// main.center
		mainPanel.add(cPanel, "Center");
		cPanel.setBorder(new TitledBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"server log", TitledBorder.CENTER,TitledBorder.CENTER)));
		taServerLog = new JTextArea();
		taServerLog.setEditable(false);
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
		btnStart.setActionCommand("startServer");
		btnStop.setActionCommand("stopServer");
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

	
	// 다른 클래스에서 맴버에 접근하기 위한 getter와 setter.
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
	public JPanel getcPanel() {
		return cPanel;
	}
	public void setcPanel(JPanel cPanel) {
		this.cPanel = cPanel;
	}
	public JPanel getsPanel() {
		return sPanel;
	}
	public void setsPanel(JPanel sPanel) {
		this.sPanel = sPanel;
	}
	public JPanel getsPanelCenter() {
		return sPanelCenter;
	}
	public void setsPanelCenter(JPanel sPanelCenter) {
		this.sPanelCenter = sPanelCenter;
	}
	public JPanel getsPanelSouth() {
		return sPanelSouth;
	}
	public void setsPanelSouth(JPanel sPanelSouth) {
		this.sPanelSouth = sPanelSouth;
	}
	public JTextArea getTaServerLog() {
		return taServerLog;
	}
	public void setTaServerLog(JTextArea taServerLog) {
		this.taServerLog = taServerLog;
	}
	public JTextField getTfPort() {
		return tfPort;
	}
	public void setTfPort(JTextField tfPort) {
		this.tfPort = tfPort;
	}
	public JScrollPane getSpServerLog() {
		return spServerLog;
	}
	public void setSpServerLog(JScrollPane spServerLog) {
		this.spServerLog = spServerLog;
	}
	public JButton getBtnStart() {
		return btnStart;
	}
	public void setBtnStart(JButton btnStart) {
		this.btnStart = btnStart;
	}
	public JButton getBtnStop() {
		return btnStop;
	}
	public void setBtnStop(JButton btnStop) {
		this.btnStop = btnStop;
	}
	public JButton getBtnFunction1() {
		return btnFunction1;
	}
	public void setBtnFunction1(JButton btnFunction1) {
		this.btnFunction1 = btnFunction1;
	}
	public JButton getBtnFunction2() {
		return btnFunction2;
	}
	public void setBtnFunction2(JButton btnFunction2) {
		this.btnFunction2 = btnFunction2;
	}
	public JButton getBtnFunction3() {
		return btnFunction3;
	}
	public void setBtnFunction3(JButton btnFunction3) {
		this.btnFunction3 = btnFunction3;
	}
	public JButton getBtnFunction4() {
		return btnFunction4;
	}
	public void setBtnFunction4(JButton btnFunction4) {
		this.btnFunction4 = btnFunction4;
	}
	public ServerThread getsThread() {
		return serverThread;
	}
	public void setsThread(ServerThread sThread) {
		this.serverThread = sThread;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ActionListener getListener() {
		return listener;
	}
	public void setListener(ActionListener listener) {
		this.listener = listener;
	}
	
}
