package exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopulationManager {
	List<Population> populations = Collections.synchronizedList(new ArrayList<Population>());
	private ArrayList<PopulationBlueprint> blueprints = new ArrayList<PopulationBlueprint>();
	//private ProgressListener progressListener;

	public PopulationManager() {
		super();
	}
	
	public boolean addPopulation(Population p) {
		for (Population pThis : populations)
			if (pThis == p)
				return false;
		
		return populations.add(p);
	}
	
	public void addPopulationBlueprint(PopulationBlueprint... p) {
		for (PopulationBlueprint pBl : p)
			this.blueprints.add(pBl);
	}
	
	public void initializePopulations() {
		if (blueprints == null)
			return;
		
		//Population.setManager(this);

		
		long beginTime = System.nanoTime();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for (PopulationBlueprint pBl : blueprints) {	
			threads.add(new Thread(new Runnable() {
				@Override
				public void run() {
					Population p = new Population(pBl);
					populations.add(p);
					p.initialize();
				}
			}));
		}
		

		ThreadPool threadPool = new ThreadPool(threads);
		//progressListener = new ProgressListener(threadPool);
		
		//progressListener.start();
		threadPool.start().join();
		

		
		SmartRockets.printExecTime("Create time", beginTime);
		blueprints = null;
	}
	
	public void draw() {
		for (Population p : populations)
			p.draw();
	}
	
	public void start() {
		for (Population p : populations)
			p.start();
	}
	
	public void continueWork() {
		//setWait(false);
		for (Population p : populations)
			if (!p.allDone())
				p.continueWork();
	}
	
	public void setToStop() {
		for (Population p : populations)
			p.setToStop();
	}
	
	public void evaluate() {
		for (Population p : populations)
			p.evaluate();
	}
	
	@SuppressWarnings("unused")
	public void selection() {
		if (true)
			for (Population p : populations)
				p.selection();
		else {
			for (Population p : populations)
				p.startSelection();
			while(!finishedSelection());
//			Thread[] threads = new Thread[populations.size()];
//			int counter = 0;
//			for (Population p : populations) {	
//				threads[counter++] = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						p.selection();
//					}
//				});
//			}
//	
//	
//			ThreadPool threadPool = new ThreadPool(threads)
//					.start();
//			
//			threadPool.join();
		}
	}
	
	public boolean finishedSelection() {
		for (Population p : populations)
			if (!p.finishedSelection())
				return false;
		return true;
	}
	
	public boolean allDone() {
		for (Population p : populations)
			if (!p.allDone())
				return false;
		return true;
	}
	
	public void setWait(boolean wait) {
		for (Population p : populations)
			p.setWait(wait);
	}
	
	public int getProgress() {
		int res = 0;
		ArrayList<Integer> buff = new ArrayList<Integer>();
	
		if (populations.size() != 0) {
			for (Population p : populations) {
				res += p.getProgress();
				if (p.getProgress() != 0)
					buff.add(p.getProgress());
			}
			
			if (res != 0)
				res /= populations.size();

			if (res == 100)
				for (Population p : populations)
					p.resetProgress();
		}
		
//		if (res != 0) {
//			System.out.println("progress start");
//
//			for (int i : buff)
//				System.out.println(i);
//			
//			System.out.println("progress end");
//		}

		return res;
	}
}












