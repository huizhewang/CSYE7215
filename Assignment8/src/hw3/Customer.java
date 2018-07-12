package hw3;

/**
 * Author Name: Huizhe Wang   
 * NUID: 001614841  
 */


import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;
	private static Object enterLock = new Object();


	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	
     public void run() {
    	 // YOUR CODE GOES HERE...
    	 
    	 //start CoffeeShop

    	 Simulation.logEvent(SimulationEvent.customerStarting(this));
    	 
    	 //enter CoffeeShop
    	 synchronized (enterLock) {
    	 while (!Simulation.areTablesAvailable()){
			try {
				enterLock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    	 Simulation.enterCoffeeShop(this);
    	 Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
    	 }
 
    	 //place an order
    	 Simulation.logEvent(SimulationEvent.customerPlacedOrder(this,order, orderNum));
    	 Simulation.placedOrder(this,order,orderNum);
    	 
    	 //receive the order
    	 try {
			Simulation.receivedOrder(this, orderNum);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
    	 
    	 //leaving CoffeeShop
    	 synchronized (enterLock) {
    	 Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
    	 Simulation.leavingCoffeeShop(this);
    	 enterLock.notifyAll();
    	 }
     }

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}