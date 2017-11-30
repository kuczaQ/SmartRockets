package exec;

import java.util.ArrayList;

import objects.Rocket;

public class ThreadManager {
	private class Worker extends Thread {
		Rocket[] toProcess;
		volatile boolean done = false, toStop = false;
		long startTime, endTime;
		
		public Worker(Rocket... r) {
			System.out.println(r.length + "@" + this.getId());
			toProcess = r;
		}
		
		@Override
		public void run() {
			while (!toStop) {
				//while (done); // wait
				startTime = System.nanoTime();
				for (Rocket r : toProcess) {
					r.update();
					done = true;
				}
				endTime = System.nanoTime() - startTime;
				long time = (long) ((1000000000d / SmartRockets.FPS) - endTime) / 1000000;
				if (time < 5)
					time = 5;
				
				try {
					sleep(time); // nanoseconds * 1,000,000 = milliseconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void setDone() {
			done = false;
		}
		
		public boolean done() {
			return done;
		}
		public void setToStop() {
			toStop = true;
		}
	}
	
	//###############################
	
	Population population;
	int threadCount;
	Worker[] threads;
	
	
	public ThreadManager(Population p, int threadCount, Rocket[] toProcess) {
		population = p;
		threads = new Worker[threadCount]; 
		this.threadCount = threadCount;
		createThreads(toProcess);
	}
	
	private void createThreads(Rocket[] toProcess) {
		int chunkSize = toProcess.length / threadCount, counter = 0;
		ArrayList<Rocket> buffer = new ArrayList<Rocket>(chunkSize);
		
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
			
			threads[a] = new Worker( res);
		}
	}
	
	public void start() {
		for (Thread t : threads) {
			t.start();
		}
	}
	
	public void setDoneFalse() {
		for (Worker w : threads) {
			w.setDone();
		}
	}
	
	public void stop() {
		for (Worker w : threads) {
			w.setToStop();
		}
	}
		
	public void join() throws InterruptedException {
		for (Worker w : threads)
			w.join();
	}
	
	public boolean allDone() {
		for (Worker w : threads)
			if (!w.done())
				return false;
		return true;
	}
}
























