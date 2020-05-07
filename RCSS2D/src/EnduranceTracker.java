
public class EnduranceTracker implements Runnable{
	
	private double stam;
	private double effort;
	private double recover;
	
	public EnduranceTracker() {
	}
	
	private int calcStam() {
		return 0;
	}
	
	private double calcKickDist() {
		return 0;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
 }
