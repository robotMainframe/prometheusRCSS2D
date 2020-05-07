import java.util.ArrayList;

public class LocalView {
	
	private ArrayList<Tuple> itemsInView;
	private int time; 
	
	public LocalView() {
		itemsInView = new ArrayList<Tuple>();
	}
	
	//(see 0 ((f r t) 54.6 -38) ((f r b) 54.6 38) ((f g r b) 43.8 9) ((g r) 42.9 0) 
	//	((f g r t) 43.8 -9) ((f p r b) 33.4 37) ((f p r c) 26.6 0 0 0) ((f p r t) 33.4 -37) 
	//	((f t r 50) 56.3 -44) ((f b r 50) 56.3 44) ((f r 0) 47.9 0) ((f r t 10) 48.9 -12) 
	//	((f r t 20) 51.9 -23) ((f r t 30) 56.8 -32) ((f r b 10) 48.9 12) ((f r b 20) 51.9 23) 
	//	((f r b 30) 56.8 32) ((l r) 42.9 90))
//	(see 0 ((F) 0.4 47) ((f c t) 34.5 8) ((f t 0) 39.3 8) ((f t r 10) 40.4 22) 
//			((f t r 20) 44.3 35) ((f t r 30) 49.4 45) ((f t l 10) 40.4 -7) 
//			((f t l 20) 43.8 -19) ((f t l 30) 49.4 -30) ((f t l 40) 55.7 -38) 
//			((f t l 50) 63.4 -44) ((B) 0.4 47) ((l t) 34.5 -83))
//	(see 0 ((f c) 0.4 -15 0 0) ((f c b) 34.5 -9 0 0) ((f b 0) 39.3 -9) 
//			((f b r 10) 40.4 -23) ((f b r 20) 44.3 -36) ((f b l 10) 40.4 5) 
//			((f b l 20) 44.3 18) ((f b l 30) 49.4 29) ((f b l 40) 56.3 37) 
//			((f b l 50) 63.4 43) ((b) 0.4 -15 0 0) ((l b) 34.8 81))
//	(see 0 ((f c b) 4 -9 0 0) ((f b 0) 9 -9 0 0) ((f b l 10) 13.5 39 0 0) ((l b) 4.1 81))

	public LocalView(String message) {
		itemsInView = new ArrayList<Tuple>();
		String[] splitS = message.split(" ");
		time = Integer.parseInt(splitS[1]);
		
		for (int i = 2; i < splitS.length; i++) {
			if (splitS[i].length() > 2){
				if (splitS[i].substring(0, 2).equals("((")) {
					String loc = "";
					if (splitS[i].endsWith(")")) {
						loc += splitS[i].substring(2, splitS[i].length() - 1);
					} else {
						loc += splitS[i].substring(2);
						i++;
						while (splitS[i].charAt(splitS[i].length() - 1) != ')') {
							loc += splitS[i];
							i++;
						}
						loc += splitS[i].substring(0, splitS[i].length() - 1);
					}
					i++;
					String[] sParams = {"time", "dist", "dir", "distChange", "dirChange", "bodyDir", "headDir"};
					double[] iParams = new double[7]; 
					iParams[0] = time;
					int j = 1;
					while(!splitS[i].endsWith(")") && !splitS[i].contains("))")) {
						try {
						double param = Double.parseDouble(splitS[i]);
						iParams[j] = param;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						i++;
						j++;
					}
					
					if (splitS[i].contains("))")) {
						iParams[j] = Double.parseDouble(splitS[i].substring(0, splitS[i].indexOf(')')));
						break;
					} else {
						try {
							iParams[j] = Double.parseDouble(splitS[i].substring(0, splitS[i].length() - 1));
					
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					addWaypoint(new Tuple(loc, sParams, iParams));
				}
			}		
		}
	}
	
	public void addWaypoint(Tuple tuple) {
		itemsInView.add(tuple);
	}
	
	public ArrayList<Tuple> getItemsInView() {
		return itemsInView;
	}
	
	public int getTime() {
		return time;
	}
}
