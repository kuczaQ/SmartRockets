package objects;

import java.awt.Color;
import java.util.ArrayList;

import exec.Population;
import exec.SmartRockets;
import processing.core.PApplet;
import processing.core.PVector;

public class Rocket {
	private static final int SPEED_LIMIT = 3;
	static PVector target;
	public static PApplet parent;

	private DNA dna;
	private CalculateFitness calcFitness;
	private Population population; // population to which the rocket belongs to

	public PVector pos = new PVector(parent.width/2, parent.height - 20),
			acc = new PVector(),
			vel = new PVector();

	public boolean finished = false;
	private boolean crashed = false;
	boolean crashedOnObstacle = false;
	public Double fitness = 1d;
	public Integer finishTime, passedObstacleTime;



	public Rocket(Population p) {
		this.population = p;
		calcFitness = population.getPopulationFitnessFunction();
		setDna(new DNA(population.getFill()));
	}

	public Rocket(Rocket r) {
		this.setDna(r.getDna().copy());
		//dna.fill = c;
	}

	public Rocket(Population p, DNA dna) {
		this.population = p;
		calcFitness = population.getPopulationFitnessFunction();
		this.setDna(dna);
	}

	public void applyForce(PVector force) {
		this.acc.add(force);
	}

	public void calcFitness() {
		if (calcFitness == null)
			throw new NullPointerException("calcFitness == null");

		fitness = calcFitness.calc(this);
	}

	// Updates state of rocket
	public void update() {

		if (!this.finished && !hasCrashed()) {
			float d = PApplet.dist(this.pos.x, this.pos.y, target.x, target.y);

			if (pos.y <= SmartRockets.ry) {
				if (passedObstacleTime == null) 
					passedObstacleTime = SmartRockets.getCounter();
				if (!population.passedTheObstacle)
					population.passedTheObstacle = true;
			}

			if (d < SmartRockets.TARGET_R) {
				this.finished = true;
				this.pos = target.copy();
				finishTime = SmartRockets.getCounter();
				finished = true;
				population.targetPos = this.pos.copy();
				population.setSuccess();
			}
			
			//TODO generic object array
			
			// Rocket hit the barrier
			if (this.pos.x > SmartRockets.rx && this.pos.x < SmartRockets.rx + SmartRockets.rw 
					&& this.pos.y > SmartRockets.ry && this.pos.y < SmartRockets.ry + SmartRockets.rh) {

				this.setCrashed(true);
				crashedOnObstacle = true;
			}

			// Rocket has hit left or right of window
			if (this.pos.x > parent.width || this.pos.x < 0) {
				this.setCrashed(true);
			}
			// Rocket has hit top or bottom of window
			if (this.pos.y > parent.height || this.pos.y < 0) {
				this.setCrashed(true);
			}


			//applies the random vectors defined in dna to consecutive frames of rocket
			this.applyForce(this.getDna().genes.get(SmartRockets.getCounter() >= getDna().genes.size() ? getDna().genes.size() - 1 : SmartRockets.getCounter()));

			// if rocket has not got to goal and not crashed then update physics engine
			if (!this.finished && !this.hasCrashed()) {
				this.vel.add(this.acc);
				this.pos.add(this.vel);
				this.acc.mult(0);
				this.vel.limit(SPEED_LIMIT);
			}


		}
	}

	public void show() {
		// Push and pop allow's rotating and translation not to affect other objects
//		parent.pushMatrix();	
//		parent.translate(this.pos.x, this.pos.y);
//		parent.rotate(this.vel.heading());
//		parent.rect(0, 0, 25, 5);	
//		parent.popMatrix();
		
//		PVector point = vel.copy();
//		
//		point = point.mult(5);
//		point = pos.sub(point);
		
		
		parent.point(pos.x, pos.y);

	}

	public static void setParent(PApplet p) {
		parent = p;
	}	

	public static synchronized void setTarget(PVector target) {
		Rocket.target = target;
	}

	public Population getPopulation() {
		return population;
	}

	private void setCrashed(boolean crashed) {
		this.crashed = crashed;
	}

	public boolean hasCrashed() {
		return crashed;
	}

	public DNA getDna() {
		return dna;
	}

	public void setDna(DNA dna) {
		this.dna = dna;
	}
}








