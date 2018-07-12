package hw3;

/**
 * Author Name: Huizhe Wang   
 * NUID: 001614841  
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private List<Food> completedFood;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
	
	public void completeFoodItem(Food food){
		completedFood.add(food);
	}
	
	public List<Food> getCompletedFoodLock(){
		return completedFood;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				
				//start an order
				  //for current costumer 
				int orderNumber = Simulation.getOrderNum();
				List<Food> order = Simulation.getOrder(orderNumber);
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order, orderNumber));
				completedFood = new ArrayList<Food>();
				//submit request to food machine
				for (Food food : order) {
					Simulation.logEvent(SimulationEvent.cookStartedFood(Cook.this, food, orderNumber));
					Simulation.cookFood(this, food);
					//receive a completed food item
    				Simulation.logEvent(SimulationEvent.cookFinishedFood(Cook.this, completedFood.get(completedFood.size() - 1), orderNumber));

				}

				//complete an order
				synchronized(completedFood){
					while(completedFood.size() < order.size()){
						completedFood.wait();
					}
				}
				
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, orderNumber));
				Simulation.completedOrder(orderNumber, order);
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}    
}