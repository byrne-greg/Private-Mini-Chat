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

public class Base64Coder 
{	//open CLASS

	private static final String BASE64_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";
	
	private static final int SPLIT_LINES_AT = 76;
	
//--------------------------------------
	
	public static String encode(String plainText)
	{	//open METHOD
	
		return encode(plainText, true);
		
	}	//close METHOD
	
	public static String encode(String plainText, Boolean addCRLF)
	{	//open METHOD
		
		//declare and instantiate encodedText  & the paddingString
		String encodedText = "", paddingString = "";
		
		//by default, add CRLF at every 76 chars unless passed arg not to
		boolean splitLines = true;
		splitLines = addCRLF;
				
		//determine the amount of paddingString needed using modulus (answer will be between 0 - 2)
		int padCount = plainText.length() % 3;
				
		//if paddingString is needed, lengthen plainText with NULL to make it a multiple of 3
		//fill paddingString with '=' for each NULL character
		if(padCount > 0)
		{	//open IF
					
			for(int i = padCount; i < 3; i++)
			{	//open FOR
						
				paddingString += "=";
				plainText += "\0";
					
			}	//close FOR
					
		}	//close IF
				
		//increment over the length of the string, 3 characters at a time
		for(int i = 0; i < plainText.length(); i += 3)
		{	//open FOR
					
			//take the first three 8-bit ASCII characters to become one 24-bit string
			int bit24string = (plainText.charAt(i) << 16) + 
							  (plainText.charAt(i + 1) << 8)  +
							  (plainText.charAt(i + 2));
			
			//separate the 24-bit string into four 6-bit strings
			int bit6string1 = ((bit24string >> 18) & 63);
			int bit6string2 = ((bit24string >> 12) & 63);
			int bit6string3 = ((bit24string >> 6 ) & 63);
			int bit6string4 = (bit24string & 63);
		
			//assign each 6-bit to a character in the BASE64_CHARSET and instantiate encodedText
			encodedText += "" + BASE64_CHARSET.charAt(bit6string1) + 
								BASE64_CHARSET.charAt(bit6string2) +
								BASE64_CHARSET.charAt(bit6string3) +
								BASE64_CHARSET.charAt(bit6string4);
			
		}	//close FOR
		
		//add paddingString to encodedText and place within a StringBuffer
		//StringBuffer allows mutation of Strings
		StringBuffer encodedTextWithPadding = new StringBuffer(encodedText.substring(0, encodedText.length() - paddingString.length()) + paddingString);
				
		
		//test if SPLIT_LINES are required & that the text is over 76 char's, then add CRLF's every 76 char's 
		if(splitLines)
		{	//open IF
			
			if(encodedTextWithPadding.length() > 76)
			{	//open IF
			
				for(int x = SPLIT_LINES_AT; x < encodedTextWithPadding.length(); x += SPLIT_LINES_AT)
				{	//open FOR
			
					encodedTextWithPadding.insert(x, "\r\n");
					
				}	//close FOR
					
			}	//close IF
		
		}	//close IF	
		
		//return requires variable to be a String
		return encodedTextWithPadding.toString();
				
	}	//close METHOD
	
	public static String decode(String codedText)
	{	//open METHOD
		
		//declare and instantiate encodedText
		String decodedText = "";
		
		//replace padding with "A"
		if(codedText.contains("="))
			codedText = codedText.replace("=", "A");

		
		//increment over the length of the string, 4 characters at a time
		for(int i = 0; i < codedText.length(); i += 4)
		{	//open FOR
					
			//take the first four 6-bit BASE64_CHARSET characters to become one 24-bit string
			int bit24string = (BASE64_CHARSET.indexOf(codedText.charAt(i)) << 18) + 
							  (BASE64_CHARSET.indexOf(codedText.charAt(i + 1)) << 12) +
							  (BASE64_CHARSET.indexOf(codedText.charAt(i + 2)) << 6 ) +
							  (BASE64_CHARSET.indexOf(codedText.charAt(i + 3)));
			
			//assign each 8-bit to a character and instantiate decodedText
			decodedText += "" + (char) ((bit24string >>> 16) & 255) +
								(char) ((bit24string >>>  8) & 255) +
								(char) ((bit24string) & 255);
			
		}	//close FOR
						
		return decodedText;
				
	}	//close METHOD

}	//close CLASS
