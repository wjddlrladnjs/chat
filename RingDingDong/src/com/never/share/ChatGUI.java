package com.never.share;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

public class ChatGUI {

	JFrame f;
	JPanel mainPanel, nPanel, nPanelCenter, nPanelCenterLeft, nPanelCenterRight,
	cPanel, cPanelCenter, cPanelCenterSouth, cPanelSouth, cPanelEast, cPanelEastCenter, cPanelEastSouth, 
	cPanelEastSouthNorth,  cPanelEastSouthCenter, cPanelEastSouthSouth,
	sPanel;
	JTextField tfIP, tfPort, tfNickname, tfMessage;
	
	JTextArea taChatLog;
	// JEditorPane epChatLog;
	JScrollPane spUserList, spChatLog;
	JList<String> userList;
	JButton btnConnect, btnNickname, btnFun1, btnFun2, btnFun3, btnFun4, btnNameChange, btnSend;

	String taImgPath;
	Image img;
	
	ActionListener listener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			String cmd = e.getActionCommand();
			
			switch( cmd ) {
			case "A":

				break;
				
			default:
				break;
			}
		}
	};
	
	public ChatGUI() {
		initGUI();
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
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		f.add(mainPanel);

		// main.north
		nPanel = new JPanel(new BorderLayout());
		nPanelCenter = new JPanel(new GridLayout(1, 2));
		nPanelCenterLeft = new JPanel(new BorderLayout());
		nPanelCenterRight = new JPanel(new BorderLayout());

		btnConnect = new JButton("접속");
		nPanel.add(btnConnect, "East");
		nPanel.add(nPanelCenter, "Center");

		nPanelCenter.add(nPanelCenterLeft);
		nPanelCenterLeft.add(new JLabel("IP : "), "West");
		nPanelCenterLeft.setBackground(Color.WHITE);
		tfIP = new JTextField("1.1.1.6", 10);
		nPanelCenterLeft.add(tfIP, "Center");
		nPanelCenter.add(nPanelCenterRight);
		nPanelCenterRight.add(new JLabel("Port : "), "West");
		nPanelCenterRight.setBackground(Color.WHITE);
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

		taImgPath = "./src/com/never/data/jung/chat/file/a.jpg";
		taChatLog = new JTextArea();
		spChatLog = new JScrollPane(taChatLog);
		// taChatLog = createTextArea(taImgPath);
		// editer 실험중
		// epChatLog = new JEditorPane();
		// spChatLog = new JScrollPane(epChatLog);
		// 가로 스크롤 나오지 않게 함.
		spChatLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cPanelCenter.add(spChatLog, "Center");
		cPanelCenter.add(cPanelCenterSouth, "South");
		tfMessage = new JTextField();
		cPanelCenterSouth.add(tfMessage, "Center");
		btnSend = new JButton("send");
		cPanelCenterSouth.add(btnSend, "East");

		// main.center.east
		// main.center.east.center
		cPanel.add(cPanelEast, "East");
		cPanelEast.add(cPanelEastCenter, "Center");
		userList = new JList<String>();
		userList.setFixedCellWidth(130);
		spUserList = new JScrollPane(userList);
		spUserList.setBackground(Color.WHITE);
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
		btnFun1 = new JButton("Fun1");
		btnFun2 = new JButton("Fun2");
		btnFun3 = new JButton("Fun3");
		btnFun4 = new JButton("Fun4");
		cPanelEastSouthNorth.add(btnFun1);
		cPanelEastSouthNorth.add(btnFun2);
		cPanelEastSouthNorth.add(btnFun3);
		cPanelEastSouthNorth.add(btnFun4);
		cPanelEastSouth.add(cPanelEastSouthSouth, "South");
		btnNickname = new JButton("nick");
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

		new ChatGUI();

	}

}
