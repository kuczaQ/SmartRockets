package objects;

import java.awt.Color;
import java.util.ArrayList;

import exec.SmartRockets;
import processing.core.PApplet;
import processing.core.PVector;

public class Rocket {
	static PVector target;
	public static PApplet parent;
	//static volatile ArrayList<Boolean> success = new ArrayList<Boolean>();
	
	CalculateFitness calcFitness;
			
	public PVector pos = new PVector(parent.width/2, parent.height - 20),
			acc = new PVector(),
			vel = new PVector();
	
	public boolean finished = false;
	boolean crashed = false;
	boolean crashedOnObstacle = false;
	public Double fitness = 1d;
	public Integer finishTime, passedObstacleTime;
	DNA dna;

	
	public Rocket(Color color, CalculateFitness calc) {
		calcFitness = calc;
		dna = new DNA(color);
	}
	
	public Rocket(Rocket r, Color c) {
		this.dna = r.dna.copy();
		dna.fill = c;
	}
	
	public Rocket(DNA dna, CalculateFitness calc) {
		calcFitness = calc;
		this.dna = dna;
	}

	public void applyForce(PVector force) {
	    this.acc.add(force);
	}
	
	public void calcFitness(boolean success, Population pop) {
		if (calcFitness == null)
			throw new NullPointerException("calcFitness == null");
		
		fitness = calcFitness.calc(this, success, pop);
	}
	
	// Updates state of rocket
	public boolean update(Boolean finished, Population pop, PVector targetPos) { //TODO return values
		// Checks distance from rocket to target
		if (!this.finished && !crashed) {
			if (pop.targetPos != null) {
				PVector dir = PVector.sub(pos, targetPos);
				float d = PApplet.dist(this.pos.x, this.pos.y, target.x, target.y);
				
				if (pos.y >= SmartRockets.ry) {
					if (passedObstacleTime != null) 
						passedObstacleTime = SmartRockets.getCounter();
					if (!pop.passedTheObstacle)
						pop.passedTheObstacle = true;
				}
				
				// If distance less than 10 pixels, then it has reached target
				if (d < SmartRockets.TARGET_R) {
					this.finished = true;
					this.pos = target.copy();
					finishTime = SmartRockets.getCounter();
					finished = true;
				}
				// Rocket hit the barrier
				if (this.pos.x > SmartRockets.rx && this.pos.x < SmartRockets.rx + SmartRockets.rw 
						&& this.pos.y > SmartRockets.ry && this.pos.y < SmartRockets.ry + SmartRockets.rh) {
	
					this.crashed = true;
					crashedOnObstacle = true;
				}
	
				// Rocket has hit left or right of window
				if (this.pos.x > parent.width || this.pos.x < 0) {
					this.crashed = true;
				}
				// Rocket has hit top or bottom of window
				if (this.pos.y > parent.height || this.pos.y < 0) {
					this.crashed = true;
				}
	
	
				//applies the random vectors defined in dna to consecutive frames of rocket
				this.applyForce(dir);
	
				// if rocket has not got to goal and not crashed then update physics engine
				if (!this.finished && !this.crashed) {
					this.vel.add(this.acc);
					this.pos.add(this.vel);
					this.acc.mult(0);
					this.vel.limit(4);
				}
				
				return false;
			} else {
float d = PApplet.dist(this.pos.x, this.pos.y, target.x, target.y);
				
				if (pos.y >= SmartRockets.ry) {
					if (passedObstacleTime != null) 
						passedObstacleTime = SmartRockets.getCounter();
					if (!pop.passedTheObstacle)
						pop.passedTheObstacle = true;
				}
				
				// If distance less than 10 pixels, then it has reached target
				if (d < SmartRockets.TARGET_R) {
					this.finished = true;
					this.pos = target.copy();
					finishTime = SmartRockets.getCounter();
					finished = true;
					pop.targetPos = this.pos.copy();
				}
				// Rocket hit the barrier
				if (this.pos.x > SmartRockets.rx && this.pos.x < SmartRockets.rx + SmartRockets.rw 
						&& this.pos.y > SmartRockets.ry && this.pos.y < SmartRockets.ry + SmartRockets.rh) {
	
					this.crashed = true;
					crashedOnObstacle = true;
				}
	
				// Rocket has hit left or right of window
				if (this.pos.x > parent.width || this.pos.x < 0) {
					this.crashed = true;
				}
				// Rocket has hit top or bottom of window
				if (this.pos.y > parent.height || this.pos.y < 0) {
					this.crashed = true;
				}
	
	
				//applies the random vectors defined in dna to consecutive frames of rocket
				this.applyForce(this.dna.genes.get(SmartRockets.getCounter() >= dna.genes.size() ? dna.genes.size() - 1 : SmartRockets.getCounter()));
	
				// if rocket has not got to goal and not crashed then update physics engine
				if (!this.finished && !this.crashed) {
					this.vel.add(this.acc);
					this.pos.add(this.vel);
					this.acc.mult(0);
					this.vel.limit(4);
				}
				
				return false;
			}
		}
		
		return false;
	}
	
	public void show() {
		// Push and pop allow's rotating and translation not to affect other objects
		parent.pushMatrix();	
		parent.translate(this.pos.x, this.pos.y);
		parent.rotate(this.vel.heading());
		parent.rect(0, 0, 25, 5);	
		parent.popMatrix();
		
	}

	public static void setParent(PApplet p) {
		parent = p;
	}	
	
	public static synchronized void setTarget(PVector target) {
		Rocket.target = target;
	}
}








