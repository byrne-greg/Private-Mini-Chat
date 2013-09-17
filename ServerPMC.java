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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


//--------------------------------------

public class ServerPMC
{	//open CLASS

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private ServerSocket server;
	private Socket connection;
	private ServerPMCGUI gui;
	private String userID;
	private int cryptoKey;
	
//--------------------------------------	
	
	public ServerPMC()
	{	// open CONSTRUCTOR
		
		// require user to enter a cryptography value
		do
		{	// open DO
			try
			{	// open TRY
				
				//prompt user for cryptography key
				cryptoKey = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Enter the variation key (1 to 5 OR -1 to -5):\n(NOTE: All clients must enter the correct variation key to have readable communication)",
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
		
		// create GUI instance for ServerPMC
		gui = new ServerPMCGUI(this);
		
		// start ServerPMC
		runServerPMC();
		
	}	// close CONSTRUCTOR
	
	public void runServerPMC()
	{	// open METHOD
		
		try
		{	// open TRY
			
			// create Server Socket (port, socket connection #)
			server = new ServerSocket(12366, 1);
			
			// loop indefinitely
			while(true)
			{	// open WHILE
				
				try
				{	// open TRY
					
					// call to wait for incoming connections
					waitForConnection();
					// call to establish I/O streams
					getStreams();
					// call to facilitate communications
					processConnection();
					
				}	// close TRY
				catch(EOFException eofe)
				{	// open CATCH
					
					eofe.printStackTrace();
					displayMessage("ERROR: Server terminated connection");
				
				}	// close CATCH
				
			}	// close TRY
			
		}	// close WHILE
		catch(BindException be)
		{	// open CATCH
			
			// server is already running on IP
			be.printStackTrace();
			displayMessage("ERROR: \nServer is already running on IP - Close instance of Messager Server and restart application");
		
		}	// close CATCH
		catch(IOException ioe)
		{	// open CATCH
			
			ioe.printStackTrace();
			// if client unexpectedly drops connection (e.g. presses x on window)
			displayMessage("ERROR: Unexpected termination from client connection");
		
		}	// close CATCH
	
	}	// close METHOD
	
	private void waitForConnection() throws IOException
	{	// open METHOD
		
		// start of communication
		displayMessage("--------------");
		
		displayMessage("Waiting For Connection.....");
		
		// connection request found
		connection = server.accept();
		displayMessage("SUCESS! - Connection accepted from " + connection.getInetAddress().getHostName() );
		
	}	// close METHOD

	private void getStreams() throws IOException
	{	// open METHOD
		
		// establish ObjectOutput on Socket's output stream and clear line
		output = new ObjectOutputStream(connection.getOutputStream() );
		output.flush();
		// establish ObjectInput on Socket's input stream
		input = new ObjectInputStream(connection.getInputStream() );
		
		displayMessage("Input/Output streams established");
		
	}	// close METHOD
	
	private void processConnection() throws IOException
	{	// open METHOD
		
		// start of communication
		displayMessage("--------------");
		
		// opening message upon connection to MessagerClient
		String message = "Privacy Channel to " + connection.getInetAddress().getHostName() + " activated";
		sendMessage(message);
		
		// activate JTextField for communication
		setTextFieldEditable(true);
		
		try
		{	// open TRY
			
			// do-while loop until client activates Terminate message
			do
			{	// open DO
			
				try
				{	// open TRY
				
					// read incoming objects and convert to String
					message = (String) input.readObject();
					message = decodeMessage(message);
					displayMessage(message);
				
				}	// close TRY
				catch(ClassNotFoundException cnfe)
				{	// open CATCH
				
					cnfe.printStackTrace();
					displayMessage("ERROR: Unknown message type received");
				
				}	// close CATCH
		
			}	// close DO
			while(!message.contains(".QUIT"));
		
		}	// close TRY 
		catch(SocketException se)
		{	// open CATCH
		
			// fires when client exits application (either expectedly or unexpectedly)
			se.printStackTrace();
			
		}	// close CATCH
		
		displayMessage(connection.getInetAddress().getHostName() + " has exited communication channel");
		setTextFieldEditable(false);
		
	}	// close METHOD
	
	private void closeConnection()
	{	// open METHOD
		
		// deactivate JTextField for communication
		setTextFieldEditable(false);
		
		displayMessage("Attempting to terminate connection.....");
		
		try
		{	// open TRY
			
			// close all open connections
			output.close();
			input.close();
			connection.close();

		}	// close TRY
		catch(IOException ioe)
		{	// open CATCH1
			
			ioe.printStackTrace();
			displayMessage("ERROR: Unexpected error occured");
			
		}	// close CATCH
	
		displayMessage("Connection to MessagerClient terminated");
	
	}	// close METHOD
	
	public void sendMessage(String message)
	{	// open METHOD
		
		// limit character length
		if(message.length() > 40)
		{	// open IF
			
			message = message.substring(0, 40);
			displayMessage("[SERVER] Keep messages to under 40 characters");
			
		}	// close IF
		
		// add user to message
		userID = server.getInetAddress().getHostName();
		message = userID + " >>> " + message;
		
		// display message on GUI
		displayMessage(message);
		
		// obfuscate and encode message
		String encMessage = encodeMessage(message);
		
		try
		{	// open TRY
			
			// send message to MessagerClient
			output.writeObject(encMessage);
			output.flush();
			
		}	// close CATCH
		catch(SocketException se)
		{	// open CATCH
			
			se.printStackTrace();
			displayMessage("ERROR: No client to send communication");
		
		}	// close CATCH
		catch(IOException ioe)
		{	// open CATCH
			
			ioe.printStackTrace();
			displayMessage("ERROR: Error writing object");
		
		}	// close CATCH
		
		// check if it's a quit message
		if(message.contains(".QUIT"))
		{	// open IF
			
			closeConnection();			
			
		}	// close IF

	}	// close METHOD
	
	private void displayMessage(final String messageToDisplay)
	{	// open METHOD
		
		SwingUtilities.invokeLater( new Runnable()
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
		
	}	//close METHOD
	
	public static void main(String args[])
	{	// open METHOD
			   	
		new ServerPMC();
		
	}	// close METHOD
	
}	//close CLASS
