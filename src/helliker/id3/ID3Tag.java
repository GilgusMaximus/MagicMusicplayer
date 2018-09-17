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
 *  A common interface for ID3Tag objects so they can easily communicate with
 *  each other.
 *
 * @author:  Jonathan Hilliker
 * @version: $Id: ID3Tag.java,v 1.2 2002/01/27 03:25:48 helliker Exp $
 * Revisions: 
 *  $Log: ID3Tag.java,v $
 *  Revision 1.2  2002/01/27 03:25:48  helliker
 *  Added getBytes method.
 *
 *  Revision 1.1  2002/01/13 07:50:57  helliker
 *  Initial version
 *
 *
 *
 */

package helliker.id3;

import java.io.IOException;

public interface ID3Tag {
    
    /**
     * Copies information from the ID3Tag parameter and inserts it into
     * this tag.  Previous data will be overwritten.
     *
     * @param tag the tag to copy from
     */
    public void copyFrom( ID3Tag tag );

    /**
     * Saves all data in this tag to the file it is bound to.
     *
     * @exception IOException if an error occurs
     */
    public void writeTag() throws IOException;

    /**
     * Removes this tag from the file it is bound to.
     *
     * @exception IOException if an error occurs
     */
    public void removeTag() throws IOException;
    

    /**
     * Returns a binary representation of the tag as it would appear in
     * a file.
     *
     * @return a binary representation of the tag
     */
    public byte[] getBytes();

} // ID3Tag
