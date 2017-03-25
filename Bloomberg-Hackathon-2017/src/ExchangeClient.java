/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author atamarkin2
 */
public class ExchangeClient {
	
	public static boolean DEBUG = false;
	public static String HOST;
	public static int PORT;
	public static String USER, PASSWORD;
	
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
    	System.out.println("Usage: \nclientTask <command...>");       
		if (DEBUG){
		 	HOST = "ec2-52-14-116-90.us-east-2.compute.amazonaws.com";
		 	PORT = 17429;
		 	USER = "a";
		 	PASSWORD = "a";
		}else{
			HOST = "codebb.cloudapp.net";
			PORT = 17429;
			USER = "bananas";
			PASSWORD = "pajamas";
		}
		Comms.init(HOST, PORT, USER, PASSWORD);
		while (true)
			 GameLogic.run();	 
	 }

}
