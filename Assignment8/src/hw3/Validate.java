package hw3;

/**
 * Author Name: Huizhe Wang   
 * NUID: 001614841  
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hw3.SimulationEvent;


/**
 * Validates a simulation
 */
public class Validate {
	private static class InvalidSimulationException extends Exception {
		public InvalidSimulationException() { }
	};

	// Helper method for validating the simulation
	private static void check(boolean check,
			String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : "+message);
			throw new Validate.InvalidSimulationException();
		}
	}

	/** 
	 * Validates the given list of events is a valid simulation.
	 * Returns true if the simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation
	 *   in the order they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<SimulationEvent> events) {
		try {
			check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
					"Simulation didn't start with initiation event");
			check(events.get(events.size()-1).event == 
					SimulationEvent.EventType.SimulationEnded,
					"Simulation didn't end with termination event");

			/* In hw3 you will write validation code for things such as:
				Should not have more eaters than specified
				Should not have more cooks than specified
				The coffee shop capacity should not be exceeded
				The capacity of each machine should not be exceeded
				Eater should not receive order until cook completes it
				Eater should not leave coffee shop until order is received
				Eater should not place more than one order
				Cook should not work on order before it is placed
			 */
			
			int machineCapacity = 0;
			int numCooks = 0;
			int numMachines = 0;
			
			
			// current customers in CoffeeShop
			ArrayList<Customer> currentCustomers = new ArrayList<Customer>();
			//customers waiting for tables
			ArrayList<Customer> waitingCustomers = new ArrayList<Customer>();
			
			// current customers placed order
			ArrayList<Customer> customerPlaced = new ArrayList<Customer>();
			// current customers receive order
			ArrayList<Customer> customerReceived = new ArrayList<Customer>();

			//the total orders from customers
			HashMap<Integer, List<Food>> placedOrders = new HashMap<Integer, List<Food>>();
			//the orders are cooking
			HashMap<Integer, List<Food>> cookingOrders = new HashMap<Integer, List<Food>>();
			//the orders have been completed
			ArrayList<Integer> completedOrders = new ArrayList<Integer>();
			List<Food> order;
			
			for (SimulationEvent event : events.subList(1, events.size() - 1)) {
				switch (event.event) {
				case SimulationStarting:
					break;
					
				case SimulationEnded:
					break;
					
				case CustomerStarting:
					break;
					
				case CustomerEnteredCoffeeShop:
					currentCustomers.add(event.customer);
					waitingCustomers.add(event.customer);
					check(
							currentCustomers.size() <= events.get(0).simParams[2], 
							"Customer broke in " + currentCustomers.toString() + " number of tables " + events.get(0).simParams[2] + " " + currentCustomers.size()
							);
					break;
					
				case CustomerPlacedOrder:
					check(
							waitingCustomers.contains(event.customer), "Customer broke in and placed order"
							);
					waitingCustomers.remove(event.customer);			
					customerPlaced.add(event.customer);
					placedOrders.put(event.orderNumber, event.orderFood);
					break;
					
				case CustomerReceivedOrder:
					check(
							completedOrders.contains(event.orderNumber), "Order " + event.orderNumber + " was not completed"
							);
					completedOrders.remove(Integer.valueOf(event.orderNumber));
					customerPlaced.remove(event.customer);
					customerReceived.add(event.customer);
					break;
					
				case CustomerLeavingCoffeeShop:
					check(
							customerReceived.contains(event.customer), "Customer left"
							);
					customerReceived.remove(event.customer);
					currentCustomers.remove(event.customer);
					break;
					
					
				case CookStarting:
					numCooks++;
					break;
					
				case CookReceivedOrder:
					order = placedOrders.remove(event.orderNumber);
					check(order != null, "Order was not placed");
					cookingOrders.put(event.orderNumber, event.orderFood);
					break;
					
				case CookStartedFood:
					break;
					
				case CookFinishedFood:
					break;
					
				case CookCompletedOrder:
					order = cookingOrders.remove(event.orderNumber);
					check(order != null, "Food was not in cooking");
					completedOrders.add(event.orderNumber);
					break;
					
				case CookEnding:
					numCooks--;
					break;
					
					
				case MachineStarting:
					numMachines++;
					break;
					
				case MachineStartingFood:
					machineCapacity++;
					check(machineCapacity <= 4, "too much food " + machineCapacity);
					break;
					
				case MachineDoneFood:
					machineCapacity--;
					break;
					
				case MachineEnding:
					numMachines--;
					break;
					
				default:
					break;
				}
			}
			check(waitingCustomers.isEmpty(), "Customers kept waiting");
			check(customerPlaced.isEmpty(), "Customer didn't receive order");
			check(customerReceived.isEmpty(), "Customer didn't leave");

			if (placedOrders.size() != cookingOrders.size() + completedOrders.size())
				return false;
			
			if (!placedOrders.isEmpty())
				return false;
			
			if (!cookingOrders.isEmpty())
				return false;
			
			if (!completedOrders.isEmpty())
				return false;
			
			if (numCooks != 0)
				return false;
			
			if (numMachines != 0)
				return false;
			
			if (machineCapacity != 0)
				return false;	
			
			return true;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}
}
