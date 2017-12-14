package exec;

public class VolatileIntWrap {
	public volatile int val;
	
	public VolatileIntWrap() {
		super();
	}
	
	public VolatileIntWrap(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return val + "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + val;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VolatileIntWrap other = (VolatileIntWrap) obj;
		if (val != other.val)
			return false;
		return true;
	}
	
}
