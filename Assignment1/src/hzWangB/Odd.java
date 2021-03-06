package hzWangB;

import java.util.LinkedList;

//Part B
//1. Inspector named “Even”: Collect only even numbers in the list.
//2. Inspector named “Odd”: Collect only odd numbers in the list.
//3. Inspector named “Order”: Collect only those numbers that are larger than the last collected number.￼￼
//4. Inspector called “Jack”: Collect numbers until either the sum adds up to 21, or the list is empty.

public class Odd extends Thread{
	protected LinkedList<Integer> list;
	protected LinkedList<Integer> Odd = new LinkedList<>();
	
	public Odd(LinkedList<Integer> list){
		this.list = list;		
	}
	
	public void run() {
		while (true) {
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty()){
//					System.out.println("");
					System.out.println("OddInspector: " + Thread.currentThread().getId() + ": " + Odd);
					return; // list is empty
				}
					
				number = list.remove();
			}
			
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
			if(number %2 == 1){
				Odd.add(number);
			}

		}
	}

}
