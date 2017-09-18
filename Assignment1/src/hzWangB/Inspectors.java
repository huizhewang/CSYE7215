package hzWangB;


import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;



public class Inspectors {
	Even even;
	Odd odd;
	Order order;
	Jack jack;
	ArrayList<Thread> workers = new ArrayList<Thread>();
	
	public static void main(String[] args) throws InterruptedException {
		int numElements = 1000; // number of integers in the list
		
		Inspectors inspectors = new Inspectors();
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++){
			Random rd = new Random();
			int next = rd.nextInt();
			list.add(next);
		}
		
       //repeated ten times for the same LinkedList
		try{
			for(int i = 0; i < 10; i++){
				LinkedList<Integer> list2 = new LinkedList<Integer>();
				for(int j = 0; j < numElements; j++){
					list2.add(list.get(j));
				}
				inspectors.inspect(list2);

//					System.out.println("Maximum Number: " + maximizer.max(list2));
				}
		} catch (InterruptedException e){
			e.printStackTrace();
			fail("The test failed because the procedure was interrupted unexpectedly.");

		}
			
	}

	private void inspect(LinkedList<Integer> list) throws InterruptedException{
		// TODO Auto-generated method stub
		even = new Even(list);
		odd = new Odd(list);
		order = new Order(list);
		jack = new Jack(list);
		
//		even.start();
//		odd.start();
//		order.start();
//		jack.start();
		
		workers.add(even);
		workers.add(odd);
		workers.add(order);
		workers.add(jack);

		// start threads
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).start();
		}
		
		// wait for threads to finish
		for (int i=0; i<workers.size(); i++){
			workers.get(i).join();	
		}
		workers.clear(); 
		System.out.println("-------------------");

	} 

}
