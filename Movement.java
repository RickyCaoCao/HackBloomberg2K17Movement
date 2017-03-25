package bloomberg_movement_test;

import java.util.Timer;
import java.util.TimerTask;

//To calculate certain values that are not available initially (accelVal and maxPossibleVel)
//When we first spawn, we turn acceleration to max towards x direction
//Check how much our velX changes = acceleration/tick
//We can verify this through multiple tests
//Hence, we have accelVal
//maxPossibleVel can be calculated through checking past and present velocity at accel max
//if they are equal, then terminal velocity is reached = maxPossibleVel

public class Movement{
	public static void main(String[] args){
		//Testing
		
		//Bug Temp. Fix: Keep initial velocities as 0
		aShip newShip = new aShip(0, 0, 300, 300, 0, 0);
		
		Timer aTimer = new Timer();
		TimerTask aTask = new TimerTask(){
			public void run(){
				newShip.movement();
			}
		};
		
		aTimer.schedule(aTask, 2000, 2000);
	}
}

class aShip{
	//what the movement should have been

	//We get normalized direction vector
	//D = (Xmine - Xplayer, Ymine - Yplayer) = (x, y)
	//d = (x/r, y/r) where r = sqrt(x^2+y^2)
	//v(unit) = V/sqrt(vx^2+vy^2)
	
	private static final double maxPossibleVel = 100; //this value would be constantly updated, we assign as constant here
	private static final double accelVal = 20; //calculated from technique above

	//inputs
	private double playerX, playerY, mineX, mineY, playerVelX, playerVelY;
	
	//calculated variables
	private double[] directionVector = new double[2];	//direction from player to mine
	private double distance;	//distance from player to mine
	
	private double[] velocityVector = new double[2]; //velocity unit vector of player
	private double velMag; //velocity of player
	
	private boolean firstTime = true;	//sets up initial angle and stage 
	private double angle; //angle the ship needs to turn
	
	private boolean stoppedAdj = false;	//turns true when the ship is facing the mine
	private boolean slowedDown = false; //once the ship has started to slow down, it will not speed up again
	
	//the finite state machine of the movement
	//stage0 = no acceleration
	//stage1 = full thrusters to place
	//stage2 = reverse thrusters to slow down
	//stage3 = Part1 of docking on mine.. Occurs when the ship "stops" within distance of the mine
	//stage4 = Part2 of docking.. reverses acceleration from Stage3
	enum MoveStage {stage0, stage1, stage2, stage3, stage4};
	MoveStage myMove;
	
	//The thruster value needed for stage 3 and stage 4
	private double oneJumpThruster;
	
