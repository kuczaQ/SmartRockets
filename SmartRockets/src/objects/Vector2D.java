/**
 * This class is a shameless rip-off of the PVector, only without the z variable for better 2D performance.
 * Additionally, a lot of functionality is missing.
 * 
 * @see https://github.com/processing/processing/blob/master/core/src/processing/core/PVector.java
 */

package objects;

public class Vector2D {
	public float x, y;

	public Vector2D() {
		super();
		this.x = 0;
		this.y = 0;
	}

	public Vector2D(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Vector2D copy() {
		return new Vector2D(x, y);
	}

	public float mag() {
		return (float) Math.sqrt(x*x + y*y);
	}

	public float magSq() {
		return (x*x + y*y);
	}

	public Vector2D setMag(float len) {
		normalize();
		mult(len);
		return this;
	}

	public Vector2D limit(float max) {
		if (magSq() > max*max) {
			normalize();
			mult(max);
		}
		return this;
	} 

	public Vector2D normalize() {
		float m = mag();
		if (m != 0 && m != 1) {
			div(m);
		}
		return this;
	}

	public Vector2D add(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2D mult(float n) {
		x *= n;
		y *= n;
		return this;
	}

	public Vector2D div(float n) {
		x /= n;
		y /= n;
		return this;
	}

	static public Vector2D random2D() {
		return fromAngle((float) (Math.random() * Math.PI*2));
	}

	static public Vector2D fromAngle(float angle) {
		return new Vector2D((float)Math.cos(angle),(float)Math.sin(angle));
	}

	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Vector2D other = (Vector2D) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}

























