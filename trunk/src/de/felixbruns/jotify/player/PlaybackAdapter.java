package de.felixbruns.jotify.player;

import de.felixbruns.jotify.media.Track;

public class PlaybackAdapter implements PlaybackListener {
	@Override
	public void playbackStarted(Track track){}
	@Override
	public void playbackStopped(Track track){}
	@Override
	public void playbackResumed(Track track){}
	@Override
	public void playbackPosition(Track track, int ms){}
	@Override
	public void playbackFinished(Track track){}
}
