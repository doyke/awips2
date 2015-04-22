/*
 * FillPatternList
 * 
 * Date created: 01 DECEMBER 2008
 *
 * This code has been developed by the NCEP/SIB for use in the AWIPS2 system.
 */
package gov.noaa.nws.ncep.ui.pgen.display;

import java.util.EnumMap;

/**
 * This object contains a list of defined Fill Patterns that can be applied to
 * a multi-point line path.<P>
 * 
 * This class is implemented as a singleton, and the predefined Fill Patterns are
 * constructed when the instance is created.  Users can get a reference to this
 * object using the static method getInstance().
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Apr 22, 2010  ??     	sgilbert    Initial creation
 * May 21, 2013  #996    	J. Wu     	Updated pattern1 - dashed line and
 * 										pattern4 - wave line to match legacy.
 * 
 * </pre>
 * 
 * @author sgilbert
 * @version 1.0
 *
 */
public class FillPatternList {
	
	/**
	 * An Enumeration of available Fill Patterns
	 * @author sgilbert
	 *
	 */
	public static enum FillPattern { FILL_PATTERN_0, FILL_PATTERN_1, 
		FILL_PATTERN_2,	FILL_PATTERN_3, FILL_PATTERN_4, FILL_PATTERN_5, 
		FILL_PATTERN_6,  SOLID, TRANSPARENCY };

	/**
	 * The singleton instance
     */
	private static FillPatternList instance=null;;
	
	/**
	 * An EnumMap holding the Fill Patterns
	 */
	private EnumMap<FillPattern, byte[]> patternMap;
	
	/**
	 * Constructor used by the getInstance method.
	 */
	protected FillPatternList() {
		
		patternMap = new EnumMap<FillPattern, byte[]>(FillPattern.class);
		initialize();
		
	}
	
	/**
	 * Static method used to request the instance of the FillPatternList object.
	 * @return reference to this object
	 */
	public static synchronized FillPatternList getInstance() {
		
		if ( instance == null ) {
			instance = new FillPatternList();
		}
		return instance;	
				
	}
	
	/**
	 * Initialize the HashMap holding the Fill Patterns
	 */
	private void initialize() {
	
		loadInternal();
	}

	/**
	 * Gets the fill pattern mapped to the requested FillPattern enumerated type
	 * @param key Requested Fill Pattern
	 * @return The requested Fill Pattern.  Returns null, if a SOLID or TRANSPARENCY
	 * FillPattern is requested.
	 */
	public byte[] getFillPattern(FillPattern key) {
		
		if ( key.equals(FillPattern.SOLID) || key.equals(FillPattern.TRANSPARENCY)) {
			return null;
		} else {
			return patternMap.get(key);
		}
	}

/*	
	public String[] getPatternNames() {
		
		String[] names = new String[patternMap.size()];
		
		int i=0;
		for ( LinePattern lp : patternMap.values() ) {
			names[i++] = lp.getName();
		}
		return names;
	}
*/
	
