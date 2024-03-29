/**
 *
 */
package museum.delphi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * @author pschmitz
 *
 */
public class XMLUtils {

	public static void writeXMLDocToFile(Document outputDoc, String outFileName) {
		try {
			//FileWriter writer = new FileWriter( outFileName );
			final String encoding = "UTF-8";
			OutputStreamWriter writer =
				new OutputStreamWriter(new FileOutputStream(outFileName), encoding);
			// Use a Transformer for output
			TransformerFactory tFactory =
		    TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(outputDoc);
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			writer.close();
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Could not create output XML file: " + outFileName);
		} catch (TransformerConfigurationException tce) {
		  // Error generated by the parser
		  System.out.println ("* Transformer Factory error");
		  System.out.println("  " + tce.getMessage() );

		   // Use the contained exception, if any
		  Throwable x = tce;
		  if (tce.getException() != null)
		    x = tce.getException();
		  x.printStackTrace();
		} catch (TransformerException te) {
		  // Error generated by the parser
		  System.out.println ("* Transformation error");
		  System.out.println("  " + te.getMessage() );

		  // Use the contained exception, if any
		  Throwable x = te;
		  if (te.getException() != null)
		    x = te.getException();
		  x.printStackTrace();
		}
	} // close writeXMLDocToFile()

}
