package es.udc.subasta.model.productservice;

import java.util.Calendar;

@SuppressWarnings("serial")
public class DatesException extends Exception {
	
	private Calendar startingDate;

	public DatesException(Calendar startingDate) {
        super("EndingDate after startingDate => startingDate = " + startingDate);
        this.startingDate = startingDate;
    }

	public Calendar getStartingDate() {
		return startingDate;
	}
	
}
