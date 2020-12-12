package smishingcutter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SCThread extends Thread{
	private Socket socket;	
	private String savePath = "/home/ubuntu/sftp/savePage/";
	private String date, pDate, receiveURL, nowURL, receiverID, senderID;
	private long startTime;
	private SCDBManager scm;

	// Constructor
	public SCThread(Socket socket) {
		this.socket = socket;
		this.startTime = System.currentTimeMillis();
		this.date = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_").format(new Date());
		this.pDate = new SimpleDateFormat("HH:mm:ss ").format(new Date());
		this.scm = new SCDBManager();
	}

	// Method
	public void setReceiverID(String receiverID) {
		this.receiverID = receiverID;
	}
	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
	public void setReceiveURL(String receiveURL) {
		this.receiveURL = receiveURL;
	}
	public void setNowURL(String nowURL) {
		this.nowURL = nowURL;
	}
	
	private String SCwget(String URL, int msgNum) {
		int lineNum = 0;
		Process process = null;
		String fileName = date+senderID+"_"+String.format("%03d",msgNum);

		// ** wget **
		try {
			process = new ProcessBuilder("wget", "--tries=2", "-O", savePath+fileName+".html", URL).start();
			// wget --level=1 --glob=on --accept=htm,html,xml,php,js,asp,nhn --remote-encoding=utf-8 <URL> --input-file=<URL> -O <outFileName>
			// About ErrorStream	
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while(errorReader.readLine() != null) {
				lineNum++;
			}
			// About InputStream 
			/*
			BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line = outReader.readLine()) != null) {
				System.out.println(line);
			}
			*/
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			process.destroy();
		}

		// After wget process
		long totalTime = System.currentTimeMillis()-startTime;
		File myFile = new File(savePath+fileName+".html");
		String cr = "1";

		System.out.println(pDate+"] "+totalTime+"ms]["+((myFile.length()>0)?"O":"X")+"] "+senderID+"->"+receiverID+" \" "+receiveURL+" \" -> \" "+fileName+".html\" ");
		if(myFile.length()==0) {
			// Failed wget
			SCDeleteFile(savePath+fileName+".html");
			cr = "-1";
		} else{
			receiveURL  = receiveURL.replaceFirst("(?i)^https?://", "");
			// Succeed wget
			scm.insert("insert into SC_URL(is_block, URL, file_name, receiver_number, sender_number) value('1', '"+receiveURL+"', '"+fileName+"', '"+receiverID.replace("-","")+"', '"+senderID.replace("-","")+"')");
			// ** js-beautify **
			try {
				process = new ProcessBuilder("js-beautify", "-f", savePath+fileName+".html", "-o", savePath+fileName, "-s", "1", "--type", "js").start();
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while(errorReader.readLine() != null) {
					lineNum++;
				}
				process = new ProcessBuilder("js-beautify", "-f", savePath+fileName, "-o", savePath+fileName, "-s", "1", "--type", "html").start();
				errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while(errorReader.readLine() != null) {
					lineNum++;
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				process.destroy();
			}
			cr = safetyCheck(savePath+fileName);
			scm.update("update SC_URL SET is_block ='"+cr+"' where file_name='"+fileName+"'");
		}
		// cr = { -1:none, 0:warning, 1:ok, 2:safe }
		return cr;

	}

	private void SCDeleteFile(String filePath) {
		File deleteFile = new File(filePath);

		if(deleteFile.exists()){
			deleteFile.delete();
		}
	}

	private String safetyCheck(String fileName){
		String str = null;
		try{
			File file = new File(fileName);
			Scanner scan = new Scanner(file);
			while(scan.hasNextLine()){
				str = scan.nextLine();
				if((str.contains(".apk") || str.contains(".app")) && (str.contains("src=\"") || str.contains("href=\""))){
					return "0";
				}
			}
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return "1";
	}
	
	private void getMsg() throws IOException, ClassNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
		String str = null;

		for(int msgNum=0;; msgNum++) {
			str = br.readLine();
			if(str == null) {
				break;
			}
			if(msgNum == 0) {
				setReceiverID(str);
			} else if(msgNum == 1){
				setSenderID(str);
			} else{
				String rMsg = Integer.toString(msgNum-2)+"=";
				String sURL = str.replaceFirst("(?i)^https?://", "");
				String sURLRemove = sURL.split("/")[0].replaceFirst("(?i)^(m\\.|www\\.)", "");
				String selectResult;

				if(scm.select("select is_block from SC_URL where URL='"+sURLRemove+"' and is_block=2").equals("2")){
					System.out.println(pDate+"] "+senderID+"->"+receiverID+" \" "+str+" \" == SAFE");
					rMsg = rMsg+"2";
				} else{
					selectResult = scm.select("select is_block from SC_URL where URL='"+sURL+"'");
					if(selectResult.equals("none")){
						setReceiveURL(str);
						setNowURL(str);
						rMsg = rMsg+SCwget(this.receiveURL, msgNum-1);
					} else{
						System.out.println(pDate+"] "+senderID+"->"+receiverID+" \" "+str+" \" == "+selectResult);
						rMsg = rMsg+selectResult;
						scm.update("update SC_URL set report_num=report_num+1 where URL='"+sURL+"'");
					}
				}
				bw.write(rMsg+"\n");
				bw.flush();
			}
		}
		br.close();
		bw.close();
	}
	
	public void run() {
		try {
			getMsg();
			socket.close();
		} catch(IOException e) {
			System.out.println("[ Thread IOException ]");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
