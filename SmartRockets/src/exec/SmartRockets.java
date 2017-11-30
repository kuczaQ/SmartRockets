package exec;

import java.awt.Color;

import objects.CalculateFitness;
import objects.DNA;
import objects.Rocket;
import processing.core.PApplet;
import processing.core.PVector;

public class SmartRockets extends PApplet {
	public static final int LIFESPAN = 3600;
	public static final int TARGET_R = 10;
	public static final int POPULATION_SIZE = 1000;
	public static final int ROCKET_ALPHA = 200;
	static final float FPS = 60;
	
	public static volatile int counter = 0;
	
	PopulationManager populationManager;
	PVector target;
	private boolean run = true;
	public static boolean[] toStop = {false, false};

	
	public static int rx = -1000;
	public static int ry = 150;
	public static int rw = 200;
	public static int rh = 25;

	private volatile boolean auto = true;
	
	private CalculateFitness fitnessPop1 = new CalculateFitness() {
		@Override
		public double calc(Rocket r) {
			double fitness = 1;

			if (target == null)
				throw new RuntimeException("Target was not set! Use setTarget().");

			if ((!r.finished || r.hasCrashed()) && r.getPopulation().hasSuccess()) {
				return 0;
			}
			
			// if at least one rocket has passed the obstacle
			if (r.getPopulation().passedTheObstacle && r.passedObstacleTime == null)
				return 0;
			
			//		else if (!success && crashedOnObstacle) {
			//			fitness = 0;
			//			return;
			//		}

			// Takes distance to target
			float d = PApplet.dist(r.pos.x, r.pos.y, target.x, target.y);

			final float MAX_DISTANCE = (float) Math.sqrt(Math.pow(Rocket.parent.width, 2) + Math.pow(Rocket.parent.height, 2)); // Pythagoras for max distance
			// Maps range of fitness
			fitness = (double) PApplet.map(d, 0, MAX_DISTANCE, MAX_DISTANCE, 1);

			float timeBonus;
			if (r.finished)
				timeBonus = PApplet.map(r.finishTime, 0, SmartRockets.LIFESPAN, 100000, 1);
			else
				timeBonus = 1;

			fitness *= timeBonus;

			return fitness;
		}
	}, fitnessPop2 = new CalculateFitness() {
		@Override
		public double calc(Rocket r) {
			double fitness = fitnessPop1.calc(r);	
			float timeBonus;
			
			if (r.passedObstacleTime != null)
				timeBonus = PApplet.map(r.passedObstacleTime, 0, SmartRockets.LIFESPAN, 100000, 1);
			else
				return fitness;

			return fitness * timeBonus;
		}
	};
	
	public static void main(String[] args) {		
		PApplet.main("exec.SmartRockets");
	}
	
	public void settings() {
		size(600, 600);
		fullScreen();
		
	}

	public void setup() {
		frameRate(FPS);
		
		rw = (int) ((width) * .8);
		rx = (width/2) - rw/2;
		rw -= 200;
		rx += 200;
		ry = height/2;
		
		setParents(this);
		background(220);
		noStroke();
		textSize(20);
		
		target = new PVector(width/2, 50);
		Rocket.setTarget(target);
		
		populationManager = new PopulationManager();
		populationManager.addPopulation( new Population(POPULATION_SIZE, fitnessPop1, new Color(0, 0, 0, ROCKET_ALPHA)));
		
		for (int a = 0; a < 5; a ++)
		populationManager.addPopulation( new Population(POPULATION_SIZE, fitnessPop2, 
				new Color((137 * (a + 1)) % 250, (89 * (a + 1)) % 250, (354 * (a + 1)) % 250, ROCKET_ALPHA)));
		populationManager.start();
		textSize(20);
	}

	public void draw() {
		//while (!population.allThreadsDone());
		

		
		//text(fitnessDebug, 15, 70);
		if (run) {
			background(220);
			fill(130);
			rect(rx, ry, rw, rh);

			// Renders target
			fill(218, 64, 12);
			stroke(1);
			line(rx, ry, rx + rw, ry);
			noStroke();
			ellipse(target.x, target.y, TARGET_R, TARGET_R);
			
			
			//population.update();
			populationManager.draw();
			populationManager.continueWork();
			
			//counter++;
			if (counter++ == LIFESPAN || populationManager.allDone()) {
				populationManager.setWait(true);
				if (!auto) {
					run = false;
					populationManager.evaluate();
				} else {
					populationManager.evaluate();
					populationManager.selection();
					counter = 0;
				}

			}

			text(counter, 15, 30);
			
			text("Auto = " + auto, 15, 50);
		} else {
			//population.draw();
		}

	}
	
	public void mousePressed() {
//		for (Rocket r : population.rockets) {
//			float d = PVector.dist(new PVector(mouseX, mouseY), new PVector(r.pos.x, r.pos.y));
//			if (d < 10) {
//				if (r.fitness == Float.NaN) {
//					System.out.println("xD");
//				}
//				System.out.println(r.fitness);
//				fitnessDebug =  Double.toString(r.fitness);
//			}
//		}
		
//		target.x = mouseX;
//		target.y = mouseY;
	}
	
	public void keyPressed() {
		if (key == ' ') {
			populationManager.selection();
			counter = 0;
			run = true;
		}
		
		if (key == 'a') {
			auto = !auto;
		}
		
	}
	
	public void setParents(PApplet p) {
		DNA.setParent(p);
		Rocket.setParent(p);
		Population.setParent(p);
	}
		
	public static int getCounter() {
		return counter;
	}
}
