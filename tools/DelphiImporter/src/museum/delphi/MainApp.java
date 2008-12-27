/**
 *
 */
package museum.delphi;

//import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Point;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;
//import org.w3c.dom.Node;

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
 * @author pschmitz
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
				+ "  Olga Amuzinskaya, Michael Black, Adrienne Hilgert, Jon Lesser, Patrick Schmitz,"
				+ " and Jerry Yu.\n"
				+ "  Thanks to Aron Roberts for documentation and production support.\n"
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
					saveImagePathSQL();
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
		if( imagePathsReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Let the user point to the base path for the images.
		chooser.setSelectedFile(new File( imagePathsReader.getInFile() ));
		chooser.setFileFilter(sqlfilter);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int returnVal = chooser.showOpenDialog(getJFrame());
		String pathBase;
	    if(returnVal != JFileChooser.APPROVE_OPTION)
	    	return;
    	pathBase = chooser.getSelectedFile().getPath();
		debug(1,"Computing Image Orientations...");
		try {
			int nToProcessMax = Integer.MAX_VALUE;
			int nToReport = 2500;
			int nTillReport = nToReport;
			int nProcessed = 0;
			Collection<ArrayList<ImageInfo>> allImgs = imagePathsReader.GetAllAsList();
			java.util.Iterator<ArrayList<ImageInfo>> allImgsIterator = allImgs.iterator();
			while( allImgsIterator.hasNext() ) {
				ArrayList<ImageInfo> imgsForId = allImgsIterator.next();
				java.util.ListIterator<ImageInfo> objImgIterator = imgsForId.listIterator();
				while( objImgIterator.hasNext() ) {
					ImageInfo ii = objImgIterator.next();
					if(ii.computeAspectR(pathBase)) {
						nProcessed++;
						if( nProcessed >= nToProcessMax )
							break;
						if( --nTillReport <= 0 ) {
					        debug(1, "Processed " + nProcessed + " entries" );
							nTillReport = nToReport;
						}
				        debug(3, ii.toString() );
					}
				}
				if( nProcessed >= nToProcessMax )
					break;
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Computing Aspect ratios: ", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveImagePathSQL() {
		// Write the current results. User can cancel if not interested.
		String filename = getSafeOutfile(imagePathsReader.getInFile(),sqlfilter);
	    if(filename != null) {
	        setStatus("Saving Updated Image Info to file: " + filename);
	        imagePathsReader.writeContents(filename);
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
    		String filename = getSafeOutfile(outFileName,sqlfilter);
		    if(filename != null) {
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
    		String filename = getSafeOutfile(outFileName,sqlfilter);
		    if(filename != null) {
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

	private void buildObjectSQL() {
		try {
			debug(1,"Build Objects SQL...");
			// We need to pick an output file
			if( openMDFile() && metaDataReader != null ) {
				SQLUtils.writeObjectsSQL(metaDataReader, imagePathsReader);
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(getJFrame(),
					"You cannot categorize before the ontology has been loaded\n"
					+ "Please load the ontology and then run this command.",
					"Categorize error", JOptionPane.ERROR_MESSAGE);
		} else if( !openMDFile() || metaDataReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(),
					"You must specify a metadata source to categorize.\n",
					"Categorize error", JOptionPane.ERROR_MESSAGE);
		} else {
			for( String facetName : facetMapHashTree.GetFacetNames()) {
				metaDataReader.resetToLine1();
				Categorizer.categorizeForFacet(metaDataReader, facetMapHashTree,
												facetName, false, dbName);
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
	 * Initialize the list of columns for which user can request to
	 * Build Usage Reports.
	 * @return javax.swing.JList set up with column names
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

	/**
	 * Opens an XML ontology file in Delphi format.
	 * Format is documented on the project site.
	 * @return TRUE if successfully opened and parsed
	 */
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

	/**
	 * Opens a special vocabulary dump file in a fairly funky format.
	 * @see VocabTermsReader for details
	 * @return TRUE if successfully opened and parsed
	 */
	protected boolean openVocabFile() {
		boolean opened = false;
		try {
			chooser.setFileFilter(null);
		    int returnVal = chooser.showOpenDialog(getJFrame());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				vocabOutputDoc = builder.newDocument();
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
		    	File file = chooser.getSelectedFile();
		    	String filename = file.getPath();
		        debug(2,"Scanning column config info from file: " + filename);
		        setStatus("Scanning column config info from file: " + filename);
		        opened = DumpColumnConfigInfo.OpenConfigFile(filename);
		    }
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
    		String filename = getSafeOutfile(outFileName,null);
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
    		String filename = getSafeOutfile(null,sqlfilter);
    		if( filename == null )
    			return;
	        setStatus("Saving SQL Vocab Load to file: " + filename);
	        try {
				BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
				SQLUtils.writeFacetsSQL( facetMapHashTree.GetFacets(),
											dbName, writer, fWithNewlines );
				SQLUtils.writeCategoriesSQL( facetMapHashTree.GetFacets(),
											dbName, writer, fWithNewlines );
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

	protected String getSafeOutfile( String suggest, FileNameExtensionFilter filter ) {
    	String filename;
    	if( suggest != null )
    		chooser.setSelectedFile(new File( suggest ));
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
    		String filename = getSafeOutfile(null,sqlfilter);
    		if( filename == null )
    			return;
    		String action = fSaveHooks ? "hooks":"exclusions";
	        setStatus("Saving SQL for "+action+" to file: " + filename);
	        try {
	    		setStatus( "Building SQL "+action+" Load file...");
				BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
				SQLUtils.writeHooksOrExclusionsSQL(facetMapHashTree.GetFacets(),
												dbName, fSaveHooks, writer, fWithNewlines);
				writer.flush();
				writer.close();
				debug(1, "Finished writing SQL Dump to file: "+filename);
				setStatus( "Finished writing SQL Dump to file: "+filename);
			} catch( IOException e ) {
	            e.printStackTrace();
				throw new RuntimeException("Could not create output file: " + filename);
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocab Hooks Or Exclusions As SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	protected void saveVocabAsXML() {
		try {
    		String filename = getSafeOutfile(null,xmlfilter);
		    if(filename != null) {
		    	XMLUtils.writeXMLDocToFile(vocabOutputDoc, filename);
		        setStatus("Saved Vocabulary as XML to file: " + filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

}
