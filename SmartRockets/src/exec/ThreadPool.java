package exec;
import java.util.ArrayList;

import objects.Rocket;

class ThreadPool {
	static class Worker extends Thread {
		Rocket[] toProcess;
		ProcessingFunction func;

		public Worker(ProcessingFunction func, Rocket... r) {
			this.func = func;
			toProcess = r;
		}

		@Override
		public void run() {
			func.process(toProcess);
		}
	}

	static class ProcessArray extends Thread {
		ArrayList<Rocket> arr;
		ProcessArrayListFunction func;

		public ProcessArray(ProcessArrayListFunction func, ArrayList<Rocket> arr) {
			this.arr = arr;
			this.func = func;
		}

		@Override
		public void run() {
			func.process(arr);
		}
	}
	
	static class ProcessArrayWithResults extends Thread {
		Rocket[] arr;
		ArrayList<Rocket> res;
		ProcessArrayListWithResultsFunction func;

		public ProcessArrayWithResults(ProcessArrayListWithResultsFunction func, Rocket[] arr, ArrayList<Rocket> res) {
			this.arr = arr;
			this.res = res;
			this.func = func;
		}

		@Override
		public void run() {
			func.process(arr, res);
		}
	}

	Thread[] threads;
	ArrayList<Rocket> results = null;

	public ThreadPool(Thread[] threads) {
		super();
		this.threads = threads;
	}

	public ThreadPool(ArrayList<Rocket[]> arrays, ProcessingFunction func) {
		super();
		threads = new Thread[arrays.size()];

		for (int a = 0; a < arrays.size(); a++)
			threads[a] = new Worker(func, arrays.get(a));
	}
	
	public ThreadPool(ArrayList<Rocket[]> arrays, ArrayList<ArrayList<Rocket>> res, ProcessArrayListWithResultsFunction func) {
		super();
		threads = new Thread[arrays.size()];

		for (int a = 0; a < arrays.size(); a++)
			threads[a] = new ProcessArrayWithResults(func, arrays.get(a), res.get(a));
	}

	public ThreadPool start() {
		for (Thread t : threads)
			t.start();
		return this;
	}

	public ThreadPool join() {
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return this;
	}

	public static void start(Thread[] threads) {
		for (Thread t : threads)
			t.start();
	}

	public static void join(Thread[] threads) {
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public static ThreadPool createThreadPool(int threadCount, Rocket[] toProcess, ProcessingFunction func) {
		int chunkSize = toProcess.length / threadCount, counter = 0;
		ArrayList<Rocket> buffer = new ArrayList<Rocket>(chunkSize);
		Thread[] threads = new Thread[threadCount];

		for (int a = 0; a < threadCount; a++) {
			buffer.clear();
			counter++;
			if (a != threadCount - 1) 
				for (; counter % (chunkSize + 1) != 0; counter++) 
					buffer.add(toProcess[counter - 1]);

			else 
				for (; counter <= toProcess.length; counter++) 
					buffer.add(toProcess[counter - 1]);	

			Rocket[] res = new Rocket[buffer.size()];

			for (int index = 0; index < buffer.size(); index++)
				res[index] = buffer.get(index);

			threads[a] = new Worker(func, res);
		}

		return new ThreadPool(threads);
	}

	public static ArrayList<ArrayList<Rocket>> getArrayLists(int avlCores) {
		ArrayList<ArrayList<Rocket>> res = new ArrayList<ArrayList<Rocket>>();
		
		// Initialize the array with as many arrays as there are available cores
		for (int a = 0; a < avlCores; a++) {
			res.add(new ArrayList<Rocket>());
		}

		return res;
	}
	
	public static ThreadPool getThreadsFromArrays(ArrayList<ArrayList<Rocket>> initArrays, ProcessArrayListFunction func) {
		int sz = initArrays.size();
		Thread[] threads = new Thread[sz];


		// Create threads and give them their respective arrays
		for (int a = 0; a < initArrays.size(); a++) {
			threads[a] = new ProcessArray(func, initArrays.get(a));
		}

		return new ThreadPool(threads);
	}

	public static Rocket[] mergeArrays(ArrayList<ArrayList<Rocket>> initArrays) {
		int szSum = 0;
		Rocket[] res;
		
		// Get the number of all elements
		for (ArrayList<Rocket> arr : initArrays)
			szSum += arr.size();

		// Initialize the rockets array
		res = new Rocket[szSum];

		// Merge all arrays into one
		for (int a = 0, counter = 0, index = 0; a < szSum; a++) {
			if (a % ( initArrays.get(counter).size()) == 0 && a != 0) {
				counter++;
				index = 0;
			}
			res[a] = initArrays.get(counter).get(index++);
		}

		return res;
	}

	public static ArrayList<Rocket[]> splitArray(int threadCount, Rocket[] toProcess) {
		int chunkSize = toProcess.length / threadCount, counter = 0;
		ArrayList<Rocket> buffer = new ArrayList<Rocket>(chunkSize);
		ArrayList<Rocket[]> rockets = new ArrayList<Rocket[]>(threadCount);

		for (int a = 0; a < threadCount; a++) {
			buffer.clear();
			counter++;
			if (a != threadCount - 1) 
				for (; counter % (chunkSize + 1) != 0; counter++) 
					buffer.add(toProcess[counter - 1]);

			else 
				for (; counter <= toProcess.length; counter++) 
					buffer.add(toProcess[counter - 1]);	

			Rocket[] res = new Rocket[buffer.size()];

			for (int index = 0; index < buffer.size(); index++)
				res[index] = buffer.get(index);

			rockets.add(res);
		}

		return rockets;
	}

	public static ArrayList<Rocket[]> splitArray(int threadCount, ArrayList<Rocket> toProcess) {
		int chunkSize = toProcess.size() / threadCount, counter = 0;
		ArrayList<Rocket> buffer = new ArrayList<Rocket>(chunkSize);
		ArrayList<Rocket[]> rockets = new ArrayList<Rocket[]>(threadCount);

		for (int a = 0; a < threadCount; a++) {
			buffer.clear();
			counter++;
			if (a != threadCount - 1) 
				for (; counter % (chunkSize + 1) != 0; counter++) 
					buffer.add(toProcess.get(counter - 1));

			else 
				for (; counter <= toProcess.size(); counter++) 
					buffer.add(toProcess.get(counter - 1));	

			Rocket[] res = new Rocket[buffer.size()];

			for (int index = 0; index < buffer.size(); index++)
				res[index] = buffer.get(index);

			rockets.add(res);
		}

		return rockets;
	}
	
	public int getThreadsCount() {
		return threads.length;
	}

	public ArrayList<Rocket> getResults() {
		return results;
	}
}






















