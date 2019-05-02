package automail;

import automail.Robot.RobotState;
import exceptions.ItemTooHeavyException;

import java.util.ArrayList;
import java.util.ListIterator;

public class Team {
	static public final int MAX_TEAM_SIZE = 3;
	
	private int nRobots;
	
	ArrayList<Robot> robots;
	public Team(int nRobots){
    	this.nRobots = nRobots;
    	this.robots = new ArrayList<Robot>();
    }
	public ArrayList<Robot> getRobots(){
		return this.robots;
	}
	public void setRobots(ListIterator<Robot> i){
		for (int j=0; j < this.nRobots; j++) {
			Robot robot = i.next();
			assert(robot.isEmpty());
			this.robots.add(robot);
		}
	}
	public void addRobot(Robot r){
		this.robots.add(r);
	}
	public void handleTeamHand(MailItem mail) throws ItemTooHeavyException{
		for (Robot r: robots) {
			try {
				System.out.println("TEAM HAND in rob funk");
			r.addToHand(mail, this.robots.size());
			}
			catch(Exception e) {
				throw e;
			}
		}
	}

}
