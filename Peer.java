import java.net.*;

//Peer class contains all the Key characteristics and methods of a Peer

public class Peer {
		public static int peerID;                                          /* peerID is the integer defining peer identity */
		public static int succID;                                          /* succID is the first successor ID of the peer */
		public static int succID2;                                         /* succID2 is the second successor ID of the peer */
		public static int preID1;                                          /* preID1 is the first predecessor ID of the peer */
		public static int preID2;                                          /* preID2 is the second predecessor ID of the peer */
		public static int temp1;                                           //THESE PROLLY ARENT USED DOUBLE CHECK
		public static int temp2;
		public static InetAddress IPAddress;                               /* declaration of IP address used through out */
		public static ServerSocket tcpPeerSocket;                          /* as well as the TCP socket */
		public static DatagramSocket udpPeerSocket;                        /* and UDP socket */
		
		/*PING REQUEST - method given a peer's ID as argument sends a
		 *               REQUEST message via UPD to the specified peer's
		 *               UDP port.
		 *  Message Form: |"REQ"|s|ID of sending peer|s|        
		 *                                                                    |s| indicates space */
		
		static void pingRequest(int reqPeerID) throws Exception {
			String messageSent = "REQ "+peerID+" ";
			byte[] sendData = new byte[1024];
			sendData = messageSent.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,Peer.IPAddress,50000+reqPeerID);
			//System.out.println("ping request being sent");
			Peer.udpPeerSocket.send(sendPacket);
			}
		
		/*PING RESPONSE - method given a peer's ID as argument sends a
		 *                RESPONSE message via UPD to the specified peer's
		 *                UDP port.
		 *  Message Form:  |"REP"|s|ID of sending peer|s|             */
		
		static void pingResponse(int repPeerID) throws Exception {
			String messageSent = "REP "+peerID+" ";
			byte[] sendData = new byte[1024];
			sendData = messageSent.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,Peer.IPAddress,50000+repPeerID);
			Peer.udpPeerSocket.send(sendPacket);
			}
		
		/*GET PREDECESSORS - method given a peer's ID (as result of ping request in PingListener) will assign it
		 *                    as either predecessor 1 or 2 depending on order of assigning (receiving) peer ID     */
		
		static void getPred (int pred) {
			if (Peer.preID1 == 777 && Peer.preID2 == 777)                  /* initial state both predecessor IDs are of value 777 (arbitrary value outside of 0-255 */
				Peer.preID1 = pred;                                        /* if both are 777, then pred IDs have not been yet assigned, the first peer ID provided */
			else if (Peer.preID2 == 777 && Peer.preID1 != 777)             /* will be predecessor 1, when one is assigned and is no longer equal to 777 anymore but */
				Peer.preID2 = pred;                                        /* pred ID 2 still is. Then the next peer ID provided will be predecessor 2.             */
		}
		
		
		/*GET HASH - method obtains the integer from the filename string provided as argument, and from
		 *           this integer calculates the file's hash value (resulting modulus of 256) and returns
		 *           hash as an integer                                                                 */
		
		static int getHash(String filename) {
			String fileNo = filename;
			int hash = Integer.parseInt(fileNo) % 256;
			return hash;
		}
		
		/* FILE STORE CHECK - method provided with an integer (file's hash value) check against the peer 
		 *                    to see if the peer stores the file - and returns either true or false     
		 *                    depending on result                                                       */
		
		static boolean fileStoreCheck (int hash) {
			if (hash == peerID)                                                /* if hash equals peerID, then file is stored there without exception */
				return true;
			if (hash > peerID && hash > preID1 && preID1 > peerID)             /* handles file location when hash is bigger than largest peer i.e peer 1 file 2012 */
				return true;
			if (hash < peerID && preID1 > peerID && succID > peerID)           /* handles file location when file hash equates to ZERO (either 0000 or divisible by 256 */
				return true;
			if (hash > peerID)                                                 /* in all other circumstances return false when hash is larger the peer identity */
				return false;
			if (preID1 > hash)                                                 /* and thus if hash is smaller than peer identity, return false if its predecessor is greater */
				return false;                                                  /* than hash -- ensuring file stored in closest successor to hash and not simply largest peer ID */
			else
				return true;
		}
		
		/*GOODBYE - method that prints to screen a goodbye message and exits the program (peer quits network) */
		
		static void goodbye () throws InterruptedException{
			System.out.print('\n');
			System.out.println("Peer will now leave in... ");
			Thread.sleep(1000);
			System.out.println("                                   3");
			Thread.sleep(1000);
			System.out.println("                                   2");
			Thread.sleep(1000);
			System.out.println("                                   1");
			Thread.sleep(1000);
			System.out.println("                                             Peer "+Peer.peerID+" has left...");
			System.exit(0);
		}
	}
