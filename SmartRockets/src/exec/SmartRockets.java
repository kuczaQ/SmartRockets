package exec;

import objects.CalculateFitness;
import objects.DNA;
import objects.Population;
import objects.Rocket;
import processing.core.PApplet;
import processing.core.PVector;

public class SmartRockets extends PApplet {
	public static final int LIFESPAN = 1500;
	public static final int TARGET_R = 25;
	public static final int POPULATION_SIZE = 1000;
	static final float FPS = 120;
	
	public static volatile int counter = 0;
	
	Population population;
	PVector target;
	private boolean run = true;
	public static boolean[] toStop = {false, false};

	
	public static int rx = -1000;
	public static int ry = 150;
	public static int rw = 200;
	public static int rh = 25;

	String fitnessDebug = "";	private volatile boolean auto = true;
	
	private CalculateFitness fitnessPop1 = new CalculateFitness() {
		@Override
		public double calc(Rocket r, Boolean success, Population pop) {
			double fitness = 1;

			if (target == null)
				throw new RuntimeException("Target was not set! Use setTarget().");

			if (!r.finished && success) {
				return 0;
			}
			//		else if (!success && crashedOnObstacle) {
			//			fitness = 0;
			//			return;
			//		}

			// Takes distance to target
			float d = PApplet.dist(r.pos.x, r.pos.y, target.x, target.y);

			final float MAX_DISTANCE = (float) Math.sqrt(Math.pow(Rocket.parent.width, 2) + Math.pow(Rocket.parent.height, 2)); // Pythagoras for max distance
			// Maps range of fitness
			fitness = (double) PApplet.map(d, 0, MAX_DISTANCE, MAX_DISTANCE, 0);

			float timeBonus;
			if (r.finished)
				timeBonus = PApplet.map(r.finishTime, 0, SmartRockets.LIFESPAN, 100000, 1);
			else
				timeBonus = 1;

			fitness *= timeBonus;

			// passed the obstacle
			if (pop.passedTheObstacle && r.pos.y >= SmartRockets.ry)
				fitness *= 0;

			return fitness;
		}
	};
	
	
	public static void main(String[] args) {
		
		PApplet.main("exec.SmartRockets");
	}
	
	public void settings() {
		size(600, 600);
		fullScreen(2);
		
	}

	public void setup() {
		frameRate(FPS);
		
		rw = (int) ((width) * .8);
		rx = (width/2) - rw/2;
		ry = height/2;
		
		setParents(this);
		background(220);
		noStroke();
		textSize(20);
		
		target = new PVector(width/2, 25);
		Rocket.setTarget(target);
		int sz = Runtime.getRuntime().availableProcessors();
		population = new Population(POPULATION_SIZE, fitnessPop1, sz);
		population.start();
	}

	public void draw() {
		//while (!population.allThreadsDone());
		
		textSize(20);
		
		text(fitnessDebug, 15, 70);

		
		
		if (run) {
			background(220);
			fill(130);
			rect(rx, ry, rw, rh);

			// Renders target
			fill(218, 64, 12);
			ellipse(target.x, target.y, TARGET_R, TARGET_R);
			
			
			//population.update();
			population.draw();
			//population.setToUpdate();
			
			//counter++;
			if (counter++ == LIFESPAN || population.allDone()) {
				if (!auto) {
					run = false;
					toStop[0] = false;
					toStop[1] = false;

					population.evaluate();


				} else {

					population.evaluate();
					population.selection();

					counter = 0;
					toStop[0] = false;
					toStop[1] = false;
				}
//				population.evaluate();
//				toStop = false;
			}
			// Renders barrier for rockets
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
			population.selection();
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
	
//	public static synchronized void setCounter(int counter) {
//		SmartRockets.counter = counter;
//	}
	
	public static int getCounter() {
		return counter;
	}
}
