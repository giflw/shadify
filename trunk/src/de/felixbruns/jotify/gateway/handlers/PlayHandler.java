package de.felixbruns.jotify.gateway.handlers;

import java.util.Map;

import de.felixbruns.jotify.gateway.GatewayConnection;
import de.felixbruns.jotify.gateway.GatewayApplication;
import de.felixbruns.jotify.gateway.GatewayHandler;
import de.felixbruns.jotify.media.File;
import de.felixbruns.jotify.media.Track;

public class PlayHandler extends GatewayHandler {
	@Override
	public String handle(Map<String, String> params){
		/* Check if required parameters are present. */
		if(params.containsKey("session")){
			String session = params.get("session");
			
			/* Check if session is valid. */
			if(GatewayApplication.sessions.containsKey(session)){
				GatewayConnection jotify = GatewayApplication.sessions.get(session);
				
				if(params.containsKey("id") && params.containsKey("file")){
					String id      = params.get("id");
					String file    = params.get("file");
					
					System.out.println("Trying to play song." + id + " file " + file + "Sizeof");
					/* Play track. */
					Track track = new Track(id);
						
					track.addFile(new File(file,"Ogg Vorbis,"+File.BITRATE_160));
					
					
					try{
						jotify.stop();
						
						jotify.play(track, File.BITRATE_160, null);
						System.out.println(id + " ... "+file+" ...Playing song" + track.getTitle() + " " + track.getId());
					}
					catch(Exception e){
						System.out.println("Error ");
						e.printStackTrace();
						return "<error>" + e.getMessage() + "</error>";
					}
				}else{
					jotify.play();
				}
				
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
