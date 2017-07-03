package com.never.project.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class LogDown extends Thread{
	String logdata  ;
	ChatClient Cilent;
	public LogDown(ChatClient Cilent, String logdata) {
		this.logdata = logdata;
		this.Cilent = Cilent;
	}

	public LogDown(){}

	@Override
	public void run() {
		
		
		byte[] data =null;
		data = logdata.getBytes();
		
		Calendar c = Calendar.getInstance();
		String fileName = c.get(Calendar.HOUR_OF_DAY)+"_"+c.get(Calendar.MINUTE);
		
		
		File file1 = new File ("c:\\");
		
		File f2 = new File(file1, "chatlog");
		if(!f2.exists()){	//없을떄만 새로만들어
			f2.mkdir();
		}
		
		File f3 = new File(f2, "logDownlod");
		if(!f3.exists()){
			f3.mkdir();
		}
		File f4 = new File(f3, "ChatLog_"+fileName+".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f4);
			fos.write(data);
		} catch (FileNotFoundException e) {
		}catch (IOException e) {}
		finally{
			if(fos!=null){
			try {
				fos.close();
			} catch (IOException e) {
			}
			}
		}
		Cilent.addLog("다운로드완료   위치 :  c:\\chatlog\\logDownlod");
		
	}

}
