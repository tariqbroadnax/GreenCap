import java.awt.Component;
import java.awt.geom.AffineTransform;

public class Camera
{
	private Vector focus, zoom;
	
	public Camera()
	{
		focus = new Vector();
		
		zoom = new Vector(2, 2);
	}
	
	public void setFocus(Vector focus) {
		this.focus = focus;
	}
	
	public void setFocus(double x, double y) {
		focus = new Vector(x, y);
	}
	
	public void setZoom(Vector zoom) {
		this.zoom = zoom;
	}
	
	public void setZoom(double zx, double zy) {
		zoom = new Vector(zx, zy);
	}
	
	public AffineTransform createTransform(Component screen)
	{
		double tx = (-zoom.x * focus.x + screen.getWidth() / 2.0),
			   ty = (-zoom.y * focus.y + screen.getHeight() / 2.0);
				
		AffineTransform transform = new AffineTransform();

		transform.setToTranslation(tx, ty);
		transform.scale(zoom.x, zoom.y);
		
		return transform;
	}
	
	public Rectangle2 getFrame(Component screen)
	{
		double w = screen.getWidth() * zoom.x,
			   h = screen.getHeight() * zoom.y,
			   x = focus.x - w / 2,
			   y = focus.y - h / 2;
		
		return new Rectangle2(x, y, w, h);
	}
}
