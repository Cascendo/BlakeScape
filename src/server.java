public class server implements Runnable {

	public static final int cycleTime = 600;

	public static void main(String... arguments) {
		clientHandler = new server();
		new Thread(clientHandler).start();
		playerHandler = new PlayerHandler();

		long lastTicks = System.currentTimeMillis();

		while(!shutdownServer) {
			playerHandler.process();
			long timeSpent = System.currentTimeMillis()-lastTicks;

			if(timeSpent >= cycleTime) {
				timeSpent = cycleTime;
			}

			try { Thread.sleep(cycleTime-timeSpent); } catch(java.lang.Exception _ex) { }
			lastTicks = System.currentTimeMillis();
		}

		playerHandler.destruct();
		clientHandler.killServer();
		clientHandler = null;
	}

	public static server clientHandler = null;			// handles all the clients
	public static java.net.ServerSocket clientListener = null;
	public static boolean shutdownServer = false;		// set this to true in order to shut down and kill the server
	public static boolean shutdownClientHandler;			// signals ClientHandler to shut down
	public static int serverlistenerPort = 43594; //43594=default

	public static PlayerHandler playerHandler = null;


	public void run() {
		// setup the listener
		try {
			shutdownClientHandler = false;
			clientListener = new java.net.ServerSocket(serverlistenerPort, 1, null);
			misc.println("Starting BlakeScape Server on " + clientListener.getInetAddress().getHostAddress() + ":" + clientListener.getLocalPort());
			while(true) {
				java.net.Socket s = clientListener.accept();
				s.setTcpNoDelay(true);
				String connectingHost = s.getInetAddress().getHostName();
				if(/*connectingHost.startsWith("localhost") || connectingHost.equals("127.0.0.1")*/true) {
					misc.println("ClientHandler: Accepted from "+connectingHost+":"+s.getPort());
					playerHandler.newPlayerClient(s, connectingHost);
				}
				else {
					misc.println("ClientHandler: Rejected "+connectingHost+":"+s.getPort());
					s.close();
				}
			}
		} catch(java.io.IOException ioe) {
			if(!shutdownClientHandler)
				misc.println("Error: Unable to startup listener on "+serverlistenerPort+" - port already in use?");
			else misc.println("ClientHandler was shut down.");
		}
	}

	public void killServer()
	{
		try {
			shutdownClientHandler = true;
			if(clientListener != null) clientListener.close();
			clientListener = null;
		} catch(java.lang.Exception __ex) {
			__ex.printStackTrace();
		}
	}

}
