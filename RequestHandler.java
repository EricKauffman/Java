import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;

// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread {

	
	Socket clientSocket;
	InputStream inFromClient;
	OutputStream outToClient;
	byte[] request = new byte[1024];

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
	
	public void run() {

		//get the input string
		//read the string with buffer reader?
		//if get, then get the url
		//Write the url to log 
		//Check if in cache, if respond with data, if not write to cache
		//Process with proxyServertoCLient
		
		while(true){
			try{
				//output string
				DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
				//read the data from the client socket.
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String results = br.readLine();
	
				String[] token = results.split(" ");
				String url = token[1];
	
				//check if its a get request
				if(token[0] == "GET"){
					//Write to the log
					server.writeLog(url);
	
					String cache = server.getCache(url);
					if(cache.length()>=1){
						System.out.println(cache);
					} else {
						byte[] clientRequest = results.getBytes();
						proxyServertoClient(clientRequest);
					}
	
				} 
			
			}catch(IOException e){
	
	
			}
		}
		
		

		/** I think we do this to do second
			 * To do
			 * Process the requests from a client. In particular, 
			 * (1) Check the request type, only process GET request and ignore others
             * (2) Write log. 			 * 
			 * (3) If the url of GET request has been cached, respond with cached content
			 * (4) Otherwise, call method proxyServertoClient to process the GET request
			 * 
			 * 
			 * Questions: What is the response from the get request
			 * What format is the request in, is it already in bytes? 
			 * If so , then we are able to pass that to the proxyServertoClient method
			 * 
			 * How do we run this?
			 * 
			 * How are thes methods being called? Im assuming we need to be implementing it into each of these code blocks
			 * I
			 *
		*/

	}

	//This server to client means web server to client(Request handler) not proxyserver to client
	private void proxyServertoClient(byte[] clientRequest) {

		FileOutputStream fileWriter = null;
		Socket toWebServerSocket = null;
		InputStream inFromServer;
		OutputStream outToServer;
		String requestURL = clientRequest.toString();
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		
		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		
		try{
			// connect to the web server
			toWebServerSocket = new Socket(requestURL, 80);
			//write to the server
			outToServer = toWebServerSocket.getOutputStream();
			//recieve response
			inFromServer = toWebServerSocket.getInputStream();
			//serverReply = inFromServer?
			//write to cache
			server.putCache(inFromServer.toString(), fileName);
			// fileWriter.write(clientRequest);
		}
		catch(Exception e){}
		


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