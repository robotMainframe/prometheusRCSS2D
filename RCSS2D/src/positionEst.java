import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

public class positionEst {
	
	ArrayList<Point2D.Double> calcedTimes;
	private static double decay = .4; //rough decay. different per each player
	
	
	public positionEst() {
		calcedTimes = new ArrayList<Point2D.Double>();
	}
	
	public static Point2D.Double calcPosToTime(int t, double DistChange, double Dir) {
		double x = 0;
		double y = 0;
		DistChange = Math.abs(DistChange);
		double dx = (3*DistChange + 4) * Math.cos(Math.toRadians(Dir));
		double dy = (3*DistChange + 4) * Math.sin(Math.toRadians(Dir));
		for (int i = 0; i < t; i++) {
			x += dx;
			y += dy;
			dx *= decay;
			dy *= decay;
		}
		return new Point2D.Double(x, y);
	}
	
	public static double distance(int t, double Dist, double dir, double x, double y) {
		Point2D.Double est = calcPosToTime(t, Dist, dir);
		double distance = Math.sqrt(Math.pow(est.x, 2)+Math.pow(est.y, 2));
				//y - est.y, 2) + Math.pow(x - est.x, 2));
		return distance;
		
	}
	
	public double[] calcStep(double[] preStep) {
		
		return null;
	}

	public double[] calc() {
		return null;
	}
}
