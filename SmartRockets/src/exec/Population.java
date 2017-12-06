package exec;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.CalculateFitness;
import objects.DNA;
import objects.Rocket;
import processing.core.PApplet;

public class Population extends Thread {
	//private static final long TARGET_WAIT_TIME = (long) (1000000000d / SmartRockets.FPS); // nanoseconds

	static PApplet parent;
	
	public Rocket[] rockets, matingpool;
	//ArrayList<Rocket> matingpool = new ArrayList<Rocket>();
	private boolean success = false;

	boolean allDone = false;
	public boolean passedTheObstacle = false;
	//public PVector targetPos = null;
	private CalculateFitness populationFitnessFunction;
	private Color fill;
	
	boolean debug = false;
	
	//private ThreadManager threadManager;
	private volatile boolean toStop = false, wait = false; //, toEvaluate = false, toSelect = false;
	


	public Population(int popsize, CalculateFitness calc, Color color) {
		// Class to process the arrays
		abstract class ProcessArray implements Runnable {
			ArrayList<Rocket> arr;
			
			public ProcessArray(ArrayList<Rocket> arr) {
				this.arr = arr;
			}
		}
		
		fill = color;
		setPopulationFitnessFunction(calc);
		
		int avlCores = Runtime.getRuntime().availableProcessors();
		ArrayList<ArrayList<Rocket>> initArrays = ThreadPool.getArrayLists(avlCores);/*new ArrayList<ArrayList<Rocket>>();
		
		
		// Initialize the array with as many arrays as there are available cores
		for (int a = 0; a < avlCores; a++) {
			initArrays.add(new ArrayList<Rocket>());
		}
		
		Thread[] threads = new Thread[avlCores];*/
		final int iterations = popsize / avlCores;
		
		ThreadPool.getThreadsFromArrays(initArrays, new ProcessArrayListFunction() {
			@Override
			public void process(ArrayList<Rocket> arr) {
				for (int i = 0; i < iterations; i++) {
					arr.add(new Rocket(Population.this));
				}
			}
		})
		.start()
		.join();
		
		rockets = ThreadPool.mergeArrays(initArrays);
	}

	@Override
	public void run() {
		while (!toStop) {
			update();
			try {
				sleep(10000);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * An alias for the java.lang.Thread.interrupt()
	 */
	public void continueWork() {
		interrupt();
	}
	
	public void setToStop() {
		toStop = true;
	}
	
	public void evaluate() {
		//this.matingpool.clear();
		Double maxfit = 0d;
		int threadCount = Runtime.getRuntime().availableProcessors();
		ArrayList<Rocket[]> arrays = ThreadPool.splitArray(threadCount, rockets);
		
		// Iterate through all rockets and calculate their fitness
		for (int i = 0; i < rockets.length; i++) {
			// Calculates fitness
			rockets[i].calcFitness();
			// If current fitness is greater than max, then make max equal to current
			if (rockets[i].getFitness() > maxfit) {
				maxfit = rockets[i].getFitness();
			}
		}
		
		ArrayList<Rocket> matingPollCandidates = new ArrayList<Rocket>();
		final Double MAX_FITNESS = maxfit;
		
		// Normalizes fitness
		new ThreadPool(arrays, new ProcessingFunction() {
			@Override
			public void process(Rocket[] arr) {
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].getFitness() != 0) {
						synchronized(matingPollCandidates) {
							matingPollCandidates.add(arr[i]);
						}
						arr[i].setFitness(arr[i].getFitness() / MAX_FITNESS);
						arr[i].setFitness(arr[i].getFitness() * arr[i].getFitness());
					}
				}
			}
		})
		.start()
		.join();
		
		double threshhold = 0;
		Collections.sort(matingPollCandidates, (r1, r2) -> (r2.getFitness()).compareTo(r1.getFitness()));
		int points = 10;
		int sz = points < matingPollCandidates.size() ? points : matingPollCandidates.size();

		for (int i = 0; i < sz; i++) {
			Rocket r = matingPollCandidates.get(i);
			if (debug)
				System.out.println(i + ". " + " - " + r.getFitness() + " :: " + r.getFinishTime());
			threshhold += matingPollCandidates.get(i).getFitness();
		}
		threshhold /= sz;
		
		if (debug)
			System.out.println(fill.toString()
				+ "\n\nThresh:" + threshhold);
		
		final double THRESH = threshhold;
		
		arrays = ThreadPool.splitArray(threadCount, matingPollCandidates);
		ArrayList<ArrayList<Rocket>> results = ThreadPool.getArrayLists(threadCount);
		
		new ThreadPool(arrays, results, new ProcessArrayListWithResultsFunction() {
			@Override
			public void process(Rocket[] arr, ArrayList<Rocket> res) {
				for (Rocket r : arr) {
					double fitness = r.getFitness();
					int n = (int) (fitness * 100);

					if (fitness >= THRESH) {
						for (int j = 0; j < n; j++) {
							res.add(r);
						}
					}
				}
			}
		})
		.start()
		.join();

		this.matingpool = ThreadPool.mergeArrays(results);
		
		if (debug) { // DEBUG
			parent.pushStyle();
			for (int d = 0; d < matingPollCandidates.size(); d++) {
				Rocket r = matingPollCandidates.get(d);
				double fitness = r.getFitness();			
				if (fitness >= threshhold) {
					parent.fill(255, 0, 0);
					parent.ellipse(r.getPos().x, r.getPos().y, 10, 10);
					parent.fill(0);
					parent.textSize(10);
					int x = (int) Math.abs(r.getPos().x), y = (int) Math.abs(r.getPos().y);
					if (x > 2560)
						x = 2560;
					if (y > 1080)
						y = 1080;
					parent.text(d, x + (x >= parent.width/2 ? -10 : 0), y + 10);

					System.out.println(d + ". " + r.getPos() + " - " + r.getFitness() + " :: " + r.getFinishTime());
				}
			}
			parent.popStyle();
		} // END DEBUG
	}
	
