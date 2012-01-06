/*
 * gov.noaa.nws.ncep.ui.pgen.file.FileTools
 * 
 * Date created: 17 February 2009
 *
 * This code has been developed by the NCEP/SIB for use in the AWIPS2 system.
 */
package gov.noaa.nws.ncep.ui.pgen.file;

import java.net.URL;

import gov.noaa.nws.ncep.ui.pgen.productTypes.ProductTypes;

import org.apache.log4j.Logger;

import com.raytheon.uf.common.serialization.SerializationException;
import com.raytheon.uf.common.serialization.SerializationUtil;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import java.io.File;

import gov.noaa.nws.ncep.viz.localization.impl.LocalizationManager;

/**
 * Define a file tool for reading/writing PGEN products using JAXB.
 * 
 *  You must register your serializable class in 
 *  /META-INF/services/com.raytheon.uf.common.serialization.ISerializableObject
 * 
 * <pre>
 * SOFTWARE HISTORY
 * Date       	Ticket#		Engineer	Description
 * ------------	----------	-----------	--------------------------
 * 02/17/09					J. Wu   	Initial Creation.
 * 09/09					J. Wu   	Added methods to read/write 
 * 										product types.
 * 05/10                    M.Laryukhin Refactored to use SerializationUtil for better performance
 * 01/11					J. Wu   	Added validation against "Product.xsd" before
 * 										marshalling and creating non-existing dirs.
 *
 * </pre>
 * 
 * @author	J. Wu
 */
public class FileTools {
	
	private static Logger log = Logger.getLogger(FileTools.class);
    
	/**
	 * Reads product in a file into a JAXB-based Products object
	 */
	public static Products read(String fileName) {
        
		Products products = null;
		Object result = null;
				
		try {
//			products = (Products) SerializationUtil.jaxbUnmarshalFromXmlFile(fileName);
			result =  SerializationUtil.jaxbUnmarshalFromXmlFile(fileName);
		} catch (SerializationException e) {
//			log.error("SerializationException thrown", e);
		}
		
		// Avoid ClassCastException
		if ( result instanceof Products  ) {
			products = (Products)result;
		}
		
		return products;
	}

	/**
	 * Writes a JAXB-based Products object into a file
	 */
	public static void write(String fileName, Products products) {
        
		// Create non-existing directories
		int  lind = fileName.lastIndexOf( "/" );
  	    if ( lind >= 0 ) {
  			String fdir= fileName.substring( 0, lind );
  			File checkDir = new File( fdir );
  		    if ( !(checkDir.exists() && checkDir.isDirectory() ) ) {
  		    	checkDir.mkdirs();	 
   		    }			
  	    }
		
		// Write
		try {

			SerializationUtil.jaxbMarshalToXmlFile(products, fileName);

		} catch (SerializationException e) {
			log.error("SerializationException thrown", e);
		}

	}

	/**
	 * Reads product types in a file into a JAXB-based ProductTypes object
	 */
	public static ProductTypes readProductTypes(String fileName) {

		ProductTypes types = null;
		try {
			
			types = (ProductTypes) SerializationUtil.jaxbUnmarshalFromXmlFile(fileName);
			
		} catch (SerializationException e) {
			log.error("SerializationException thrown", e);
		}

		return types;

	}
	
	/**
	 * Writes a JAXB-based ProductTypes object into a file
	 */
	public static void write(String fileName, ProductTypes types) {
        
		try {

			SerializationUtil.jaxbMarshalToXmlFile(types, fileName);

		} catch (SerializationException e) {
			log.error("SerializationException thrown", e);
		}

	}
	
	/**
	 * Validate if an xml file is validate against a schema file.
	 * By default, it will validate against the PGEN product file schema
	 * "product.xsd".
	 */
	public static boolean validate(String fileName, String xsdFile ) {
		
		boolean valid = true;
		
		if ( xsdFile == null ) {
		   xsdFile = LocalizationManager.getInstance().getFilename("Pgen_Product_Schema");
		}
		
		File xsdf = new File( fileName );
		if ( !xsdf.exists() || !xsdf.canRead() ) {
			return true;
		}
		
		xsdFile = "file://" + xsdFile;

		try {
		    
			URL schemaFile = new URL( xsdFile );
		    Source xmlFile = new StreamSource(new File( fileName ));
		    
		    SchemaFactory schemaFactory = SchemaFactory
		                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    
		    Schema schema = schemaFactory.newSchema( schemaFile );
		    
		    Validator validator = schema.newValidator();		
		    
		    try {
		        validator.validate( xmlFile );
		    }catch (Exception e) {
				valid = false;
			}
		} catch ( Exception e ) {
			valid = false;
		}
        
		return valid;
	}
	
}
