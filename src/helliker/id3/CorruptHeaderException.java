
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
 *  This exception is thrown when a corrupt mpeg or Xing header is encountered.
 *
 * @author:  Jonathan Hilliker
 * @version: $Id: CorruptHeaderException.java,v 1.1 2002/01/21 05:07:38 helliker Exp $
 * Revsisions: 
 *  $Log: CorruptHeaderException.java,v $
 *  Revision 1.1  2002/01/21 05:07:38  helliker
 *  Initial version.
 *
 *
 *
 */

package helliker.id3;
    
public class CorruptHeaderException extends Exception {
    
    /**
     * Create a CorruptHeaderException with a default message
     *
     */
    public CorruptHeaderException() {
	super("Header is corrupt");
    }

    /**
     * Create a CorruptHeaderException with a specified message
     *
     * @param msg the message for this exception
     */
    public CorruptHeaderException( String msg ) {
	super( msg );
    }
    
} // CorruptHeaderException
