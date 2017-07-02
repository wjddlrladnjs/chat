package com.never.share;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MakeFileExample {
	
	JFrame f;
	JPanel p;
	String[] titles = {"기본", "file 상태정보", "directory info"
			, "이름 바꾸기", "폴더 만들기"};	//버튼 패턴화시켜서 여기만늘리면늘어나죠?
	String[] cmds = {"A", "B", "C", "D", "E"};
	
	ActionListener al = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//String cmd = e.getActionCommand();
			switch(e.getActionCommand()){
			case "A":
				doThis("a.txt");//폴더는 윈도명칭, 공식은 path
				break;
			case "B":
				doAction2("a.ser");
				break;
			case "C":
				doAction3("fData"); //c:\fData
				break;
			case "D":
				doAction4("a.txt", "x.txt"); //c:\fData
				break;
			case "E":
				doAction5("C:\\javaDev"); //c:\fData
				break;
			}
		}
	};
	
	public void doThis(String fileName){
		//String path = "C:" + File.separator + "\\fData";
		String path = "C:\\fData";	// \\해야 ㄹㅇ 역슬래시
//		File f = new File(path, fileName);
		File f = new File("."); // .은현재경로
		boolean exists = f.exists(); //존재하는 파일넴 가질수없ㅇ므로..
		System.out.println(exists ? "응 있어" : "응 없어");
		
		
		String name = f.getName();
		System.out.println("name: " + name);
			
		String paths = f.getPath();
		System.out.println("paths: " + paths);
		
		String absolutePath = f.getAbsolutePath();
		System.out.println("absolutePath: " + absolutePath);
		
		String parent = f.getParent();
		System.out.println("parent: " + parent);

		String canonicalPath = null;
		try {
			canonicalPath = f.getCanonicalPath();
		} catch (IOException e) {}
		
		System.out.println("canonicalPath: " + canonicalPath);
	}
	
	
	void doAction2(String fName){
		//현재 경로의 fName 파일에 접근하고 싶다
		File path = new File("."); //현재경로(실행되는위치 = 현재위ㅊ;)
		File f1 = new File(path, fName); 
		//System.out.println(exists ? "응 있어" : "응 없어");
		boolean isRead = f1.canRead();
		System.out.println(isRead ? "읽어두대" : "못 읽어");
		boolean isWrite = f1.canWrite();
		System.out.println(isRead ? "써두대	" : "못써");

		boolean isHidden = f1.isHidden();
		System.out.println(isRead ? "숨겨진파일	" : "공개ㅏ일");
		
		
		long mTime =  f1.lastModified();
		System.out.println("mTime: " + mTime);
		Calendar c = Calendar.getInstance();//
				c.setTimeInMillis(mTime);
		System.out.println();
		
	}
	void doAction3(String dir){
		
		File f1 = new File("C:");
		File f2 = new File(f1, dir); //f1 밑에있는 f2
		
//		FileInputStream fis = null;
//		try{
//			fis = new FileInputStream(new File(f2, "at.txt")); 
//			System.out.println("aa");
//		}catch(IOException e){}
		
		//판단먼저 (구분에따라 작업)
		boolean isDirectory = f2.isDirectory();
		System.out.println(isDirectory ? "디렉토리" : "파일");
		
		//디렉토리 기반으로한 하위루다가 디렉토리를만들것이야
		File f3 = new File(f2, "sam");	//파일객체만들고 이 파일객체 갖고잇는메소드로 만드는거래
		//f3.mkdir(); //만들어진것의 결과물 X,  상태 O
//		System.out.println(f3.mkdir());	//바로쓰지말고if문들어가야겠지?
//		System.out.println(f3.mkdir());
		//있나없나 따져서 없으면 f3.mkdkr() 이렇게해야지
		
		
		
		if(!f3.exists()){
			f3.mkdirs();
		}
		if(!f3.exists()){
			f3.delete();
		}
		
//		System.out.println(f3.delete());//반환값 boolean //file or Directory 삭제
//		System.out.println(f3.delete());//파일 열고있으면 못지워) --->쓰레드 잘짜야겠ㅈ;?
		
		File[] fList = f2.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				//System.out.println(name);  //JS에서 여기  함수처리할때 콜리져씀..(객체하나 절달시킨거고 메소드 하나 재정의되어있는것...찍어보니까 여러번돌고있네 for문돌린적도없는데? 
				//리스트 목록 뽑아오면서 필터 하나밖에 준적 없는데 찍어보니까 accept가 목록만큼 다 불리고있네?
				//객체를 전달하면 몇개는 골라서 실행되고 몇개는 안되는 그런게많대

				
				//이름에 a 들어갈때만 출력하는건?
				//if(name.contains("a")) return true;
				//charSequence -> String부몬거알지?
				
				//파일일때만 출력은?
				//File이 디렉토리와 파일을 다관리하니까 이런메소드를 주고있는것이야
				File file = new File(dir, name);	//필터쓰는걸 연습해버릇하자
				if(file.isDirectory()) return true;	//불러올때부터 파일인애들만 불러오고 아님 안불러온다
				
				
				//false면 나오겠지? 조건 만족시 true하고말이야
				return false;
			}
		});	//directory도나오고~ file도나오고~
		
		// WHAT IF? a이름 갖고있는건만 출력하고싶다
		
		
		System.out.println("---------------------------------------");
		for(File f : fList){
			System.out.println(f.getName());
			
		}
		
		System.out.println("---------------------------------------");
		System.out.println(f3.delete());
		System.out.println(f3.delete());
		
		
		File[] roots = File.listRoots();
		for(File f: roots){
			System.out.println(f.getAbsolutePath());
		}
	}
	
	void doAction4(String srcName, String destName){	//파일이름바꾸기
						//퍄~ src, dest 겁나간지나네
		File path = new File("C:\\fData"); 		//경로설정해주죠?
		
		//srcName, destName 둘다 객체 만들어놔야한다!
		//rename은 전달되는 것이 File객체라서그래
		File sf = new File(path, srcName);
		File df = new File(path, destName);
		
		boolean flag = sf.renameTo(df);	//메솓 반환타입 boolean! 둘째인자를 file객체로만들어야대
		System.out.println("이름변경: " + (flag ? "성공" : "실패"));
		
		//renameTo 같은애들 리턴타입 void 안한이유가 뭘까? 성공여부 확인해보라그런거겟지?
		
		
	}
	
	void doAction5(String dir){
		File f1 = new File(dir);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//월은 대문자^^ 이렇게해야 월이 3월이면 03이나온다 안그러면 +1 해줘야되자너~
		
		String result = sdf.format(c.getTime());
		String hour = c.get(Calendar.HOUR_OF_DAY) + "";
		String minute = c.get(Calendar.MINUTE) + "";
		
		
		File f2 = new File(f1, result);
		if(!f2.exists()){	//없을떄만 새로만들어
			f2.mkdir();
		}
		File f3 = new File(f2, hour);
		if(!f3.exists()){
			f3.mkdir();
		}
		File f4 = new File(f3, minute);
		if(!f4.exists()){
			f4.mkdir();
		}
		
		File f5 = new File(f4, "a.txt");
		String str = "asfdsfsdfdf";
		byte[] brr = null;
		brr = str.getBytes();
		try {
			FileOutputStream fos = new FileOutputStream(f5);
			fos.write(brr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("성공");
		//클릭시 년월일 + 시 + 분 으로 폴더 생성하기 각가 밑에다가
	}
	
	public MakeFileExample(){
		f = new JFrame("File 예제");
		f.setBounds(200, 200, 300, 300);
		
		p = new JPanel();
		
		JButton btn; //갯수늘어도 이건 걍 변수죠?
		
		for(int i = 0; i < titles.length; i++){	//반복문
			btn = new JButton(titles[i]);
			btn.setActionCommand(cmds[i]);
			btn.addActionListener(al);
			p.add(btn);
		}
		
		f.add(p, BorderLayout.CENTER);
		
		
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void main(String[] args){
		new MakeFileExample();
	}
}
