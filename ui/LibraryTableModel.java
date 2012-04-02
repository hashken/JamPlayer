package iitm.apl.player.ui;

import iitm.apl.player.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for a library
 * 
 */
public class LibraryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 8230354699902953693L;

	// TODO: Change to your implementation of Trie/BK-Tree
	private Vector<Song> songListing;
	private int songIteratorIdx;
	private Song currentSong;
	private Iterator<Song> songIterator;

	LibraryTableModel() {
		songListing = new Vector<Song>();
		songIterator = songListing.iterator();
	}
	public void sort(){
		//Collections.sort(songListing);
	}

	public Vector<Song> getSong(){
		return songListing;
	}
	public void add(Song song) {
		songListing.add(song);
		resetIdx();
		fireTableDataChanged();
	}
	public void add(ArrayList<Song> array) {
		for(int i=0; i<array.size();i++){
		add(array.get(i));
		}
		resetIdx();
		fireTableDataChanged();
	}

	public void add(Vector<Song> songs) {
		songListing.addAll(songs);
		resetIdx();
		fireTableDataChanged();
	}

	public void filter(String searchString) {
		// TODO: Connect the searchText keyPressed handler to update the filter
		// here.
	}
	
	public void resetIdx()
	{
		songIteratorIdx = -1;
		currentSong = null;
		songIterator = songListing.iterator();
	}
	// Gets the song at the currently visible index
	public Song get(int idx) {
		if( songIteratorIdx == idx )
			return currentSong;
		
		if(songIteratorIdx > idx)
		{
			resetIdx();
		}
		while( songIteratorIdx < idx && songIterator.hasNext() )
		{
			currentSong = songIterator.next();
			songIteratorIdx++;
		}
		return currentSong;
	}
	public void deleteAll(){
			songListing.clear();
	}
	@Override
	public int getColumnCount() {
		// Title, Album, Artist, Duration.
		return 4;
	}

	@Override
	public int getRowCount() {
		// TODO: Changes if you've filtered the list
		return songListing.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO: Get the appropriate row
		Song song = get(row);
		if(song == null) return null;

		switch (col) {
		case 0: // Title
			return song.getTitle();
		case 1: // Album
			return song.getAlbum();
		case 2: // Artist
			return song.getArtist();
		case 3: // Duration
			int duration = song.getDuration();
			int mins = duration / 60;
			int secs = duration % 60;
			return String.format("%d:%2d", mins, secs);
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: // Title
			return "Title";
		case 1: // Album
			return "Album";
		case 2: // Artist
			return "Artist";
		case 3: // Duration
			return "Duration";
		default:
			return super.getColumnName(column);
		}
	}

}