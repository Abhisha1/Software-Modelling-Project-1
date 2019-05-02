package automail;

import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

import java.util.ArrayList;
import java.util.ListIterator;

public class Team {
	static public final int MAX_TEAM_SIZE = 3;
	
	private int nRobots;
	IMailDelivery delivery;
	ArrayList<Robot> robots;
	
	private static MailItem mail;
	public Team(int nRobots, MailItem mailItem){
    	this.nRobots = nRobots;
    	this.robots = new ArrayList<Robot>();
    	mail = mailItem;
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
		r.addTeam(this);
	}
	public void handleTeamHand() throws ItemTooHeavyException{
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
	public void step() throws ExcessiveDeliveryException {    	
    	for (Robot r: this.robots) {
    		if (r.current_state.name() == "DELIVERING") {
    			this.nRobots--;
    		}
    		System.out.println("NROBOTS"+nRobots);
    		if (this.nRobots == 0) {
    			//THIS LOGIC IS NOT RIGHT
    			delivery.deliver(mail);
    			this.robots.forEach(robot -> {
    				robot.resetDeliveryItem();
    				robot.resetRobot();
    			});
    		}
    	}
    }

}
