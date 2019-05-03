package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Team;
import exceptions.ItemTooHeavyException;
import exceptions.NotEnoughRobotsException;

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

	private final int maxRobots;

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
	    maxRobots = nrobots;
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
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
				MailItem mailItem = j.next().mailItem;
				if(mailItem.getWeight() <= 2000) {
					robot.addToHand(mailItem); // hand first as we want higher priority delivered first
					j.remove();
					if (pool.size() > 0) {
						mailItem = j.next().mailItem;
						if(mailItem.getWeight() <= 2000) {
							robot.addToTube(mailItem);
							j.remove();
						}
					}
					robot.dispatch(); // send the robot off if it has any items to deliver
					i.remove();       // remove from mailPool queue
				} else if(mailItem.getWeight() > 2000 && mailItem.getWeight() <= 2600) {
					// deliver only if 2 robots are available
					if(robots.size() >= 2) {
						j.remove();
						Team team = new Team(mailItem, robot.getDelivery());
						team.addRobot(robot);
						i.remove();

						robot = i.next();
						team.addRobot(robot);
						i.remove();
						team.start();
					}else {
						// If we can never get 2 robots throw an error
						// other wise we shall prioritise this delivery less
						// and wait till a robot is freed up
					    if(maxRobots < 2) {
                            throw  new NotEnoughRobotsException();
                        }
                    }
				} else if(mailItem.getWeight() > 2600 && mailItem.getWeight() <= 3000) {
					if(robots.size() >= 3) {
						j.remove();
						Team team = new Team(mailItem, robot.getDelivery());
						team.addRobot(robot);
						i.remove();

						robot = i.next();
						team.addRobot(robot);
						i.remove();

						robot = i.next();
						team.addRobot(robot);
						i.remove();
						team.start();
					}else {
						// If we can never get 2 robots throw an error
						// other wise we shall prioritise this delivery less
						// and wait till a robot is freed up
					    if(maxRobots < 3) {
                            throw new NotEnoughRobotsException();
                        }
                    }

				}else  {
					throw new ItemTooHeavyException();
				}

			} catch (NotEnoughRobotsException e) {
			    System.err.println(e.getMessage());
	            System.exit(1);

	        } 
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
