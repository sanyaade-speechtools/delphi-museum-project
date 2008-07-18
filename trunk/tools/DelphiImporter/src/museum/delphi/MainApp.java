/**
 *
 */
package museum.delphi;

//import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Point;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

// For image resizing.
/*
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
*/
import javax.swing.ImageIcon;



/**
 * @author Patrick
 *
 */
public class MainApp {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	//private JMenuItem openMenuItem = null;
	//private JMenuItem saveMenuItem = null;
	private JMenuItem exitMenuItem = null;
	private JFileChooser chooser = null;
	private FileNameExtensionFilter xmlfilter = null;
	private FileNameExtensionFilter sqlfilter = null;
	private VocabTermsReader vocabTermsReader = null;  //  @jve:decl-index=0:
	private MetaDataReader metaDataReader = null;  //  @jve:decl-index=0:
	private ImagePathsReader imagePathsReader = null;  //  @jve:decl-index=0:
	private String columnNames[] = null;
	private String dbName = "pahma_dev";

	private JMenu vocabActionsMenu = null;
	private JMenuItem openVocabMenuItem = null;
	private JMenuItem saveVocabAsXMLMenuItem = null;
	private JMenuItem loadVocabFromXMLMenuItem = null;
	//private JMenuItem addVocabToDBMenuItem = null;
	private JMenuItem saveOntologyAsSQLMenuItem = null;
	private JMenuItem saveHooksAsSQLMenuItem = null;
	private JMenuItem saveExclusionsAsSQLMenuItem = null;

	private JMenu metaDataActionsMenu = null;
	private JMenuItem openMDCfgFileMenuItem = null;
	private JMenuItem openMDFileMenuItem = null;
	private JMenuItem buildUsageReportMenuItem = null;

	private JMenu objectActionsMenu = null;
	private JMenuItem loadImagePathsMenuItem = null;
	private JMenuItem computeImageOrientationsMenuItem = null;
	//private JMenuItem saveObjectSQLUpdatesMenuItem = null;
	private JMenuItem saveSQLMediaLoadFileMenuItem = null;
	private JMenuItem saveSQLMediaInsertFileMenuItem = null;
	private JMenuItem buildObjectSQLMenuItem = null;
	private JMenuItem categorizeObjectsMenuItem = null;

	//private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem aboutMenuItem = null;
	//private JMenuItem cutMenuItem = null;
	//private JMenuItem copyMenuItem = null;
	//private JMenuItem pasteMenuItem = null;

	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="561,13"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JTextPane aboutText = null;

	private JPanel logoPanel = null;
	private JLabel jImageLabel = null;

	private JDialog buildUsageReportDialog = null;  //  @jve:decl-index=0:visual-constraint="105,376"
	private JPanel buildUsageReportContentPane = null;
	private JLabel buildUsageReportLabel = null;
	private JList burColumnList;
	private JScrollPane burColListScrollPane;
	//private JTextPane aboutText = null;
	private JButton buildReportButton;

	private Document vocabOutputDoc = null;  //  @jve:decl-index=0:
	private DoubleHashTree vocabHashTree = null;
	private DoubleHashTree facetMapHashTree = null;  //  @jve:decl-index=0:
	private JTextField jStatusBarTextField = null;

