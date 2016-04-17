import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;

/* COMMAND MESSENGER - listens from standard input to create command messengers 
 *                     and sends them to the respective peers */

class CommandMessenger implements Runnable {
	public void run() {
		while (true) {
			String stdIn = null;
			String msgOut = null;
			String msgIn = null;
			String filename = null;
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = null;
			DataOutputStream outToServer = null;
			BufferedReader inFromServer = null;
			int type = 0;
			int origPeer = 0;
			int num1 = 0;
			int num2 = 0;
		
			
			try {
				stdIn = inFromUser.readLine();                                          /* read in from standard input into stdIn string */
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String[] recoveredMessage = stdIn.split("\\s");                             /* splits stdIn into substrings separated by whitespace */
			String command = recoveredMessage[0];
			
			switch (command.toLowerCase()) {
			
			/* if command is 'request'*/
			case "request":
				try{
				filename = recoveredMessage[1];                                                           /* next substring after 'request' is the filename */
				}
				catch (Exception e) {
					System.out.println("ERROR: command \"request\" must be followed by valid file name"); /* and error is printed if no file name present */
					break;                                                                                
				}
				
				if (filename.length() != 4) {                                                             /* if filename present - it is validated for correct number characters */
					System.out.println("ERROR: invalid filename");                                        /* must have exactly FOUR - if incorrect and error message is printed */
					break;
				}
		                                                                           		
				try {
					int i = Integer.parseInt(filename);                                                   /* if correct number of characters - each character is tested to see if its */
				}                                                                                         /* and integer --if anything other than integers and error message is printed */
				catch (Exception e) {
					System.out.println("ERROR: invalid filename");
					break;
				}
				
				                                                                                          /* on validating correct form of file request */
				msgOut = "file "+filename+" "+Peer.peerID+" "+Peer.peerID+" ";                            /* a 'file' message is formed containing 'file' command, the originating peerID*/                          
				                                                                                          /* and the sending peerID, which in the initiating peer are both the same */
				SendCommandTCP.send(msgOut, Peer.succID);                                                 /* the 'file' message is then forwarded to the peers first successor */                          
				
				System.out.println("File request message for "+filename+" has been forwarded to my successor.");                   

				break;
			
				
			/* if command is 'quit' */
			case "quit":
				
				msgOut = "depart 1 "+Peer.peerID+" "+Peer.succID+" "+Peer.succID2+" "+'\n';               /* form a 'depart' message type 1 to first predecessor */
				
				try {
					clientSocket = new Socket(Peer.IPAddress, 50000+Peer.preID1);                         /* open establish connection to first predecessor */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outToServer.writeBytes(msgOut);                                                       /* and send 'depart' type 1 message */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				try {
					msgIn = inFromServer.readLine();                                                     /* read response in from predecessor into msgIn */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.print('\n');
				
				String[] reply = msgIn.split("\\s");                                                     /*msgIn split into substrings separated by whitespace*/
				
				if (reply[0].equals("depAck")) {                                                         /* if command is 'depAck' */
					type = Integer.parseInt(reply[1]);                                                   /* the next substring is type either 1 or 2 */
					origPeer = Integer.parseInt(reply[2]);                                               /* origPeer is the peer the 'depAck' was sent from */
					num1 = Integer.parseInt(reply[3]);                                                   /* num1 is origPeers new successor 1 ID number */
					num2 = Integer.parseInt(reply[4]);                                                   /* num2 is origPeers new successor 2 ID number */
					
					if (type == 1) {                                                                     /* if 'depAck' type 1 */
						if (Peer.succID == num1 && Peer.succID2 == num2) {                               /* the origPeers successors match this departing peers successors */
	
							System.out.println("Acknoledgement recieved from peer "+origPeer+", it's peers have been successfully updated.");  /* print success message */
							}
						else {
							System.out.println("ERROR: depAck 1"); 
						}
					}                                                                                    /* otherwise print error message */
					else {
						System.out.println("ERROR: depAck 1");
					}
				}
				try {
					clientSocket.close();                                                                 /* interaction with this predecessor is completed, and socket is closed */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				msgOut = "depart 2 "+Peer.peerID+" "+Peer.succID+" "+Peer.succID2+" "+'\n';      /* new 'depart' message formed this time, type 2 for second predecessor */
				
				
				try {
					clientSocket = new Socket(Peer.IPAddress, 50000+Peer.preID2);                        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				                                                                                                  /* establish TCP connections and streams for this predecessor */
				try {
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					outToServer.writeBytes(msgOut);                                                              /* send 'depart' type 2 command message */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				try {
					msgIn = inFromServer.readLine();                                                              /* and read in predecessor 2 response into msgIn */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				reply = msgIn.split("\\s");                                                                       /* again split msgIn into substrings*/
				
				if (reply[0].equals("depAck")) {                                                                   
					type = Integer.parseInt(reply[1]);
					origPeer = Integer.parseInt(reply[2]);                                                        /* substrings are again assigned as above */
					num1 = Integer.parseInt(reply[3]);
					num2 = Integer.parseInt(reply[4]);
					
					if (type == 2) {                                                                              /* if type 2 */
						if (Peer.succID == num2) {                                                                /* and sender of 'depAck' type 2 */
		                                                                                                          /* second successor equals departing peers first successor */
							System.out.println("Acknoledgement recieved from peer "+origPeer+", it's peers have been successfully updated");  /* print success message */
						}
						else {
							System.out.println("ERROR: depAck 2");
						}
					}                                                                                             /* otherwise print error messages */
					else {
						System.out.println("ERROR: depAck 2");
					}
				}
				
				try {
					clientSocket.close();                                                                         /* interaction with this last predecessor is complete */
				} catch (IOException e) {                                                                         /* so close socket */
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					Peer.goodbye();                                                                               /* peer is now ready to exit , goodbye method is called */
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
				
			//DEBUG COMMAND
			//case "report":
			//	System.out.println("pre1 is : "+Peer.preID1+" pre2 is : "+Peer.preID2);                            /* debug command: on typing report prints out peers*/
			//	break;                                                                                             /*                two predecessors                 */
			
			default: 
				System.out.println("ERROR: invalid command");
				break;
			}
		}
	}
}