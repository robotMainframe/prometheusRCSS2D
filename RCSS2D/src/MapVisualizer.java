
import javax.swing.*;


public class MapVisualizer implements Runnable {

    private GlobalMap pitch;
    public JFrame f;

    public MapVisualizer(GlobalMap pitch) {
    	this.pitch = pitch;
        f = new JFrame("MapVisualizer");
    }

    public void run() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0, 0, 1050, 680);
        pitch.setFocusable(true);
        pitch.grabFocus();
        f.add(pitch);
        f.setVisible(true);
    }

}
