package exec;
import java.util.ArrayList;

import objects.Rocket;

class Worker extends Thread {
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
	
	public static Thread[] createThreadPool(int threadCount, Rocket[] toProcess, ProcessingFunction func) {
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
		
		return threads;
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
}






















