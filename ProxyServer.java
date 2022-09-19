
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class ProxyServer {

	//cache is a Map: the key is the URL and the value is the file name of the file that stores the cached content
	Map<String, String> cache;
	
	ServerSocket proxySocket;

	String logFileName = "log.txt";

	public static void main(String[] args) {
		new ProxyServer().startServer(Integer.parseInt(args[0]));
	}

	void startServer(int proxyPort) {

		cache = new ConcurrentHashMap<>();

		// create the directory to store cached files. 
		File cacheDir = new File("cached");
		if (!cacheDir.exists() || (cacheDir.exists() && !cacheDir.isDirectory())) {
			cacheDir.mkdirs();
		}

		while(true){
			
			ServerSocket serverSocket = null;
			Socket clientSocket = null;
			// DataInputStream din = null;
			// DataOutputStream dout =  null;
			System.out.println("Server Started");

			try{
				
				serverSocket = new ServerSocket(proxyPort);
				System.out.println("Waiting for Client");
				clientSocket = new ServerSocket().accept(); //I believe that this is what is creating a new user
				System.out.println("Client Accepted");
				// Assign new thread
				Thread thread = new RequestHandler(clientSocket,this);
				thread.start();
	
			}catch(IOException e){
				System.out.println("Expection Caught on port: " + proxyPort + "or listening for a connection.");
			}

			try{
			serverSocket.close();
			} catch(IOException e){

			}
		}

		/*
			 * To do:
			 * create a serverSocket to listen on the port (proxyPort)                 
			 * Create a thread (RequestHandler) for each new client connection        
			 * remember to catch Exceptions!                                           
			 *
		*/
		
		
	}



	public String getCache(String hashcode) {
		return cache.get(hashcode);
	}

	public void putCache(String hashcode, String fileName) {
		cache.put(hashcode, fileName);
	}

	public synchronized void writeLog(String info) {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

			try{
				FileWriter myWriter = new FileWriter(logFileName);
				myWriter.write(info + timeStamp);
				myWriter.close();
			}catch(IOException e){
				System.out.println(e + "File Not Found");
			}
			/*
			 * To do
			 * write string (info) to the log file, and add the current time stamp 
			 * e.g. String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			 *
			*/
	}

}

