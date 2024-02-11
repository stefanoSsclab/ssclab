package org.ssclab.dataset.exception;

import org.ssclab.i18n.RB;
public class InvalidNameDataset extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidNameDataset(String name_ds) {
		super(RB.getString("it.ssc.dataset.exception.InvalidNameDataset.msg1")+name_ds);
	}
}
