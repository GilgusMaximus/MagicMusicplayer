
/**
 * Copyright (C) 2001 Jonathan Hilliker
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Description: 
 *  This class keeps track of all the genre numbers and their corresponding
 *  Strings based on the ID3 and Nullsoft standards.
 *
 * @author:  Jonathan Hilliker
 * @version: $Id: NullsoftID3GenreTable.java,v 1.3 2001/12/04 05:16:38 helliker Exp $
 * Revsisions: 
 *  $Log: NullsoftID3GenreTable.java,v $
 *  Revision 1.3  2001/12/04 05:16:38  helliker
 *  Added some nullsoft genres I missed.
 *
 *  Revision 1.2  2001/10/19 03:57:53  helliker
 *  All set for release.
 *
 *
 */

package helliker.id3;

public final class NullsoftID3GenreTable {

    private final static String[] genres = {
	"Blues"
	, "Classic Rock"
	, "Country"
	, "Dance"
	, "Disco"
	, "Funk"
	, "Grunge"
	, "Hip-Hop"
	, "Jazz"
	, "Metal"
	, "New Age"
	, "Oldies"
	, "Other"
	, "Pop"
	, "R&B"
	, "Rap"
	, "Reggae"
	, "Rock"
	, "Techno"
	, "Industrial"
	, "Alternative"
	, "Ska"
	, "Death Metal"
	, "Pranks"
	, "Soundtrack"
	, "Euro-Techno"
	, "Ambient"
	, "Trip-Hop"
	, "Vocal"
	, "Jazz+Funk"
	, "Fusion"
	, "Trance"
	, "Classical"
	, "Instrumental"
	, "Acid"
	, "House"
	, "Game"
	, "Sound Clip"
	, "Gospel"
	, "Noise"
	, "AlternRock"
	, "Bass"
	, "Soul"
	, "Punk"
	, "Space"
	, "Meditative"
	, "Instrumental Pop"
	, "Instrumental Rock"
	, "Ethnic"
	, "Gothic"
	, "Darkwave"
	, "Techno-Industrial"
	, "Electronic"
	, "Pop-Folk"
	, "Eurodance"
	, "Dream"
	, "Southern Rock"
	, "Comedy"
	, "Cult"
	, "Gangsta"
	, "Top 40"
	, "Christian Rap"
	, "Pop/Funk"
	, "Jungle"
	, "Native American"
	, "Cabaret"
	, "New Wave"
	, "Psychadelic"
	, "Rave"
	, "Showtunes"
	, "Trailer"
	, "Lo-Fi"
	, "Tribal"
	, "Acid Punk"
	, "Acid Jazz"
	, "Polka"
	, "Retro"
	, "Musical"
	, "Rock & Roll"
	, "Hard Rock"
	, "Folk"
	, "Folk-Rock"
	, "National Folk"
	, "Swing"
	, "Fast Fusion"
	, "Bebob"
	, "Latin"
	, "Revival"
	, "Celtic"
	, "Bluegrass"
	, "Avantgarde"
	, "Gothic Rock"
	, "Progressive Rock"
	, "Psychedelic Rock"
	, "Symphonic Rock"
	, "Slow Rock"
	, "Big Band"
	, "Chorus"
	, "Easy Listening"
	, "Acoustic"
	, "Humour"
	, "Speech"
	, "Chanson"
	, "Opera"
	, "Chamber Music"
	, "Sonata"
	, "Symphony"
	, "Booty Brass"
	, "Primus"
	, "Porn Groove"
	, "Satire"
	, "Slow Jam"
	, "Club"
	, "Tango"
	, "Samba"
	, "Folklore"
	, "Ballad"
	, "Power Ballad"
	, "Rhythmic Soul"
	, "Freestyle"
	, "Duet"
	, "Punk Rock"
	, "Drum Solo"
	, "A Capela"
	, "Euro-House"
	, "Dance Hall"
	, "Goa" 
	, "Drum & Bass" 
	, "Club-House" 
	, "Hardcore" 
	, "Terror" 
	, "Indie" 
	, "BritPop" 
	, "Negerpunk" 
	, "Polsk Punk" 
	, "Beat" 
	, "Christian Gangsta Rap" 
	, "Heavy Metal" 
	, "Black Metal" 
	, "Crossover" 
	, "Contemporary Christian" 
	, "Christian Rock" 
	, "Merengue" 
	, "Salsa" 
	, "Thrash Metal" 
	, "Anime" 
	, "JPop" 
	, "SynthPop" 
    };
  
    /**
     * Return the corresponding String for the integer coded provided.  Returns
     * null if the code returned is invalid (less than 0 or greater than 125).
     *
     * @param i the genre code
     * @return the genre String or null if the genre code is invalid
     */
    public static String getGenre( int i ) {
	String str = null;

	if( (i < genres.length) && (i >= 0) ) {
	    str = genres[i]; 
	}

	return str;
    }

    /**
     * Tries to find the string provided in the table and returns the
     * corresponding int code if successful.  Returns -1 if the genres is 
     * not found in the table.
     *
     * @param str the genre to search for
     * @return the integer code for the genre or -1 if the genre is not found
     */
    public static int getGenre( String str ) {
	int retval = -1;

	for( int i = 0; (i < genres.length) && (retval == -1); i++ ) {
	    if( genres[i].equalsIgnoreCase( str ) ) {
		retval = i;
	    }
	}

	return retval;
    }

    /**
     * Returns an array of all the genres which can be used to put into an
     * OptionPane or some other component for easy display.
     *
     * @return an array of genres
     */
    public static String[] getGenres() {
	return genres;
    }
    
    
} // NullsoftID3GenreTable
