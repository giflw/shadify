package de.felixbruns.jotify.protocol.channel;

public class ChannelAdapter implements ChannelListener {
	@Override
	public void channelHeader(Channel channel, byte[] header){}
	@Override
	public void channelData(Channel channel, byte[] data){}
	@Override
	public void channelError(Channel channel){}
	@Override
	public void channelEnd(Channel channel){}
}
