import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.JPanel;

import jdk.nashorn.api.tree.ForInLoopTree;


@SuppressWarnings("serial")
public class GlobalMap extends JPanel{
	//TODO: teammates and opposing team identification
	//TODO: Removal of old points on map
	
	private Tuple playerPos;
	private String teamId;
	private double curTime;
	private Tuple ball;
	private static int scale = 8; //must be even
	private ArrayList<Tuple> teammates;
	private ArrayList<Tuple> opponents;
	private ArrayList<int[]> lastteam;
	private ArrayList<int[]> lastoppo;
	
	private Hashtable<String, Point2D.Double> flags;
	
	private ArrayList<ArrayList<Tuple[]>> gridMap;
	
	public GlobalMap(String teamid) {
		initFlagHash();
		teamId = teamid;
		
		gridMap = new ArrayList<ArrayList<Tuple[]>>();
		for (int i = 0; i < 21; i++) {
			gridMap.add(new ArrayList<Tuple[]>());
			for (int j = 0; j < 10; j++) {
				gridMap.get(i).add(new Tuple[24]);
			}
		}
		
		String[] sParam = {"x", "y", "absDir"};
		double[] iParam = {0, 0, 0};
		playerPos = new Tuple("player", sParam, iParam);
		ball = new Tuple("player", sParam, iParam);
		
		teammates = new ArrayList<Tuple>(10);
		opponents = new ArrayList<Tuple>(11);
		
		for (int i = 1; i < 12; i++) {
			String[] sParam1 = {"time", "x", "y", "ax", "ax", "rx", "ry", "ddist", "dir"};
			double[] iParam1 = {0, -1, -1, 0, 0, 0, 0, -1, -1};
			Tuple team = new Tuple(teamId + i, sParam1, iParam1);
			teammates.add(team);
		}
		for (int i = 1; i < 12; i++) {
			String[] sParam2 = {"time", "x", "y", "ax", "ax", "rx", "ry", "ddist", "dir"};
			double[] iParam2 = {0, -1, -1, 0, 0, 0, 0, -1, -1};
			Tuple oppo = new Tuple("" + i, sParam2, iParam2);
			opponents.add(oppo);
		}
		
		lastteam = new ArrayList<int[]>();
		lastoppo = new ArrayList<int[]>();
		
		for (int i = 1; i < 12; i++) {
			int[] teamplace = {0, 1000, 1000};
			lastteam.add(teamplace);
			int[] oppoplace = {0, 1000, 1000};
			lastoppo.add(oppoplace);
		} 
		
		
		
	}
	
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//blank reset
		g2.setColor(new Color(255, 255, 255));
		g2.fillRect(0, 0, 105 * scale, 68 * scale);
		
		g2.setColor(new Color(0, 0, 0));
		
		//field and center line
		g2.drawRect(0, 0, 105 * scale, 68 * scale);
		g2.drawLine(105 * scale/2, 0, 105 * scale/2, 68 * scale);
		g2.drawOval((int)(52.5 * scale) - 8, 34 * scale - 8, 16, 16);
		
		//penalty boxes
		g2.drawRect(0, (int)((34 - 20.16) * scale), (int)(16.5 * scale), (int)(40.32 * scale));
		g2.drawRect((int)(105 - 16.5) * scale, (int)((34 - 20.16) * scale), (int)(16.5 * scale), (int)(40.32 * scale));
		
		Point2D.Double b = calcPos(ball);
		g2.setColor(Color.red);
		g2.drawOval((int)b.x - 2, (int)b.y - 2, 4, 4);
		
		g2.setColor(new Color(0, 0, 255));
		g2.fillOval((int)(playerPos.iParams[0] * scale + 52.5 * scale) - 5, 
				(int)(playerPos.iParams[1] * scale + 34 * scale) - 5, 10, 10);
		
