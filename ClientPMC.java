/* 
 * Private Mini-Chat
 * 
 * Created by Greg Byrne, June 2013
 * Email: byrne.greg@gmail.com
 * Copyright remains reserved
 * 
 * Package Description:
 * A client/server application that provides discrete communication between a client-server pair over TCP/IP.
 * Discrete communication is provided by a text obfuscation (through a user-set character shift) and a Base64 encoding scheme.
 * 
 * Package Content:
 * ServerPMC (executable class)
 * ServerPMCGUI
 * ClientPMC (executable class)
 * ClientPMCGUI
 * TextObfuscate
 * Base64Coder
 * 
 */

package privateminichat.gregbyrne;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//--------------------------------------

public class ClientPMC
{	// open CLASS
	
	private Socket client;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String ipToConnectTo ;
	private ClientPMCGUI gui;
	private String userID;
	private boolean callToExit = false;
	private int cryptoKey;
	
//--------------------------------------
	
	public ClientPMC(String ipGivenByUser)
	{	// open CONSTRUCTOR
		
		// require user to enter a cryptography value
		do
		{	// open DO
		
			try
			{	// open TRY
						
				//prompt user for cryptography key
				cryptoKey = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Enter the variation key (1 to 5 OR -1 to -5):\n(NOTE: Variation key must match Server in order to have readable communication)",
						"Cryptography Key\n", JOptionPane.PLAIN_MESSAGE));
				if(cryptoKey < -5 || cryptoKey > 5 || cryptoKey == 0)
					JOptionPane.showMessageDialog(null, "Only enter the numbers within the range: \n1 to 5\n-1 to -5");
				
			}	// close TRY
			catch(NumberFormatException nfe)
			{	// open CATCH
				
				JOptionPane.showMessageDialog(null, "Invalid input \nPlease enter using ONLY numbers");
						
			}	// close CATCH
				
		}	// close DO
		while(cryptoKey < -5 || cryptoKey > 5 || cryptoKey == 0);
		
		//MessagerServer location
		ipToConnectTo = ipGivenByUser;
		
		// create GUI instance for ClientPMC
		gui = new ClientPMCGUI(this);
		
		// start ClientPMC
		runClientPMC();
		
	}	// close CONSTRUCTOR
	
	public void runClientPMC()
	{	// open METHOD
		
		try
		{	// open TRY
			
			// call to connect to MessagerServer
			connectToServer();
			
			if(!callToExit)
			{	// open IF
				
				// call to get I/O Streams
				getStreams();
				// call to facilitate communication
				processConnection();
			
			}	// close IF
		
		}	// close TRY
		catch(EOFException eofe)
		{	// open CATCH
			
			eofe.printStackTrace();
			displayMessage("ERROR: Client terminated connection");
			
		}	// close CATCH
		catch(IOException ioe)
		{	// open CATCH
			
			ioe.printStackTrace();
			
		}	// close CATCH
		
	}	// close METHOD
	
	private void connectToServer() throws IOException
	{	// open METHOD
		
		displayMessage("Attempting connection to " + ipToConnectTo + ".....");
		
		try
		{	// open TRY
			
			// create connection to MessagerServer
			client = new Socket(InetAddress.getByName(ipToConnectTo), 12366);
			displayMessage("SUCCESS! - Connected to " + client.getInetAddress().getHostName() );
			displayMessage("In queue for server access...");
			
		}	// close TRY
		catch(ConnectException ce)
		{	// open CATCH
			
			ce.printStackTrace();
			displayMessage("ERROR: Server cannot be found - Check IP address");
			callToExit = true;
			closeConnection();
		
		}	// close CATCH
		
	}	// close METHOD
	
	private void getStreams() throws IOException
	{	// open METHOD
		
		// establish ObjectOutput on Socket's output stream and clear line
		output = new ObjectOutputStream(client.getOutputStream() );
		output.flush();
		// establish ObjectInput on Socket's input stream
		input = new ObjectInputStream(client.getInputStream() );
		
		displayMessage("Input/Output streams established");
		
	}	// close METHOD
	
	private void processConnection() throws IOException
	{	// open METHOD
		
		// start of communication
		displayMessage("--------------");
		
		//enable JTextField on GUI for user
		setTextFieldEditable(true);
		
		//loop processConnection until quit message received by ClientPMC
		try
		{	// open TRY
		
			do
			{	// open DO
			
				try
				{	// open TRY
				
					// convert incoming objects to String's, decode, and display on GUI
					message = (String) input.readObject();
					message = decodeMessage(message);
					displayMessage(message);
				
				}	// close TRY
				catch(ClassNotFoundException cnfe)
				{	// open CATCH
				
					cnfe.printStackTrace();
					// incoming object cannot be converted to String
					displayMessage("ERROR: Unknown object type received");
			
				}	// close CATCH
			
			}	// close DO
			while(!message.contains(".QUIT"));
			
			// action upon quit command
			displayMessage("Server has terminated application");
			closeConnection();
		
		}	// close TRY
		catch(SocketException se)
		{	// open CATCH
			
			se.printStackTrace();
			displayMessage("ERROR: Server has unexpectedly terminated connection. If required, please attempt to reconnect.");
			closeConnection();
			
		}	// close CATCH
		
	}	// close METHOD
	
	private void closeConnection()
	{	// open METHOD
		
		// disable JTextField on GUI
		setTextFieldEditable(false);
		
		displayMessage("Attempting to close connections.....");
		
		try
		{	// open TRY
			
			// close streams and socket connection
			output.close();
			input.close();
			client.close();
			
		}	// close TRY
		catch(NullPointerException npe)
		{	// open CATCH
			
			// called when output stream does not exist due to a null server connection
			npe.printStackTrace();
			
		}	// close CATCH
		catch(IOException ioe)
		{	// open CATCH
			
			ioe.printStackTrace();
			
		}	// close CATCH
		
		displayMessage("Connections now closed - please exit application");
		
	}	// close METHOD
	
	public void sendMessage(String message)
	{	//open METHOD
		
		// limit character length
		if(message.length() > 40)
		{	// open IF
			
			message = message.substring(0, 40);
			displayMessage("[SERVER] Keep messages to under 40 characters");
			
		}	// close IF
	
		// add user to message
		userID = client.getInetAddress().getHostName();
		message = userID + " >>> " + message;
		
		// display message on user GUI
		displayMessage(message);
		
		// encode message
		String encMessage = encodeMessage(message);
		
		try
		{	// open TRY
			
			// send message to MessagerServer
			output.writeObject(encMessage);
			output.flush();			
		
		}	// close TRY
		catch(IOException ioe)
		{	// open CATCH
			
			ioe.printStackTrace();
			displayMessage("ERROR: Error Writing Object");

		}	// close CATCH

		// check if it's a quit message
		if(message.contains(".QUIT"))
		{	// open IF
			
			closeConnection();
			// close GUI so that it doesn't display SocketException for unexpected server connection
			gui.exitGUI();
			
		}	// close IF
		
	}	// close METHOD
	
	private void displayMessage(final String messageToDisplay)
	{	// open METHOD
		
		SwingUtilities.invokeLater(new Runnable() 
		{	// open ANON RUNNABLE
			
			@Override
			public void run()
			{	// open RUNNABLE METHOD
				
				gui.displayArea.append("\n" + messageToDisplay);
			
			}	// close RUNNABLE METHOD
			
		});	// close ANON RUNNABLE
	
	}	// close METHOD
	
	
	private void setTextFieldEditable(final boolean editable)
	{	// open METHOD
		
		SwingUtilities.invokeLater(new Runnable() 
		{	// open ANON RUNNABLE
			
			@Override
			public void run()
			{	// open RUNNABLE METHOD
				
				gui.enterField.setEditable(editable);
			
			}	// close RUNNABLE METHOD
			
		});	// close ANON RUNNABLE
		
	}	// close METHOD
	
	private void obDisplayMessage(final String messageToDisplay)
	{	// open METHOD
		
		SwingUtilities.invokeLater(new Runnable() 
		{	// open ANON RUNNABLE
			
			@Override
			public void run()
			{	// open RUNNABLE METHOD
				
				gui.obDisplayArea.append("\n" + messageToDisplay);
			
			}	// close RUNNABLE METHOD
			
		});	// close ANON RUNNABLE
	
	}	// close METHOD
	
	private void b64DisplayMessage(final String messageToDisplay)
	{	// open METHOD
		
		SwingUtilities.invokeLater(new Runnable() 
		{	// open ANON RUNNABLE
			
			@Override
			public void run()
			{	// open RUNNABLE METHOD
				
				gui.b64DisplayArea.append("\n" + messageToDisplay);
			
			}	// close RUNNABLE METHOD
			
		});	// close ANON RUNNABLE
	
	}	// close METHOD
	
	private String encodeMessage(final String message)
	{	// open METHOD
	
		String messageObfused = TextObfuscate.obfuscate(message, cryptoKey);
		obDisplayMessage(messageObfused);
		String messageEncoded = Base64Coder.encode(messageObfused);
		b64DisplayMessage(messageEncoded);
		
		return messageEncoded;
	
	}	// close METHOD
	
	private String decodeMessage(final String message)
	{	// open METHOD
		
		b64DisplayMessage(message);
		String messageDecoded = Base64Coder.decode(message);
		obDisplayMessage(messageDecoded);
		String messageDeobfused = TextObfuscate.deobfuscate(messageDecoded, cryptoKey);
		
		return messageDeobfused;
	}
	
	public static void main(String args[])
	{	// open MAIN METHOD
		
		String serverLoc;
		
		 //prompt user for server connection
	   	serverLoc = JOptionPane.showInputDialog(null,
	   			"Enter the Server IP for connection:\n(HELP TIP: Ask the server host to run ipconfig and configure firewall)", "Server Connection\n",
	             JOptionPane.PLAIN_MESSAGE);
	   	if(serverLoc == null)
	   	{	//open IF
	    		
	   		JOptionPane.showMessageDialog(null, "Default Server Address Used\nServer on: 127.0.0.1 (localhost)");
	   		serverLoc = "127.0.0.1";
	    		
	   	}	//close IF
			
		new ClientPMC(serverLoc);
	
	}	// close MAIN METHOD
	
}	//close CLASS
