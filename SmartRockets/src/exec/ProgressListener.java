package exec;

import java.util.ArrayList;

public class ProgressListener extends Thread {
	int progress;
	Thread[] threads;
	// ArrayList<Thread> threads = new ArrayList<Thread>();
	
	public ProgressListener(ThreadPool tp) {
		super();
		for (Thread t : tp.threads)
			if (!(t instanceof ProcessArray))
				throw new RuntimeException("One does not simply use ProgressListner "
						+ "without ProcessArray!");
		
		this.threads = tp.threads;
	}
	
	@Override
	public void run() {
		int sum;
		
		while (true) {
			sum = 0;
			if (threads.length != 0) {
				for (Thread t : threads) {
					sum += ((ProcessArray) t).getProgress();
				}

				progress = sum / threads.length;

				if (!ThreadPool.areAlive(threads))
					break;
			}
//			try {
//				wait(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		progress = 100;
		System.out.println("ProgressListener stopped");
	}
}
