		for (int i = 0; i < gridMap.size(); i++) {
			for (int j = 0; j < gridMap.get(i).size(); j++) {
				for (int k = 0; k < gridMap.get(i).get(j).length; k++) {
					if (gridMap.get(i).get(j)[k] == null) {
						continue;
					}
					Tuple item = gridMap.get(i).get(j)[k];
					if (item.label.contains(teamId)) {
						g2.setColor(Color.green);
					} else {
						g2.setColor(Color.red);
					}
					
					//g2.setColor(new Color(0,(int)(255 * item.iParams[0]/curTime), 0));
					g2.fillRect((int)((item.iParams[1] + 52.5) * scale - scale/2), 
							(int)((item.iParams[2] + 34) * scale - scale/2), scale, scale);
					g2.drawString(item.label,
							(int)((item.iParams[1] + 52.5) * scale), 
							(int)((item.iParams[2] + 30) * scale));
//					if (item.iParams[3]) {
//						
//					}
					int radius = (int)positionEst.distance(5, item.iParams[7], item.iParams[6],
							(int)(gridMap.get(i).get(j)[k].iParams[1]), 
							(int)(gridMap.get(i).get(j)[k].iParams[2]));
					g2.drawOval((int)(item.iParams[1] + 52.5 - radius/2) * scale, 
							(int)(item.iParams[2] + 34 - radius/2) * scale, 
//							10, 10
							radius * scale, radius * scale
							);

				}
			}
		}
	}
	
	/**
	 * Updates global map with new localView
	 * @param view
	 */
	public void update(LocalView view) {
		ArrayList<Tuple> inView = view.getItemsInView();
		curTime = view.getTime();
		locatePlayer(inView);
		filterItems(inView);
		
		repaint();
	}
	
	/**
	 * identifies and maps objects of interest in a list of items
	 * separates the flags from other players and balls
	 * @param items
	 */
	public void filterItems(ArrayList<Tuple> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).label.charAt(0) == 'p') {
				mapItem(items.get(i));
			} else if (items.get(i).label.charAt(0) == 'b') {
				ball = items.get(i);
			} else {
				items.remove(i);
				i--;
			}
		}
	}
	
	public Point2D.Double calcPos(Tuple item) {
		double dist = item.iParams[1];
		double dir = item.iParams[2] + playerPos.getIParams()[2];
		double x = playerPos.getIParams()[0] + Math.cos(Math.toRadians(dir)) * dist;
		double y = playerPos.getIParams()[1] + Math.sin(Math.toRadians(dir)) * dist;
		return new Point2D.Double(x, y);
	}
	
	/**
	 * adds item to grid map
	 * @param item
	 */
	public void mapItem(Tuple item) {
		int num = -1;
		String team = "";
		if (item.label.split("\"").length == 3) {
			num = Integer.parseInt(item.label.split("\"")[2]);
			team = item.label.split("\"")[1];
		} else if (item.label.split("\"").length == 2) {
			team = item.label.split("\"")[1];
		}
		
		int index = findPlayer(num, team);

		Tuple player;

		Point2D.Double xy = calcPos(item);
		int x = (int)((xy.getX() + 52.5)/5);
		int y = (int)((xy.getY() + 34)/6.8);
		clearSection((int)item.iParams[0], x, y);
		if (teammates.size() == 0) {
			
		}
		if (opponents.size() == 0) {
			
		}
		if (index == 13) {
			player = teammates.get(0);
			teammates.remove(0);
		} else if (index == -13) {
			player = opponents.get(0);
			opponents.remove(0);
		} else if (index > 0) {
			player = teammates.get(index);
			teammates.remove(index);
		} else {
			player = opponents.get(-index);
			opponents.remove(-index);
		}
		
//		String[] sparams = {"time", "x", "y", "ax", "ax", "rx", "ry", "ddist", "dir"};
//		double[] iparams = {curTime, xy.getX(), xy.getY(), 0, 0, 0, 0, item.getIParams()[3], item.getIParams()[2]};
		
		player.iParams[0] = curTime;
		player.iParams[1] = xy.x;
		player.iParams[2] = xy.y;
		player.iParams[3] = x;
		player.iParams[4] = y;
		player.iParams[5] = xy.getX() - x;
		player.iParams[6] = xy.getY() - y;
		player.iParams[7] = item.iParams[3];
		player.iParams[8] = item.iParams[2];
//		System.out.println("opponents size: " + opponents.size());
//		System.out.println("teammates size: " + teammates.size());
		
//		Tuple newItem = new Tuple(item.label, sparams, iparams);
//		System.out.println("Mapped " + player.label + " to x: " + xy.x + ", y: " +xy.y);
		addMap(player, x, y);
	}
	
	/**
	 * Returns index of arraylist for player of team team and number num 
	 * index gets a minus if is of opposing team
	 * returns (-)13 if player not found in array
	 * @param num
	 * @param team
	 * @return
	 */
	public int findPlayer(int num, String team) {
		
		if (team.length() == 0) {
			return -13;
		}
		
		if (teamId.equals(team)) {
			if (num == -1) {
				return 13;
			}
			for (int i = 0; i < teammates.size(); i++) {
				if (teammates.get(i).label.equals(teamId + num)) {
					return i;
				}
			}
			returnPlayer(num, true);
			return teammates.size()-1;
		} else {
			if (num == -1) {
				return -13;
			}
			for (int i = 0; i < opponents.size(); i++) {
				if (opponents.get(i).label.equals("" + num)) {
					return -i;
				}
			}
			returnPlayer(num, false);
			return -opponents.size()+1;
		}
	}
	
	/**
	 * Removes seen player from gridMap and adds them to respective list
	 * @param num
	 * @param team
	 */
	public void returnPlayer(int num, boolean team) {
		for (int i = 0; i < gridMap.size(); i++) {
			for (int j = 0; j < gridMap.get(i).size(); j ++) {
				for (int k = 0; k < gridMap.get(i).get(j).length; k++) {
					Tuple player = gridMap.get(i).get(j)[k];
					if (player == null) {
						continue;
					}
					if (team) {
						if (player.label.length() < teamId.length() + 1) {
							continue;
						}
						if (player.label.substring(0, teamId.length()).equals(teamId)) {
							if (player.label.substring(teamId.length()).equals("" + num)) {
								gridMap.get(i).get(j)[k] = null;
								teammates.add(player);
							}
						}
					} else {
						if (player.label.equals("" + num)) {
							gridMap.get(i).get(j)[k] = null;
							opponents.add(player);
						}
					}
				}
			}
		}
	}
	
	public void timedOutPlayer() {
		
	}
	
	/**
	 * Removes old entries in a area on gridMap
	 * @param time
	 * @param x
	 * @param y
	 */
	public void clearSection(int time, int x, int y) {
		Tuple[] section = gridMap.get(x).get(y);
		for (int i = 0; i < section.length; i++) {
			if (section[i] == null) {
				continue;
			} else {
				if (section[i].iParams[0] < curTime) {
					if (section[i].label.contains(teamId)) {
						teammates.add(section[i]);
					} else {
						opponents.add(section[i]);
					}
					section[i] = null;
				}
			}
		}
	}
	
	/**
	 * Searches through grid section for empty space to place item. 
	 */
	public void addMap(Tuple item, int x, int y) {
		Tuple[] section = gridMap.get(x).get(y);
		int empty = 0;
		boolean found = false;
		for (int i = 0; i < section.length; i++) {
			if (section[i] == null) {
				empty = i;
			} else {
				if (section[i].label.toLowerCase().equals(item.label.toLowerCase())) {
					section[i] = item;
					found = true;
				}
			}
		}
		if (!found)
			section[empty] = item;
	}
	
	public Tuple getPlayerPos() {
		return playerPos;
	}


	public double getCurTime() {
		return curTime;
	}


	public Tuple getBall() {
		return ball;
	}


	public ArrayList<ArrayList<Tuple[]>> getGridMap() {
		return gridMap;
	}


	/**
	 * Locates position of the player based on the first two flags in sight
	 * @param items ArrayList<Tuple> of the objects that the player sees
	 */
	public void locatePlayer(ArrayList<Tuple> items) {
		Tuple item1;
		Tuple item2;
		//obtain two flag items to locate player with
		item1 = getNextFlag(items);
		items.remove(item1);
		item2 = getNextFlag(items);
		double dist1 = item1.getIParams()[1];
		double dist2 = item2.getIParams()[1];
		double dir1 = item1.getIParams()[2];
		double dir2 = item2.getIParams()[2];
		
		//grab flags from hashtable of flag coordinates
		Point2D.Double point1 = flags.get(item1.getLabel().toLowerCase()); //flags.getOrDefault(item1.label.toLowerCase(), new Point2D.Double(0, 0));
		Point2D.Double point2 = flags.get(item2.getLabel().toLowerCase()); //getOrDefault(item2.label.toLowerCase(), new Point2D.Double(0, 0));
		
		//solves for intersection of circles
		//finds midpoint between circle centers
		double dx = point1.getX() - point2.getX();
		double dy = point1.getY() - point2.getY();
		
		//distance between circle centers
		double r = Math.sqrt(dx * dx + dy * dy);
		double r2 = r*r;
		double r4 = r2*r2;
		
		double i = (dist1 * dist1 - dist2 * dist2) / (2 * r2);
		double r2r2 = (dist1 * dist1 - dist2 * dist2);
		
		double j = Math.sqrt(2 * (dist1 * dist1 + dist2 * dist2) / r2 - (r2r2 * r2r2) / r4 - 1);

		double fx = (point1.getX()+point2.getX()) / 2 + i * (point2.getX() - point1.getX());
		double gx = j * (point2.getY() - point1.getY()) / 2;
		double fy = (point1.getY()+point2.getY()) / 2 + i * (point2.getY() - point1.getY());
		double gy = j * (point1.getX() - point2.getX()) / 2;

		double[] iparams = playerPos.getIParams();
		//choose proper intersection dependent on which flag is to the left/right
		if (dir1 < dir2) {
			iparams[0] = fx - gx;
			iparams[1] = fy - gy;
		} else {
			iparams[0] = fx + gx;
			iparams[1] = fy + gy;
		}
		
		//calculate direction player is facing wrt first flag
		iparams[2] = Math.toDegrees(
				Math.atan((point1.getY() - iparams[1]) / (point1.getX() - iparams[0]))
				) - dir1;
		
		//System.out.println(iparams[0] + ", " + iparams[1]);
		//change parameters
		playerPos.setIParams(iparams);
	}
	
	/**
	 * finds next flag out of the list of items
	 * @param items
	 * @return first flag in the list of items
	 */
	public Tuple getNextFlag(ArrayList<Tuple> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getLabel().toLowerCase().charAt(0) != 'p' 
					&& items.get(i).getLabel().toLowerCase().charAt(0) != 'b'
					&& items.get(i).getLabel().toLowerCase().charAt(0) != 'l')
				return items.get(i);
		}
		System.out.println("no other points found");
		return null;
	}
	
	/**
	 * generates hashtable of flags
	 */
	private void initFlagHash() {
		flags = new Hashtable<String, Point2D.Double>(51);
		flags.put("ftl50", new Point2D.Double(-50, -39));
		flags.put("ftl40", new Point2D.Double(-40, -39));
		flags.put("ftl30", new Point2D.Double(-30, -39));
		flags.put("ftl20", new Point2D.Double(-20, -39));
		flags.put("ftl10", new Point2D.Double(-10, -39));
		flags.put("ft0", new Point2D.Double(0, -39));
		flags.put("ftr10", new Point2D.Double(10, -39));
		flags.put("ftr20", new Point2D.Double(20, -39));
		flags.put("ftr30", new Point2D.Double(30, -39));
		flags.put("ftr40", new Point2D.Double(40, -39));
		flags.put("ftr50", new Point2D.Double(50, -39));
		flags.put("fbl50", new Point2D.Double(-50, 39));
		flags.put("fbl40", new Point2D.Double(-40, 39));
		flags.put("fbl30", new Point2D.Double(-30, 39));
		flags.put("fbl20", new Point2D.Double(-20, 39));
		flags.put("fbl10", new Point2D.Double(-10, 39));
		flags.put("fb0", new Point2D.Double(0, 39));
		flags.put("fbr10", new Point2D.Double(10, 39));
		flags.put("fbr20", new Point2D.Double(20, 39));
		flags.put("fbr30", new Point2D.Double(30, 39));
		flags.put("fbr40", new Point2D.Double(40, 39));
		flags.put("fbr50", new Point2D.Double(50, 39));
		flags.put("frt30", new Point2D.Double(57.5, -30));
		flags.put("frt20", new Point2D.Double(57.5, -20));
		flags.put("frt10", new Point2D.Double(57.5, -10));
		flags.put("fr0", new Point2D.Double(57.5, 0));
		flags.put("frb10", new Point2D.Double(57.5, 10));
		flags.put("frb20", new Point2D.Double(57.5, 20));
		flags.put("frb30", new Point2D.Double(57.5, 30));
		flags.put("flt30", new Point2D.Double(-57.5, -30));
		flags.put("flt20", new Point2D.Double(-57.5, -20));
		flags.put("flt10", new Point2D.Double(-57.5, -10));
		flags.put("fl0", new Point2D.Double(-57.5, 0));
		flags.put("flb10", new Point2D.Double(-57.5, 10));
		flags.put("flb20", new Point2D.Double(-57.5, 20));
		flags.put("flb30", new Point2D.Double(-57.5, 30));
		
		flags.put("fct", new Point2D.Double(0, -34));
		flags.put("fc", new Point2D.Double(0, 0));
		flags.put("fcb", new Point2D.Double(0, 34));
		
		flags.put("fglt", new Point2D.Double(-52.5, -7.01));
		flags.put("gl", new Point2D.Double(-52.5, 0));
		flags.put("fglb", new Point2D.Double(-52.5, 7.01));
		
		flags.put("fplt", new Point2D.Double(-52.5 + 16.5, -20.16));
		flags.put("fplc", new Point2D.Double(-52.5 + 16.5, 0));
		flags.put("fplb", new Point2D.Double(-52.5 + 16.5, 20.16));
		
		flags.put("flt", new Point2D.Double(-52.5, -34));
		flags.put("flb", new Point2D.Double(-52.5, 34));
		
		flags.put("fgrt", new Point2D.Double(52.5, -7.01));
		flags.put("gr", new Point2D.Double(52.5, 0));
		flags.put("fgrb", new Point2D.Double(52.5, 7.01));
		
		flags.put("fprt", new Point2D.Double(52.5 - 16.5, -20.16));
		flags.put("fprc", new Point2D.Double(52.5 - 16.5, 0));
		flags.put("fprb", new Point2D.Double(52.5 - 16.5, 20.16));
		
		flags.put("frt", new Point2D.Double(52.5, -34));
		flags.put("frb", new Point2D.Double(52.5, 34));
	}
}
