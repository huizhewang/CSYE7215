package hzWangB;

import java.util.LinkedList;

//Part B
//1. Inspector named “Even”: Collect only even numbers in the list.
//2. Inspector named “Odd”: Collect only odd numbers in the list.
//3. Inspector named “Order”: Collect only those numbers that are larger than the last collected number.￼￼
//4. Inspector called “Jack”: Collect numbers until either the sum adds up to 21, or the list is empty.

public class Jack extends Thread{
	protected LinkedList<Integer> list;
	protected LinkedList<Integer> Jack = new LinkedList<>();
	
	public Jack(LinkedList<Integer> list){
		this.list = list;		
	}
	
	public void run() {
		while (true) {
			int number;
			int sum = 0;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty() || sum >= 21){
//					System.out.println("");
					System.out.println("JackInspector: " + Thread.currentThread().getId() + ": (sum = " + sum + ") " + Jack);
					return; // list is empty or the sum adds up to 21

				}	
				number = list.remove();
			}
			
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
			if(sum < 21){
				Jack.add(number);
				sum = sum + number;
			}

		}
	}

}
