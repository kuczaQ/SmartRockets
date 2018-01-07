package exec;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.CalculateFitness;
import objects.DNA;
import objects.Rocket;
import processing.core.PApplet;

public class Population extends Thread {
	private static final int NUM_OF_THREADS = 1; //Runtime.getRuntime().availableProcessors(); 
	private static final boolean MULTITHREADED = false;
	static PApplet parent;
	static PopulationManager manager;

	public Rocket[] rockets;
	private ArrayList<Rocket> matingPool = new ArrayList<Rocket>();
	//ArrayList<Rocket> matingpool = new ArrayList<Rocket>();
	private boolean success = false;

	boolean allDone = false;
	public boolean passedTheObstacle = false;
	//public PVector targetPos = null;
	private CalculateFitness populationFitnessFunction;
	private Color fill;

	boolean debug = false;

	private ProgressListener progressListener;
	private Integer progress;

	//private ThreadManager threadManager;
	private volatile boolean toStop = false, wait = false; //, toEvaluate = false, toSelect = false;
	int popSize;
	private volatile boolean startSelection = false, doneSelecting = true;

	public Population(int popSize, CalculateFitness calc, Color color) {
		this.fill = color;
		setPopulationFitnessFunction(calc);
		this.popSize = popSize;
		//initialize(popSize);
	}

	public Population(PopulationBlueprint blueprint) {
		this(blueprint.POPULATION_SIZE, blueprint.CALCULATE_FITNESS, blueprint.COLOR);
		//this.progress = progress;
	}

	public void initialize() {
		ArrayList<ArrayList<Rocket>> initArrays = ThreadPool.getArrayLists(NUM_OF_THREADS);
		final int iterations = popSize / NUM_OF_THREADS;



		ThreadPool threads = ThreadPool.getThreadsFromArrays(initArrays, new ProcessArrayListFunction() {
			@Override
			public void process(ArrayList<Rocket> arr) {
				for (int i = 0; i < iterations; i++) {
					arr.add(new Rocket(Population.this));
					progress = (i * 100) / iterations;
				}
			}
		});
		progressListener = new ProgressListener(threads);
		progressListener.start();

		threads.start().join();

		rockets = ThreadPool.mergeArraysToRocketArray(initArrays);
	}

	@Override
	public void run() {
		while (!toStop) {
			if (startSelection) {
				doneSelecting = false;
				selection();
				startSelection = false;
				doneSelecting = true;
			} else {
				update();
			}
			
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
		if (!toStop)
			toStop = true;
	}

	public void evaluate() {
		//this.matingpool.clear();
		Double maxfit = 0d;
		ArrayList<Rocket[]> arrays = ThreadPool.splitArray(NUM_OF_THREADS, rockets);

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

		for (int i = 0; i < rockets.length; i++) {
			if (rockets[i].getFitness() != 0) {
				//debugR.add(rockets[i]);
				matingPollCandidates.add(rockets[i]);
				//System.out.println("Fitness: " + rockets[i].fitness + " :: " + rockets[i].finishTime);
				rockets[i].setFitness(rockets[i].getFitness() / maxfit);
				rockets[i].setFitness(rockets[i].getFitness() * rockets[i].getFitness());
			}
		}

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


		ArrayList<Rocket> matingPoolBuffer = new ArrayList<Rocket>();
		for (Rocket r : matingPollCandidates) {
			double fitness = r.getFitness();
			int n = (int) (fitness * 100);

			if (fitness >= threshhold) {
				for (int j = 0; j < n; j++) {
					matingPoolBuffer.add(r);
				}
			}
		}

		this.matingPool.clear();
		int counter = 0;

		for (Rocket r : matingPoolBuffer)
			this.matingPool.add(r);

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
		long beginTime = System.nanoTime();
		if (!MULTITHREADED) {
			// Single-threaded code
			this.progress = new Integer(0);
			
			Rocket[] newRockets = new Rocket[rockets.length];

			int mSz = matingPool.size();

			//int childrenPopulationSz = (int) (rockets.length * 1.0f);


			for (int i = 0; i < rockets.length; i++) {
				// Picks random DNA
				Rocket parentA = matingPool.get((int) parent.random(mSz));
				Rocket parentB = matingPool.get((int) parent.random(mSz));

				// Creates child by using crossover function
				DNA child = parentA.getDna().crossover(parentB.getDna(), parentA.getFitness() > parentB.getFitness());
				child.mutation();
				// Creates new rocket with child's DNA
				newRockets[i] = new Rocket(this, child);
				
				progress = (i * 100) / rockets.length;
			}
			//			for (Rocket r : newRockets) {
			//				if (r == null) {
			//					throw new RuntimeException("null rocket !!!");
			//				}
			//			}

			rockets = newRockets;
			progress = 100;
		} else {
			// Multi-threaded code
			final int avlCores = Runtime.getRuntime().availableProcessors(),
					iterations = rockets.length / avlCores,
					mSz = matingPool.size();
			ArrayList<ArrayList<Rocket>> newRocketsLists = ThreadPool.getArrayLists(avlCores);


			ThreadPool.getThreadsFromArrays(newRocketsLists, new ProcessArrayListFunction() {
				@Override
				public void process(ArrayList<Rocket> arr) {
					for (int i = 0; i < iterations; i++) {
						Rocket parentA = matingPool.get((int) parent.random(mSz));
						Rocket parentB = matingPool.get((int) parent.random(mSz));

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
			this.rockets = ThreadPool.mergeArraysToRocketArray(newRocketsLists);
		}

		success = false;
		allDone = false;

		if (debug)
			SmartRockets.printExecTime("@" + this.getId() + "-selection time", beginTime);
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

	public int getProgress() {
		if (progressListener != null && progress != null) {
			throw new RuntimeException("Invalid progress state!");
		}

		if (progressListener != null)
			return progressListener.progress;
		else if (progress != null)
			return progress;
		else
			return 0;
	}

	public void resetProgress() {
		if (progressListener != null && progress != null) {
			throw new RuntimeException("Invalid progress state!");
		}

		if (progressListener != null)
			this.progressListener = null;
		else if (progress != null)
			this.progress = null;
	}

	public void startSelection() {
		startSelection = true;
		interrupt();
	}
	
	public boolean finishedSelection() {
		return doneSelecting;	
	}

	public static void setManager(PopulationManager manager) {
		Population.manager = manager;
	}
}

















