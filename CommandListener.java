import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;


/* COMMNAD LISTENER - listens for command messages from command messenger
 *                    and implements actions/responses accordingly  */

class CommandListener implements Runnable {
	public void run() {
		while (true) {
		String msgIn = null;
		String msgOut = null;
		Socket connectionSocket = null;
		BufferedReader inFromPeer = null;
		DataOutputStream outToPeer = null;
		String filename = null;
		int origPeer = 0;
		int sendPeer = 0;
		int hash = 0;
		int type = 0;
		int num1 = 0;
		int num2 = 0;
	
		
		try {
			connectionSocket = Peer.tcpPeerSocket.accept();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			inFromPeer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));     /* establishment of socket and input stream */
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			msgIn = inFromPeer.readLine();                                                                 /* data in stored as msgIn string */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			outToPeer = new DataOutputStream(connectionSocket.getOutputStream());                         /* dataoutput stream established for response to "depart" messages*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] recoveredMessage = msgIn.split("\\s");                                                   /* msgIn split by whitespaces */
		String command = recoveredMessage[0];                                                             /* first substring is designated as 'command' */
		
		switch (command.toLowerCase()) {
		
		/* when command is "file" */
		case "file":                                                                                         /* if command (start of message is "file" */
			filename = recoveredMessage[1];                                                                  /* next substring is the filename */
			origPeer = Integer.parseInt(recoveredMessage[2]);                                                /* after the peer message originated from (peer that made 'request' in the first place */
			sendPeer = Integer.parseInt(recoveredMessage[3]);                                                /* peer ID from where this message was sent */
			
			hash = Peer.getHash(filename);                                                                   /* filename string passed to getHash method where where file integer is obtained*/                    
			                                                                                                 /* and has value for file requested is calculated */
			/* hash match found */
			if (Peer.fileStoreCheck(hash)) {                                                                 /* hash passed to fileStoreCheck method, which checks if file is stored on this peer */
				System.out.println("File "+filename+" is here.");                                            /* if yes, then 'is here' message is printed */
				msgOut = "found "+filename+" "+Peer.peerID+" ";                                              /* and a response message to the originating peer is sent via TCP */
				SendCommandTCP.send(msgOut, origPeer);				
				System.out.println("A response message, destined for peer "+origPeer+", has been sent.");
			}
			/* hash match not found */
			else {
				System.out.println("File "+filename+" is not stored here.");	                             /* if not, then print statement 'not here' */
				msgOut = "file "+filename+" "+origPeer+" "+Peer.peerID+" "+'\n';
				SendCommandTCP.send(msgOut, Peer.succID);                                                    /* and file command message is amended (with this peers id as sending peer */
				System.out.println("File request message has been forwarded to my successor.");              /* and message is forwarded to this peers successor */
			}
			break;
			
			/* when a 'found' response message is received display result. */ 
		case "found":                                                                                                    /* if this peer is the originating peer of the request - once file is  */ 
			filename = recoveredMessage[1];                                                                              /* is found-- it will receive a 'found' command message from the peer */
			sendPeer = Integer.parseInt(recoveredMessage[2]);                                                            /* on  which the file is stored */
			System.out.println("Received a response message from peer "+sendPeer+", which has the file "+filename+".");
			break;
			
			/* when a depart message is received from quitting peer */
		case "depart":
			type = Integer.parseInt(recoveredMessage[1]);                                       /* substring 2 in message is type of depart message, either 1 or 2 */           
			origPeer = Integer.parseInt(recoveredMessage[2]);                                   /* substring 3 is the peerID of the quitting peer */
			num1 = Integer.parseInt(recoveredMessage[3]);                                       /* substring 4 is the quitting peer's first successor ID */
			num2 = Integer.parseInt(recoveredMessage[4]);                                       /* substring 5 is the quitting peer's second successor ID */
			
			
			System.out.println("Peer "+origPeer+" will depart from the network");               /* print departing message for quitting peer */
			if (type == 1) {                                                                    /* if its a type 1 depart message */
				Peer.succID = num1;                                                             /* quitting peer's first successor is now this peers first successor */
				Peer.succID2 = num2;	                                                        /* and similarly with the second */
			}
			else if (type == 2) {                                                               /* if its a type 2 depart message */
				Peer.succID2 = num1;                                                            /* the quitting peer's second successor is this peers first successor */
			}                                                                                   /* and this peer retains their first successor */
			System.out.println("My first successor in now peer "+Peer.succID+".");              /* print message confirming the changes to successors */
			System.out.println("My second successor in now peer "+Peer.succID2+".");
			
			
			msgOut = "depAck "+type+" "+Peer.peerID+" "+Peer.succID+" "+Peer.succID2+" "+'\n';  /* a "depAck" message is formed to as response to "depart" message */
			                                                                                    /* and confirm the changes to the quitting peer */
			try {
				outToPeer.writeBytes(msgOut);                                                   /* and written out to stream/socket initiated by "depart" message TCP connection */
			} catch (IOException e) {                                                           /* (unlike other singular commands - this request/response takes place in same single TCP connection)*/
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		}
	}
}