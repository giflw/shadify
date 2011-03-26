package de.felixbruns.jotify.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.felixbruns.jotify.Jotify;
import de.felixbruns.jotify.exceptions.AuthenticationException;
import de.felixbruns.jotify.exceptions.ConnectionException;
import de.felixbruns.jotify.gui.listeners.ControlListener;
import de.felixbruns.jotify.gui.listeners.JotifyBroadcast;
import de.felixbruns.jotify.gui.listeners.PlayerListener.Status;
import de.felixbruns.jotify.media.File;
import de.felixbruns.jotify.media.Track;
import de.felixbruns.jotify.player.PlaybackListener;

/*
 * This player backend listens for control events and playback events,
 * so it can forward them to the Jotify instance. It also handles the
 * playback queue.
 */
public class JotifyPlayer implements ControlListener, PlaybackListener {
	private JotifyBroadcast        broadcast;
	private JotifyPlaybackQueue    queue;
	private List<PlaybackListener> listeners;
	private float                  volume;
	
	private final Jotify jotify;
	
	public JotifyPlayer(final Jotify jotify) throws ConnectionException, AuthenticationException {
		this.broadcast = JotifyBroadcast.getInstance();
		this.queue     = new JotifyPlaybackQueue();
		this.listeners = new ArrayList<PlaybackListener>();
		this.jotify    = jotify;
		this.volume    = 1.0f;
	}
	
	public void addPlaybackListener(PlaybackListener listener){
		this.listeners.add(listener);
	}
	
	@Override
	public void controlPlay(){
		if(this.queue.hasCurrent()){
			this.jotify.play();
			
			this.broadcast.firePlayerStatusChanged(Status.PLAY);
		}
		else{
			this.controlNext();
		}
	}
	
	@Override
	public void controlPause(){
		this.jotify.pause();
		
		this.broadcast.firePlayerStatusChanged(Status.PAUSE);
	}
	
	@Override
	public void controlPrevious(){
		if(this.queue.hasPrevious()){
			Track track = this.queue.previous();
			
			this.jotify.stop();
			
			try{
				this.jotify.play(track, File.BITRATE_160, this);
				this.jotify.volume(this.volume);
				
				this.broadcast.firePlayerTrackChanged(track);
				this.broadcast.fireQueueUpdated(this.queue);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void controlNext(){
		if(this.queue.hasNext()){
			Track track = this.queue.next();
			
			this.jotify.stop();
			
			try{
				this.jotify.play(track, File.BITRATE_160, this);
				this.jotify.volume(this.volume);
				
				this.broadcast.firePlayerTrackChanged(track);
				this.broadcast.firePlayerStatusChanged(Status.PLAY);
				this.broadcast.fireQueueUpdated(this.queue);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void controlVolume(float volume){
		this.volume = volume;
		
		this.jotify.volume(volume);
	}
	
	@Override
	public void controlSeek(float percent){
		try{
			this.jotify.seek((int)(percent * this.jotify.length()));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void controlSelect(Track track){
		if(!this.queue.hasCurrent()){
			this.queue.add(track);
		}
		
		this.broadcast.fireQueueUpdated(this.queue);
	}
	
	@Override
	public void controlSelect(List<Track> tracks){
		this.queue.clear();
		this.queue.addAll(tracks);
		
		this.broadcast.fireQueueUpdated(this.queue);
	}
	
	@Override
	public void controlQueue(Track track){
		this.queue.queue(track);
		
		this.broadcast.fireQueueUpdated(this.queue);
	}
	
	@Override
	public void playbackFinished(Track track){
		this.controlNext();
		
		for(PlaybackListener listener : this.listeners){
			listener.playbackFinished(track);
		}
	}
	
	@Override
	public void playbackPosition(Track track, int position){
		this.broadcast.firePlayerPositionChanged(position);
		
		for(PlaybackListener listener : this.listeners){
			listener.playbackPosition(track, position);
		}
	}
	
	@Override
	public void playbackStarted(Track track){
		for(PlaybackListener listener : this.listeners){
			listener.playbackStarted(track);
		}
	}
	
	@Override
	public void playbackStopped(Track track){
		for(PlaybackListener listener : this.listeners){
			listener.playbackStopped(track);
		}
	}
	
	@Override
	public void playbackResumed(Track track) {
		for(PlaybackListener listener : this.listeners){
			listener.playbackResumed(track);
		}
	}
}
