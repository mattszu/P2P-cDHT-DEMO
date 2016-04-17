import java.util.TimerTask;

/* PING PEERS - routinely on a timer, uses pingRequest method (defined in Peer)
 * to ping the Peer's first and second successor                             */ 

class PingPeers extends TimerTask {
	public void run() {
	try {
		Peer.preID1 = 777;                        /* at the beginning of every ping cycle predecessor ID 1 and 2 are refreshed */
		Peer.preID2 = 777;                        /* are blanked to value 777 (value outside peer range 0-255) allowing new assignment */
		Thread.sleep(1000);                       /* should the network change (i.e a peer leaves ) */
		
		Peer.pingRequest(Peer.succID);            /* ping request to successor 1 */
		Thread.sleep(2000);                       /* small time delay interval - ensures that FIRST request message received will be allocated as preID1 */
		Peer.pingRequest(Peer.succID2);           /* ping request to successor 2 */
		
		Thread.sleep(1000);                     
	} catch (Exception e) {
		// TODO Auto-generated catch block        /* other time delays not mentioned maybe removed, but they allow for some stability due to sync issues */
		e.printStackTrace();                      /* should peers not all start at same initial time */
	}
	}
}