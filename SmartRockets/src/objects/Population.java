package objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import exec.SmartRockets;
import exec.ThreadManager;
import processing.core.PApplet;
import processing.core.PVector;

public class Population extends Thread {
	static PApplet parent;
	
	public Rocket[] rockets;
	ArrayList<Rocket> matingpool = new ArrayList<Rocket>();
	boolean success = false, allDone = false;
	public boolean passedTheObstacle = false;
	public PVector targetPos = null;
	CalculateFitness populationFitnessFunction;
	Color fill = new Color(0, 0, 0);
	
	private ThreadManager threadManager;
	private volatile boolean toStop = false, toEvaluate = false, toSelect = false;
	

	public Population(int popsize, CalculateFitness calc, int threadCount) {
		populationFitnessFunction = calc;
		rockets = new Rocket[popsize];
		for (int i = 0; i < popsize; i++) {
			rockets[i] = new Rocket(new Color(0, 0, 0), populationFitnessFunction);
		}
		
		threadManager = new ThreadManager(this, threadCount, rockets);
	}

	@Override
	public void run() {
		threadManager.start();
		
//		while (!toStop) {
//			if (toEvaluate) {
//				evaluate();
//				toEvaluate = false;
//			}
//			
//			if (toSelect) {
//				selection();
//				toSelect = false;
//			}
//		}
//		
	}
	
	public void setToStop() {
		toStop = true;

		threadManager.stop();
		try {
			threadManager.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setToUpdate() {
		threadManager.setDoneFalse();
	}
	
	public boolean allThreadsDone() {
		return threadManager.allDone();
	}
	
	public void evaluate() {
		System.out.println("Evaluate called");
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.matingpool.clear();
		
		Double maxfit = 0d;
		// Iterate through all rockets and calcultes their fitness
			for (int i = 0; i < rockets.length; i++) {
				// Calculates fitness
				rockets[i].calcFitness(success, this);
				// If current fitness is greater than max, then make max eqaul to current
				if (rockets[i].fitness > maxfit) {
					maxfit = rockets[i].fitness;
				}
			}
		

		ArrayList<Rocket> debugR = new ArrayList<Rocket>();
		ArrayList<Rocket> matingPollCandidates = new ArrayList<Rocket>();

		for (int i = 0; i < rockets.length; i++) {
			System.out.println("Fitness: " + rockets[i].fitness + " :: " + rockets[i].finishTime);
			if (rockets[i].fitness != 0) {
				debugR.add(rockets[i]);
				
//				rockets[i].fitness /= maxfit;
//				rockets[i].fitness *= rockets[i].fitness;
			}
		}
		
		if (debugR.size() == 0)
			System.out.println();
		
		// Normalises fitnesses

		for (int i = 0; i < rockets.length; i++) {
			if (rockets[i].fitness != 0) {
				debugR.add(rockets[i]);
				matingPollCandidates.add(rockets[i]);
				System.out.println("Fitness: " + rockets[i].fitness + " :: " + rockets[i].finishTime);
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
		
		System.out.println("Thresh:" + threshhold);
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

				System.out.println(d + ". " + r.pos + " - " + r.fitness);
			}
		}
		System.out.println("done");
	}
	
	// Selects appropriate genes for child
	public void selection() {
		System.out.println("Selection called");
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Rocket[] newRockets = new Rocket[rockets.length];
		if (matingpool.size() == 0 )
			System.out.println("chuj");
		int mSz = matingpool.size();

		int childrenPopulationSz = (int) (rockets.length * .9f),
				//newPopulationSz = rockets.length - childrenPopulationSz,
				i = 0;


		for (; i < childrenPopulationSz; i++) {
			// Picks random DNA
			Rocket parentA = matingpool.get((int) parent.random(mSz));
			Rocket parentB = matingpool.get((int) parent.random(mSz));

			// Creates child by using crossover function
			DNA child = parentA.dna.crossover(parentB.dna, parentA.fitness > parentB.fitness);
			child.mutation();
			// Creates new rocket with child's DNA
			newRockets[i] = new Rocket(child, populationFitnessFunction);
		}
		//i--;
		for (; i < rockets.length; i++) {
			newRockets[i] = new Rocket(new Color(255, 0, 0), populationFitnessFunction);
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
			r.update(success, this, targetPos );
			
			if (!r.crashed && !r.finished) { // if not crashed and not finished
				if (allDone)
					allDone = false;
			}
		}
		
		if (allDone)
			this.allDone = true;
		
	}

	public void draw() {
		parent.pushStyle();parent.noStroke();
		parent.fill(fill.getRed(), fill.getGreen(), fill.getGreen(), 150);
		parent.rectMode(PApplet.CENTER);
		
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

	public synchronized void setToEvaluate() {
		this.toEvaluate = true;
	}

	public synchronized void setToSelect() {
		this.toSelect = true;
	}
}
