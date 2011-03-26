package de.felixbruns.jotify.gateway.handlers;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.felixbruns.jotify.gateway.GatewayConnection;
import de.felixbruns.jotify.gateway.GatewayApplication;
import de.felixbruns.jotify.gateway.GatewayHandler;
import de.felixbruns.jotify.util.Hex;

public class PlaylistHandler extends GatewayHandler {
	@Override
	public String handle(Map<String, String> params){
		/* Check if required parameters are present. */
		if(params.containsKey("session") && params.containsKey("id")){
			String session = params.get("session");
			String id      = params.get("id");
			
			/* Check if session is valid. */
			if(GatewayApplication.sessions.containsKey(session)){
				GatewayConnection jotify = GatewayApplication.sessions.get(session);
				
				/* Get playlist. */
				try{
					if (Hex.isHex(id))return jotify.playlist(id);
					else throw new Exception("Playlist is not hex");
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
