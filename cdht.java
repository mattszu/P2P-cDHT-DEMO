/*########################################################################*
 *                                                                        *
 *                       title                                            *
 *                                                                        * 
 *                                                                        *
 *                                                                        *
 *                                                                        *
 *########################################################################*/

import java.net.*;
import java.util.Timer;

public class cdht {
	public static void main(String[] args) throws Exception {
		
		 //INITIALIZATION OF PEERS based on arguments provided
		
		Peer.peerID = Integer.parseInt(args[0]);                                                /* including peer number (identity) */
		Peer.succID = Integer.parseInt(args[1]);                                                /* the identity of its first successor */
		Peer.succID2 = Integer.parseInt(args[2]);                                               /* and its second successor */
		Peer.IPAddress = InetAddress.getByName("127.0.0.1");                                    /* address is fixed to host address as peers will be */
		                                                                                        /* distinguished by ports rather than IP addresses */             
		
		Peer.tcpPeerSocket = new ServerSocket(50000+Peer.peerID);                               /* Peer TCP socket created as port 50000 plus peer ID*/
		
		Peer.udpPeerSocket = new DatagramSocket(50000+Peer.peerID);                             /* and similarly for UDP socket */
		
		//TIMED PEER PINGING - routinely pings peers with UDP
		                                                                                        /* using timer class and method */
		Timer timer = new Timer();                                                              /* peer pinging with UDP start after 3secs initially */
		timer.scheduleAtFixedRate(new PingPeers(), 3000, 45000);                                /* and then every 45secs thereafter */
		
		//PING LISTENER - listens for UDP peer pings and responds to each.
		
		PingListener pListener = new PingListener();                                            /* A 'runnable' PingListener is created, attached to thread */
		Thread t1 = new Thread(pListener);                               
		t1.start();                                                                             /* and started*/
		
		//COMMAND LISTENER - listens for COMMAND messages and responds as required.
		
		CommandListener cListener = new CommandListener();                                      /* similarly a CommandLister and CommandMessenger are created  */
		Thread t2 = new Thread(cListener);                                              
		t2.start();                                                                             /* attached to threads and also started */ 
		
		//INPUT LISTENER - waits for standard input commands and sends resulting COMMAND messages via TCP
		
		CommandMessenger cMessenger = new CommandMessenger();
		Thread t3 = new Thread(cMessenger);
		t3.start();
	}
}

