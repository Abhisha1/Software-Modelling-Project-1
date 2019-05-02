package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Team;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		try{
			ListIterator<Robot> i = robots.listIterator();
			while (i.hasNext()) loadRobot(i);
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {
		ListIterator<Item> j = pool.listIterator();
		Robot robot = i.next();
		assert(robot.isEmpty());
		if (pool.size() > 0) {
			try {
			MailItem mail = j.next().mailItem;	
			robot.addToHand(mail); // hand first as we want higher priority delivered first
			j.remove();
			if (pool.size() > 0) {
				mail = j.next().mailItem;
				robot.addToTube(mail);
				j.remove();
			}
			robot.dispatch(); // send the robot off if it has any items to deliver
			i.remove();       // remove from mailPool queue
			}
			catch( ItemTooHeavyException e) {
					j = pool.listIterator();
					int robotsNeeded = 1;
					MailItem nextMail = j.next().mailItem;
					if (nextMail.getWeight() <=2600 && nextMail.getWeight()>2000) {
						robotsNeeded = 2;
					}
					else if (nextMail.getWeight() <=3000) {
						robotsNeeded = 3;
					}
					nextMail.setNTrips(robotsNeeded);
					Team team = new Team(robotsNeeded);
					team.addRobot(robot);
					i.forEachRemaining(r -> {
						team.addRobot(r);
					});
					if (team.getRobots().size() == robotsNeeded) {
						team.handleTeamHand(nextMail);
						for (Robot r: team.getRobots()) {
							r.dispatch();
//							i.remove();
						}
						j.remove();
					}
//					for (int k=1; k < robotsNeeded; k++) {
//						if(i.hasNext()) {
//							robot = i.next();
//							team.addRobot(robot);
//							i.remove();
//						}
//					}
//					if (team.getRobots().size() != robotsNeeded) {
//						System.out.println("not enough avaluiable robots");
//						for(Robot r: team.getRobots()) {
//							i.add(r);
//						}
//					}
//					else {
//						team.handleTeamHand(nextMail);	
//						for (Robot r: team.getRobots()) {
//							r.dispatch();
//						}
//						j.remove();
//					}
				}
			
			
			catch (Exception e) { 
	            throw e; 
	        } 
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
