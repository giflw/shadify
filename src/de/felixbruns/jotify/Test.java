package de.felixbruns.jotify;

import java.util.List;
import de.felixbruns.jotify.exceptions.AuthenticationException;

import de.felixbruns.jotify.media.File;
import de.felixbruns.jotify.media.Playlist;
import de.felixbruns.jotify.media.PlaylistContainer;
import de.felixbruns.jotify.media.Track;
import de.felixbruns.jotify.media.User;
import de.felixbruns.jotify.player.PlaybackAdapter;
import java.util.concurrent.TimeoutException;

import de.felixbruns.jotify.gui.listeners.JotifyBroadcast;

/**
 * A small commandline Spotify client using jotify.
 * 
 * @author Felix Bruns <felixbruns@web.de>
 * 
 * @category Example
 */
public class Test {
	/* Create a connection to Spotify. */
	private static JotifyConnection jotify = new JotifyConnection();
	
	/* Current list of tracks. */
	private static List<Track> tracks = null;
	
	/* Current track. */
	private static int position = -1;
	
	static private JotifyBroadcast   broadcast;
	
	/**
	 * Main method.
	 * 
	 * @param args Commandline arguments.
	 * 
	 * @throws Exception Because I don't want to catch all of them :-P
	 */
	public static void main(String[] args) throws Exception {
		broadcast = JotifyBroadcast.getInstance();	
		
		String username = "";
		String password = "";
		try{
			jotify.login(username, password);
			
			System.out.println("Logged in! Press enter to see available commands.\n");
			
		}catch(AuthenticationException e){
			System.out.println("Invalid username and/or password! Try again.\n");
		}
		
		/* Get and print some user info. */
		User user = jotify.user();
		
		System.out.println(user);
		
		/* Check if user has a premium account. */
		if(!user.isPremium()){
			System.err.println("\n\nWARNING: You don't have a premium account! Jotify will NOT work!\n");
		}
		
		
		/* Get information about account playlists. */
		PlaylistContainer playlists = null;
		
		
		while(true){
			try{
				playlists = jotify.playlistContainer();
				
				break;
			}
			catch(TimeoutException e){
				continue;
			}
		}
		
		/* Fire playlist added events. */
		for(Playlist playlist : playlists){
			broadcast.firePlaylistAdded(playlist);
		}
		
		/* Get details for each playlist. */
		for(Playlist playlist : playlists){
			/* Get playlist details. */
			while(true){
				try{
					playlist = jotify.playlist(playlist.getId(), false);
					
					break;
				}
				catch(TimeoutException e){
					continue;
				}
			}
			
			/* If playlist contains tracks, browse for track information. */
			if(!playlist.getTracks().isEmpty()){
				int totalTracks = playlist.getTracks().size();
				int numTracks   = 200;
				int numRequests = (totalTracks / numTracks) + 1;
				
				/* Browse for 200 tracks at a time tracks and add them to the playlist. */
				for(int i = 0; i < numRequests; i++){
					List<Track> tracks = null;
					
					while(true){
						try{
							tracks = jotify.browse(
								playlist.getTracks().subList(
									i * numTracks,
									Math.min((i + 1) * numTracks, totalTracks)
								)
							);
							
							break;
						}
						catch(TimeoutException e){
							continue;
						}
					}
					
					/* Add track information to playlist (also works with duplicate tracks). */
					for(Track track : tracks){
							for(int j = 0; j < playlist.getTracks().size(); j++){
								if(track.equals(playlist.getTracks().get(j))){
									playlist.getTracks().set(j, track);
								}
							}
						}
				}
			}
			
			
		}
		
		int i = 0;
		Track track = new Track("spotify:track:4SpK8v76Z0ROCjqYgAkOcp");
		
		jotify.browse(track);
		for(Playlist playlist : playlists){
			i++;
			playlist.getTracks().add(track);
			broadcast.firePlaylistUpdated(playlist);
			
		}
		System.out.println(i + "playlists available.");
		
		
		jotify.stop();
		jotify.close();
		
		System.exit(0);
	}
	
	public static void play(int i) throws Exception {
		/* Check position in current track list. */
		if(i >= 0 && i < tracks.size()){
			/* Stop if something is already playing. */
			jotify.stop();
			
			/* Load metadata (files etc.) for track. */
			Track track = jotify.browse(tracks.get(i));		
			
			if(track == null){
				throw new Exception("Browsing track failed!");
			}
			
			/* Print artist and title. */
			System.out.format(
				"Playing: %s - %s\n",
				track.getArtist().getName(),
				track.getTitle()
			);
			
			/* Start playing (without a PlaybackListener). */
			jotify.play(track, File.BITRATE_160, new PlaybackAdapter(){
				@Override
				public void playbackFinished(Track track){
					// Do NOTHING.
				}
			});
		}
		else{
			System.out.format("Position '%d' not available in current track list!\n", position);
		}
	}
}
