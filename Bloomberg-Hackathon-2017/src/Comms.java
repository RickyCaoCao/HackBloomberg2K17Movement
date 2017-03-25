/*Patrick H.
 * Comms client.
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class Comms {
	public static String HOST;
	public static int PORT;
	public static String USER, PASSWORD;

	private static final String CMD_ACCELERATE = "ACCELERATE";
	private static final String CMD_STATUS = "STATUS";
	private static final String CMD_BRAKE = "BRAKE";
	private static final String CMD_BOMB = "BOMB";
	private static final String CMD_SCAN = "SCAN";
	private static final String CMD_SCOREBOARD = "SCOREBOARD";
	private static final String CMD_CONFIGURATIONS = "CONFIGURATIONS";
	
	
	public static void init(String host, int port, String user, String password){
		HOST = host;
		PORT = port;
		USER = user;
		PASSWORD = password;
	}

	public static String sendRaw(String message){
		try{
			Socket socket = new Socket(HOST, PORT);
			PrintWriter pout = new PrintWriter(socket.getOutputStream());
	        BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        pout.println(USER + " " + PASSWORD);
	        pout.println(message);
	        pout.println("CLOSE_CONNECTION");
	        pout.flush();
	        String line;
	        StringBuilder ret = new StringBuilder();
	        while ((line = bin.readLine()) != null) {
	            ret.append(line);
	        }
	        pout.close();
	        bin.close();
	        socket.close();
	        System.out.println(ret.toString());
	        return ret.toString();
		}catch (IOException e){
			return e.getMessage();
		}
	}
	
	public static String sendRaw(String[] args){
		StringBuilder msg = new StringBuilder();
		for (int i = 0; i < args.length; ++i){
			msg.append(args[i]);
			msg.append(' ');
		}
		return sendRaw(msg.toString());
	}
	
	public static boolean accelerate(float rads, float acc){
		String[] args = new String[3];
		args[0] = CMD_ACCELERATE;
		args[1] = Float.toString(rads);
		args[2] = Float.toString(acc);
		
		return sendRaw(args).equals("ACCELERATE_OUT DONE");
	}
	
	public static boolean brake(){
		return sendRaw(CMD_BRAKE).equals("BRAKE_OUT DONE");
	}
	
	public static Status status(){
		Status ret = new Status();
		ret.Parse(sendRaw(CMD_STATUS), true);
		return ret;
	}
	
	public static Status scan(float x, float y){
		Status ret = new Status();
		String[] args = new String[3];
		args[0] = CMD_SCAN;
		args[1] = Float.toString(x);
		args[2] = Float.toString(y);
		ret.Parse(sendRaw(args), false);
		ret.x = x;
		ret.y = y;
		ret.dx = 0;
		ret.dy = 0;
		return ret;
	}
	
	public static Config config(){
		Config cfg = new Config();
		cfg.Parse(sendRaw(CMD_CONFIGURATIONS));
		return cfg;
	}
	
	public static boolean bomb(float x, float y){
		String[] args = new String[3];
		args[0] = CMD_BOMB;
		args[1] = Float.toString(x);
		args[2] = Float.toString(y);
		
		return sendRaw(args).equals("BOMB_OUT DONE");
	}
	public static boolean bomb(float x, float y, int t){
		String[] args = new String[4];
		args[0] = CMD_BOMB;
		args[1] = Float.toString(x);
		args[2] = Float.toString(y);
		args[3] = Integer.toString(t);
		
		return sendRaw(args).equals("BOMB_OUT DONE");
	}

}
