package org.ssclab.util;

import java.util.GregorianCalendar;

import org.ssclab.parser.DateFormat;

public class GregCalForInFunction {
	private GregorianCalendar cal;
	private DateFormat.DATE_FORMAT type_format;
	
	public GregCalForInFunction(GregorianCalendar cal,DateFormat.DATE_FORMAT type_format)   {
		this.cal=cal;
		this.type_format=type_format;
	}

	public GregorianCalendar getCal() {
		return cal;
	}

	public DateFormat.DATE_FORMAT getTypeFormat() {
		return type_format;
	}

}
