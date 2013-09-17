Private Mini-Chat

Created by Greg Byrne, June 2013
Email: byrne.greg@gmail.com
Copyright remains reserved

Package Description:
A client/server application that provides discrete communication between a client-server pair over TCP/IP. Discrete communication is provided by a text obfuscation (through a user-set character shift) and a Base64 encoding scheme.

Package Content:
 * ServerPMC (executable class)
 * ServerPMCGUI
 * ClientPMC (executable class)
 * ClientPMCGUI
 * TextObfuscate
 * Base64Coder

How to Use:
Run the ServerPMC and enter a variation key. This number will shift all characters and ensure semi-private communication. The connected client must also enter the same variation key to ensure communication is clear. The server will wait for clients to connect. Run the ClientPMC, enter the variation key and IP address of server. Upon connection, communication will be shown in the display area. When sending text, the program will first shift characters based on the variation key and then send in Base64 Encoding for decoding on the other end.