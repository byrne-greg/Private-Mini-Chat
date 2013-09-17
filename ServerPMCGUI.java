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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

//--------------------------------------

@SuppressWarnings("serial")
public class ServerPMCGUI extends JComponent
{	// open CLASS
	
	private String versionID = "1.0";
	
	private JFrame window;
	private JPanel textAreas;
	public JTextField enterField;
	public JTextArea displayArea;
	public JTextArea obDisplayArea;
	public JTextArea b64DisplayArea;
	private JMenuBar menuBar;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	
//--------------------------------------
	
	public ServerPMCGUI(final ServerPMC mServer)
	{	// open CONSTRUCTOR
		
		super();
		window = new JFrame();

		// Title Window
		window.setTitle("Private Mini-Chat Server");
		
		// TextField for communication
		enterField = new JTextField();
		enterField.setEditable(false);
		enterField.addActionListener( new ActionListener()
		{	// open ANON ACTIONLISTENER

			@Override
			public void actionPerformed(ActionEvent ae) 
			{	// open ACTIONLISTENER METHOD
			
				// call to ServerPMC to send message
				mServer.sendMessage(ae.getActionCommand() );
				enterField.setText("");
				
			}	// close ACTIONLISTENER METHOD
		
		});	// close ANON ACTIONLISTENER
		
		// TextArea for communication display
		displayArea = new JTextArea();
		displayArea.setBorder(new TitledBorder("Display Area"));
		displayArea.setColumns(30);
		displayArea.setRows(15);
		displayArea.setEditable(false);
		displayArea.setFocusable(true);
		displayArea.setLineWrap(true);
		
		// TextArea for display of obfuscated text
		obDisplayArea = new JTextArea();
		obDisplayArea.setBorder(new TitledBorder("Obfuscated Text - Incoming/Outgoing"));
		obDisplayArea.setColumns(30);
		obDisplayArea.setRows(7);
		obDisplayArea.setEditable(false);
		obDisplayArea.setFocusable(true);
		obDisplayArea.setLineWrap(false);
		
		// TextArea for display of Base64 encoded text
		b64DisplayArea = new JTextArea();
		b64DisplayArea.setBorder(new TitledBorder("Base64 Encoded Text - Incoming/Outgoing"));
		b64DisplayArea.setColumns(30);
		b64DisplayArea.setRows(7);
		b64DisplayArea.setEditable(false);
		b64DisplayArea.setFocusable(true);
		b64DisplayArea.setLineWrap(false);
		
		// Menu Bar
		menuBar = new JMenuBar();
		helpMenu = new JMenu("Help");
		aboutMenuItem = new JMenuItem("About");
		
		// add components to menuBar
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		
		//	Menu Bar - About
		aboutMenuItem.addActionListener(new ActionListener()
		{	// open ANON ACTIONLISTENER

			@Override
			public void actionPerformed(ActionEvent ae) 
			{	// open ACTIONLISTENER METHOD
				
				JOptionPane.showMessageDialog(null, "Created by Greg Byrne, June 2013\nEmail: byrne.greg@gmail.com\nVersion: " + versionID);
				
			}	// close ACTIONLISTENER METHOD
			
		});	// close ANON ACTIONLISTENER
		
		// Panel to hold display panes
		textAreas = new JPanel();
		textAreas.setLayout(new FlowLayout());
		textAreas.add(new JScrollPane(displayArea));	
		textAreas.add(new JScrollPane(obDisplayArea));
		textAreas.add(new JScrollPane(b64DisplayArea));

		// add components to GUI frame
		window.setJMenuBar(menuBar);
		window.getContentPane().add(enterField, BorderLayout.NORTH);
		window.getContentPane().add(textAreas, BorderLayout.CENTER);
		
		// JFrame housekeeping
		window.setPreferredSize(new Dimension(500,700));
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
	}	// close CONSTRUCTOR
	
	public void exitGUI()
	{	// open METHOD
		
		window.dispose();
		
	}	// close METHOD
	
}	// close CLASS