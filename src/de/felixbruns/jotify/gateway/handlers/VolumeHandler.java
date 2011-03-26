package de.felixbruns.jotify.gateway.handlers;

import java.util.Map;

import de.felixbruns.jotify.gateway.GatewayConnection;
import de.felixbruns.jotify.gateway.GatewayApplication;
import de.felixbruns.jotify.gateway.GatewayHandler;


public class VolumeHandler extends GatewayHandler {
	@Override
	public String handle(Map<String, String> params){
		
		/* Check if required parameters are present. */
		if(params.containsKey("session") && params.containsKey("level")){
			String session = params.get("session");
			float level = Float.parseFloat(params.get("level"));
			/* Check if session is valid. */
			if(GatewayApplication.sessions.containsKey(session)){
				GatewayConnection jotify = GatewayApplication.sessions.get(session);
				
				jotify.volume(level);
				
				return "<success />";
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
