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

public class TextObfuscate 
{	// open CLASS
		
	// declare a default variance
	public static int variance = 3;
	
	public static String obfuscate(String strToObfus)
	{	// open METHOD
		
		String obfusedString = "";		

		for(int i = 0; i < strToObfus.length(); i ++)
		{	// open FOR
		
			double c2d = strToObfus.charAt(i);
			
			c2d = c2d + variance;
					
			obfusedString += (char) c2d;
			
		}	// close FOR
		
		return obfusedString;
		
	}	//close METHOD

	public static String obfuscate(String strToObfus, int variation)
	{	// open METHOD
		
		variance = variation;
		String obfusedString = "";		

		for(int i = 0; i < strToObfus.length(); i ++)
		{	// open FOR
		
			double c2d = strToObfus.charAt(i);
			
			c2d = c2d + variance;
					
			obfusedString += (char) c2d;
			
		}	// close FOR
		
		return obfusedString;
		
	}	//close METHOD
	
	public static String deobfuscate(String strToDeobfus)
	{	// open METHOD
		
		String deobfusedString = "";
		
		for(int i = 0; i < strToDeobfus.length(); i ++)
		{	// open FOR
			
			double c2d = strToDeobfus.charAt(i);
			
			c2d = c2d - variance;
			
			// take out null characters
			if(c2d > 0)
				deobfusedString += (char) c2d;
			
		}	// close FOR
		
		return deobfusedString;
	
	}	//close METHOD
	
	public static String deobfuscate(String strToDeobfus, int variation)
	{	// open METHOD
		
		variance = variation;
		String deobfusedString = "";
		
		for(int i = 0; i < strToDeobfus.length(); i ++)
		{	// open FOR
			
			double c2d = strToDeobfus.charAt(i);
			
			c2d = c2d - variance;
			
			// take out null characters
			if(c2d > 0)
				deobfusedString += (char) c2d;
			
		}	// close FOR
		
		return deobfusedString;
	
	}	//close METHOD
	
}	// close CLASS
