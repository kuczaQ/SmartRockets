package exec;

import java.util.ArrayList;

import objects.Rocket;

class ProcessArray extends Thread {
	ArrayList<Rocket> arr;
	ProcessArrayListFunction func;

	public ProcessArray(ProcessArrayListFunction func, ArrayList<Rocket> arr) {
		this.arr = arr;
		this.func = func;
	}

	@Override
	public void run() {
		func.process(arr);
	}
	
	public int getProgress() {
		return func.progress;
	}
}
