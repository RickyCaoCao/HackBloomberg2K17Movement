import java.util.Vector;
import java.lang.Math;

public class GameLogic {
	//Self STATUS variables
	public static float x, y;
	public static float velx, vely;
	public static float acc, dir;
	
	
	
	//CONFIGURATIONS variables
	public static Config c;
	
	
	public static int state = 0;
	/*
	 * 0:	Game init, chart zamboni
	 * 1:	Zamboni-ing, store nodes detected
	 * 2:	Off-track, return to zamboni charting
	 * 3:	Found node, reaching full stop
	 * 4:	Found node, traveling there
	 * 
	 * 11:
	 */
	
	//Self variables
	public static float zamboniAngle;
	public static float[][] zamboniNodes = new float[16][2];
	public static int currNode = 0;
	//public static float accelConstant;
	public static class path {
		public static class mines {
			float xpos, ypos;
			float line;
		}
	}
	public static Vector<path> xpaths = new Vector<path>(99);
	public static Vector<path> ypaths = new Vector<path>(99);
	
	public static float[] minePos = new float[2];
	
	public static void configInit() {
		//TODO: check all CONFIGURATIONS variables
		c = Comms.config();
		//(0, 0) indicates no mines nearby
		minePos[0] = 0;
		minePos[1] = 0;
	}
	
	public static Status statusCheck() {
		
		Status status = Comms.status();	//STATUS command to server, check Status.java
		x = status.x;
		y = status.y;
		velx = status.dx;
		vely = status.dy;
		
	
		return status;
	}
	
	public static void run() {
		Status s = statusCheck();
		System.out.println("STATE: "+state);
		switch (state) {
		case 0:
			configInit();
			dir = chartZamboniPath() * 4;
			//chartZamboniNodes();
			
			//TODO: find acceleration constant (somehow); apply movement
			//TODO: initialize map
			
			Comms.accelerate(dir, 1);
			state = 1;
			break;
		case 1:
			//System.out.println("Current Node: " + currNode);
			//System.out.println("Placing bomb");
			acc = 1;
			Comms.bomb(x + 2*velx, y + 2*vely, 20);
			/*
			moveTo(zamboniNodes[currNode][0], zamboniNodes[currNode][1]);
			switch (currNode) {
			case 0:
			case 4:
			case 8:
			case 12:
				if (x > zamboniNodes[currNode][0]) {
					currNode++;
					state = 2;
				}
				break;
			case 1:
			case 5:
			case 9:
			case 13:
				if (y > zamboniNodes[currNode][1]) {
					currNode++;
					state = 2;
				}
				break;
			case 2:
			case 6:
			case 10:
			case 14:
				if (x < zamboniNodes[currNode][0]) {
					currNode++;
					state = 2;
				}
				break;
			case 3:
			case 7:
			case 11:
			case 15:
				if (y < zamboniNodes[currNode][1]) {
					currNode++;
					state = 2;
				}
				break;
			}
			if (currNode > 15)
				currNode = 0;
			*/
			if (s.num_mines > 0) {
				//TODO: Charter the bombs on background thread
				//For the most part we'll find only one node, but in case we find two
				for (int i = 0; i < s.num_mines; i++) {
					if (!s.mines[i].owner.equals("bananas")) {
						System.out.println(s.closestMine(x, y).owner);
						Comms.brake();			//Brake first because it's so much easier to do
						minePos[0] = s.mines[i].x;
						minePos[1] = s.mines[i].y;
						acc = 1;
						//moveTo(minePos[0], minePos[1]);
						state = 3;				//Let's go!
						return;
					}
				}
			}
			break;
		case 2:
			Comms.brake();
			if (Math.abs(velx) < 0.9 && Math.abs(vely) < 0.9) {
				acc = 1;
				state = 1;
			}
			break;
		case 3:
			//Make sure we come to a full stop
			if (Math.abs(velx) < 0.1 && Math.abs(vely) < 0.1) {
				state = 4;
				acc = (float) 0.6;	//Make sure we don't jump over the desired node
				moveTo(minePos[0], minePos[1]);		//Direct heading, meaning that we only need to call it once
			}
			break;
		case 4:
			//Distance is by magnitude and Pythagorean theorem
			//if (Math.abs(minePos[0]-x) < c.capture_radius && Math.abs(minePos[1]-y) < c.capture_radius)
			
			
			if (s.closestMine(x, y) != null){
				System.out.println(s.closestMine(x, y).owner);
			} 
			else {
				//state = 0;
				//Comms.bomb(x, y);
				break;
			}
			moveTo(minePos[0], minePos[1]);
			if (s.closestMine(x, y) != null && s.closestMine(x, y).owner.equals("bananas"))
				//Comms.bomb(x, y);
				state = 0;			//We've captured the node, return to standard procedures
			
			break;
		}
	}
	
	
	//returns angle below horizon in which our ship will traverse (radians)
	public static float chartZamboniPath() {
		float angle = (float) Math.atan2(2*c.vision_radius, c.mapwidth);	//Will explain how this is done in person
		//Break map into 4 quadrants. It's entirely unnecessary, but very helpful
		if (x <= c.mapwidth/2) {
			if (y <= c.mapheight/2)
				return angle;
			return (float) (2.0*Math.PI - angle);			
		}
		if (y <= c.mapheight/2)
			return (float) (Math.PI - angle);

		
		System.out.println("Acceleration angle: " + Float.toString(angle));
		return(float) (Math.PI + angle);
	}
	
	
	public static void moveTo(float x2, float y2) {
		dir = (float) Math.atan2(y2 - y, x2 - x);
		Comms.accelerate(dir, acc);
	}
	
	
	
	
}
