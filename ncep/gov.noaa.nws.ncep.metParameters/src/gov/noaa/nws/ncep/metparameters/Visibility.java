package gov.noaa.nws.ncep.metparameters;

import javax.measure.unit.Unit;

import com.raytheon.uf.common.dataplugin.IDecoderGettable.Amount;

public class Visibility extends AbstractMetParameter implements
							javax.measure.quantity.Length {

	public Visibility() {
		super( UNIT );
	}

}
