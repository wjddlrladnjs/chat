package com.never.data.jung.chat.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatServer {

	JFrame f;
	JPanel mainPanel, nPanel, cPanel, sPanel,
			sPanelCenter, sPanelSouth;
	JTextArea taChatLog;
	JTextField tfPort;
	JButton btnStart, btnStop, btnFuntion1, btnFuntion2, btnFuntion3, btnFuntion4 ;
	
	public ChatServer() {
		initGUI();
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
		taChatLog = new JTextArea();
		cPanel.add(taChatLog, "Center");
		// main.south
		mainPanel.add(sPanel, "South");
		sPanel.add(sPanelCenter, "Center");
		btnFuntion1 = new JButton("fun1");
		btnFuntion2 = new JButton("fun2");
		btnFuntion3 = new JButton("fun3");
		btnFuntion4 = new JButton("fun4");
		sPanelCenter.add(btnFuntion1);
		sPanelCenter.add(btnFuntion2);
		sPanelCenter.add(btnFuntion3);
		sPanelCenter.add(btnFuntion4);
		btnStart = new JButton("start");
		btnStop = new JButton("stop");
		sPanel.add(sPanelSouth, "South");
		sPanelSouth.add(btnStart);
		sPanelSouth.add(btnStop);
		
		f.setVisible(true);
		
	}
	
	public static void main(String[] args) {

		new ChatServer();
		
	}

}