	public void selection() {
		final int avlCores = Runtime.getRuntime().availableProcessors(),
				iterations = rockets.length / avlCores,
				mSz = matingpool.length;
		ArrayList<ArrayList<Rocket>> newRocketsLists = ThreadPool.getArrayLists(avlCores);

		
		ThreadPool.getThreadsFromArrays(newRocketsLists, new ProcessArrayListFunction() {
			@Override
			public void process(ArrayList<Rocket> arr) {
				for (int i = 0; i < iterations; i++) {
					Rocket parentA = matingpool[(int) parent.random(mSz)];
					Rocket parentB = matingpool[(int) parent.random(mSz)];

					// Creates child by using crossover function
					DNA child = parentA.getDna().crossover(parentB.getDna(), parentA.getFitness() > parentB.getFitness());
					child.mutation();
					// Creates new rocket with child's DNA
					arr.add(new Rocket(Population.this, child));
				}
			}
		})
		.start()
		.join();
		
		// Assign new rockets array to the old array
		this.rockets = ThreadPool.mergeArrays(newRocketsLists);
		success = false;
		allDone = false;
	}
	
	public void update() {
		boolean allDone = true;
		
		for (Rocket r : rockets) {
			r.update( );
			
			if (!r.hasCrashed() && !r.hasFinished()) { // if not crashed and not finished
				if (allDone)
					allDone = false;
			}
		}
		
		if (allDone)
			this.allDone = true;
		
	}

	public void draw() {
		for (int i = 0; i < rockets.length; i++) {
			rockets[i].show();
		}
	}
	
	public static void setParent(PApplet p) {
		parent = p;
	}

	public boolean allDone() {
		return allDone;
	}

	public boolean hasSuccess() {
		return success;
	}

	public void setSuccess() {
		if (!success)
			success = true;
	}

	public CalculateFitness getPopulationFitnessFunction() {
		return populationFitnessFunction;
	}

	public void setPopulationFitnessFunction(CalculateFitness populationFitnessFunction) {
		this.populationFitnessFunction = populationFitnessFunction;
	}

	public Color getFill() {
		return fill;
	}

	public void setFill(Color fill) {
		this.fill = fill;
	}
	
	public void setWait(boolean wait) {
		this.wait = wait;
	}
}
