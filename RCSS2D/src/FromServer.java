import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FromServer implements Runnable{
	
	private boolean running;
	private GlobalMap pitch;
    private DatagramSocket clientSocket;
	
	public FromServer (GlobalMap pitch, DatagramSocket clientSocket) {
		running = true;
		this.pitch = pitch;
		this.clientSocket = clientSocket;
	}
	
	public void run() {
        if (clientSocket == null) {
        	running = false;
        }
        byte[] receiveData = new byte[1024];
		while (running) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
				clientSocket.receive(receivePacket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            String modifiedSentence = new String(receivePacket.getData());
            if (modifiedSentence.substring(0, 4).equals("(see")) {
            	updateMap(modifiedSentence);
            }
            //System.out.println(modifiedSentence);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		running = false;
	}

	/**
	 * updates map with new information
	 * @param s information from server
	 */
	public void updateMap(String s) {
		LocalView sight = new LocalView(s);
		pitch.update(sight);
	}
}