	//Basic Constructor
	public aShip(double playerX, double playerY, double mineX, double mineY, double playerVelX, double playerVelY){
		this.playerX = playerX;
		this.playerY = playerY;
		this.mineX = mineX;
		this.mineY = mineY;
		this.playerVelX = playerVelX;
		this.playerVelY = playerVelY;
	}

	
	public void movement(){
		//Basic calculations as described above
		distance = Math.sqrt((mineX - playerX) * (mineX - playerX) + (mineY - playerY) * (mineY - playerY));
		velMag =  Math.sqrt(playerVelX * playerVelX + playerVelY * playerVelY);
		directionVector[0] = (mineX - playerX)/distance;
		directionVector[1] = (mineY - playerY)/distance;
		velocityVector[0] = (velMag <= 0.001) ? 0 : playerVelX/velMag;
		velocityVector[1] = (velMag <= 0.001) ? 0 : playerVelY/velMag;
		
		//Determine acceleration vector needed for ship to change direction
		double[] accelVector = new double[2];
		accelVector[0] = directionVector[0]-velocityVector[0];
		accelVector[1] = directionVector[1]-velocityVector[1];
		
		//this value is used to determine angle... here to prevent 0/0 error
		double intermediateCalc = (Math.abs(accelVector[0]) <= 0.0001
				&& Math.abs(accelVector[1]) <= 0.0001) ? 1 : accelVector[1]/accelVector[0];
		
		//starts acceleration/deceleration steps once the ship has stopped adjusting direction
		if(!stoppedAdj && Math.abs(accelVector[0]) <= 0.0001 && Math.abs(accelVector[1]) <= 0.0001){
			stoppedAdj = true;
		}
		
		//determines if the mine is close enough to just "jump to"
		if(firstTime){
			if(distance <= accelVal*Math.sqrt(2)){
				myMove = MoveStage.stage3;
			}
			else{
				myMove = MoveStage.stage1;
			}
		}
		
		//determines angle the acceleration vector needs to be pointing		
		if(!stoppedAdj || firstTime){
			firstTime = false;
			angle = (accelVector[0] >= 0.00001) ? Math.atan(intermediateCalc) : Math.PI+Math.atan(intermediateCalc);
		}
		
		//determines the next velocity, used to see if ship needs to slow down (enter stage2
		double nextVel = slowedDown ? velMag : velMag + accelVal * Math.sqrt(2); 
		if(nextVel > maxPossibleVel) nextVel = maxPossibleVel;
		
		//calculates the number of ticks the ship has to stop, given current velocity
		double timesToSlow = Math.ceil(nextVel/accelVal*Math.sqrt(2));
		
		//calculates how much distance the ship has left after it has moved
		//this is because the ship moves 
		double nextDistance = Math.sqrt((mineX - playerX-playerVelX) * (mineX - playerX-playerVelY)
				+ (mineY - playerY-playerVelY) * (mineY - playerY-playerVelY));

		//enter "no acceleration" mode after reaching destination
		if(myMove == MoveStage.stage4){
			myMove = MoveStage.stage0;
		}
		
		//enter Part 2 of docking if currently in Part 1 of docking
		else if(myMove == MoveStage.stage3){
			myMove = MoveStage.stage4;
		}
		
		//Enter Part 1 of docking mode if ship is close to mine and has stopped
		else if((Math.abs(mineX-playerX) <= accelVal) && (Math.abs(mineY-playerY) <= accelVal) && velMag <= 0.001){
			myMove = MoveStage.stage3;
		}
		
		//Algorithm for determining if the ship needs to slow down
		//The first "if" statement determines that the ship cannot speed up
		//At the next spot the ship will be (which is already determined), the ship will plan to move nextVel magnitude. Hence "+nextVal"
		//(nextVel)/2 * (timesToSlow-1) is the distance the ship will move as the current velocity slows to 0
		else if(nextDistance <= (nextVel)/2 * (timesToSlow-1) + nextVel){
			//Once SlowDown has been initiated, the ship should never accelerate again
			slowedDown = true;
			
			//if current velocity is too fast, the ship needs to slow down
			if(nextDistance <= (velMag)/2 * (timesToSlow-1) + velMag){
				myMove = MoveStage.stage2;
			}
			
			//maintain current velocity if ship does not need to slow down
			else{
				myMove = MoveStage.stage0;
			}
		}
		
		//During slowdown sequence, if ship does not need to slow down, the ship just maintains current velocity
		else if(stoppedAdj && slowedDown){
			myMove = MoveStage.stage0;
		}
		
		else{
			myMove = MoveStage.stage1;
		}
		
		
		//Defines movement
		switch(myMove){
			case stage0:	//stop acceleration
				acceleration(0, 0);
				break;
				
			case stage1:	//full steam ahead
				acceleration(angle, 1);
				break;
				
			case stage2: //reverse acceleration (at full steam level)
				acceleration(Math.PI+angle, 1);
				break;
				
			case stage3: //one jump (Part 1 Docking)
				oneJumpThruster= distance/accelVal; //ratio to accurately reach location in the one jump
				acceleration(angle, oneJumpThruster);
				break;
				
			case stage4: //counter velocity created in Part 1 Docking
				acceleration(Math.PI+angle, oneJumpThruster);
				break;
				
			default:
				myMove = MoveStage.stage0;
				break;
		}
	}
	
	//ship moves according to last determined velocity
	//then new velocity for next tick is determined based on inputs for the acceleration
	public void acceleration(double angle, double thruster){
		playerX += playerVelX;
		playerY += playerVelY;
		
		playerVelX += accelVal * thruster * Math.cos(angle);
		if(Math.abs(playerVelX) > maxPossibleVel){
			playerVelX = playerVelX > 0 ? maxPossibleVel : -maxPossibleVel;
		}
		
		playerVelY += accelVal * thruster * Math.sin(angle);
		if(Math.abs(playerVelY) > maxPossibleVel){
			playerVelY = playerVelY > 0 ? maxPossibleVel : -maxPossibleVel;
		}
		
		
		System.out.println("-----------------1 TICK-----------------");
		System.out.println(myMove);
		System.out.println("Angle: " + angle + ", Thruster: " + thruster);
		System.out.println("(X ,Y): (" + playerX  + ", " + playerY + ")");
		System.out.println("(VelX ,VelY): (" + playerVelX + ", " + playerVelY + ")");		
		System.out.println("----------------------------------------");
	}
	
}
