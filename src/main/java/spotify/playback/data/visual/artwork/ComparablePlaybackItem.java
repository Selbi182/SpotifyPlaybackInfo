package spotify.playback.data.visual.artwork;

import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.Episode;
import com.wrapper.spotify.model_objects.specification.Track;

public class ComparablePlaybackItem implements Comparable<IPlaylistItem> {

	private final IPlaylistItem item;

	ComparablePlaybackItem(IPlaylistItem item) {
		this.item = item;
	}

	@Override
	public int compareTo(IPlaylistItem o) {
		return getItem().getId().compareTo(o.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.getUri().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		String id = null;
		if (obj instanceof Track) {
			id = ((Track) obj).getUri();
		} else if (obj instanceof Episode) {
			id = ((Episode) obj).getUri();
		} else if (obj instanceof ComparablePlaybackItem) {
			id = ((ComparablePlaybackItem) obj).getItem().getUri();
		}
		return this.getItem().getUri().equals(id);
	}

	public IPlaylistItem getItem() {
		return item;
	}
}
