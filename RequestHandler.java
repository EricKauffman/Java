import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.swing.plaf.synth.SynthSplitPaneUI;

// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread {

	
	Socket clientSocket;
	InputStream inFromClient;
	OutputStream outToClient;
	byte[] request = new byte[1024];
	BufferedReader br;

	private ProxyServer server;


	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {

		
		this.clientSocket = clientSocket;
		this.server = proxyServer;

		try {
			clientSocket.setSoTimeout(2000);
			inFromClient = clientSocket.getInputStream();
			outToClient = clientSocket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {				// test URL: http://students.cs.ndsu.nodak.edu

		try {

			int counter = 0;
			while((counter = inFromClient.read(request)) != -1){

					String requestString = new String(request,StandardCharsets.UTF_8);
					System.out.println("String Request ------------------------" + requestString);
					
					//If get
					if(requestString.contains("GET")){

							String[] token = requestString.split(" ");
							String url = token[1];
							String[] preHost = url.split("/");
							String host = preHost[2];
							String ip = InetAddress.getByName(host).getHostAddress();
							String info = ip + " " + host;
							System.out.println(info + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							server.writeLog(info);
						
							//if cache
							if(server.getCache(requestString) != null){
								sendCachedInfoToClient(requestString);				//Does this need to be a filename? private void sendCachedInfoToClient(String fileName)
							} else {
								proxyServertoClient(request);
							}
						
						
						}

					}
				} 
				catch (Exception e) {
					System.out.println("Exception Found: " + e);
				}
				
			
				

		/** 
			 * To do
			 * Process the requests from a client. In particular, 
			 * (1) Check the request type, only process GET request and ignore others
             * (2) Write log. 			 * 
			 * (3) If the url of GET request has been cached, respond with cached content
			 * (4) Otherwise, call method proxyServertoClient to process the GET request
		*/
		
	}

	//This server to client means web server to client(Request handler) not proxyserver to client
	private void proxyServertoClient(byte[] clientRequest) {

		FileOutputStream fileWriter = null;
		Socket toWebServerSocket = null;
		InputStream inFromServer;
		OutputStream outToServer;
		String requestString = new String(clientRequest,StandardCharsets.UTF_8);
		String[] token = requestString.split(" ");
		String requestURL = token[1];
		String[] preHost = requestURL.split("/");
		String host = preHost[2];
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		File cacheFile = new File(fileName);

		



		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		System.out.println("+++++++++++++++++++++++++++BEFORE THE TRY BLOCK OF PROXYSERVERTOCLIENT ++++++++++++++++++++++++++++");
		
		
		try{
			if(!cacheFile.exists()){
				
				cacheFile.createNewFile();
				System.out.println("Created Cache file");
			}
			FileWriter wr = new FileWriter(fileName);

			// connect to the web server, host name
			toWebServerSocket = new Socket(host, 80);		  //java.net.UnknownHostException http://detectportal.firefox.com/canonical.html
																												 //Now no error, but don't make it to "did we make it here?" statement
																												 //Captured hostname detectportal.firefox.com
																												 
			//write to the server																			//while loop here? Eric-I think so? I Think we did what we did above where we read for the entirety of the response?
			outToServer = toWebServerSocket.getOutputStream();
			//recieve response
			inFromServer = toWebServerSocket.getInputStream();


			System.out.println("Pre-While");
			int counter = 0;

			while((counter = inFromServer.read(serverReply))!= -1){
				System.out.println("in while");
				outToServer.write(clientRequest);
				System.out.println("done while");
			}
			
			outToServer.flush();
			//cache it 
			//cacheFile.write(serverReply);
			


			//Write bytes to file
			fileWriter = new FileOutputStream(fileName);
			fileWriter.write(serverReply);
			System.out.println("did we make it here?");		//The answer was no :(
			//write to cache
			sendCachedInfoToClient(fileName);
			server.putCache(host, fileName);
			//fileWriter.write(clientRequest, true);
			fileWriter.close();
			toWebServerSocket.close();
		}
		catch(Exception e){ e.printStackTrace(); }
		


		/** I think we do this to do first
		 * To do
		 * (1) Create a socket to connect to the web server (default port 80)
		 * (2) Send client's request (clientRequest) to the web server, you may want to use flush() after writing.
		 * (3) Use a while loop to read all responses from web server and send back to client
		 * (4) Write the web server's response to a cache file, put the request URL and cache file name to the cache Map
		 * (5) close file, and sockets.
		*/
		
	}
	
	
	
	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {

		try {

			byte[] bytes = Files.readAllBytes(Paths.get(fileName));

			outToClient.write(bytes);
			outToClient.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			if (clientSocket != null) {
				clientSocket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	
	// Generates a random file name  
	public String generateRandomFileName() {

		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}

}
