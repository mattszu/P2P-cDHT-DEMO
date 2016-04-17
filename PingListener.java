import java.io.IOException;
import java.net.DatagramPacket;


/* PING LISTENER - (as thread) listens on Peer UDP port for ping REQUESTS
 *                and sends RESPONSE (using pingResponse - defined in Peer) to respective peer 
 *                - also assigning predecessor IDs 1 and 2 via getPred (also defined in Peer) */

class PingListener implements Runnable {
	public void run() {
		byte[] receiveData = new byte[1024];
		
		while(true) {
			String request = "REQ";                                                               /* command REQUEST represented in message as "REQ" */
			String response = "REP";                                                              /* command RESPONSE represented in message as "REP" */
			DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
		//System.out.println("receiving message...");
			try {
				Peer.udpPeerSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String messageReceived = new String(receivePacket.getData());                         /* retrieve data from UDP packet as string */
		//System.out.println(messageReceived);
			String[] recoverMessage = messageReceived.split("\\s");                               /* splits data string message by whitespace */
			String mType = recoverMessage[0];                                                     /* first substring is message type -- either "REQ" or "REP"*/
			int mNum = Integer.parseInt(recoverMessage[1]);                                       /* message number refers to peerID of sending peer */
			if (mType.equals(request)) {                                                          /* if first substring recognized as request ("REQ") */
				System.out.println("A ping request message was receieved from Peer " + mNum);     /* print to screen ping received message*/
				
				if (Peer.preID1 == 777 || Peer.preID2 == 777)                                     /* in case of either predecessors not assigned */
					Peer.getPred(mNum);                                                           /* getPred called to assign predecessor ID  */
				
				try {
					Peer.pingResponse(mNum);                                                      /* pingResponse used to send response message to */
				} catch (Exception e) {                                                           /* the requesting peer */
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			else if (mType.equals(response)) {                                                    /* otherwise */
				System.out.println("A ping response message was received from Peer " + mNum);     /* if first substring recognized as response ("REP") */
			}                                                                                     /* print to screen response received message */
		}
	}
}