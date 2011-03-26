package de.felixbruns.jotify.gateway.handlers;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.felixbruns.jotify.gateway.GatewayConnection;
import de.felixbruns.jotify.gateway.GatewayApplication;
import de.felixbruns.jotify.gateway.GatewayHandler;

public class PlaylistsHandler extends GatewayHandler {
	@Override
	public String handle(Map<String, String> params){
		/* Check if required parameters are present. */
		if(params.containsKey("session")){
			String session = params.get("session");
			
			/* Check if session is valid. */
			if(GatewayApplication.sessions.containsKey(session)){
				GatewayConnection jotify = GatewayApplication.sessions.get(session);
				
				/* Get playlists. */
				try{
					String pl = jotify.playlistContainer();

					System.out.println("Load playlist container debug: ");
					System.out.println(pl);
					return pl;
				}
				catch(TimeoutException e){
					e.printStackTrace();
					return "<error>" + e.getMessage() + "</error>";
				}catch(Exception e){
					e.printStackTrace();
					return "<error>" + e.getMessage() + "</error>";
				}
			}
			else{
				return "<error>Session not found!</error>";
			}
		}
		else{
			return "<error>Invalid request parameters!</error>";
		}
	}
}