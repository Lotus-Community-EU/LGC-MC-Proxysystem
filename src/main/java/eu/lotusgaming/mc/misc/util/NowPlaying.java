//Created by Maurice H. at 11.05.2025
package eu.lotusgaming.mc.misc.util;

public class NowPlaying {
	
	private final String track, artist, trackId;
	private final boolean isPlaying, isLocalTrack;
	
	public NowPlaying(String track, String artist, boolean isPlaying, String trackId, boolean isLocalTrack) {
		this.track = track;
        this.artist = artist;
        this.isPlaying = isPlaying;
        this.trackId = trackId;
		this.isLocalTrack = isLocalTrack;
	}
	
	public String getArtist() {
		return this.artist;
	}
	
	public String getTrack() {
		return this.track;
	}

	public String getTrackId() {
		return this.trackId;
	}
	
	public boolean isPlaying() {
		return this.isPlaying;
	}

	public boolean isLocalTrack() {
		return this.isLocalTrack;
	}
}