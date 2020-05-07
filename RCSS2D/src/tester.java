import java.util.Scanner;

public class tester {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		String cmd ="";
		Player player1 = new Player(1, "t1-");
		Thread thread1 = new Thread(player1);
		thread1.start();
		Player player2 = new Player(2, "t1-", false);
		Thread thread2 = new Thread(player2);
		thread2.start();
		String playerCtl = "p1";
		Player oppo1 = new Player(1, "t2-", false);
		Thread opthread1 = new Thread(oppo1);
		opthread1.start();
		Player oppo2 = new Player(2, "t2-", false);
		Thread opthread2 = new Thread(oppo2);
		opthread2.start();
		
		actionParser(player1, "move -10 2");
		actionParser(player2, "move -10 -2");
		actionParser(oppo1, "move -10 2");
//		actionParser(oppo2, "move -10 -2");
		
		
		while (!cmd.equalsIgnoreCase("stop\n")) {
			cmd = scanner.nextLine();
			if (cmd.equals("p1")) {
				playerCtl = "p1";
			} else if (cmd.equals("p2")) {
				playerCtl = "p2";
			} else if (cmd.equals("demo")) {
				oppo1.opdemo();
//				oppo2.opdemo();
			}
			System.out.println(cmd);
			if (playerCtl.equals("p1"))
				actionParser(player1, cmd);
			else 
				actionParser(player2, cmd);
			
		}
		
		scanner.close();

	}
	
	public static void actionParser(Player player, String str) {

		if (str.equals("test")) {
			player.test();
		}
		for(int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				try {
					player.doAction(str.substring(0, i), str.substring(i + 1));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		
	}
}
