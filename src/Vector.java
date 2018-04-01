
public class Vector
{
	public double x, y;
	
	public Vector()
	{
		x = y = 0;
	}
	
	public Vector(double x, double y)
	{
		this.x = x; this.y = y;
	}
	
	public Vector(Vector v)
	{
		x = v.x; y = v.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}
