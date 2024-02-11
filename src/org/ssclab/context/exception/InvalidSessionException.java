package org.ssclab.context.exception;

import org.ssclab.i18n.RB;

public class InvalidSessionException  extends Exception  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSessionException ()  {
		super( RB.getString("it.ssc.context.exception.InvalidSessionException.msg1"));
	}
}
