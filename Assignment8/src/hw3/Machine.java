package hw3;

/**
 * Author Name: Huizhe Wang   
 * NUID: 001614841  
 */

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
    public final int capacity;
	//YOUR CODE GOES HERE...
    public int numCooking;
    
	private Object fullLock = new Object();
    private Object timer = new Object(); 
   

	 
	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		this.capacity = capacityIn;
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.machineStarting(this, foodIn, capacityIn));

	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public Object makeFood(Cook cook, Food food) throws InterruptedException {
		//YOUR CODE GOES HERE...

	        Thread makeFoodThread = new Thread(new CookAnItem(cook));
	        makeFoodThread.start();
	        makeFoodThread.join();
	        return timer;
	}

	//request to use machine
	private void submitFood(Food food) throws InterruptedException {
		synchronized (fullLock) {
			while (numCooking >= capacity )
				fullLock.wait();
			Simulation.logEvent(SimulationEvent.machineCookingFood(this, machineFoodType));
			numCooking++;
		}
	}
	
	//finish to cook food
	private void takeFood(Food food) throws InterruptedException {
		synchronized (fullLock) {
			Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, food));
			numCooking--;
			fullLock.notifyAll();
		}
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		private Cook cook;
		
		public CookAnItem(Cook cook){
			this.cook = cook;
		}
		public void run() {

			try {
				//YOUR CODE GOES HERE...
				submitFood(machineFoodType);
				Thread.sleep(machineFoodType.cookTimeMS);
				cook.completeFoodItem(machineFoodType);
				
					takeFood(machineFoodType);
					synchronized(cook.getCompletedFoodLock()){
						cook.getCompletedFoodLock().notify();
					}
				
			} catch(InterruptedException e) { }			
		}
	}
 

	public String toString() {
		return machineName;
	}
}