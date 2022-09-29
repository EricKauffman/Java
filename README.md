# Java
CSCI 455 Project 1: Building a Proxy Server and Request Handler With a Log File and Caching

How to Run:
	1. Download a copy of the code. This will either be submitted as separate files on Blackboard
	   or our github link to the project will be posted where the code can be pulled onto a local
         device. 

	2. Configure your Firefox to accept proxy connections on the same port numbers as ours. 
		- Open Firefox and settings
		- In the search bar search for network settings
		- Under Connection Settings click the Manual proxy configuration radio button.
		- Under HTTP proxy enter the localhost IP address 127.0.0.1
		- Under Port Number in HTTP Proxy enter 6500.
		- No other settings need to be changed, click OK.
 		- Close FireFox before running the project

	3. Click into the ProxyServer.java file in desired code editor. We created this project 
	   in visual studio code. Click the play button in VSCode, or click run on other IDE to start
	   program execution. 

	4. With the program now running, reopen FireFox for requests to start coming in.

	5. To check that HTTP sites can be reached through the proxy connection, enter the
	   following URL into the search bar: http://students.cs.ndsu.nodak.edu

	6. The Website should open to a full white screen with text in the top left corner
	   that reads, "Nothing to see here." If this screen is reached the program is
	   running as intended.

	7. Each GET request is logged in the log.txt file, and those requests are cached in 
	   randomely named files and placed in the cached folder.
	
	8. To stop requests from coming in close FireFox, to stop execution of the program
	   simply click the stop button on your IDE.