	/**
	 * Constructs the EnumMap holding all the FillPatterns.
	 * <P>
	 * Currently all FillPatterns are constructed in line in the code.  We will
	 * likely want all these patterns defined in an an external source in the 
	 * future.  At that point, this method may load the LinePatterns from an
	 * external file(s), instead of constructing them explicitly.
	 */
	private void loadInternal() {

		int[] pattern0 = {   0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  0xAA, 0xAA, 0xAA, 0xAA, 
				  0x55, 0x55, 0x55, 0x55, 
				  };

		byte[] pattern_0 = new byte[pattern0.length];
		for (int i=0; i<pattern0.length; i++) {
			pattern_0[i] = (byte)pattern0[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_0, pattern_0);
/*
		int[] pattern1 = {   0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 	
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  };
*/
		//Modified pattern 1 and 5 to match CCFP fill patterns in NMAP (TTR648) -- bingfan 11/28/2012
/*		int[] pattern1 = {  0x80, 0x00, 0x80, 0x00, 
				0x40, 0x00, 0x40, 0x00, 
	  			0x20, 0x00, 0x20, 0x00, 
	  			0x10, 0x00, 0x10, 0x00, 
	  			0x08, 0x00, 0x08, 0x00, 
	  			0x04, 0x00, 0x04, 0x00, 
	  			0x02, 0x00, 0x02, 0x00, 
	  			0x01, 0x00, 0x01, 0x00, 
	  			0x00, 0x80, 0x00, 0x80, 
				0x00, 0x40, 0x00, 0x40, 
	  			0x00, 0x20, 0x00, 0x20, 
	  			0x00, 0x10, 0x00, 0x10, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x80, 0x00, 0x80, 0x00, 
				0x40, 0x00, 0x40, 0x00, 
	  			0x20, 0x00, 0x20, 0x00, 
	  			0x10, 0x00, 0x10, 0x00, 
	  			0x08, 0x00, 0x08, 0x00, 
	  			0x04, 0x00, 0x04, 0x00, 
	  			0x02, 0x00, 0x02, 0x00, 
	  			0x01, 0x00, 0x01, 0x00, 
	  			0x00, 0x80, 0x00, 0x80, 
				0x00, 0x40, 0x00, 0x40, 
	  			0x00, 0x20, 0x00, 0x20, 
	  			0x00, 0x10, 0x00, 0x10, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  			0x00, 0x00, 0x00, 0x00, 
	  };
*/
		//Modified pattern 1 to match fill patterns in NMAP -- J. Wu 05/21/2013
		int[] pattern1 = {  
			0x80, 0x80, 0x80, 0x80, 
			0x40, 0x40, 0x40, 0x40, 
			0x20, 0x20, 0x20, 0x20, 
			0x10, 0x10, 0x10, 0x10,
			
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00,
			
			0x80, 0x80, 0x80, 0x80, 
			0x40, 0x40, 0x40, 0x40, 
			0x20, 0x20, 0x20, 0x20, 
			0x10, 0x10, 0x10, 0x10,
			
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			
			0x80, 0x80, 0x80, 0x80, 
			0x40, 0x40, 0x40, 0x40, 
			0x20, 0x20, 0x20, 0x20, 
			0x10, 0x10, 0x10, 0x10,
			
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00,
			
			0x80, 0x80, 0x80, 0x80, 
			0x40, 0x40, 0x40, 0x40, 
			0x20, 0x20, 0x20, 0x20, 
			0x10, 0x10, 0x10, 0x10,
			
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00 
        };

		byte[] pattern_1 = new byte[pattern1.length];
		for (int i=0; i<pattern1.length; i++) {
			pattern_1[i] = (byte)pattern1[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_1, pattern_1);
		
		int[] pattern2 = {   0x80, 0x00, 0x80, 0x00, 
				  0x40, 0x00, 0x40, 0x00, 
				  0x20, 0x00, 0x20, 0x00, 
				  0x10, 0x00, 0x10, 0x00, 
				  0x08, 0x00, 0x08, 0x00, 
				  0x04, 0x00, 0x04, 0x00, 
				  0x02, 0x00, 0x02, 0x00, 
				  0x01, 0x00, 0x01, 0x00, 
				  0x00, 0x80, 0x00, 0x80, 
				  0x00, 0x40, 0x00, 0x40, 
				  0x00, 0x20, 0x00, 0x20, 
				  0x00, 0x10, 0x00, 0x10, 
				  0x00, 0x08, 0x00, 0x08, 
				  0x00, 0x04, 0x00, 0x04, 
				  0x00, 0x02, 0x00, 0x02, 
				  0x00, 0x01, 0x00, 0x01, 
				  0x80, 0x00, 0x80, 0x00, 
				  0x40, 0x00, 0x40, 0x00, 
				  0x20, 0x00, 0x20, 0x00, 
				  0x10, 0x00, 0x10, 0x00, 
				  0x08, 0x00, 0x08, 0x00, 
				  0x04, 0x00, 0x04, 0x00, 
				  0x02, 0x00, 0x02, 0x00, 
				  0x01, 0x00, 0x01, 0x00, 
				  0x00, 0x80, 0x00, 0x80, 
				  0x00, 0x40, 0x00, 0x40, 
				  0x00, 0x20, 0x00, 0x20, 
				  0x00, 0x10, 0x00, 0x10, 
				  0x00, 0x08, 0x00, 0x08, 
				  0x00, 0x04, 0x00, 0x04, 
				  0x00, 0x02, 0x00, 0x02, 
				  0x00, 0x01, 0x00, 0x01, 
				  };

		byte[] pattern_2 = new byte[pattern2.length];
		for (int i=0; i<pattern2.length; i++) {
			pattern_2[i] = (byte)pattern2[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_2, pattern_2);
		
		int[] pattern3 = {   0x08, 0x08, 0x08, 0x08, 
				  0x04, 0x04, 0x04, 0x04, 
				  0x02, 0x02, 0x02, 0x02, 
				  0x01, 0x01, 0x01, 0x01, 
				  0x80, 0x80, 0x80, 0x80, 
				  0x40, 0x40, 0x40, 0x40, 
				  0x20, 0x20, 0x20, 0x20, 
				  0x10, 0x10, 0x10, 0x10, 
				  0x08, 0x08, 0x08, 0x08, 
				  0x04, 0x04, 0x04, 0x04, 
				  0x02, 0x02, 0x02, 0x02, 
				  0x01, 0x01, 0x01, 0x01, 
				  0x80, 0x80, 0x80, 0x80, 
				  0x40, 0x40, 0x40, 0x40, 
				  0x20, 0x20, 0x20, 0x20, 
				  0x10, 0x10, 0x10, 0x10, 
				  0x08, 0x08, 0x08, 0x08, 
				  0x04, 0x04, 0x04, 0x04, 
				  0x02, 0x02, 0x02, 0x02, 
				  0x01, 0x01, 0x01, 0x01, 
				  0x80, 0x80, 0x80, 0x80, 
				  0x40, 0x40, 0x40, 0x40, 
				  0x20, 0x20, 0x20, 0x20, 
				  0x10, 0x10, 0x10, 0x10, 
				  0x08, 0x08, 0x08, 0x08, 
				  0x04, 0x04, 0x04, 0x04, 
				  0x02, 0x02, 0x02, 0x02, 
				  0x01, 0x01, 0x01, 0x01, 
				  0x80, 0x80, 0x80, 0x80, 
				  0x40, 0x40, 0x40, 0x40, 
				  0x20, 0x20, 0x20, 0x20, 
				  0x10, 0x10, 0x10, 0x10, 
				  };

		byte[] pattern_3 = new byte[pattern3.length];
		for (int i=0; i<pattern3.length; i++) {
			pattern_3[i] = (byte)pattern3[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_3, pattern_3);
		
/*		int[] pattern4 = {    0x08, 0x04, 0x1e, 0x08, 0x0f, 0x04, 0x02, 0x0f, 0x81, 0x07, 0x02, 0x81,
				   0x81, 0xc0, 0x03, 0x81, 0x81, 0x40, 0xe0, 0x81, 0xf0, 0x40, 0x20, 0xf0,
				   0x10, 0x78, 0x20, 0x10, 0x10, 0x08, 0x3c, 0x10, 0x1e, 0x08, 0x04, 0x1e,
				   0x02, 0x0f, 0x04, 0x02, 0x02, 0x81, 0x07, 0x02, 0x03, 0x81, 0xc0, 0x03,
				   0xe0, 0x81, 0x40, 0xe0, 0x20, 0xf0, 0x40, 0x20, 0x20, 0x10, 0x78, 0x20,
				   0x3c, 0x10, 0x08, 0x3c, 0x04, 0x1e, 0x08, 0x04, 0x04, 0x02, 0x0f, 0x04,
				   0x07, 0x02, 0x81, 0x07, 0xc0, 0x03, 0x81, 0xc0, 0x40, 0xe0, 0x81, 0x40,
				   0x40, 0x20, 0xf0, 0x40, 0x78, 0x20, 0x10, 0x78, 0x08, 0x3c, 0x10, 0x08,
				   0x08, 0x04, 0x1e, 0x08, 0x0f, 0x04, 0x02, 0x0f, 0x81, 0x07, 0x02, 0x81,
				   0x81, 0xc0, 0x03, 0x81, 0x81, 0x40, 0xe0, 0x81, 0xf0, 0x40, 0x20, 0xf0,
				   0x10, 0x78, 0x20, 0x10, 0x10, 0x08, 0x3c, 0x10 };
*/
		//Modified pattern 4 to match fill patterns in NMAP -- J. Wu 05/21/2013
		int[] pattern4 = {
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0xff, 0x80, 0xff, 0x80,				
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x80, 0xff, 0x80, 0xff,				
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0x80, 0x00, 0x80, 0x00,
				0xff, 0x80, 0xff, 0x80,				
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x00, 0x80, 0x00, 0x80,
				0x80, 0xff, 0x80, 0xff
		};

		byte[] pattern_4 = new byte[pattern4.length];
		for (int i=0; i<pattern4.length; i++) {
			pattern_4[i] = (byte)pattern4[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_4, pattern_4);
		
	/*	int[] pattern5 = {   0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x11, 0x11, 0x11, 0x11, 
				  };
		*/
		int[] pattern5 = {   0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x33, 0x33, 0x33, 0x33, 
				  0x00, 0x00, 0x00, 0x00, 
				  0x00, 0x00, 0x00, 0x00, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  0xCC, 0xCC, 0xCC, 0xCC, 
				  };

		byte[] pattern_5 = new byte[pattern5.length];
		for (int i=0; i<pattern5.length; i++) {
			pattern_5[i] = (byte)pattern5[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_5, pattern_5);
		
		int[] pattern6 = {   0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  0x88, 0x88, 0x88, 0x88, 
				  0x44, 0x44, 0x44, 0x44, 
				  0x22, 0x22, 0x22, 0x22, 
				  0x11, 0x11, 0x11, 0x11, 
				  };

		byte[] pattern_6 = new byte[pattern6.length];
		for (int i=0; i<pattern6.length; i++) {
			pattern_6[i] = (byte)pattern6[i];
		}
		patternMap.put(FillPattern.FILL_PATTERN_6, pattern_6);
		
	}
	
}