	private int _debugLevel = 1;
	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	/**
	 * This method initializes aboutText
	 *
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getAboutText() {
		if (aboutText == null) {
			aboutText = new JTextPane();
			aboutText.setText(
				  "This is an early version of the Delphi Import Manager application.\n\n"
				+ "This application will assist in the import of ontology/taxonomy\n"
				+ "from CSV files, with some cleaning and construction of the tree.\n\n"
				+ "It will also provide a means to import CVS data dumps that have\n"
				+ "text annotations of objects in named columns, with some tokenizing\n"
				+ "and cleaning of this data to produce token annotations of the objects.\n"
				+ "This data is then indexed against the imported taxonomies for term\n"
				+ "association, to produce a semantic index of the collection.\n"
				+ "This semantic index is stored in a Delphi database for use in the\n"
				+ "faceted search browse UI.\n\n"
				+ "Delphi was created by:\n"
				+ "  Olga Amuzinskaya, Adrienne Hilgert, Jon Lesser, Patrick Schmitz,"
				+ " and Jerry Yu.\n"
				+ "  Thanks as well to Eun Kyoung Choe for her UI assistance.\n");
		}
		return aboutText;
	}

	/**
	 * This method initializes logoPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getLogoPanel() {
		if (logoPanel == null) {
			//Toolkit toolkit = Toolkit.getDefaultToolkit();
			//String imgPath = "D:/Patrick/_Grad school/Final Project/Src/delphi/trunk/tools/DelphiImporter/bin/museum/delphi/Delphi3_logo2.jpg";
			String imgPath = "images/Delphi_logo2mid.jpg";
			ImageIcon logo = new ImageIcon(imgPath);
			jImageLabel = new JLabel(logo, SwingConstants.CENTER);
			jImageLabel.setBackground(java.awt.Color.black);
			logoPanel = new JPanel();
			logoPanel.setLayout(new BorderLayout());
			logoPanel.setBackground(java.awt.Color.black);
			logoPanel.add(jImageLabel, BorderLayout.CENTER);
			logoPanel.add(getJStatusBarTextField(), BorderLayout.SOUTH);
		}
		return logoPanel;
	}

	/**
	 * This method initializes jStatusBarTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJStatusBarTextField() {
		if (jStatusBarTextField == null) {
			jStatusBarTextField = new JTextField();
			jStatusBarTextField.setEditable(false);
		}
		return jStatusBarTextField;
	}

	private void setStatus(String status){
		jStatusBarTextField.setText(status);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainApp application = new MainApp();
				//application.chooser = new JFileChooser( new File("D:/PAHMA/VocabDump3b.txt"));
				application.chooser = new JFileChooser( new File("C:/Patrick/Delphi"));
				application.xmlfilter = new FileNameExtensionFilter( "XML files", "xml" );
				application.sqlfilter = new FileNameExtensionFilter( "SQL files", "sql" );
				application.getJFrame().setVisible(true);
			}
		});
	}

	/**
	 * This method initializes jFrame
	 *
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(532, 336);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Application");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getLogoPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 *
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			//jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getVocabActionsMenu());
			jJMenuBar.add(getDataActionsMenu());
			jJMenuBar.add(getObjectActionsMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("App");
			//fileMenu.add(getOpenMenuItem());
			//fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}


	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}
	 */

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getVocabActionsMenu() {
		if (vocabActionsMenu == null) {
			vocabActionsMenu = new JMenu();
			vocabActionsMenu.setText("Vocabulary");
			vocabActionsMenu.add(getOpenVocabMenuItem());
			vocabActionsMenu.add(getSaveVocabAsXMLMenuItem());
			vocabActionsMenu.add(getLoadVocabFromXMLMenuItem());
			// vocabActionsMenu.add(getAddVocabToDBMenuItem());
			vocabActionsMenu.add(getSaveOntologyAsSQLMenuItem());
			vocabActionsMenu.add(getSaveHooksAsSQLMenuItem());
			vocabActionsMenu.add(getSaveExclusionsAsSQLMenuItem());
			saveVocabAsXMLMenuItem.setEnabled(false);
			loadVocabFromXMLMenuItem.setEnabled(true);
			//addVocabToDBMenuItem.setEnabled(false);
			saveOntologyAsSQLMenuItem.setEnabled(false);
			saveHooksAsSQLMenuItem.setEnabled(false);
			saveExclusionsAsSQLMenuItem.setEnabled(false);
		}
		return vocabActionsMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenVocabMenuItem() {
		if (openVocabMenuItem == null) {
			openVocabMenuItem = new JMenuItem();
			openVocabMenuItem.setText("Open Text Vocabulary File...");
			openVocabMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if( openVocabFile() ) {
						saveVocabAsXMLMenuItem.setEnabled(true);
						// NYI addVocabToDBMenuItem.setEnabled(true);
					}
					debug(2,"Open Vocabulary File");
				}
			});
		}
		return openVocabMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLoadVocabFromXMLMenuItem() {
		if (loadVocabFromXMLMenuItem == null) {
			loadVocabFromXMLMenuItem = new JMenuItem();
			loadVocabFromXMLMenuItem.setText("Open Ontology File...");
			loadVocabFromXMLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if( openOntologyFile() ) {
						saveOntologyAsSQLMenuItem.setEnabled(true);
						saveHooksAsSQLMenuItem.setEnabled(true);
						saveExclusionsAsSQLMenuItem.setEnabled(true);
						// NYI addVocabToDBMenuItem.setEnabled(true);
						/*
						TaxoNode node = facetMapHashTree.FindNodeByName("Color", "blue");
						System.out.println( "Cat ID for blue is: " + node.id );
						node = facetMapHashTree.FindNodeByName("Color", "brownish-black");
						System.out.println( "Cat ID for brownish-black is: " + node.id );
						if(( node = facetMapHashTree.FindNodeByName("Color", "jade green")) == null )
							System.out.println( "No Cat ID for jade green!!!" );
						else
							System.out.println( "Cat ID for jade green is: " + node.id );
						if(( node = facetMapHashTree.FindNodeByName("Color", "jade-colored")) == null )
							System.out.println( "No Cat ID for jade-colored!!!" );
						else
							System.out.println( "Cat ID for jade-colored is: " + node.id );
						if(( node = facetMapHashTree.FindNodeByName("Color", "Turquoise Green")) == null )
							System.out.println( "No Cat ID for Turquoise Green!!!" );
						else
							System.out.println( "Cat ID for Turquoise Green is: " + node.id );
						if(( node = facetMapHashTree.FindNodeByName("Material", "bronze")) == null )
							System.out.println( "No Cat ID for bronze!!!" );
						else
							System.out.println( "Cat ID for bronze is: " + node.id );
						 */
					}
				}
			});
		}
		return loadVocabFromXMLMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveVocabAsXMLMenuItem() {
		if (saveVocabAsXMLMenuItem == null) {
			saveVocabAsXMLMenuItem = new JMenuItem();
			saveVocabAsXMLMenuItem.setText("Save Vocabulary as XML...");
			saveVocabAsXMLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveVocabAsXML();
				}
			});
		}
		return saveVocabAsXMLMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveOntologyAsSQLMenuItem() {
		if (saveOntologyAsSQLMenuItem == null) {
			saveOntologyAsSQLMenuItem = new JMenuItem();
			saveOntologyAsSQLMenuItem.setText("Save Vocabulary as SQL Load...");
			saveOntologyAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveVocabAsSQL();
				}
			});
		}
		return saveOntologyAsSQLMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveHooksAsSQLMenuItem() {
		if (saveHooksAsSQLMenuItem == null) {
			saveHooksAsSQLMenuItem = new JMenuItem();
			saveHooksAsSQLMenuItem.setText("Save Hooks as SQL Load...");
			saveHooksAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveVocabHooksOrExclusionsAsSQL( true );
				}
			});
		}
		return saveHooksAsSQLMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveExclusionsAsSQLMenuItem() {
		if (saveExclusionsAsSQLMenuItem == null) {
			saveExclusionsAsSQLMenuItem = new JMenuItem();
			saveExclusionsAsSQLMenuItem.setText("Save Exclusions as SQL Load...");
			saveExclusionsAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveVocabHooksOrExclusionsAsSQL( false );
				}
			});
		}
		return saveExclusionsAsSQLMenuItem;
	}



	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	private JMenuItem getAddVocabToDBMenuItem() {
		if (addVocabToDBMenuItem == null) {
			addVocabToDBMenuItem = new JMenuItem();
			addVocabToDBMenuItem.setText("Add Vocabulary to DB...");
			addVocabToDBMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Add Vocabulary to DB");
				}
			});
		}
		return addVocabToDBMenuItem;
	}
	 */

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getDataActionsMenu() {
		if (metaDataActionsMenu == null) {
			metaDataActionsMenu = new JMenu();
			metaDataActionsMenu.setText("MetaData");
			metaDataActionsMenu.add(getOpenMDCfgFileMenuItem());
			metaDataActionsMenu.add(getOpenMDFileMenuItem());
			metaDataActionsMenu.add(getBuildUsageReportMenuItem());
			openMDFileMenuItem.setEnabled(false);
			buildUsageReportMenuItem.setEnabled(false);
		}
		return metaDataActionsMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenMDCfgFileMenuItem() {
		if (openMDCfgFileMenuItem == null) {
			openMDCfgFileMenuItem = new JMenuItem();
			openMDCfgFileMenuItem.setText("Open MetaData Config File...");
			openMDCfgFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					debug(2,"Open MetaData Config File...");
					if( openMDConfigFile() ) {
						openMDFileMenuItem.setEnabled(true);
					}
				}
			});
		}
		return openMDCfgFileMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenMDFileMenuItem() {
		if (openMDFileMenuItem == null) {
			openMDFileMenuItem = new JMenuItem();
			openMDFileMenuItem.setText("Open MetaData File...");
			openMDFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if( openMDFile() ) {
						buildUsageReportMenuItem.setEnabled(true);
					}
				}
			});
		}
		return openMDFileMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getBuildUsageReportMenuItem() {
		if (buildUsageReportMenuItem == null) {
			buildUsageReportMenuItem = new JMenuItem();
			buildUsageReportMenuItem.setText("Build Usage Report...");
			buildUsageReportMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog burDialog = getBuildUsageReportDialog();
					burDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(50, 50);
					burDialog.setLocation(loc);
					burDialog.setVisible(true);
					debug(1,"Generate Usage report...");
				}
			});
		}
		return buildUsageReportMenuItem;
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getObjectActionsMenu() {
		if (objectActionsMenu == null) {
			objectActionsMenu = new JMenu();
			objectActionsMenu.setText("Objects");
			objectActionsMenu.add(getLoadImagePathsMenuItem());
			objectActionsMenu.add(getComputeImageOrientationsMenuItem());
			objectActionsMenu.add(getSaveSQLMediaInsertFileMenuItem());
			/* OBSOLETE
			objectActionsMenu.add(getSaveObjectSQLUpdatesMenuItem());
			 */
			objectActionsMenu.add(getSaveSQLMediaLoadFileMenuItem());
			objectActionsMenu.add(getBuildObjectSQLMenuItem());
			objectActionsMenu.add(getCategorizeObjectsMenuItem());
		}
		return objectActionsMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLoadImagePathsMenuItem() {
		if (loadImagePathsMenuItem == null) {
			loadImagePathsMenuItem = new JMenuItem();
			loadImagePathsMenuItem.setText("Load Image Paths...");
			loadImagePathsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadImagePaths();
				}
			});
		}
		return loadImagePathsMenuItem;
	}

	private void loadImagePaths() {
		debug(1,"Loading Image paths...");
		try {
			chooser.setFileFilter(null);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
		        debug(1,"Scanning image paths from file: " + filename);
		        setStatus("Scanning image paths from file: " + filename);
		    	imagePathsReader = new ImagePathsReader(filename);
		    	int nObjs = imagePathsReader.readInfo(Integer.MAX_VALUE, true);
		        setStatus("Found: " + nObjs + " objects with images.");
		        debug(2,"Found: " + nObjs + " objects with images.");
		        debug(3, "Mid path for 3716: [mids/"
		        		+imagePathsReader.GetSimpleSubPathForID(3716, null)+"]" );
		        debug(3,"Thumb path for 5204: [thumbs/"
		        		+imagePathsReader.GetSimpleSubPathForID(5204, null)+"]" );
				computeImageOrientationsMenuItem.setEnabled(true);
				//saveObjectSQLUpdatesMenuItem.setEnabled(true);
				saveSQLMediaLoadFileMenuItem.setEnabled(true);
				saveSQLMediaInsertFileMenuItem.setEnabled(true);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Loading Image paths File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getComputeImageOrientationsMenuItem() {
		if (computeImageOrientationsMenuItem == null) {
			computeImageOrientationsMenuItem = new JMenuItem();
			computeImageOrientationsMenuItem.setText("Compute Image Orientations...");
			computeImageOrientationsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					computeImageOrientations();
				}
			});
		}
		computeImageOrientationsMenuItem.setEnabled(false);
		return computeImageOrientationsMenuItem;
	}

	/**
	 * Assumes the base path is set, and that there are variants under the thumbs.
	 */
	private void computeImageOrientations() {
		// HACK HACK HACK
		String pathBase = "D:/PAHMA/ObjImages/thumbs/";
		if( imagePathsReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		debug(1,"Computing Image Orientations...");
		try {
			int nToProcessMax = Integer.MAX_VALUE;
			int nToReport = 2500;
			int nTillReport = nToReport;
			int nProcessed = 0;
			int nSkipped = 0;
			Collection<ArrayList<ImageInfo>> allImgs = imagePathsReader.GetAllAsList();
			java.util.Iterator<ArrayList<ImageInfo>> allImgsIterator = allImgs.iterator();
			while( allImgsIterator.hasNext() ) {
				ArrayList<ImageInfo> imgsForId = allImgsIterator.next();
				java.util.ListIterator<ImageInfo> objImgIterator = imgsForId.listIterator();
				while( objImgIterator.hasNext() ) {
					ImageInfo ii = objImgIterator.next();
					if( ii.getAspectR() != ImageInfo.UNKNOWN_ORIENTATION ) {
						nSkipped++;
						continue;
					}
					// We need to load one of the image variants (mid, thumb)
					// and then compute and set the orientation
					String fullRelPath = ii.path + "/" + ii.filename;
			        ImageIcon image = new ImageIcon(pathBase+fullRelPath);
			        ii.setWidth(image.getIconWidth());
			        ii.setHeight(image.getIconHeight());
			        debug(3, ii.toString() );
					nProcessed++;
					if( nProcessed >= nToProcessMax )
						break;
					if( --nTillReport <= 0 ) {
				        debug(1, "Processed " + nProcessed + " entries" );
						nTillReport = nToReport;
					}
				}
				if( nProcessed >= nToProcessMax )
					break;
			}
			// Now it is time to write the results. User can cancel if not interested.
			String outFileName = imagePathsReader.getInFile();
			chooser.setSelectedFile(new File( outFileName ));
			chooser.setFileFilter(sqlfilter);
		    int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
		    	// TODO Check for existing file overwrite
		        setStatus("Saving Updated Image Info to file: " + filename);
		        imagePathsReader.writeContents(filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Computing Aspect ratios: ", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	private JMenuItem getSaveSQLMediaInsertFileMenuItem() {
		if (saveSQLMediaInsertFileMenuItem == null) {
			saveSQLMediaInsertFileMenuItem = new JMenuItem();
			saveSQLMediaInsertFileMenuItem.setText("Save SQL Media Insert File...");
			saveSQLMediaInsertFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveSQLMediaInsertFile();
				}
			});
		}
		saveSQLMediaInsertFileMenuItem.setEnabled(false);
		return saveSQLMediaInsertFileMenuItem;
	}

	private void saveSQLMediaInsertFile() {
		if( imagePathsReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		debug(1,"Saving SQL Media Insert File...");
		try {
			String outFileName = imagePathsReader.getInFile();
			int lastSlash = outFileName.lastIndexOf(File.separatorChar);
			if( lastSlash >= 0 )
				outFileName = outFileName.substring(0, lastSlash+1) + "mediaInsert.sql";
			else
				outFileName = "mediaInsert.sql";
			chooser.setSelectedFile(new File( outFileName ));
			chooser.setFileFilter(sqlfilter);
		    int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
		    	// TODO Check for existing file overwrite
		        setStatus("Saving SQL Media Load File to file: " + filename);
		        imagePathsReader.writeSQLMediaTableInsertFile(filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Saving SQL Media Insert File: ", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	private JMenuItem getSaveSQLMediaLoadFileMenuItem() {
		if (saveSQLMediaLoadFileMenuItem == null) {
			saveSQLMediaLoadFileMenuItem = new JMenuItem();
			saveSQLMediaLoadFileMenuItem.setText("Save SQL Media Load File...");
			saveSQLMediaLoadFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveSQLMediaLoadFile();
				}
			});
		}
		saveSQLMediaLoadFileMenuItem.setEnabled(false);
		return saveSQLMediaLoadFileMenuItem;
	}

	private void saveSQLMediaLoadFile() {
		if( imagePathsReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		debug(1,"Saving SQL Media Load File...");
		try {
			String outFileName = imagePathsReader.getInFile();
			int lastSlash = outFileName.lastIndexOf(File.separatorChar);
			if( lastSlash >= 0 )
				outFileName = outFileName.substring(0, lastSlash+1) + "mediaLoadFile.sql";
			else
				outFileName = "mediaLoadFile.sql";
			chooser.setSelectedFile(new File( outFileName ));
			chooser.setFileFilter(sqlfilter);
		    int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
		    	// TODO Check for existing file overwrite
		        setStatus("Saving SQL Media Load File to file: " + filename);
		        imagePathsReader.writeSQLMediaTableLoadFile(filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Saving SQL Media Load File: ", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getBuildObjectSQLMenuItem() {
		if (buildObjectSQLMenuItem == null) {
			buildObjectSQLMenuItem = new JMenuItem();
			buildObjectSQLMenuItem.setText("Build Objects SQL...");
			buildObjectSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					buildObjectSQL();
				}
			});
		}
		return buildObjectSQLMenuItem;
	}

	/**
	 * Run through the metadata file, and gather up the ID, ObjectNumber,
	 * Name/Title and Description information for insertion into the DB.
	 */
	private void buildObjectSQL() {
    	String filename = null;
    	String basefilename = null;
    	String extension = ".sql";
    	String currFilename = null;
    	int iOutputFile;
    	int nObjsOutMax = Integer.MAX_VALUE;
    	int nObjsProcessedFile = 0;
    	int nObjsProcessedTotal = 0;
    	int nObjsPerDumpFile = 50000;
    	String descMergeSeparator = "|";
		try {
			debug(1,"Build Objects SQL...");
			// We need to pick an output file
			if( openMDFile() && metaDataReader != null ) {
				// Have an open file - let's check the columns
				// We need ObjectID, ObjectNumber, ObjectName, Description
				// TODO These should be mapped in the ColConfig file, and then
				// TODO we should check that the named columns are present.
				int objIDCol = 0;
				int objNumCol = 4;
				int objNameCol = 5;
				int objDescCol = 12;
				if( !columnNames[objIDCol].equalsIgnoreCase("ObjectID")
					|| !columnNames[objNumCol].equalsIgnoreCase("ObjectNumber")
					|| !columnNames[objNameCol].equalsIgnoreCase("ObjectName")
					|| !columnNames[objDescCol].equalsIgnoreCase("Description"))
					throw new RuntimeException("BuildObjectSQL columns not as expected!");
				/*
			    int returnVal = chooser.showSaveDialog(getJFrame());
			    if(returnVal != JFileChooser.APPROVE_OPTION)
			    	return;
		    	filename = chooser.getSelectedFile().getPath();
		    	*/
				filename = metaDataReader.getFileName();
		    	int iDot = filename.lastIndexOf('.');
		    	if( iDot<=0 )
		    		throw new RuntimeException("BuildObjectSQL: bad output filename!");
		    	/*
		    	extension = filename.substring(iDot);
		    	if(( filename.charAt(iDot-1) == '1' )
		    		&& ( filename.charAt(iDot-2) == '_' ))
		    		iDot -= 2;
		    	 */
		    	basefilename = filename.substring(0, iDot)+'_';
				BufferedWriter objWriter = null;
		    	iOutputFile = 0;
				ArrayList<String> nextLine;
				boolean fFirst = true;
				boolean fWithNewlines = true;
				int lastIDVal = -1;
				ArrayList<String> lastStrings = new ArrayList<String>();
				while((nextLine = metaDataReader.getNextLineAsColumns()) != null ) {
					if(nextLine.get(objIDCol).length() == 0) {
						//debug( )
						debug(2,"Skipping line with empty id" );
						continue;
					}
					int id = Integer.parseInt(nextLine.get(objIDCol));
					if( lastIDVal < 0 ) {
						lastStrings.addAll(nextLine);
						lastIDVal = id;
						continue;
					}
					if( id == lastIDVal ) {
						// TODO we should configure which columns go into SQL description
						// We cannot merge objNum or Name, but we will allow out of order
						// primary lines, and set these fields
						if( lastStrings.get(objNumCol).length() == 0 )
							lastStrings.set(objNumCol, nextLine.get(objNumCol));
						if( lastStrings.get(objNameCol).length() == 0 )
							lastStrings.set(objNameCol, nextLine.get(objNameCol));
						// Merge descriptions
						String desc = nextLine.get(objDescCol);
						if(desc.length()!=0) {	// If new token is empty ("") skip it
							String lastDesc = lastStrings.get(objDescCol);
						// BUG - need not use get(i) over and over)
							if(lastDesc.length()==0)		// If old token is empty (""), just set
								lastStrings.set(objDescCol, desc);
							else if(!desc.equalsIgnoreCase(lastDesc)
									&& !lastDesc.contains(desc)) {
								lastStrings.set(objDescCol, lastDesc+descMergeSeparator+desc);
								debug(2,"Combining descriptions for id: "+id+
											" ["+lastStrings.get(objDescCol)+"]");
							}
						}
						continue;
					}
					if( objWriter == null ) {
			        	nObjsProcessedFile = 0;
			    		iOutputFile++;
						fFirst = true;
			    		currFilename = basefilename + iOutputFile + extension;
						objWriter = new BufferedWriter(new FileWriter( currFilename ));
						//objWriter.append("USE "+dbName+";");
						//objWriter.newLine();
						// INSERT ALL in order:
						// id, objnum, name, description, thumb_path, med_img_path, lg_img_path, creation_time
						objWriter.append("INSERT IGNORE INTO objects(id, objnum, name, description, img_path, creation_time) VALUES" );
						objWriter.newLine();
					}
					// Otherwise, now we output the info for the last Line, and then transfer
					// the next line to Last line and loop
					dumpSQLForObject( lastIDVal, lastStrings, objWriter, fFirst, fWithNewlines );
					fFirst = false;
					lastIDVal = id;
					lastStrings = nextLine;
					nObjsProcessedFile++;
					nObjsProcessedTotal++;
					if( nObjsProcessedTotal >= nObjsOutMax ) {
						if(lastStrings.size() > 0)
							dumpSQLForObject( lastIDVal, lastStrings, objWriter, false, fWithNewlines );
						break;
					}
					if( nObjsProcessedFile >= nObjsPerDumpFile ) {
						objWriter.append(";");
						objWriter.flush();
						objWriter.close();
						objWriter = null;	// signal to open next one
						debug(1,"Wrote "+nObjsProcessedFile+" objects to file: "+currFilename);
					}
				}
				if( objWriter != null ) {
					objWriter.append(";");
					objWriter.newLine();
					objWriter.append("SHOW WARNINGS;");
					objWriter.flush();
					objWriter.close();
				}
				debug(1,"Wrote "+nObjsProcessedTotal+" total objects.");
			}
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Could not create or other problem writing: "+
										((filename==null)?"null":filename));
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}


	private int dumpSQLInsertForObjectCat( int id, TaxoNode category, boolean inferred, int reliability,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines ) {
		String entry = "";
		try {
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			// obj_id, cat_id, inferred, reliability
			entry = "("+id+","+category.id+(inferred?",1,":",0,")+reliability+")";
			writer.append(entry);
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Problem writing obj_cats entry: "+entry );
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
								"Categorization SQL Dump File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		// When we add the work to add the inferred cats, this will return non-zero
		return 1;
	}

	private int dumpSQLLoadFileForObjectCat( int id, TaxoNode category, boolean inferred, int reliability,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines ) {
		String entry = "";
		String sep = "|";
		String newLn = "\n";
		try {
			// obj_id, cat_id, inferred, reliability
			entry = id+sep+category.id+sep+(inferred?"1":"0")+sep+reliability+newLn;
			writer.append(entry);
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Problem writing obj_cats entry: "+entry );
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
								"Categorization SQL Dump File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		// When we add the work to add the inferred cats, this will return non-zero
		return 1;
	}

	private void dumpSQLForObject( int id, ArrayList<String> line, BufferedWriter writer,
									boolean fFirst, boolean fWithNewlines ) {
		try {
			int objNumCol = 4;
			int objNameCol = 5;
			int objDescCol = 12;
			String name = (line.get(objNameCol).length() == 0)?"(no name)"
					             :(line.get(objNameCol).replace("\"", "\\\"").replace("'", "\\'"));
			ArrayList<ImageInfo> imgInfo = null;
			if( imagePathsReader != null )
				imgInfo = imagePathsReader.GetInfoForID(id);
			// TODO Need to be smarter about getting Description
			// For now, just take the description column and fold all white space
			// (especially the newlines) into a single space.
			String description = line.get(objDescCol).replaceAll("[\\s]+", " " );
			//description = description.replaceAll("\\(['\"]\\)", "\\\1");
			description = description.replace("\"", "\\\"");
			description = description.replace("'", "\\'");
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			writer.append("("+id+",\""+line.get(objNumCol).trim()+"\",\""+name+"\",\""
							+description+"\",");
			if(imgInfo == null)
				writer.append("null,");
			else {
				ImageInfo info = imgInfo.get(0);
				String imgPath = "\""+info.path + "/" + info.filename+"\",";
				writer.append(imgPath);
			}
			writer.append("now())");
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Could not create or other problem writing MD SQL file." );
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"MD SQL Dump File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCategorizeObjectsMenuItem() {
		if (categorizeObjectsMenuItem == null) {
			categorizeObjectsMenuItem = new JMenuItem();
			categorizeObjectsMenuItem.setText("Categorize Objects...");
			categorizeObjectsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					categorizeObjects();
				}
			});
		}
		return categorizeObjectsMenuItem;
	}

	private void categorizeObjects() {
		debug(1,"Categorize Objects and generate association SQL...");
		if( facetMapHashTree == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "You cannot categorize before the ontology has been loaded\n"
										+ "Please load the ontology and then run this command.",
				"Categorize error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Open the dump file
		if( openMDFile() && metaDataReader != null ) {
			for( String facetName : facetMapHashTree.GetFacetNames()) {
				metaDataReader.resetToLine1();
				categorizeForFacet(facetName);
			}
		}
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
					/*
					String test = "each had; some more etc. and then each work. Now hello.";
					String noisetokens = "^each | each | etc\\.";
					test = test.replaceAll(noisetokens, " ");
					debug(3,"After noise reduction: " + test);
					String sepregex = "[;,\\.]|and";
					String[] subtokens = test.split(sepregex);
					debug(3,"Resulting tokens: ");
					for( String str : subtokens )
						debug(3,"["+str.trim()+"]");
					 */
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog
	 *
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setSize(new Dimension(526, 338));
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 4;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.ipady = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridheight = 2;
			gridBagConstraints1.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints1.insets = new Insets(3, 0, 0, 0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 2, 0);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 233;
			gridBagConstraints.ipady = 73;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weighty = 4.0;
			gridBagConstraints.gridx = 0;
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setColumns(1);
			aboutContentPane = new JPanel();
			aboutContentPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			aboutContentPane.setLayout(new GridBagLayout());
			aboutContentPane.add(getAboutVersionLabel(), gridBagConstraints);
			aboutContentPane.add(getAboutText(), gridBagConstraints1);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Delphi Import Manager Version 0.1");
			aboutVersionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}


	/**
	 * This method initializes buildUsageReportDialog
	 *
	 * @return javax.swing.JDialog
	 */
	private JDialog getBuildUsageReportDialog() {
		if (buildUsageReportDialog == null) {
			buildUsageReportDialog = new JDialog(getJFrame(), true);
			buildUsageReportDialog.setTitle("Build Usage Report");
			buildUsageReportDialog.setSize(new Dimension(418, 338));
			buildUsageReportDialog.setContentPane(getBuildUsageReportPane());
		}
		return buildUsageReportDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getBuildUsageReportPane() {
		if (buildUsageReportContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 20, 2, 20);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 33;
			gridBagConstraints.ipady = 10;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weighty = 4.0;
			gridBagConstraints.gridx = 0;
			GridBagLayout gridBagLayout = new GridBagLayout();
			buildUsageReportContentPane = new JPanel();
			buildUsageReportContentPane.setLayout(gridBagLayout);
			buildUsageReportContentPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			buildUsageReportContentPane.add(getBuildUsageReportLabel(), gridBagConstraints);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(10, 20, 2, 20);
			gridBagConstraints2.ipadx = 20;
			gridBagConstraints2.ipady = 10;
			gridBagConstraints2.gridheight = 1;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weighty = 4.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			buildUsageReportContentPane.add(getBURColumnListScrollPane(), gridBagConstraints2);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(10, 20, 2, 20);
			gridBagConstraints3.ipadx = 10;
			gridBagConstraints3.ipady = 2;
			gridBagConstraints3.gridheight = 1;
			gridBagConstraints3.fill = GridBagConstraints.EAST;
			gridBagConstraints3.weighty = 4.0;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			buildUsageReportContentPane.add(getBuildReportButton(), gridBagConstraints3);

		}
		return buildUsageReportContentPane;
	}

	/**
	 * This method initializes burColListScrollPane
	 *
	 * @return javax.swing.JLabel
	 */
	private JScrollPane getBURColumnListScrollPane() {
		if (burColListScrollPane == null) {
			burColListScrollPane = new JScrollPane(getBURColumnList(),
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		}
		return burColListScrollPane;
	}

	/**
	 * This method initializes burColumnList
	 *
	 * @return javax.swing.JLabel
	 */
	private JList getBURColumnList() {
		if (burColumnList == null) {
			String[] allButObjID = new String[columnNames.length-1];
			System.arraycopy(columnNames, 1, allButObjID, 0, columnNames.length-1);
			burColumnList = new JList(allButObjID);
			burColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
			if( allButObjID.length > 6 )
				burColumnList.setVisibleRowCount(6);
		}
		return burColumnList;
	}

	/**
	 * This method initializes buildUsageReportLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getBuildUsageReportLabel() {
		if (buildUsageReportLabel == null) {
			buildUsageReportLabel = new JLabel();
			buildUsageReportLabel.setText("Choose a column for which to build a usage report:");
			buildUsageReportLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			buildUsageReportLabel.setHorizontalAlignment(SwingConstants.LEFT);
			java.awt.Font font = buildUsageReportLabel.getFont();
			buildUsageReportLabel.setFont(font.deriveFont(font.getSize2D()*(float)1.2));
		}
		return buildUsageReportLabel;
	}


	/**
	 * This method initializes buildReportButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBuildReportButton() {
		if (buildReportButton == null) {
			buildReportButton = new JButton("Build Report");
			buildReportButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					burColListScrollPane.setEnabled(false);
					buildReportButton.setEnabled(false);
					// Note that we remove the first ObjectID column from the list
					// to build the UI.
					int colIdx = 1+ burColumnList.getSelectedIndex();
					if( buildReport(colIdx) )
						buildUsageReportDialog.setVisible(false);
					burColListScrollPane.setEnabled(true);
					buildReportButton.setEnabled(true);
				}
			});
		}
		return buildReportButton;
	}


	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}
	 */

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}
	 */

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}
	 */

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("Open");
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					Event.CTRL_MASK, true));
			openMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openVocabFile();
				}
			} );
		}
		return openMenuItem;
	} */

	protected boolean openOntologyFile() {
		boolean opened = false;
		try {
			chooser.setFileFilter(xmlfilter);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
		    	String filename = chooser.getSelectedFile().getPath();
		        debug(1,"Scanning vocabulary from file: " + filename);
		        setStatus("Scanning vocabulary from file: " + filename);
				Document facetMapDoc = builder.parse(filename);
				facetMapHashTree = new DoubleHashTree();
		        facetMapHashTree.PopulateFromFacetMap(facetMapDoc);
		        String s1 = "Found a total of " + facetMapHashTree.totalTermCount()
        		+ " terms for "
        		+ facetMapHashTree.totalConceptCount() + " concepts.";
		        setStatus(s1);
		        debug(1, s1);
		        opened = true;
		    }
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error" + ", line " +
                spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println("   " + spe.getMessage());

            // Use the contained exception, if any
            Exception x = spe;

            if (spe.getException() != null) {
                x = spe.getException();
            }

            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated by this application
            // (or a parser-initialization error)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
		} catch( ParserConfigurationException pce ) {
			JOptionPane.showMessageDialog(getJFrame(), "Could not create output XML file",
					"Open VocabularyFile Error", JOptionPane.ERROR_MESSAGE);
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return opened;
	}

	protected boolean openVocabFile() {
		boolean opened = false;
		try {
			chooser.setFileFilter(null);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				vocabOutputDoc = builder.newDocument();
				Node pi = vocabOutputDoc.createProcessingInstruction("xml-stylesheet",
					       "type=\"text/xsl\" HREF=\"FacetMapToNavUI.xsl\"" );
				vocabOutputDoc.appendChild(pi);
		    	String filename = chooser.getSelectedFile().getPath();
		        debug(1,"Scanning vocabulary from file: " + filename);
		        setStatus("Scanning vocabulary from file: " + filename);
		        vocabTermsReader = new VocabTermsReader(filename);
		        vocabTermsReader.addSkipTerm("PAHMA Attributes");
		        vocabTermsReader.addSkipTerm("Object Attributes");
		        vocabHashTree =
		        	vocabTermsReader.readTerms( Integer.MAX_VALUE, vocabOutputDoc,
		        			"PAHMA AUT Collection Facets" );
		        String s1 = "Scanned a total of " + vocabHashTree.totalTermCount()
		        		+ " terms for "
		        		+ vocabHashTree.totalConceptCount() + " concepts.";
		        setStatus(s1);
		        debug(1, s1);
		        opened = true;
		    }
		} catch( ParserConfigurationException pce ) {
			JOptionPane.showMessageDialog(getJFrame(), "Could not create output XML file",
					"Open VocabularyFile Error", JOptionPane.ERROR_MESSAGE);
            pce.printStackTrace();
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return opened;
	}

	protected boolean openMDConfigFile() {
		boolean opened = false;
		try {
			chooser.setFileFilter(xmlfilter);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
		    	File file = chooser.getSelectedFile();
		    	String filename = file.getPath();
		        debug(2,"Scanning column config info from file: " + filename);
		        setStatus("Scanning column config info from file: " + filename);
				Document colConfigDoc = builder.parse(file);
				DumpColumnConfigInfo.PopulateFromConfigFile(colConfigDoc);
				debug(2,"Col Sep is: ["+(char)(DumpColumnConfigInfo.getColumnSeparator())+"]");
				if( _debugLevel>=2 ) {
					System.out.print("Token seps for ObjectName are: " );
					ArrayList<String> tokenSeps = DumpColumnConfigInfo.getTokenSeparators("ObjectName");
					for( String sep:tokenSeps ) {
						System.out.print("["+sep+"] " );
					}
					System.out.println();
				}
		        opened = true;
		    }
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error" + ", line " +
                spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println("   " + spe.getMessage());

            // Use the contained exception, if any
            Exception x = spe;

            if (spe.getException() != null) {
                x = spe.getException();
            }

            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated by this application
            // (or a parser-initialization error)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
		} catch( ParserConfigurationException pce ) {
			JOptionPane.showMessageDialog(getJFrame(), "Could not create output XML file",
					"Open VocabularyFile Error", JOptionPane.ERROR_MESSAGE);
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return opened;
	}

	protected boolean openMDFile() {
		boolean opened = false;
		try {
			chooser.setFileFilter(null);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
		        debug(2,"Scanning column names from file: " + filename);
		        setStatus("Scanning column names from file: " + filename);
		        int colSep = DumpColumnConfigInfo.getColumnSeparator();
		        if( !Character.isDefined(colSep) )
					throw new RuntimeException( "No Column separator configured!" );
		        metaDataReader = new MetaDataReader(filename, colSep );
		        columnNames = metaDataReader.readColumnHeaders();
		        setStatus("Found: " + columnNames.length + " columns.");
		        if( !(columnNames[0]).equalsIgnoreCase("objectid") )
					throw new RuntimeException( "Column 0 in metadata dump file is not ObjectID: "
												+ columnNames[0]);
		        debug(2,"Found: " + columnNames.length + " columns.");
		        if( _debugLevel >= 2 )
			        for( String str : columnNames ) {
				        System.out.println("Column name: " + str );
			        }
		        opened = true;
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return opened;
	}

	protected boolean buildReport( int colIndex ) {
		boolean built = false;
		try {
			setStatus( "Building usage report...");
			Counter<String> vocabCounts = metaDataReader.compileUsageForColumn(colIndex, 2, 100, -1 );
			String outFileName = metaDataReader.getFileName();
			int iDot = outFileName.lastIndexOf('.');
			if( iDot >0 )
				outFileName = outFileName.substring(0, iDot);
			outFileName += "_"+(columnNames[colIndex].replaceAll("\\W", "_"))+"_Usage.txt";
			chooser.setSelectedFile(new File( outFileName ));
    		String filename = getSafeOutfile(null);
    		if( filename != null ) {
    			debug(1,"Saving column usage info to file: " + filename);
		        setStatus("Saving usage report to file: " + filename);
		        try {
		        	// TODO Make this a UTF8 file out.
					BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
					vocabCounts.write(writer, true, 0, true);
					writer.flush();
					writer.close();
				} catch( IOException e ) {
		            e.printStackTrace();
					throw new RuntimeException("Could not create output file: " + outFileName);
				}
 		        built = true;
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Build Usage Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return built;
	}

	protected void saveVocabAsSQL() {
		boolean fWithNewlines = true;	// Easier for debugging output.
		try {
			setStatus( "Building SQL Vocab load file...");
    		String filename = getSafeOutfile(sqlfilter);
    		if( filename == null )
    			return;
	        setStatus("Saving SQL Vocab Load to file: " + filename);
	        try {
				BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
				// Set up the correct DB
				 writer.append("USE "+dbName+";");
				 writer.newLine();
				 writer.append("truncate facets;");
				 writer.newLine();
				// First, let's populate the facets table
				 writer.append("INSERT INTO facets( id, name, display_name, description, notes ) VALUES" );
				 boolean fFirst = true;
				for( Facet facet : facetMapHashTree.GetFacets() ) {
					if( fFirst )
						fFirst = false;
					else
						writer.append(',');
					if(fWithNewlines)
						writer.newLine();
					writer.append("("+facet.id+",\""+facet.name+"\",\""+facet.displayName+"\", \'"
										+facet.description+"\', \'" + facet.notes + "\')");
				}
				writer.append(';');
				writer.newLine();
				 writer.append("truncate categories;");
				 writer.newLine();
				// Now, let's populate the categories table
				writer.append("INSERT INTO categories(id, parent_id, name, display_name, facet_id, select_mode, always_inferred) VALUES" );
				writer.newLine();
				fFirst = true;
				for( Facet facet : facetMapHashTree.GetFacets() ) {
					for( TaxoNode child : facet.children ) {
						// for each child of the facet, dump. Mark as a root in facet.
						dumpSQLForVocabNode( child, writer, true, fFirst, fWithNewlines );
						fFirst = false;
					}
				}
				writer.append(';');
				writer.newLine();
				writer.flush();
				writer.close();
				debug(1, "Finished writing SQL Dump to file: "+filename);
				setStatus( "Finished writing SQL Dump to file: "+filename);
			} catch( IOException e ) {
	            e.printStackTrace();
				throw new RuntimeException("Could not create (or write to) output file: " + filename);
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Build Usage Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	// We insert all the fields in declaration order:
	// id, parent_id, name, display_name, facet_id, select_mode, always_inferred
	// Since the facet roots are TaxoNodes, the effective roots in all facets
	// (i.e., the first level nodes, all will have non-null parents).
	// We pass in a flag to simplify this (rather than checking if parent->parent is null).
	// TODO Need to think about getting synonyms into DB to support
	//  UI that lets people search for categories.
	// What about token-based pattern model?
	private void dumpSQLForVocabNode( TaxoNode node, BufferedWriter writer,
			boolean fRootNode, boolean fFirst, boolean fWithNewlines ) {
		try {
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			String parentID = (fRootNode || node.parent == null)?"null":Integer.toString(node.parent.id);
			String select = (node.selectSingle)? "'single'":"'multiple'";
			int infer = (node.inferredByChildren)? 1:0;
			writer.append("("+node.id+","+parentID+",\""+node.name+"\",\""+node.displayName+"\","
							+node.facetid+","+select+","+infer+")");
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					dumpSQLForVocabNode( child, writer, false, false, fWithNewlines );
				}
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Error writing to SQL dump file." );
		}
	}

	protected String getSafeOutfile( FileNameExtensionFilter filter ) {
    	String filename;
		chooser.setFileFilter(filter);
		while(true) {
			int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal != JFileChooser.APPROVE_OPTION)
		    	return null;
		    else {
		    	filename = chooser.getSelectedFile().getPath();
		    	// TODO Check for existing file overwrite
		    	File newFile = new File( filename );
		    	if( !newFile.exists() )
		    		break;
		    	else {
		    		int answer = JOptionPane.showConfirmDialog(getJFrame(), "File exists:\n" + filename
		    				+"\nAre you sure you want to overwrite this file?",
							"Saving SQL Load to file", JOptionPane.YES_NO_CANCEL_OPTION);
		    		if( answer == JOptionPane.YES_OPTION )
		    			break;
		    		else if( answer == JOptionPane.CANCEL_OPTION )
		    			return null;
		    		// else will loop to try again
		    	}
		    }
	    }
		return filename;
	}

	protected void saveVocabHooksOrExclusionsAsSQL( boolean fSaveHooks ) {
		boolean fWithNewlines = true;	// Easier for debugging output.
		try {
    		String filename = getSafeOutfile(sqlfilter);
    		if( filename == null )
    			return;
    		String tablename = fSaveHooks ? "hooks":"exclusions";
	        setStatus("Saving SQL for "+tablename+" to file: " + filename);
	        try {
	    		setStatus( "Building SQL "+tablename+" Load file...");
				BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
				// Set up the correct DB
				writer.append("USE "+dbName+";");
				writer.newLine();
				writer.append("truncate "+tablename+";");
				writer.newLine();
				// First, let's populate the hooks table
				writer.append("INSERT INTO "+tablename+" ( cat_id, token ) VALUES" );
				writer.newLine();
				boolean fFirst = true;
				for( Facet facet : facetMapHashTree.GetFacets() ) {
					for( TaxoNode child : facet.children ) {
						// for each child of the facet, dump.
						// Once something has been saved, fFirst will be marked false.
						fFirst = dumpHooksOrExclusionsSQLForVocabNode( child, fSaveHooks, writer,
															true, fFirst, fWithNewlines );
					}
				}
				writer.append(';');
				writer.newLine();
				writer.flush();
				writer.close();
				debug(1, "Finished writing SQL Dump to file: "+filename);
				setStatus( "Finished writing SQL Dump to file: "+filename);
			} catch( IOException e ) {
	            e.printStackTrace();
				throw new RuntimeException("Could not create (or write to) output file: " + filename);
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocab Hooks Or Exclusions As SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	// We insert all the hooks, with category id associations
	// This will be inefficient with all the entailment phrases, but
	// it is just db rows and may make some search (kwd match to category) easier.
	private boolean dumpHooksOrExclusionsSQLForVocabNode( TaxoNode node, boolean fSaveHooks, BufferedWriter writer,
			boolean fRootNode, boolean fFirst, boolean fWithNewlines ) {
		try {
			if( fSaveHooks ) {
				// Save the display name, even if marked as nomatch, since things like
				// design motifs should show up if they enter "bird" in kwd.
				if( !fFirst ) {
					writer.append(',');
					if( fWithNewlines )
						writer.newLine();
				}
				writer.append("("+node.id+",\""+node.displayName+"\")");
				fFirst = false;
				// Save any synonyms as hooks
				if( node.synset != null )
					for( String hook : node.synset ) {
						if( !fFirst ) {
							writer.append(',');
							if( fWithNewlines )
								writer.newLine();
						}
						writer.append("("+node.id+",\""+hook+"\")");
						fFirst = false;
					}
			} else {
				// Save any exclusions
				if( node.exclset != null )
					for( String excl : node.exclset ) {
						if( !fFirst ) {
							writer.append(',');
							if( fWithNewlines )
								writer.newLine();
						}
						writer.append("("+node.id+",\""+excl+"\")");
						fFirst = false;
					}
			}
			// Now recurse to catch children
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					fFirst = dumpHooksOrExclusionsSQLForVocabNode( child, fSaveHooks, writer,
																	false, fFirst, fWithNewlines );
				}
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Error writing to SQL dump file." );
		}
		return fFirst;
	}

	protected void saveVocabAsXML() {
		try {
			chooser.setFileFilter(xmlfilter);
		    int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String filename = chooser.getSelectedFile().getPath();
				/*
				int lastDot = filename.lastIndexOf('.');
				String outfilename = null;
				if( lastDot < 0 )
					outfilename = filename + "_tax.xml";
				else
					outfilename = filename.substring(0, lastDot) + "_tax.xml";
				*/
		        writeXMLDocToFile(vocabOutputDoc, filename);
		        setStatus("Saved Vocabulary as XML to file: " + filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	protected void writeXMLDocToFile(Document outputDoc, String outFileName) {
		try {
			FileWriter writer = new FileWriter( outFileName );
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

	/**
	 * @param facetName Name of a facet
	 */
	private void categorizeForFacet(String facetName ) {
    	String filename = null;
    	String basefilename = null;
    	String extension = ".sql";
    	String currFilename = null;
    	boolean fDumpAsSQLInsert = false;
    	int iOutputFile;
    	int nObjCatsOutMax = Integer.MAX_VALUE;
    	int nObjCatsReport = 100000;
    	int nObjCatsTillReport;
    	// Each line in the output file is about 20 chars max.
    	// We want a max of 5 MB files, so we can take up to
    	// 5000000 / 20 = 250000.
    	// To be safer, we'll set it at 200K
    	int nObjsCatsPerDumpFile = Integer.MAX_VALUE;
    	int nObjCatsDumpedToFile = 0;
    	int nObjCatsDumpedTotal = 0;
    	// TODO get this from col config
    	int objIDCol = 0;
    	// Hold the information for the Facet(s)
    	DumpColumnConfigInfo[] colDumpInfo = null;
		try {
			// Open the dump file
			if( columnNames == null || columnNames.length < 2 )
				throw new RuntimeException("categorizeForFacet: DumpColConfig info not yet built!");
			colDumpInfo = DumpColumnConfigInfo.getAllColumnInfo(columnNames);
			int nCols = columnNames.length;
			boolean[] skipCol = new boolean[nCols];
			for( int iCol=0; iCol<nCols; iCol++ ) {
				skipCol[iCol] = ( colDumpInfo[iCol].columnReliabilityForFacet(facetName) == 0);
			}
			filename = metaDataReader.getFileName();
			// Find last slash or backslash (to allow for windows paths), to get basepath
	    	int iSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
	    	//int iDot = filename.lastIndexOf('.');
	    	if( iSlash<=0 )
	    		throw new RuntimeException("categorizeForFacet: odd input filename:\n"+filename);
	    	basefilename = filename.substring(0, iSlash+1)+"obj_cats."+facetName+"_";
			BufferedWriter objCatsWriter = null;
	    	iOutputFile = 0;
			ArrayList<String> nextLine;
			boolean fFirst = true;
			boolean fWithNewlines = true;
			int lastIDVal = -1;
			ArrayList<String> lastStrings = new ArrayList<String>();
			HashMap<TaxoNode, Float> matchedCats = new HashMap<TaxoNode, Float>();
			nObjCatsTillReport = nObjCatsReport;
			while((nextLine = metaDataReader.getNextLineAsColumns()) != null ) {
				if(nextLine.get(objIDCol).length() == 0) {
					debug(2,"Skipping line with empty id" );
					continue;
				}
				int id = Integer.parseInt(nextLine.get(objIDCol));
				if( lastIDVal < 0 ) {
					lastStrings.addAll(nextLine);
					lastIDVal = id;
					continue;
				}
				if( id == lastIDVal ) {
					// Consider merging the two lines, but only for the columns we care about.
					// Separate them by '|' chars to ensure we tokenize in next step
					// Note that we always skip col 0, the ID column
					for(int i=1; i<nCols; i++ ) {
						if(skipCol[i])				// Do not bother with misc cols
							continue;
						String nextToken = nextLine.get(i);
						if(nextToken.length()==0)	// If new token is empty ("") skip it
							continue;
						String last = lastStrings.get(i);
						if(last.length()==0)		// If old token is empty (""), just set
							lastStrings.set(i, nextLine.get(i));
						else if(nextLine.get(i).equalsIgnoreCase(last)
								|| last.contains(nextLine.get(i)))
							continue;
						else
							lastStrings.set(i, last+"|"+nextLine.get(i));
						debug(2,"Combining "+columnNames[i]+ " for id: "+id+
											" ["+lastStrings.get(i)+"]");
					}
					continue;
				}
				if( objCatsWriter == null ) {
					nObjCatsDumpedToFile = 0;
		    		iOutputFile++;
					fFirst = true;
		    		currFilename = basefilename + iOutputFile + extension;
		    		objCatsWriter = new BufferedWriter(new FileWriter( currFilename ));
		    		if( fDumpAsSQLInsert ) {
			    		objCatsWriter.append("USE "+dbName+";");
			    		objCatsWriter.newLine();
			    		objCatsWriter.append("INSERT IGNORE INTO obj_cats(obj_id, cat_id, inferred, reliability) VALUES" );
			    		objCatsWriter.newLine();
		    		}
				}
				// Otherwise, now we output the info for the last Line, and then transfer
				// the next line to Last line and loop
				// We need to track the matches we have made, and the reliability assigned per column.
				// If two columns generate the same category, then we use the higher reliability.
				matchedCats.clear();
				for(int i=1; i<nCols; i++ ) {
					if(skipCol[i])				// Do not bother with misc cols
						continue;
					String source = lastStrings.get(i);
					if( source.length() <= 1 )
						continue;
					categorizeObjectForFacet( lastIDVal, source,
							facetName, colDumpInfo[i], matchedCats );
				}
				int nCatsDumped = dumpSQLForObjCatsOnFacet( lastIDVal, matchedCats,
										objCatsWriter, fFirst, fWithNewlines, fDumpAsSQLInsert );
				if( nCatsDumped > 0) {
					nObjCatsDumpedTotal += nCatsDumped;
					nObjCatsDumpedToFile += nCatsDumped;
					nObjCatsTillReport -= nCatsDumped;
					fFirst = false;
				}
				lastIDVal = id;
				lastStrings = nextLine;
				matchedCats.clear();
				if( nObjCatsDumpedTotal >= nObjCatsOutMax ) {
					if(lastStrings.size() > 0) {
						for(int i=1; i<nCols; i++ ) {
							if(skipCol[i])				// Do not bother with misc cols
								continue;
							String source = lastStrings.get(i);
							if( source.length() <= 1 )
								continue;
							categorizeObjectForFacet( lastIDVal, source,
									facetName, colDumpInfo[i], matchedCats );
						}
					}
					break;
				}
				nCatsDumped = dumpSQLForObjCatsOnFacet( lastIDVal, matchedCats,
						objCatsWriter, fFirst, fWithNewlines, fDumpAsSQLInsert );
				if( nCatsDumped > 0) {
					// Gratuitous since have already decided to quit
					// nObjsProcessedTotal += nCatsFound;
					fFirst = false;
				}
				if( nObjCatsDumpedToFile >= nObjsCatsPerDumpFile ) {
					if( fDumpAsSQLInsert )
						objCatsWriter.append(";");
					objCatsWriter.flush();
					objCatsWriter.close();
					objCatsWriter = null;	// signal to open next one
					debug(1,"Wrote "+nObjCatsDumpedToFile+" object categories to file: "+currFilename);
				} else if( nObjCatsTillReport <= 0 ) {
					debug(1,"Wrote "+nObjCatsDumpedToFile+" object categories to file: "+currFilename);
					nObjCatsTillReport = nObjCatsReport;
				}
			}
			if( objCatsWriter != null ) {
				if( fDumpAsSQLInsert ) {
					objCatsWriter.append(";");
					objCatsWriter.newLine();
					objCatsWriter.append("SHOW WARNINGS;");
				}
				objCatsWriter.flush();
				objCatsWriter.close();
			}
			debug(1,"Wrote "+nObjCatsDumpedTotal+" total object categories for facet: "+facetName);
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Could not create or other problem writing: "+
										((filename==null)?"null":filename));
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	// Returns a set of tokens and words
	private Pair<ArrayList<String>,ArrayList<String>> prepareSourceTokens( String source, DumpColumnConfigInfo colInfo ) {  //  @jve:decl-index=0:
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		// First we apply reduction. This cleans up certain oddities in the source
		String reducedSource = source;
		for(Pair<String, String> reduce : colInfo.reduceRules) {
			reducedSource = reducedSource.replaceAll(reduce.first, reduce.second);
		}
		// Next, we tokenize with the token separators
		String[] tokens_1;
		if( colInfo.tokenSeparators.size() == 0 ) {
			// throw new RuntimeException( "No Token separators for column: " + colInfo.name);
			tokens_1 = new String[1];
			tokens_1[0] = reducedSource;
		} else {
			String regex = "\\||"+colInfo.tokenSeparators.get(0);
			for( int i=1; i<colInfo.tokenSeparators.size(); i++)
				regex += "|"+colInfo.tokenSeparators.get(i);
			tokens_1 = reducedSource.split(regex);
		}
		// Next, we further split up each token on space and certain punctuation and remove the noise items
		// We also build the words list for Colors
		for( int i=0; i< tokens_1.length; i++ ){
			String[] words_1 = tokens_1[i].trim().split("[\\W&&[^\\-]]");
			if( colInfo.noiseTokens.size() == 0 ){
				tokens.add(tokens_1[i].trim());
				for( int iW=0; iW<words_1.length; iW++)
					words.add(words_1[iW].trim());
			}
			else {
				StringBuilder sb = new StringBuilder();
				for( int iW=0; iW<words_1.length; iW++)
					if( !colInfo.noiseTokens.contains(words_1[iW]) ) {
						if( iW > 0)
							sb.append(' ');
						String word = words_1[iW].trim();
						sb.append(word);
						words.add(word);
					}
				tokens.add(sb.toString());
			}
		}
		return new Pair<ArrayList<String>,ArrayList<String>>(tokens, words);
	}

	private void categorizeObjectForFacet( int id, String source,
			String facetName, DumpColumnConfigInfo colInfo, HashMap<TaxoNode, Float> matchedCats ) {
		// Tokensize the string for this column
		Pair<ArrayList<String>,ArrayList<String>> tokenPair
				= prepareSourceTokens( source, colInfo );
		float reliability = colInfo.columnReliabilityForFacet(facetName);
		// Now, we try to find a category in the hashMap for each token
		for( String token:tokenPair.first ) {
			// Test where id is 317239 - how do we match FUR?
			TaxoNode node = facetMapHashTree.FindNodeByHook(facetName,token);
			if( node == null )
				continue;
			debug(3, "Obj:"+id+" matched on token: ["+token+"] facet: "+facetName);
			// We have a candidate here. Check for the exclusions
			if( node.exclset != null ) {
				boolean fCatExcluded = false;
				for( String excl:node.exclset ) {
					// We'll have to consider whether and when to search
					// the entire string for the exclusion. May need to put
					// a flag on the exclusion terms.
					if( token.indexOf(excl) >= 0 ) {
						fCatExcluded = true;
						debug(3, "Obj:"+id+" match on token excluded on: ["+excl+"]");
						break;
					}
				}
				if( fCatExcluded )
					continue;
			}
			// We have a TaxoNode match! Update matches with this node
			updateMatches( matchedCats, reliability, node);
			// Now, consider all the implied nodes as well.
			for( int i=0; i<node.impliedNodesPending.size(); i++ ) {
				updateMatches( matchedCats, reliability, node.impliedNodes.get(i));
			}
		}
	}

	private static void updateMatches(
			HashMap<TaxoNode, Float> matchedCats,
			float baseReliability,
			TaxoNode node ) {
		// If not yet matched, or if this reliability
		// is greater than any previous match, set the value in the matchedCats.
		Float ret = matchedCats.get(node); // returns null if not in hashMap
		float priorRel = (ret==null)?0:ret.floatValue();
		if( baseReliability > priorRel )
			matchedCats.put(node, baseReliability);
	}

	private int dumpSQLForObjCatsOnFacet( int id, HashMap<TaxoNode, Float> matchedCats,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines, boolean fDumpAsSQLInsert ) {
		// We just consider each match in turn, and get all the inferred nodes.
		HashMap<TaxoNode, Float> inferredCats =  new HashMap<TaxoNode, Float>();
		for( TaxoNode node:matchedCats.keySet() ) {
			Float ret = matchedCats.get(node); // returns null if not in hashMap
			float currRel = (ret==null)?0:ret.floatValue();
			node.AddInferredNodes(inferredCats, currRel);
		}

		int nCatsMatched = 0;
		// Now we have sets of the Nodes we matched and inferred, to dump
		// We dump the mached cats that are not in the inferred map, then the inferred list
		for( TaxoNode node:matchedCats.keySet() ) {
			if( !inferredCats.containsKey(node)) {
				Float ret = matchedCats.get(node); // returns null if not in hashMap
				float reliability = (ret==null)?0:ret.floatValue();
				int iRel = Math.min(9,Math.round(10*reliability));
				if( fDumpAsSQLInsert )
					nCatsMatched += dumpSQLInsertForObjectCat(id, node, false, iRel, writer, fFirst, fWithNewlines );
				else
					nCatsMatched += dumpSQLLoadFileForObjectCat(id, node, false, iRel, writer, fFirst, fWithNewlines );
				fFirst = false;
			}
		}
		// Now emit the (remaining) inferred nodes
		for( TaxoNode node:inferredCats.keySet() ) {
			Float ret = inferredCats.get(node); // returns null if not in hashMap
			float reliability = (ret==null)?0:ret.floatValue();
			int iRel = Math.min(9,Math.round(10*reliability));
			if( fDumpAsSQLInsert )
				nCatsMatched += dumpSQLInsertForObjectCat(id, node, true, iRel, writer, fFirst, fWithNewlines );
			else
				nCatsMatched += dumpSQLLoadFileForObjectCat(id, node, true, iRel, writer, fFirst, fWithNewlines );
			fFirst = false;
		}
		return nCatsMatched;
	}


	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}
	 */

}
