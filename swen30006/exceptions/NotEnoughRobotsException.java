package exceptions;


/**
 * An exception thrown when there arent enough robots to deliver a large item
 */
public class NotEnoughRobotsException extends Throwable {
    public NotEnoughRobotsException(){
        super("Not enough robots to deliver task");
    }
}
