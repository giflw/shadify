package de.felixbruns.jotify.gateway;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import de.felixbruns.jotify.gateway.handlers.*;

public class GatewayApplication {
	public static Map<String, GatewayConnection> sessions;
	public static ExecutorService                executor;
	
	/* Statically create session map and executor for sessions. */
	static {
		sessions = new HashMap<String, GatewayConnection>();
		executor = Executors.newCachedThreadPool();
	}
	
	public static double VERSION = 2.0;
	
	/* Main thread to listen for client connections. */
	public static void main(String[] args) throws IOException {
		
		System.out.println("Starting Shadify "+VERSION+" by Peec & Strife.");
		System.out.println("Thanks for using Shadify.");
		System.out.println("Usage: java -jar spotifyserver.jar [port]");
		
		
		
		// DEFAULT is port 8080!
		int port = 8080;
		
		if(args.length == 1){
			port = Integer.parseInt(args[0]);
		}

		//
		/* Show port. */
		System.out.println("Running Shadify on port " + port);
		
		
		
		/* Get host name.*/
		try {
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    byte[] ipAddr = addr.getAddress();

		    
		    String stringIP = InetAddress.getByAddress(ipAddr).getHostAddress();
		    System.out.println("Access it at: \nhttp://" + stringIP+":"+port+"");
		} catch (UnknownHostException e) {
			System.out.println("Access it at http://localhost:"+port);
		}

		System.out.println("Waiting for requests on http://localhost:"+port+" ....... ");
		
		
		/* Create a HTTP server that listens for connections on port 8080 or the given port. */
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
		/* Set up content handlers. */
		server.createContext("/",       new ContentHandler());
		server.createContext("/images", new ContentHandler());
		
		/* Set up gateway handlers. */
		server.createContext("/start",     new StartHandler());
		server.createContext("/check",     new CheckHandler());
		server.createContext("/login",     new LoginHandler());
		server.createContext("/close",     new CloseHandler());
		server.createContext("/user",      new UserHandler());
		server.createContext("/toplist",   new ToplistHandler());
		server.createContext("/search",    new SearchHandler());
		server.createContext("/image",     new ImageHandler());
		server.createContext("/browse",    new BrowseHandler());
		
		// Playlists are currently not supported!
		server.createContext("/playlist",  new PlaylistHandler());
		server.createContext("/playlists", new PlaylistsHandler());
		// <---
		
		server.createContext("/stream",    new StreamHandler());
		
		// Volume handler for server.
		server.createContext("/volume",    new VolumeHandler());
		

		
		/* Play on server. */
		server.createContext("/play",   new PlayHandler());
		server.createContext("/pause",  new PauseHandler());
		server.createContext("/stop",   new StopHandler());
		//server.createContext("/volume", new VolumeHandler());
		
		/* Set executor for server threads. */
		server.setExecutor(executor);
		
		/* Start HTTP server. */
		server.start();
	}
}
