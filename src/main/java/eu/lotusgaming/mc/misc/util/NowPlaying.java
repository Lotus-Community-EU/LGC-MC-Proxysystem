//Created by Maurice H. at 11.05.2025
package eu.lotusgaming.mc.misc.util;

public class NowPlaying {
	
	private final String track, artist, trackId;
	private final boolean isPlaying, isLocalTrack;
	private final long progressMs, durationMs;
	
	public NowPlaying(String track, String artist, boolean isPlaying, String trackId, boolean isLocalTrack, long progressMs, long durationMs) {
		this.track = track;
        this.artist = artist;
        this.isPlaying = isPlaying;
        this.trackId = trackId;
		this.isLocalTrack = isLocalTrack;
		this.progressMs = progressMs;
		this.durationMs = durationMs;
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

	public long getProgressMs() {
		return this.progressMs;
	}

	public long getDurationMs() {
		return this.durationMs;
	}

	public long getRemainingMs() {
		return this.durationMs - this.progressMs;
	}
}