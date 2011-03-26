package de.felixbruns.jotify.async;

import java.awt.Image;
import java.util.List;

import de.felixbruns.jotify.exceptions.AuthenticationException;
import de.felixbruns.jotify.exceptions.ConnectionException;
import de.felixbruns.jotify.exceptions.ProtocolException;
import de.felixbruns.jotify.media.Album;
import de.felixbruns.jotify.media.Artist;
import de.felixbruns.jotify.media.Playlist;
import de.felixbruns.jotify.media.PlaylistContainer;
import de.felixbruns.jotify.media.Result;
import de.felixbruns.jotify.media.Track;
import de.felixbruns.jotify.media.User;

public class AsyncJotifyAdapter implements AsyncJotifyListener {
	@Override
	public void loggedIn(){}
	@Override
	public void loggedOut(){}
	
	@Override
	public void receivedUserData(User user){}
	
	@Override
	public void receivedToplist(Result toplist, Object userdata){}
	@Override
	public void receivedSearchResult(Result result, Object userdata){}
	@Override
	public void receivedImage(Image image, Object userdata){}
	@Override
	public void receivedArtist(Artist artist, Object userdata){}
	@Override
	public void receivedAlbum(Album album, Object userdata){}
	@Override
	public void receivedTracks(List<Track> tracks, Object userdata){}
	@Override
	public void receivedReplacementTracks(List<Track> tracks, Object userdata){}
	
	@Override
	public void receivedPlaylistContainer(PlaylistContainer container){}
	@Override
	public void receivedPlaylist(Playlist playlist){}
	@Override
	public void receivedPlaylistUpdate(String id){}
	
	@Override
	public void receivedException(ConnectionException e){}
	@Override
	public void receivedException(ProtocolException e){}
	@Override
	public void receivedException(AuthenticationException e){}
}
