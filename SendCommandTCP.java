import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/* SEND COMMAND TCP - given parameters message as string and peer identity as integer, sends the
 *                    command message to the specified peer through TCP.
 *                    (sends a one-way command via TCP - and closes socket after. Used for short              
 *                    commands that don't specifically require an acknowledgement. Utilized in command
 *                    messenger and listener to make code tidy                                          */

class SendCommandTCP {
	static void send (String commandMsg, int sendToPeer) {
		
		Socket outSocket = null;
		DataOutputStream outToPeer = null;
		
		try {
			outSocket = new Socket(Peer.IPAddress, 50000+sendToPeer);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			outToPeer = new DataOutputStream(outSocket.getOutputStream());      /* creates out socket and output stream */ 
		} catch (IOException e1) {                                              
			// TODO Auto-generated catch block                                  /* and sends provided message */
			e1.printStackTrace();
		}                                                                       
		try {
			outToPeer.writeBytes(commandMsg);                                   /* through output stream */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {                                                                   /* and closes socket afterwards */
			outSocket.close();                                                 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}