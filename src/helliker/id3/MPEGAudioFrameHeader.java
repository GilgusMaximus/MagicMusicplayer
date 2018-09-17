
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
 *  This class reads through the file specified and tries to find an mpeg
 *  frame.  It then reads data from the header of the first frame encountered.
 *
 * @author:  Jonathan Hilliker
 * @version: $Id: MPEGAudioFrameHeader.java,v 1.5 2002/01/22 16:16:23 helliker Exp $
 * Revsisions: 
 *  $Log: MPEGAudioFrameHeader.java,v $
 *  Revision 1.5  2002/01/22 16:16:23  helliker
 *  Made stupid mistake that causes an infininte recursive loop when
 *  reading non-VBR files.
 *
 *  Revision 1.4  2002/01/21 05:05:57  helliker
 *  Added support for VBR files.
 *  Added fields for padding and private bits.
 *  Added getFrameLength method.
 *
 *  Revision 1.3  2001/11/10 07:05:12  helliker
 *  Fixed file handle leaks.
 *  Added accessor to find the offset of the MPEG data.
 *
 *  Revision 1.2  2001/10/19 03:57:53  helliker
 *  All set for release.
 *
 *
 */

package helliker.id3;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class MPEGAudioFrameHeader  {
    
    public final static int MPEG_V_25 = 0;
    public final static int MPEG_V_2 = 2;
    public final static int MPEG_V_1 = 3;
    public final static int MPEG_L_3 = 1;
    public final static int MPEG_L_2 = 2;
    public final static int MPEG_L_1 = 3;
    public final static int MONO_MODE = 3;

    private final int HEADER_SIZE = 4;
    private final int[][] bitrateTable = { 
	{ -1, -1, -1, -1, -1 }, 
	{ 32, 32, 32, 32, 8 },
	{ 64, 48, 40, 48, 16 },
	{ 96, 56, 48, 56, 24 },
	{ 128, 64, 56, 64, 32 },
	{ 160, 80, 64, 80, 40 },
	{ 192, 96, 80, 96, 48 },
	{ 224, 112, 96, 112, 56 },
	{ 256, 128, 112, 128, 64 },
	{ 288, 160, 128, 144, 80 },
	{ 320, 192, 160, 160, 96 },
	{ 352, 224, 192, 176, 112 },
	{ 384, 256, 224, 192, 128 },
	{ 416, 320, 256, 224, 144 },
	{ 448, 384, 320, 256, 160 },
	{ -1, -1, -1, -1, -1 } };
    private final int[][] sampleTable = {
	{ 44100, 22050, 11025 },
	{ 48000, 24000, 12000 },
	{ 32000, 16000, 8000 }, 
	{ -1, -1, -1 } };
    private final String[] versionLabels = { "MPEG Version 2.5", null, 
					     "MPEG Version 2.0", 
					     "MPEG Version 1.0" };
    private final String[] layerLabels = { null, "Layer III", "Layer II", 
					   "Layer I" };
    private final String[] channelLabels = { "Stereo", "Joint Stereo (STEREO)",
					     "Dual Channel (STEREO)", 
					     "Single Channel (MONO)" };
    private final String[] emphasisLabels = { "none", "50/15 ms", null, 
					      "CCIT J.17" };
    private final int[] slotLength = { -1, 8, 8, 32 }; // in bits

    private XingVBRHeader xingHead = null;
    private File mp3 = null;
    private int version;
    private int layer;
    private int bitRate;
    private int sampleRate;
    private int channelMode;
    private boolean copyrighted;
    private boolean crced;
    private boolean original;
    private boolean privateBit;
    private int emphasis;
    private long location;
    private int frameLength;
    private boolean padding;

    /**
     * Create an MPEGAudioFrameHeader from the file specified.  Upon creation
     * information will be read in from the first frame header the object 
     * encounters in the file.
     *
     * @param mp3 the file to read from
     * @exception NoMPEGFramesException if the file is not a valid mpeg
     * @exception FileNotFoundException if an error occurs
     * @exception IOException if an error occurs
     * @exception CorruptHeaderException if an error occurs
     */
    public MPEGAudioFrameHeader( File mp3 ) 
	throws NoMPEGFramesException, FileNotFoundException, IOException,
	       CorruptHeaderException {

	this( mp3, 0 );
    }

    /**
     * Create an MPEGAudioFrameHeader from the file specified.  Upon creation
     * information will be read in from the first frame header the object 
     * encounters in the file.  The offset tells the object where to start
     * searching for an MPEG frame.  If you know the size of an id3v2 tag 
     * attached to the file and pass it to this ctor, it will take less time
     * to find the frame.
     *
     * @param mp3 the file to read from
     * @param offset the offset to start searching from
     * @exception NoMPEGFramesException if the file is not a valid mpeg
     * @exception FileNotFoundException if an error occurs
     * @exception IOException if an error occurs
     * @exception CorruptHeaderException if an error occurs
     */
    public MPEGAudioFrameHeader( File mp3, int offset ) 
	throws NoMPEGFramesException, FileNotFoundException, IOException, 
	       CorruptHeaderException {

	this.mp3 = mp3;

	version = -1;
	layer = -1;
	bitRate = -1;
	sampleRate = -1;
	channelMode = -1;
	copyrighted = false;
	crced = false;
	original = false;
	emphasis = -1;
	location = -1;
	padding = false;

	RandomAccessFile in = null;

	try {
	    in = new RandomAccessFile( mp3, "r" );

	    location = findFrame( in, offset );
	
	    if( location != -1 ) {
		readHeader( in, location );
		xingHead = new XingVBRHeader( in, location, layer, version, 
					      sampleRate, channelMode );
	    }
	    else {
		throw new NoMPEGFramesException();
	    }
	}
	finally {
	    if( in != null ) {
		in.close();
	    }
	}
    }

    /**
     * Searches through the file and finds the first occurrence of an mpeg 
     * frame.  Returns the location of the header of the frame.
     *
     * @param raf the open file to find the frame in
     * @param offset the offset to start searching from
     * @return the location of the header of the frame
     * @exception IOException if an error occurs
     */
    private long findFrame( RandomAccessFile raf, int offset ) 
	throws IOException {

	byte test;
	long loc = -1;
	raf.seek( offset );

	while( loc == -1 ) {
	    test = raf.readByte();

	    if( BinaryParser.matchPattern( test, "11111111" ) ) {
		test = raf.readByte();
		
		if( BinaryParser.matchPattern( test, "111xxxxx" ) ) {
		    loc = raf.getFilePointer() - 2;
		}
	    }
	}

	return loc;
    }

    /**
     * Read in all the information found in the mpeg header.
     *
     * @param raf the open file to find the frame in
     * @param location the location of the header (found by findFrame)
     * @exception CorruptHeaderException if an error occurs
     * @exception IOException if an error occurs
     */
    private void readHeader( RandomAccessFile raf, long location ) 
	throws IOException, CorruptHeaderException {

	byte[] head = new byte[HEADER_SIZE];
	raf.seek( location );

	if( raf.read( head ) != HEADER_SIZE ) {
	    throw new CorruptHeaderException("Error reading MPEG frame header.");
	}

	version = BinaryParser.convertToDecimal( head[1], 3, 4 );
	layer = BinaryParser.convertToDecimal( head[1], 1, 2 );
	findBitRate( BinaryParser.convertToDecimal( head[2], 4, 7 ) );
	findSampleRate( BinaryParser.convertToDecimal( head[2], 2, 3 ) );
	padding = BinaryParser.bitSet( head[2], 1 );
	privateBit = BinaryParser.bitSet( head[2], 0 );
	channelMode = BinaryParser.convertToDecimal( head[3], 6, 7 );
	copyrighted = BinaryParser.bitSet( head[3], 3 );
	crced = !BinaryParser.bitSet( head[1], 0 );
	original = BinaryParser.bitSet( head[3], 2 );
	emphasis = BinaryParser.convertToDecimal( head[3], 0, 1 );
    }

    /**
     * Based on the bitrate index found in the header, try to find and set the 
     * bitrate from the table.
     *
     * @param bitrateIndex the bitrate index read from the header
     */
    private void findBitRate( int bitrateIndex ) {
	int ind = -1;

	if( version == MPEG_V_1 ) {
	    if( layer == MPEG_L_1 ) {
		ind = 0;
	    }
	    else if( layer == MPEG_L_2 ) {
		ind = 1;
	    }
	    else if( layer == MPEG_L_3 ) {
		ind = 2;
	    }
	}
	else if( (version == MPEG_V_2) || (version == MPEG_V_25) ) {
	    if( layer == MPEG_L_1 ) {
		ind = 3;
	    }
	    else if( (layer == MPEG_L_2) || (layer == MPEG_L_3) ) {
		ind = 4;
	    }
	}
  
	if( (ind != -1) && (bitrateIndex >= 0) && (bitrateIndex <= 15) ) {
	    bitRate = bitrateTable[bitrateIndex][ind];
	}
    }

    /**
     * Based on the sample rate index found in the header, attempt to lookup
     * and set the sample rate from the table.
     *
     * @param sampleIndex the sample rate index read from the header
     */
    private void findSampleRate( int sampleIndex ) {
	int ind = -1;

	switch( version ) {
	case MPEG_V_1:
	    ind = 0;
	    break;
	case MPEG_V_2:
	    ind = 1;
	    break;
	case MPEG_V_25:
	    ind = 2;
	}

	if( (ind != -1) && (sampleIndex >= 0) && (sampleIndex <= 3) ) {
	    sampleRate = sampleTable[sampleIndex][ind];
	}
    }

    /**
     * Computes the length of the frame found.  This is not necessarily constant
     * for all frames.
     *
     * @return the length of the frame found
     */
    public int getFrameLength() {
	int length = -1;
	int padAmount = 0;

	if( padding ) {
	    padAmount = slotLength[layer];
	}

	if( layer == MPEG_L_1 ) {
	    length = (12 * (bitRate * 1000)/ sampleRate + padAmount) * 4;
	}
	else {
	    length = 144 * (bitRate * 1000)/ sampleRate + padAmount;
	}

	return length;
    }

    /**
     * Return a string representation of this object.  Includes all information
     * read in.
     *
     * @return a string representation of this object
     */
    public String toString() {
	String str = new String();

	str = getVersion() + " " + getLayer() + "\nBitRate:\t\t\t" + 
	    getBitRate() + "kbps\nSampleRate:\t\t\t" + getSampleRate() +
	    "Hz\nChannelMode:\t\t\t" + getChannelMode() + 
	    "\nCopyrighted:\t\t\t" + isCopyrighted() + "\nOriginal:\t\t\t" +
	    isOriginal() + "\nCRC:\t\t\t\t" + isProtected() + 
	    "\nEmphasis:\t\t\t" + getEmphasis() + "\nOffset:\t\t\t\t" + 
	    getLocation() + "\nPrivateBit:\t\t\t" + privateBitSet() + 
	    "\nPadding:\t\t\t" + hasPadding() + "\nFrameLength:\t\t\t" + 
	    getFrameLength() + "\nVBR:\t\t\t\t" + isVBR();

	if( isVBR() ) {
	    str += "\n" + xingHead.toString();
	}

	return str;
    }
    
    /**
     * Return the version of the mpeg in string form.  Ex: MPEG Version 1.0
     *
     * @return the version of the mpeg
     */
    public String getVersion() {
	String str = null;
	
	if( (version >= 0) && (version < versionLabels.length) ) {
	    str = versionLabels[version];
	}

	return str;
    }

    /**
     * Return the layer description of the mpeg in string form.
     * Ex: Layer III
     *
     * @return the layer description of the mpeg
     */
    public String getLayer() {
	String str = null;

	if( (layer >= 0) && (layer < layerLabels.length) ) {
	    str = layerLabels[layer];
	}

	return str;
    }

    /**
     * Return the channel mode of the mpeg in string form.
     * Ex: Joint Stereo (STEREO)
     *
     * @return the channel mode of the mpeg
     */
    public String getChannelMode() {
	String str = null;

	if( (channelMode >= 0) && (channelMode < channelLabels.length) ) {
	    str = channelLabels[channelMode];
	}
	    
	return str;
    }

    /**
     * Returns the sample rate of the mpeg in Hz
     *
     * @return the sample rate of the mpeg in Hz
     */
    public int getSampleRate() {
	return sampleRate;
    }

    /**
     * Returns true if the audio is copyrighted
     *
     * @return true if the audio is copyrighted
     */
    public boolean isCopyrighted() {
	return copyrighted;
    }

    /**
     * Returns true if this mpeg is protected by CRC
     *
     * @return true if this mpeg is protected by CRC
     */
    public boolean isProtected() {
	return crced;
    }

    /**
     * Returns true if this is the original media
     *
     * @return true if this is the original media
     */
    public boolean isOriginal() {
	return original;
    }

    public boolean isVBR() {
	return xingHead.headerExists();
    }

    /**
     * Returns the emphasis.  I don't know what this means, it just does it...
     *
     * @return the emphasis
     */
    public String getEmphasis() {
	String str = null;

	if( (emphasis >= 0) && (emphasis < emphasisLabels.length) ) {
	    str = emphasisLabels[emphasis];
	}

	return str;
    }

    /**
     * Returns the offset at which the first mpeg frame was found in the file.
     *
     * @return the offset of the mpeg data
     */
    public long getLocation() {
	return location;
    }

    /**
     * Returns true if the file passed to the constructor is an mp3 (MPEG 
     * layer III).
     *
     * @return true if the file is an mp3
     */
    public boolean isMP3() {
	return (layer == MPEG_L_3);
    }

    /**
     * Returns true if the mpeg frames are padded in this file.
     *
     * @return true if the mpeg frames are padded in this file
     */
    public boolean hasPadding() {
	return padding;
    }

    /**
     * Returns true if the private bit is set.
     *
     * @return true if the private bit is set
     */
    public boolean privateBitSet() {
	return privateBit;
    }

    /**
     * If this is a VBR file, return an accurate playing time of this mpeg.  If
     * this is not a VBR file -1 is returned.
     *
     * @return an accurate playing time of this mpeg 
     */
    public int getVBRPlayingTime() {
	return xingHead.getPlayingTime();
    }

    /**
     * Returns the bitrate of this mpeg.  If it is a VBR file the average 
     * bitrate is returned.
     *
     * @return the bitrate of this mpeg (in kbps)
     */
    public int getBitRate() {
	int br = 0;

	if( xingHead.headerExists() ) {
	    br = xingHead.getAvgBitrate();
	}
	else {
	    br = bitRate;
	}

	return br;
    }

} // MPEGAudioFrameHeader
