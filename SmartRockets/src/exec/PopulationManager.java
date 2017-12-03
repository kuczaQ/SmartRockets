package exec;

import java.util.ArrayList;

public class PopulationManager {
	ArrayList<Population> populations = new ArrayList<Population>();
	
	public PopulationManager() {
		super();
	}
	
	public boolean addPopulation(Population p) {
		for (Population pThis : populations)
			if (pThis == p)
				return false;
		
		return populations.add(p);
	}
	
	public void draw() {
		for (Population p : populations)
			p.draw();
	}
	
	public void start() {
		for (Population p : populations)
			p.start();
	}
	
	public void continueWork() {
		//setWait(false);
		for (Population p : populations)
			if (!p.allDone())
				p.continueWork();
	}
	
	public void setToStop() {
		for (Population p : populations)
			p.setToStop();
	}
	
	public void evaluate() {
		for (Population p : populations)
			p.evaluate();
	}
	
	public void selection() {
		for (Population p : populations)
			p.selection();
	}
	
	public boolean allDone() {
		for (Population p : populations)
			if (!p.allDone())
				return false;
		return true;
	}
	
	public void setWait(boolean wait) {
		for (Population p : populations)
			p.setWait(wait);
	}
}












