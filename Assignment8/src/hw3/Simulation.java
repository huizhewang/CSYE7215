package hw3;

/**
 * Author Name: Huizhe Wang   
 * NUID: 001614841  
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;  
	
	//number of how many tables have been used
	static Integer takenTables = 0;
	//total tables
	static Integer capacity;
//	private static List<Customer> customers = new ArrayList<Customer>();
	//A list of order numbers (FIFO)
    private static Queue<Integer> orderNums = new LinkedList<Integer>();
    //A list of orders which are currently available
    private static HashMap<Integer, List<Food>>orders = new HashMap<Integer, List<Food>>();
    //A list of completed orders
  	private static HashMap<Integer, List<Food>> completedOrders = new HashMap<Integer, List<Food>>();


  	static Machine Grill;
    static Machine Fryer;
    static Machine CoffeeMaker2000;
    
    private static Thread[] cooks;

    
	//methods 
	//customerEnterdCoffeeShop()
    public static boolean areTablesAvailable() {
		synchronized (takenTables) {
			return takenTables < capacity;
		}
	}
//   public static void enterCoffeeShop(Customer customer){
//	synchronized(takenTables){
////		while(!areTablesAvailable()){
////			try{
////				customers.wait();
////			}catch(InterruptedException e){
////				e.printStackTrace();
////			}
////		}
//			//System.out.println(capacity + "" + customers.size() + "" );
//			takenTables++;
//			customers.add(customer);
//		}
//   }
    private static class CustomerNode {
    	public final Customer item;
    	final AtomicReference<CustomerNode> next;

    	public CustomerNode(Customer item, CustomerNode next) {
    		this.item = item;
    		this.next = new AtomicReference<CustomerNode>(next);
    	}
    }
    private static final CustomerNode dummy = new CustomerNode(null, null);
    
    private static final AtomicReference<CustomerNode> head = new AtomicReference<CustomerNode>(dummy);
    private static final AtomicReference<CustomerNode> tail = new AtomicReference<CustomerNode>(dummy);

    public static boolean enterCoffeeShop(Customer item) {
    	 CustomerNode newNode = new CustomerNode(item, null);
    	  while (true) {
    		  CustomerNode curTail = tail.get();
    		  CustomerNode tailNext = curTail.next.get();
    	    if (curTail == tail.get()) {
    	      if (tailNext != null) {
    	        // Queue in intermediate state, advance tail
    	        tail.compareAndSet(curTail, tailNext);
    	      }
    	      else {
    	        // In quiescent state, try inserting new node
    	        if (curTail.next.compareAndSet(null, newNode)) {
    	          // Insertion succeeded, try advancing tail
    	          tail.compareAndSet(curTail, newNode);
    	          takenTables++;
    	          return true;  // Break loop by returning
    	        }
    	      }
    	    }
    	  }
    	}

    
   //customerPlacedOrder()
   public static void placedOrder(Customer customer, List<Food>order, Integer orderNum){

	   
	   synchronized(orderNums){
		orderNums.add(orderNum);
			orders.put(orderNum, order);		
		orderNums.notifyAll();
	   }   
   }
   
   //cookReceiveedOrder()
  	public static int getOrderNum() throws InterruptedException {
  		synchronized (orderNums) {
  			//cook wait(), if no orders
  			while (orderNums.isEmpty())
  				orderNums.wait();
  			//remove it from orderNums when cook start it
  			System.out.println("Cook get Order " + orderNums.peek());
  			return orderNums.remove();
  		}
  	}	
  	public static List<Food> getOrder(int orderNumber) {
  		synchronized (orders) {
  			//currently available
  			return orders.get(orderNumber);
  		}
  	}
  	
  	//cookStartedFood()
  	//machineCookingFood()
  	public static Object cookFood(Cook cook, Food food) throws InterruptedException {
		Machine machine = null;

		switch (food.toString()) {
		case "burger":
			machine = Grill;
			break;
		case "fries":
			machine = Fryer;
			break;
		case "coffee":
			machine = CoffeeMaker2000;
			break;
		}

		return machine.makeFood(cook, food);
	}
  	//machineDoneFood()
  	//cookFininshedFood()
  	
	//cookCompletedOrder()
	public static void completedOrder(int orderNum, List<Food> order) {
		synchronized (completedOrders) {
			completedOrders.put(orderNum, order);
			completedOrders.notifyAll();
		}
	}
	
  	//customerRecivedOrder()
	public static List<Food> receivedOrder(Customer customer, Integer orderNum) throws InterruptedException {
			synchronized (completedOrders) {
				// Wait if order is not done yet
				while (!completedOrders.containsKey(orderNum)) 
					completedOrders.wait();
					return completedOrders.remove(orderNum);
			}
	}   
	
   //customerLeavingCoffeeShop()
   public static void leavingCoffeeShop(Customer customer){
	   synchronized(takenTables){
		   takenTables--;
//		   customers.remove(customer);
//		   takenTables.notifyAll();
	   }
   }
   
   //endingCoffeeShop()


	/**
	 * Used by other classes in the simulation to log events
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * 	Function responsible for performing the simulation. Returns a List of 
	 *  SimulationEvent objects, constructed any way you see fit. This List will
	 *  be validated by a call to Validate.validateSimulation. This method is
	 *  called from Simulation.main(). We should be able to test your code by 
	 *  only calling runSimulation.
	 *  
	 *  Parameters:
	 *	@param numCustomers the number of customers wanting to enter the coffee shop
	 *	@param numCooks the number of cooks in the simulation
	 *	@param numTables the number of tables in the coffe shop (i.e. coffee shop capacity)
	 *	@param machineCapacity the capacity of all machines in the coffee shop
	 *  @param randomOrders a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(
			int numCustomers, int numCooks,
			int numTables, 
			int machineCapacity,
			boolean randomOrders
			) {

		//This method's signature MUST NOT CHANGE.
		

		//We are providing this events list object for you.  
		//  It is the ONLY PLACE where a concurrent collection object is 
		//  allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());




		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers,
				numCooks,
				numTables,
				machineCapacity));



		// Set things up you might need

		capacity=numTables;
		// Start up machines
        Grill = new Machine("Grill", FoodType.burger, machineCapacity);
        Fryer = new Machine("Fryer", FoodType.fries, machineCapacity);
        CoffeeMaker2000 = new Machine("CoffeeMaker2000", FoodType.coffee, machineCapacity);


		// Let cooks in
        cooks = new Thread[numCooks];
        for (int i = 0; i < numCooks; i++) {
            Cook cook = new Cook("Cook:" + i);
            Thread cookThread = new Thread(cook);
            cooks[i] = cookThread;
            cooks[i].start();

        }

		// Build the customers.
		Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.fries);
			order.add(FoodType.fries);
			order.add(FoodType.coffee);
			for(int i = 0; i < customers.length; i++) {
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}
		else {
			for(int i = 0; i < customers.length; i++) {
				Random rnd = new Random(27);
				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				order = new LinkedList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}


		// Now "let the customers know the shop is open" by
		//    starting them running in their own thread.
		for(int i = 0; i < customers.length; i++) {
			customers[i].start();
			//NOTE: Starting the customer does NOT mean they get to go
			//      right into the shop.  There has to be a table for
			//      them.  The Customer class' run method has many jobs
			//      to do - one of these is waiting for an available
			//      table...
		}


		try {
			// Wait for customers to finish
			//   -- you need to add some code here...
			
			for (int i = 0; i < customers.length; i++) {
				customers[i].join();
			}
			
			
			

			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads.  There are other approaches
			// though, so you can change this if you want to.
			for(int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			for(int i = 0; i < cooks.length; i++)
				cooks[i].join();

		}
		catch(InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		logEvent(SimulationEvent.machineEnding(Grill));
		logEvent(SimulationEvent.machineEnding(Fryer));
		logEvent(SimulationEvent.machineEnding(CoffeeMaker2000));




		// Done with simulation		
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	
	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of customers,
	 * the second is the number of cooks, the third is the number of tables
	 * in the coffee shop, and the fourth is the number of items each cooking
	 * machine can make at the same time.  
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}
		int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 10;
		int numCooks =3;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = false;


		// Run the simulation and then 
		//   feed the result into the method to validate simulation.
		System.out.println("Did it work? " + 
				Validate.validateSimulation(
						runSimulation(
								numCustomers, numCooks, 
								numTables, machineCapacity,
								randomOrders
								)
						)
				);
	}



}



