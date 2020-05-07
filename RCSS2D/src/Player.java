import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import jdk.nashorn.api.tree.ForInLoopTree;
//comment
//import tags.Tuple;

public class Player implements Runnable{
	private int num;
	private String team;
	private int port;
	private InetAddress IPAddress;
	private DatagramSocket clientSocket;
	private GlobalMap pitch;
	private positionEst posEst;
	boolean visual;
	
	/**
	 * Standard Instance. Shows map by default
	 * @param num
	 * @param team
	 */
	public Player(int num, String team) {
		this.num = num;
		this.team = team;
		visual = true;
		pitch = new GlobalMap(team);
		posEst = new positionEst();
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = ("(init " + team + " (version 15))").getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6000);
        try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
			clientSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IPAddress = receivePacket.getAddress();
        port = receivePacket.getPort();
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println(modifiedSentence);	
		for (int i = 0; i < 20; i++) {
	        receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        try {
				clientSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        modifiedSentence = new String(receivePacket.getData());
	        System.out.println(modifiedSentence);	
		}
		try {
			doAction("move", "-10 0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Allows for map visual to be disabled
	 * @param num
	 * @param team
	 * @param vis
	 */
	public Player(int num, String team, boolean vis) {
		this(num, team);
		visual = vis;
	}
	
	/**
	 * sends action to rcssserver
	 * @param command action wanted
	 * @param param string containing param or params
	 * @throws IOException
	 */
	public void doAction(String command, String param) throws IOException {
        byte[] sendData;
        sendData = ("(" + command + " " + param + ")").getBytes();
        DatagramPacket receivePacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(receivePacket);
   	}
	
	/**
	 * Method for Player to ball
	 */
	public void toBall(double speed) {
		Tuple ball = pitch.getBall();
		try {
			doAction("turn", "" + ball.iParams[1]);
			
			while(pitch.getBall().iParams[0] > 3) {
				doAction("dash", "100");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends ball in dir distant dist
	 * @param dir direction to send ball
	 * @param dist distance to send ball
	 */
	public void pass(double dir, double dist) {
		//turn??
		int power = 0;
		//pitch.getTeammates();
		
		//posEst.calc
		
		//kick with some power
		try {
			doAction("kick", "" + power);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void dribble(double dist, double time) {
		//kick, run to, kick, run to
		double distChange = 0;
		while(dist > distChange) {
			pass(0, 5);
			toBall(dist/time);
			distChange += 5; // placeholder value need to calculate later
		}
	}
	
	
	/**
	 * Move to coordinates
	 * @param x
	 * @param y
	 */
	public void moveTo(double x, double y) {
		double x2 = pitch.getPlayerPos().iParams[0];
		double y2 = pitch.getPlayerPos().iParams[1];
		double dir = Math.toDegrees(Math.atan((y-y2)/(x-x2))) + pitch.getPlayerPos().iParams[2];
		try {
			doAction("turn", "" + dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test() {
		try {
			doAction("kick", "1000 0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void opdemo() {
		try {
			while(true) {
				doAction("dash", "10000");
				Thread.sleep(100);
				doAction("dash", "10000");
				Thread.sleep(100);
				doAction("dash", "10000");
				Thread.sleep(100);
				doAction("dash", "10000");
				Thread.sleep(100);
				doAction("dash", "10000");
				Thread.sleep(2000);
				doAction("turn", "90");
				Thread.sleep(1000);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		//Start Receiving from rcss server
		FromServer reader = new FromServer(pitch, clientSocket);
		Thread reading = new Thread(reader);
		reading.start();
		
		//create field visualizer
		if (visual) {
			MapVisualizer mapper = new MapVisualizer(pitch);
			Thread mapping = new Thread(mapper);
			mapping.start();
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
