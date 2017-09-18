package hzWangA;

import static org.junit.Assert.fail;

//import java.util.LinkedList;
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {
	
	int numThreads;
	ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelMaximizer(int numThreads) {
		this.numThreads =numThreads;
		workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
	}


	
	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10; // number of integers in the list
		
		ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++){
			Random rd = new Random();
			int next = rd.nextInt();
			list.add(next);
		}
			
		// run the maximizer
	    //repeated ten times for the same LinkedList
		try {
			for(int i = 0; i < 10; i++){
			LinkedList<Integer> list2 = new LinkedList<Integer>();
			for(int j = 0; j < numElements; j++){
				list2.add(list.get(j));
			}
				System.out.println("Maximum Number: " + maximizer.max(list2));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");

		}
		
	}
	
	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		int max = Integer.MIN_VALUE; // initialize max as lowest value
		

		 System.out.println("LinkedList: " + list);
//		 System.out.println(Thread.currentThread().getId());
		// run numThreads instances of ParallelMaximizerWorker
		for (int i = 0; i < numThreads; i++) {
			workers.add(new ParallelMaximizerWorker(list));
			workers.get(i).start();
		}
		System.out.println(workers.size());
		
		 
		// wait for threads to finish
		for (int i=0; i<workers.size(); i++)
			workers.get(i).join();
	


		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
		 for(ParallelMaximizerWorker pmw:workers){
//			   System.out.println(pmw.getPartialMax());
			   max = Math.max(max,pmw.getPartialMax());
		   }
		   
		workers.clear(); 
		return max;
	}
	
}
