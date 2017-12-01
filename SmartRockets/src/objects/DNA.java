package objects;

import java.awt.Color;
import java.util.ArrayList;

import exec.SmartRockets;
import processing.core.PApplet;
import processing.core.PVector;

public class DNA {
	public static double mutationChance = 0.01d;
	static PApplet parent;
	static final float MAX_FORCE = 0.2f;
	
	Color fill;
	ArrayList<PVector> genes;
	
	
	public DNA(Color color) {
		this.fill = color;
		this.genes = new ArrayList<PVector>();
		
	    for (int i = 0; i < SmartRockets.LIFESPAN; i++) {
	      // Gives random vectors and sets maximum force of vector to be applied to a rocket
	      genes.add(PVector.random2D().setMag(MAX_FORCE));
	    }
	}
	
	public DNA(ArrayList<PVector> p) {
		this.genes = p;
	}
	
	public DNA(Color color, ArrayList<PVector> genes) {
		this.fill = color;
		this.genes = genes;
	}
	
	public DNA copy() {
		ArrayList<PVector> genesNew = new ArrayList<PVector>();
		
		for (PVector p : genes)
			genesNew.add(p.copy());
		
		return new DNA(new Color(fill.getRGB()), genesNew);
	}

	public static DNA getRandomDNA() {
		ArrayList<PVector> genes = new ArrayList<PVector>();
		
	    for (int i = 0; i < SmartRockets.LIFESPAN; i++) {
	      // Gives random vectors and sets maximum force of vector to be applied to a rocket
	      genes.add(PVector.random2D().setMag(MAX_FORCE));
	    }
	    
	    return new DNA(genes);
	}
	
	// Performs a crossover with another member of the species
	public DNA crossover(DNA partner, boolean leftHasBiggerFitness) {
		ArrayList<PVector> newGenes = new ArrayList<PVector>();
		// Picks random midpoint
		float mid = PApplet.floor(parent.random(this.genes.size()));
		
		DNA biggerFitness, smallerFitness;
		
		if (leftHasBiggerFitness) {
			biggerFitness = this;
			smallerFitness = partner;
		} else {
			biggerFitness = partner;
			smallerFitness = this;
		}
		
		for (int i = 0; i < this.genes.size(); i++) {
			// If i > mid the new gene should come from this partner
//			if (i > mid) {
			if (parent.random(1) < .9) {
				newGenes.add(biggerFitness.genes.get(i).copy());
			}
			// If i < mid new gene should come from other partners gene's
			else {
				newGenes.add(smallerFitness.genes.get(i).copy());
			}
		}

		return new DNA(this.fill, newGenes);
	}

	// Adds random mutation to the genes to add variance.
	public void mutation() {
		for (int i = 0; i < this.genes.size(); i++) {
			// if random number less than 0.01, new gene is then random vector
			if (parent.random(1) < mutationChance) {
				/*PVector og = genes.get(i);
				og.x = og.x + parent.random(-15, 15);
				og.y = og.y + parent.random(-15, 15);
				*/
				this.genes.set(i, PVector.random2D().setMag(MAX_FORCE));
			}
		}
	}

	public static void setParent(PApplet p) {
		parent = p;
	}

	public static double getMutationChance() {
		return mutationChance;
	}
	
	public static void changeMutationChance(double val) {
		DNA.mutationChance += val;
	}
}





