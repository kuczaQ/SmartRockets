package exec;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.CalculateFitness;
import objects.DNA;
import objects.Rocket;
import processing.core.PApplet;
import processing.core.PVector;

public class Population extends Thread {
	private static final long TARGET_WAIT_TIME = (long) (1000000000d / SmartRockets.FPS); // nanoseconds

	static PApplet parent;
	
	public Rocket[] rockets;
	ArrayList<Rocket> matingpool = new ArrayList<Rocket>();
	private boolean success = false;

	boolean allDone = false;
	public boolean passedTheObstacle = false;
	public PVector targetPos = null;
	private CalculateFitness populationFitnessFunction;
	private Color fill;
	
	//private ThreadManager threadManager;
	private volatile boolean toStop = false, wait = false; //, toEvaluate = false, toSelect = false;
	


	public Population(int popsize, CalculateFitness calc, Color color) {
		fill = color;
		setPopulationFitnessFunction(calc);
		rockets = new Rocket[popsize];
		for (int i = 0; i < popsize; i++) {
			rockets[i] = new Rocket(this);
		}
		
		//threadManager = new ThreadManager(this, threadCount, rockets);
	}

	@Override
	public void run() {
		long startTime, endTime, time;
		
		while (!toStop) {
			//			if (wait) {
			//				try {
			//					sleep(10000);
			//				} catch (InterruptedException e) {}
			//			}
			//			else {
			//				startTime = System.nanoTime();

			update();
			
			//
			//				endTime = System.nanoTime() - startTime;
			//				time = (TARGET_WAIT_TIME - endTime) / 1000000; // nanoseconds * 1,000,000 = milliseconds
			//				if (time < 0)
			//					time = 0;

			try {
				sleep(10000);
			} catch (InterruptedException e) {}

			//			try {
			//				sleep(10000);
			//			} catch (InterruptedException e) {
			//				// wakes up
			//			}
			//			}
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

//		threadManager.stop();
//		try {
//			threadManager.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
//	public void setToUpdate() {
//		threadManager.setDoneFalse();
//	}
//	
//	public boolean allThreadsDone() {
//		return threadManager.allDone();
//	}
	
	public void evaluate() {
//		System.out.println("Evaluate called");
//		try {
//			throw new Exception();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
		this.matingpool.clear();
		
		Double maxfit = 0d;
		// Iterate through all rockets and calcultes their fitness
			for (int i = 0; i < rockets.length; i++) {
				// Calculates fitness
				rockets[i].calcFitness();
				// If current fitness is greater than max, then make max eqaul to current
				if (rockets[i].fitness > maxfit) {
					maxfit = rockets[i].fitness;
				}
			}
		

		ArrayList<Rocket> debugR = new ArrayList<Rocket>();
		ArrayList<Rocket> matingPollCandidates = new ArrayList<Rocket>();

//		for (int i = 0; i < rockets.length; i++) {
////			System.out.println("Fitness: " + rockets[i].fitness + " :: " + rockets[i].finishTime 
////					+ " :: " + rockets[i].passedObstacleTime);
//			if (rockets[i].fitness != 0) {
//				debugR.add(rockets[i]);
//				
////				rockets[i].fitness /= maxfit;
////				rockets[i].fitness *= rockets[i].fitness;
//			}
//		}
				
		// Normalises fitnesses

		for (int i = 0; i < rockets.length; i++) {
			if (rockets[i].fitness != 0) {
				debugR.add(rockets[i]);
				matingPollCandidates.add(rockets[i]);
				//System.out.println("Fitness: " + rockets[i].fitness + " :: " + rockets[i].finishTime);
				rockets[i].fitness /= maxfit;
				rockets[i].fitness *= rockets[i].fitness;
			}
		}
		
		double threshhold = 0;
		Collections.sort(matingPollCandidates, (r1, r2) -> (r2.fitness).compareTo(r1.fitness));
		int points = 10;
		int sz = points < matingPollCandidates.size() ? points : matingPollCandidates.size();

		for (int i = 0; i < sz; i++) {
			threshhold += matingPollCandidates.get(i).fitness;
		}
		threshhold /= sz;

		if (matingPollCandidates.size() == 0)
			System.out.println();
		
		System.out.println(fill.toString()
				+ "\nThresh:" + threshhold);
		for (Rocket r : matingPollCandidates) {
			double fitness = r.fitness;
			int n = (int) (fitness * 100);

			if (fitness >= threshhold) {
				for (int j = 0; j < n; j++) {
					this.matingpool.add(r);
				}
			}
		}

		// debug
		parent.pushStyle();
		for (int d = 0; d < matingPollCandidates.size(); d++) {
			Rocket r = matingPollCandidates.get(d);
			double fitness = r.fitness;			
			if (fitness >= threshhold) {
				parent.fill(255, 0, 0);
				parent.ellipse(r.pos.x, r.pos.y, 10, 10);
				parent.fill(0);
				parent.textSize(10);
				int x = (int) Math.abs(r.pos.x), y = (int) Math.abs(r.pos.y);
				if (x > 2560)
					x = 2560;
				if (y > 1080)
					y = 1080;
				parent.text(d, x + (x >= parent.width/2 ? -10 : 0), y + 10);

				System.out.println(d + ". " + r.pos + " - " + r.fitness + " :: " + r.finishTime);
			}
		}
		parent.popStyle();
	}
	
	// Selects appropriate genes for child
	public void selection() {
//		System.out.println("Selection called");
//		try {
//			throw new Exception();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		Rocket[] newRockets = new Rocket[rockets.length];
		if (matingpool.size() == 0 )
			System.out.println("chuj");
		int mSz = matingpool.size();

		int childrenPopulationSz = (int) (rockets.length * 1.0f),
				//newPopulationSz = rockets.length - childrenPopulationSz,
				i = 0;


		for (; i < childrenPopulationSz; i++) {
			// Picks random DNA
			Rocket parentA = matingpool.get((int) parent.random(mSz));
			Rocket parentB = matingpool.get((int) parent.random(mSz));

			// Creates child by using crossover function
			DNA child = parentA.getDna().crossover(parentB.getDna(), parentA.fitness > parentB.fitness);
			child.mutation();
			// Creates new rocket with child's DNA
			newRockets[i] = new Rocket(this, child);
		}
		//i--;
		for (; i < rockets.length; i++) {
			newRockets[i] = new Rocket(this);
		}

		for (Rocket r : newRockets) {
			if (r == null) {
				System.out.println("null");
			}
		}

		// Assign new rockets array to the old array
		this.rockets = newRockets;
		success = false;
		allDone = false;
	}

	public void update() {
		boolean allDone = true;
		
		for (Rocket r : rockets) {
			r.update( );
			
			if (!r.hasCrashed() && !r.finished) { // if not crashed and not finished
				if (allDone)
					allDone = false;
			}
		}
		
		if (allDone)
			this.allDone = true;
		
	}

	public void draw() {
		parent.pushStyle();
		parent.strokeWeight(10);
		parent.stroke(getFill().getRGB(), getFill().getAlpha());
		for (int i = 0; i < rockets.length; i++) {
			rockets[i].show();
		}
		parent.popStyle();
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
