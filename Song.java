package iitm.apl.player;

import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * A song is a collection of metadata about a music file. It contains a link to
 * the file on the hard drive, as well as provide some functions to the tag
 * information of the song.
 */
public class Song {
	/** File on disk */
	private File file;
	/** The audio header, containing information about track length, etc. */
	private AudioHeader header;
	/** Tag information of artist, title, etc. */
	private Tag tag;

	/**
	 * Create a metadata-instance
	 * @param file: The file on disk corresponding to the music file
	 */
	public Song(File file) {
		this.file = file;
		try {
			AudioFile f = AudioFileIO.read(file);
			header = f.getAudioHeader();
			tag = f.getTag();
		} catch (Exception e) {
			header = null;
			tag = null;
		}

	}

	/* Getters */
	public File getFile() {
		return this.file;
	}

	public Tag getTag() {
		return tag;
	}

	public AudioHeader getHeader() {
		return header;
	}

	public String getColumns( int column )
	{
	    if( column == 1 )
	        return getTitle();
	    else if( column == 2 )
	        return getArtist();
	    else if( column == 3 )
	        return getAlbum();
	    else
	        return "";
	}
	
	public String getArtist() 
	{
		 try
		{
		    return tag.getFirst(FieldKey.ARTIST);
		}
		catch( NullPointerException e)
		{
		    return " ";
		}
	}

	public String getTitle() 
	{
		try
		{
		    return tag.getFirst(FieldKey.TITLE);
		}
		catch( NullPointerException e )
		{
		    return " ";
		}
	}

	public String getAlbum() 
	{
		try
		{
		    return tag.getFirst(FieldKey.ALBUM);
		}
		catch( NullPointerException e )
		{
		    return " ";
		}
	}
	
	public int getDuration() {
		return header.getTrackLength();
	}

	public String toString() {
		return getArtist() + " - " + getTitle() + " - " + getAlbum();
	}

}
