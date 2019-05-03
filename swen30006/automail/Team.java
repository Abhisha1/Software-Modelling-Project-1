package automail;

import exceptions.ItemTooHeavyException;

import java.util.*;

public class Team {

    private ArrayList<Robot> robots = new ArrayList<>();
    private IMailDelivery delivery;
    private MailItem mailItem;

    private boolean delivered = false;

    private int robotsReached = 0;

    private Set<String> robotsReachedSet = new HashSet<>();

    public Team(MailItem mailItem, IMailDelivery delivery) {
        this.mailItem = mailItem;
        this.delivery = delivery;
    }

    public void addRobot(Robot robot) throws ItemTooHeavyException {
        robots.add(robot);
        robot.setTeam(this);
        robot.addToHand(this.mailItem);

    }

    public void start() {
        for(int i=0; i < robots.size(); i++) {
            robots.get(i).dispatch();
        }
    }

    public int size() {
        return robots.size();
    }


    public void reachRoom(Robot robot) {
        robotsReachedSet.add(robot.id);
    }

    public int reachedRoom() {
        return robotsReachedSet.size();
    }

    public void deliver() {
        if(!delivered) {
            delivery.deliver(mailItem);
            delivered = true;
            for(int i=0; i < robots.size(); i++) {
                robots.get(i).resetTeam();
            }
            robots.clear();
        }
    }


}
