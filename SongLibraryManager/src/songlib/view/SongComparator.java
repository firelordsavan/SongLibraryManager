package songlib.view;

// Savan Patel
// Naveenan Yogeswaran

import songlib.app.*;
import java.util.Comparator;

public class SongComparator implements Comparator<Song>{
	public int compare(Song first, Song second) {
		int comparison = first.getName().toLowerCase().compareTo(second.getName().toLowerCase());
		if(comparison != 0) {
			return comparison;
		}
		return first.getArtist().toLowerCase().compareTo(second.getArtist().toLowerCase());
	}
}
