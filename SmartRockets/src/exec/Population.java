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
	
	public Rocket[] rockets;
	ArrayList<Rocket> matingpool = new ArrayList<Rocket>();
	private boolean success = false;

	boolean allDone = false;
	public boolean passedTheObstacle = false;
	//public PVector targetPos = null;
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
		// long startTime, endTime, time;
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
		this.matingpool.clear();
		Double maxfit = 0d;
		ArrayList<Rocket[]> arrays = Worker.splitArray(Runtime.getRuntime().availableProcessors(), rockets);
		//TODO finish this fucking shit
		
		// Iterate through all rockets and calcultes their fitness
		for (int i = 0; i < rockets.length; i++) {
			// Calculates fitness
			rockets[i].calcFitness();
			// If current fitness is greater than max, then make max eqaul to current
			if (rockets[i].getFitness() > maxfit) {
				maxfit = rockets[i].getFitness();
			}
		}
		
		ArrayList<Rocket> matingPollCandidates = new ArrayList<Rocket>();
				
		// Normalises fitnesses
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
			System.out.println(i + ". " + " - " + r.getFitness() + " :: " + r.getFinishTime());
			threshhold += matingPollCandidates.get(i).getFitness();
		}
		threshhold /= sz;
		
		System.out.println(fill.toString()
				+ "\n\nThresh:" + threshhold);
		for (Rocket r : matingPollCandidates) {
			double fitness = r.getFitness();
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
	}
	
	// Selects appropriate genes for child
	public void selection() {
		Rocket[] newRockets = new Rocket[rockets.length];
		if (matingpool.size() == 0 )
			System.out.println("chuj");
		int mSz = matingpool.size();

		int childrenPopulationSz = (int) (rockets.length * 1.0f), i = 0;


		for (; i < rockets.length; i++) {
			// Picks random DNA
			Rocket parentA = matingpool.get((int) parent.random(mSz));
			Rocket parentB = matingpool.get((int) parent.random(mSz));

			// Creates child by using crossover function
			DNA child = parentA.getDna().crossover(parentB.getDna(), parentA.getFitness() > parentB.getFitness());
			child.mutation();
			// Creates new rocket with child's DNA
			newRockets[i] = new Rocket(this, child);
		}
//		//i--;
//		for (; i < rockets.length; i++) {
//			newRockets[i] = new Rocket(this);
//		}

		for (Rocket r : newRockets) {
			if (r == null) {
				throw new RuntimeException("null rocket !!!");
			}
		}

		// Assign new rockets array to the old array
		this.rockets = newRockets;
		success = false;
		allDone = false;
	}

	public void doEvaluation() {
		abstract class Worker extends Thread {
			Rocket[] toProcess;
			
			public Worker(Rocket... r) {
				toProcess = r;
			}
			
			@Override
			abstract public void run();
		}
		
		int threadCount = Runtime.getRuntime().availableProcessors();
		Thread[] workers = new Thread[threadCount];
		
		int chunkSize = rockets.length / threadCount, counter = 0;
		ArrayList<Rocket> buffer = new ArrayList<Rocket>(chunkSize);
		int sz = 0;
		
		for (int a = 0; a < threadCount; a++) {
			buffer.clear();
			counter++;
			if (a != threadCount - 1) 
				for (; counter % (chunkSize + 1) != 0; counter++) 
					buffer.add(rockets[counter - 1]);

			else 
				for (; counter <= rockets.length; counter++) 
					buffer.add(rockets[counter - 1]);	
			
			Rocket[] res = new Rocket[buffer.size()];
			
			for (int index = 0; index < buffer.size(); index++)
				res[index] = buffer.get(index);
			
			sz += res.length;
		}
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
//		parent.pushStyle();
//		parent.strokeWeight(10);
//		parent.stroke(getFill().getRGB(), getFill().getAlpha());
		for (int i = 0; i < rockets.length; i++) {
			rockets[i].show();
		}
		//parent.popStyle();
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
