package spotify.playback.data.help;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.CurrentlyPlayingType;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Context;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;

import spotify.bot.api.SpotifyCall;
import spotify.playback.data.PlaybackInfoDTO;

public class PlaybackInfoUtils {

	/**
	 * Get the year of the currently playing's release date (which is in ISO format,
	 * so it's always the first four characters).
	 * 
	 * @param track the track
	 * @return the year, "LOCAL" if no year was found
	 */
	public static String findReleaseYear(Track track) {
		if (track.getAlbum().getReleaseDate() != null) {
			return track.getAlbum().getReleaseDate().substring(0, 4);
		}
		return "LOCAL";
	}

	/**
	 * Get the name of the currently playing context (either a playlist name, an
	 * artist, or an album). Only works on Tracks.
	 * 
	 * @param info       the context info
	 * @param previous   the previous context string (used for fast comparison)
	 * @param spotifyApi the api accessor to use
	 * @return a String of the current context, null if none was found
	 */
	public static String findContextName(CurrentlyPlayingContext info, String previous, SpotifyApi spotifyApi) {
		Context context = info.getContext();
		if (context != null && !context.toString().equals(previous) && info.getCurrentlyPlayingType().equals(CurrentlyPlayingType.TRACK)) {
			ModelObjectType type = context.getType();
			switch (type) {
				case PLAYLIST:
					String playlistId = context.getHref().replace(PlaybackInfoConstants.PLAYLIST_PREFIX, "");
					Playlist contextPlaylist = SpotifyCall.execute(spotifyApi.getPlaylist(playlistId));
					if (contextPlaylist != null) {
						return contextPlaylist.getName();
					}
					break;
				case ARTIST:
					String artistId = context.getHref().replace(PlaybackInfoConstants.ARTIST_PREFIX, "");
					Artist contextArtist = SpotifyCall.execute(spotifyApi.getArtist(artistId));
					if (contextArtist != null) {
						return contextArtist.getName();
					}
					break;
				case ALBUM:
					String albumId = context.getHref().replace(PlaybackInfoConstants.ALBUM_PREFIX, "");
					Album contextAlbum = SpotifyCall.execute(spotifyApi.getAlbum(albumId));
					if (contextAlbum != null) {
						return contextAlbum.getName();
					}
					break;
				default:
					break;
			}
		}
		return null;
	}

	/**
	 * Find the largest image (width x height, because not all images are squares)
	 * of a given array of images.
	 * 
	 * @param images to check
	 * @return URL of the largest image, null if no image was given
	 */
	public static String findLargestImage(Image[] images) {
		Image largest = null;
		for (Image img : images) {
			if (largest == null || (img.getWidth() * img.getHeight()) > (largest.getWidth() * largest.getHeight())) {
				largest = img;
			}
		}
		return largest != null ? largest.getUrl() : null;
	}

	/**
	 * Guess the ellapsed progress of the current song. Return true if it's still
	 * within tolerance.
	 * 
	 * @param previous the previous info
	 * @param current the current info
	 * @return true if it's within tolerance
	 */
	public static boolean isWithinEstimatedProgressMs(PlaybackInfoDTO previous, PlaybackInfoDTO current) {
		int expectedProgressMs = previous.getTimeCurrent() + PlaybackInfoConstants.INTERVAL_MS;
		int actualProgressMs = current.getTimeCurrent();
		return Math.abs(expectedProgressMs - actualProgressMs) < PlaybackInfoConstants.ESTIMATED_PROGRESS_TOLERANCE_MS;
	}
}