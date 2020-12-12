package smishingcutter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SCServer {
	
	public void go() throws IOException{
		final int portNo = 32768;
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			System.out.println(" ** SmishingCutter Server Start ** ");
			serverSocket = new ServerSocket(portNo);
			// wait for clients
			while(true) {
				socket = serverSocket.accept();
				SCThread th = new SCThread(socket);
				th.start();
			}
		} finally {
			if(serverSocket != null) {
				serverSocket.close();
			}
			if(socket != null) {
				socket.close();
			}
			System.out.println(" ** SmishingCutter Server Close ** ");
		}
	}
	
	public static void main(String[] args) {
		SCServer sc = new SCServer();

		try {
			sc.go();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
