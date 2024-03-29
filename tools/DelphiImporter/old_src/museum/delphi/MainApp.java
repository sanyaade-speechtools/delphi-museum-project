/**
 *
 */
package museum.delphi;

//import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.awt.ComponentOrientation;



/**
 * @author pschmitz
 *
 */
public class MainApp {
	private JFileChooser chooser = null;
	private FileNameExtensionFilter xmlFilter = null;
	private final String xmlWildcard = "*.xml";  //  @jve:decl-index=0:
	//private FileNameExtensionFilter sqlFilter = null;
	//private final String sqlWildcard = "*.sql";  //  @jve:decl-index=0:
	private FileNameExtensionFilter txtFilter = null;
	private final String txtWildcard = "*.txt";  //  @jve:decl-index=0:
	private FileNameExtensionFilter dpfFilter = null;
	//private final String dpfWildcard = "*.dpf";
	// TODO Let the user set this.
	private static final String dbName = "pahma_dev";  //  @jve:decl-index=0:
	private static final String settingsFilename = "./settings";
	private AppSettings appSettings = null;
	private UserProjectInfo userProjInfo = null;  //  @jve:decl-index=0:

	private Color bkgdTan = null;
	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem newMenuItem = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem copyProjMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem saveAsMenuItem = null;
	private JMenuItem exitMenuItem = null;

	private JMenu editMenu = null;
	private JMenuItem cutMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;

	private JMenu analyzeMenu = null;
	//Object Info Term Usage (requires ObjInfo file)
	//Object/Concept Association (requires ObjInfo file and Ontology. Later with load variants and an option
	private JMenuItem objInfoTermUsageMenuItem = null;
	private JMenuItem objConceptAssocMenuItem = null;
	private JMenuItem objConceptAssocByFacetMenuItem = null;

	private JMenu imagesMenu = null;
	private JMenuItem computeImageOrientationsMenuItem = null;

	private JMenu importMenu = null;
	private JMenuItem fromDBMenuItem = null;

	private JMenu exportMenu = null;
	private JMenuItem buildObjectSQLMenuItem = null;
	private JMenu conceptOntologyMenu = null;
	private JMenuItem saveOntologyAsSQLMenuItem = null;
	private JMenuItem saveVocabAsXMLMenuItem = null;
	private JMenuItem saveHooksAsSQLMenuItem = null;
	private JMenuItem saveExclusionsAsSQLMenuItem = null;
	private JMenu imagePathsMenu = null;
	private JMenuItem saveSQLMediaLoadFileMenuItem = null;
	private JMenuItem saveSQLMediaInsertFileMenuItem = null;

	private JMenu helpMenu = null;
	private JMenuItem aboutMenuItem = null;

	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="747,7"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JTextPane aboutText = null;

	private JPanel logoPanel = null;
	private JLabel jImageLabel = null;

	private JDialog buildUsageReportDialog = null;  //  @jve:decl-index=0:visual-constraint="66,566"
	private JPanel buildUsageReportContentPane = null;
	private JLabel buildUsageReportLabel = null;
	private JList burColumnList;
	private JScrollPane burColListScrollPane;
	//private JTextPane aboutText = null;
	private JButton buildReportButton;

	private JTextField jStatusBarTextField = null;

	private int _debugLevel = 1;
	private JLabel jLabel_Project = null;
	private JTextField jTextField_ProjName = null;
	private JPanel jPanel_ObjInfo = null;
	private JPanel jPanel_OntoInfo = null;
	private JPanel jPanel_ImgPathsInfo = null;
	private JTextField jTextField_ColConfigFile = null;
	private JTextField jTextField_ObjInfoFile = null;
	private JButton jButton_ColConfigBrowse = null;
	private JButton jButton_ObjInfoBrowse = null;
	private JTextField jTextField_OntologyFile = null;
	private JTextField jTextField_VocabFile = null;
	private JButton jButton_OntologyBrowse = null;
	private JButton jButton_VocabBrowse = null;
	private JTextField jTextField_ImgPathsFile = null;
	private JButton jButton_ImgPathsBrowse = null;
	private JMenu uploadMenu = null;
	private JMenuItem uploadObjMDMenuItem = null;
	private JMenu uploadOntologyMDMenu = null;
	private JMenu uploadImageMDMenu = null;
	private JMenuItem uploadOntoConceptsMDMenuItem = null;
	private JMenuItem uploadObjectConceptAssocMDMenuItem = null;
	private JMenuItem uploadImageMDMenuItem = null;
	private JMenuItem updateCachedImageMDMenuItem = null;
	private JMenuItem uploadCatCardImageMDMenuItem = null;
	private JMenuItem updateCategoryCountsMenuItem = null;
	private JMenuItem setupWebAppDBInfoMenuItem = null;
	private JMenuItem enableWebAppLockoutMenuItem = null;
	private JMenuItem releaseWebAppLockoutMenuItem = null;
	private JDialog webAppDBInfoDialog = null;  //  @jve:decl-index=0:visual-constraint="604,530"
	private JPanel webAppDBInfoContentPane = null;
	private JPanel webAppDBInfoInnerPane = null;
	private JLabel hostLabel = null;
	private JTextField webAppDB_HostNameTextField = null;
	private JLabel webAppDB_DBNameLabel = null;
	private JTextField webAppDB_DBNameTextField = null;
	private JTextField webAppDB_UserTextField = null;
	private JLabel webAppDB_UserLabel = null;
	private JLabel webAppDB_PasswordLabel = null;
	private JTextField webAppDB_PasswordTextField = null;
	private JLabel webAppDBInfoTitle1 = null;
	private JLabel webAppDBInfoTitle2 = null;
	private JButton webAppDBInfoOKButton = null;
	private JMenuItem uploadTermStatsMenuItem = null;
	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	protected void debugTrace( int level, Exception e ) {
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	/**
	 * This patches over the behavior in Swing that buttons fire on
	 * SPACE rather than ENTER (go figure).
	 * @param button
	 */
	public static void enterPressesWhenFocused(JButton button) {
	    button.registerKeyboardAction(
	        button.getActionForKeyStroke(
	            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
	            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
	            JComponent.WHEN_FOCUSED);
	    button.registerKeyboardAction(
	        button.getActionForKeyStroke(
	            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
	            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
	            JComponent.WHEN_FOCUSED);
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
				+ "This application will extract and transform data from a collections database,\n"
				+ "or from text (csv) files, with some data cleaning and noise reduction features.\n\n"
				+ "It will import ontologies from and XML file, and build the necessary structures\n"
				+ "to support concept extraction.\n\n"
				+ "The collections object info can be processed to associate objects to the ontology,\n"
				+ "and to infer additional concepts in the ontology, producing a\n"
				+ "semantic index of the collection.\n\n"
				+ "The object info, the ontology info and the semantic index can\n"
				+ "be exported for loading into a Delphi web-app database, which is used in the\n"
				+ "faceted search and browse UI of the collections browser.\n"
				+ "The tool will load the data directly into a web-app DB, if desired.\n\n"
				+ "The tool can extract image information from a collections database, or from a file,\n"
				+ "can analyze the associated images to calculate orientation (for the UI),\n"
				+ "and will export information for loading into a Delphi database.\n\n"
				+ "For more information, see the project site: \n"
				+ "  http://code.google.com/p/delphi-museum-project/.\n\n"
				+ "Delphi is maintained by:\n"
				+ "  Michael Black, Aron Roberts, and Patrick Schmitz.\n"
				+ "Delphi was created by:\n"
				+ "  Olga Amuzinskaya, Michael Black, Eun Kyoung Choe, Adrienne Hilgert, \n"
				+ "  Jon Lesser, Patrick Schmitz, and Jerry Yu.\n" );
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
			jLabel_Project = new JLabel();
			jLabel_Project.setBounds(new Rectangle(22, 15, 75, 26));
			jLabel_Project.setText("Project:");
			jLabel_Project.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 18));
			jLabel_Project.setBorder(BorderFactory.createEmptyBorder());
			String imgPath = "images/Delphi_logo2sm.jpg";
			ImageIcon logo = new ImageIcon(imgPath);
			jImageLabel = new JLabel(logo, SwingConstants.CENTER);
			jImageLabel.setBounds(new Rectangle(590, 4, 83, 60));
			jImageLabel.setBackground(bkgdTan);
			logoPanel = new JPanel();
			logoPanel.setLayout(null);
			logoPanel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			logoPanel.setBackground(bkgdTan);
			logoPanel.add(jImageLabel);
			logoPanel.add(getJStatusBarTextField());
			logoPanel.add(jLabel_Project, null);
			logoPanel.add(getJTextField_ProjName(), null);
			logoPanel.add(getJPanel_ObjInfo(), null);
			logoPanel.add(getJPanel_OntoInfo(), null);
			logoPanel.add(getJPanel_ImgPathsInfo(), null);
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
			jStatusBarTextField.setBounds(new Rectangle(0, 375, 690, 25));
			jStatusBarTextField.setBorder(BorderFactory.createLoweredBevelBorder());
			jStatusBarTextField.setBackground(java.awt.Color.white);
		}
		return jStatusBarTextField;
	}

	protected void setStatus(String status){
		jStatusBarTextField.setText(status);
	}

	/**
	 * This method initializes jTextField_ProjName
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_ProjName() {
		if (jTextField_ProjName == null) {
			jTextField_ProjName = new JTextField();
			jTextField_ProjName.setBounds(new Rectangle(100, 17, 150, 24));
			jTextField_ProjName.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			jTextField_ProjName.setBackground(bkgdTan);

			jTextField_ProjName.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					jTextField_ProjName.setBackground(bkgdTan);
					String text = jTextField_ProjName.getText();
					if( text != null && !text.isEmpty() ) {
						userProjInfo.setName(text);
				        updateUIForUserProjectInfo();
					}
				}
				public void focusGained(java.awt.event.FocusEvent e) {
					jTextField_ProjName.setBackground(Color.white);
				}
			});
		}
		return jTextField_ProjName;
	}

	/**
	 * This method initializes jPanel_ObjInfo
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ObjInfo() {
		if (jPanel_ObjInfo == null) {
			jPanel_ObjInfo = new JPanel();
			jPanel_ObjInfo.setLayout(null);
			jPanel_ObjInfo.setBounds(new Rectangle(15, 69, 660, 100));
			jPanel_ObjInfo.setBorder(BorderFactory.createLineBorder(Color.black));
			jPanel_ObjInfo.setBackground(bkgdTan);
			JLabel jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 6, 150, 26));
			jLabel.setText("Object Info:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 18));
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_ObjInfo.add(jLabel, null);
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 36, 200, 26));
			jLabel.setText("Column Configuration file:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_ObjInfo.add(jLabel, null);
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 66, 200, 26));
			jLabel.setText("Object Information file:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_ObjInfo.add(jLabel, null);
			jPanel_ObjInfo.add(getJTextField_ColConfigFile(), null);
			jPanel_ObjInfo.add(getJTextField_ObjInfoFile(), null);
			jPanel_ObjInfo.add(getJButton_ColConfigBrowse(), null);
			jPanel_ObjInfo.add(getJButton_ObjInfoBrowse(), null);
		}
		return jPanel_ObjInfo;
	}

	/**
	 * This method initializes jPanel_OntoInfo
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OntoInfo() {
		if (jPanel_OntoInfo == null) {
			jPanel_OntoInfo = new JPanel();
			jPanel_OntoInfo.setLayout(null);
			jPanel_OntoInfo.setBounds(new Rectangle(15, 179, 660, 100));
			jPanel_OntoInfo.setBorder(BorderFactory.createLineBorder(Color.black));
			jPanel_OntoInfo.setBackground(bkgdTan);
			JLabel jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 6, 250, 26));
			jLabel.setText("Vocabulary/Ontology Info:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 18));
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_OntoInfo.add(jLabel, null);
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 36, 200, 26));
			jLabel.setText("Ontology file:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_OntoInfo.add(jLabel, null);
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 66, 200, 26));
			jLabel.setText("Text (CSV) vocabulary file:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_OntoInfo.add(jLabel, null);
			jPanel_OntoInfo.add(getJTextField_OntologyFile(), null);
			jPanel_OntoInfo.add(getJTextField_VocabFile(), null);
			jPanel_OntoInfo.add(getJButton_OntologyBrowse(), null);
			jPanel_OntoInfo.add(getJButton_VocabBrowse(), null);
		}
		return jPanel_OntoInfo;
	}

	/**
	 * This method initializes jPanel_ImgPathsInfo
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ImgPathsInfo() {
		if (jPanel_ImgPathsInfo == null) {
			jPanel_ImgPathsInfo = new JPanel();
			jPanel_ImgPathsInfo.setLayout(null);
			jPanel_ImgPathsInfo.setBounds(new Rectangle(15, 289, 660, 70));
			jPanel_ImgPathsInfo.setBorder(BorderFactory.createLineBorder(Color.black));
			jPanel_ImgPathsInfo.setBackground(bkgdTan);
			JLabel jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 6, 250, 26));
			jLabel.setText("Image Paths Info:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 18));
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_ImgPathsInfo.add(jLabel, null);
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(10, 36, 200, 26));
			jLabel.setText("Image Paths file:");
			jLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel.setBorder(BorderFactory.createEmptyBorder());
			jPanel_ImgPathsInfo.add(jLabel, null);
			jPanel_ImgPathsInfo.add(getJTextField_ImgPathsFile(), null);
			jPanel_ImgPathsInfo.add(getJButton_ImgPathsBrowse(), null);
		}
		return jPanel_ImgPathsInfo;
	}

	/**
	 * This method initializes jTextField_ColConfigFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_ColConfigFile() {
		if (jTextField_ColConfigFile == null) {
			jTextField_ColConfigFile = new JTextField();
			jTextField_ColConfigFile.setBounds(new Rectangle(215, 40, 350, 21));
			jTextField_ColConfigFile.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					String text = jTextField_ColConfigFile.getText();
					if( text != null && !text.isEmpty() )
						setMDConfigFile(text);
				}
			});
		}
		return jTextField_ColConfigFile;
	}

	/**
	 * This method initializes jTextField_ObjInfoFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_ObjInfoFile() {
		if (jTextField_ObjInfoFile == null) {
			jTextField_ObjInfoFile = new JTextField();
			jTextField_ObjInfoFile.setBounds(new Rectangle(215, 70, 350, 21));
			jTextField_ObjInfoFile.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					String text = jTextField_ObjInfoFile.getText();
					if( text != null && !text.isEmpty() )
						setMDFile(text);
				}
			});
		}
		return jTextField_ObjInfoFile;
	}

	/**
	 * This method initializes jButton_ColConfigBrowse
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_ColConfigBrowse() {
		if (jButton_ColConfigBrowse == null) {
			jButton_ColConfigBrowse = new JButton();
			jButton_ColConfigBrowse.setBounds(new Rectangle(570, 40, 80, 20));
			jButton_ColConfigBrowse.setText("Browse");
			enterPressesWhenFocused(jButton_ColConfigBrowse);
			//jButton_ColConfigBrowse.setBackground(bkgdTan);
			jButton_ColConfigBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseToMDConfigFile();
				}
			});
		}
		return jButton_ColConfigBrowse;
	}

	/**
	 * This method initializes jButton_ObjInfoBrowse
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_ObjInfoBrowse() {
		if (jButton_ObjInfoBrowse == null) {
			jButton_ObjInfoBrowse = new JButton();
			jButton_ObjInfoBrowse.setBounds(new Rectangle(570, 70, 80, 20));
			jButton_ObjInfoBrowse.setText("Browse");
			enterPressesWhenFocused(jButton_ObjInfoBrowse);
			jButton_ObjInfoBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseToMDFile();
				}
			});
		}
		return jButton_ObjInfoBrowse;
	}

	/**
	 * This method initializes jTextField_OntologyFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_OntologyFile() {
		if (jTextField_OntologyFile == null) {
			jTextField_OntologyFile = new JTextField();
			jTextField_OntologyFile.setBounds(new Rectangle(215, 40, 350, 21));
			jTextField_OntologyFile.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					String text = jTextField_OntologyFile.getText();
					if( text != null && !text.isEmpty() )
						setOntologyFile(text);
				}
			});
		}
		return jTextField_OntologyFile;
	}

	/**
	 * This method initializes jTextField_VocabFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_VocabFile() {
		if (jTextField_VocabFile == null) {
			jTextField_VocabFile = new JTextField();
			jTextField_VocabFile.setBounds(new Rectangle(215, 70, 350, 21));
			jTextField_VocabFile.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					String text = jTextField_VocabFile.getText();
					if( text != null && !text.isEmpty() )
						setVocabFile(text);
				}
			});
		}
		return jTextField_VocabFile;
	}

	/**
	 * This method initializes jButton_OntologyBrowse
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_OntologyBrowse() {
		if (jButton_OntologyBrowse == null) {
			jButton_OntologyBrowse = new JButton();
			jButton_OntologyBrowse.setBounds(new Rectangle(570, 40, 80, 20));
			jButton_OntologyBrowse.setText("Browse");
			enterPressesWhenFocused(jButton_OntologyBrowse);
			jButton_OntologyBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseToOntologyFile();
				}
			});
		}
		return jButton_OntologyBrowse;
	}

	/**
	 * This method initializes jButton_VocabBrowse
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_VocabBrowse() {
		if (jButton_VocabBrowse == null) {
			jButton_VocabBrowse = new JButton();
			jButton_VocabBrowse.setBounds(new Rectangle(570, 70, 80, 20));
			jButton_VocabBrowse.setText("Browse");
			enterPressesWhenFocused(jButton_VocabBrowse);
			jButton_VocabBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseToVocabFile();
				}
			});
		}
		return jButton_VocabBrowse;
	}

	/**
	 * This method initializes jTextField_ImgPathsFile
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_ImgPathsFile() {
		if (jTextField_ImgPathsFile == null) {
			jTextField_ImgPathsFile = new JTextField();
			jTextField_ImgPathsFile.setBounds(new Rectangle(215, 40, 350, 21));
			jTextField_ImgPathsFile.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					setImagePathsFile(jTextField_ImgPathsFile.getText());
				}
			});
		}
		return jTextField_ImgPathsFile;
	}

	/**
	 * This method initializes jButton_ImgPathsBrowse
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_ImgPathsBrowse() {
		if (jButton_ImgPathsBrowse == null) {
			jButton_ImgPathsBrowse = new JButton();
			jButton_ImgPathsBrowse.setBounds(new Rectangle(570, 40, 80, 20));
			jButton_ImgPathsBrowse.setText("Browse");
			enterPressesWhenFocused(jButton_ImgPathsBrowse);
			jButton_ImgPathsBrowse.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseToImagePathsFile();
				}
			});
		}
		return jButton_ImgPathsBrowse;
	}

	/**
	 * This method initializes importMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getImportMenu() {
		if (importMenu == null) {
			importMenu = new JMenu();
			importMenu.setText("Import");
			importMenu.setMnemonic('I');
			importMenu.add(getFromDBMenuItem());
		}
		return importMenu;
	}

	/**
	 * This method initializes the Images->Compute Orientations jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFromDBMenuItem() {
		if (fromDBMenuItem == null) {
			fromDBMenuItem = new JMenuItem();
			fromDBMenuItem.setText("From Database...");
			fromDBMenuItem.setMnemonic('D');
			fromDBMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					importMetadataFromDB();
				}
			});
		}
		fromDBMenuItem.setEnabled(false);
		return fromDBMenuItem;
	}


	/**
	 * This method initializes uploadMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getUploadMenu() {
		if (uploadMenu == null) {
			uploadMenu = new JMenu();
			uploadMenu.setName("Upload");
			uploadMenu.setText("Upload");
			uploadMenu.setMnemonic(KeyEvent.VK_U);
			uploadMenu.add(getSetupWebAppDBInfoMenuItem());
			uploadMenu.add(getEnableWebAppLockoutMenuItem());
			uploadMenu.add(getReleaseWebAppLockoutMenuItem());
			uploadMenu.add(getUploadObjMDMenuItem());
			uploadMenu.add(getUploadOntologyMDMenu());
			uploadMenu.add(getUploadImageMDMenu());
			uploadMenu.add(getUploadTermStatsMenuItem());
		}
		return uploadMenu;
	}

	/**
	 * This method initializes uploadObjMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadObjMDMenuItem() {
		if (uploadObjMDMenuItem == null) {
			uploadObjMDMenuItem = new JMenuItem();
			uploadObjMDMenuItem.setText("Objects Metadata...");
			uploadObjMDMenuItem.setMnemonic(KeyEvent.VK_O);
			uploadObjMDMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					uploadObjectMetadata();
				}
			});
		}
		return uploadObjMDMenuItem;
	}

	/**
	 * This method initializes uploadOntologyMDMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getUploadOntologyMDMenu() {
		if (uploadOntologyMDMenu == null) {
			uploadOntologyMDMenu = new JMenu();
			uploadOntologyMDMenu.setText("Ontology");
			uploadOntologyMDMenu.setMnemonic(KeyEvent.VK_N);
			uploadOntologyMDMenu.add(getUploadOntoConceptsMDMenuItem());
			uploadOntologyMDMenu.add(getUploadObjectConceptAssocMDMenuItem());
			uploadOntologyMDMenu.add(getUpdateCategoryCountsMenuItem());
		}
		return uploadOntologyMDMenu;
	}

	/**
	 * This method initializes uploadImageMDMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getUploadImageMDMenu() {
		if (uploadImageMDMenu == null) {
			uploadImageMDMenu = new JMenu();
			uploadImageMDMenu.setText("Images");
			uploadImageMDMenu.setMnemonic(KeyEvent.VK_I);
			uploadImageMDMenu.add(getUploadImageMDMenuItem());
			uploadImageMDMenu.add(getUpdateCachedImageMDMenuItem());
			uploadImageMDMenu.add(getUploadCatCardImageMDMenuItem());
		}
		return uploadImageMDMenu;
	}

	/**
	 * This method initializes uploadOntoConceptsMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadOntoConceptsMDMenuItem() {
		if (uploadOntoConceptsMDMenuItem == null) {
			uploadOntoConceptsMDMenuItem = new JMenuItem();
			uploadOntoConceptsMDMenuItem.setText("Concepts Metadata...");
			uploadOntoConceptsMDMenuItem.setMnemonic(KeyEvent.VK_C);
			uploadOntoConceptsMDMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							uploadOntoConceptsMetadata();
						}
					});
		}
		return uploadOntoConceptsMDMenuItem;
	}

	/**
	 * This method initializes uploadObjectConceptAssocMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadObjectConceptAssocMDMenuItem() {
		if (uploadObjectConceptAssocMDMenuItem == null) {
			uploadObjectConceptAssocMDMenuItem = new JMenuItem();
			uploadObjectConceptAssocMDMenuItem.setText("Object to Concept Associations...");
			uploadObjectConceptAssocMDMenuItem.setMnemonic(KeyEvent.VK_A);
			uploadObjectConceptAssocMDMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							uploadObjectConceptAssociations();
						}
					});
		}
		return uploadObjectConceptAssocMDMenuItem;
	}

	/**
	 * This method initializes uploadImageMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadImageMDMenuItem() {
		if (uploadImageMDMenuItem == null) {
			uploadImageMDMenuItem = new JMenuItem();
			uploadImageMDMenuItem.setText("Upload Image Metadata...");
			uploadImageMDMenuItem.setMnemonic(KeyEvent.VK_I);
			uploadImageMDMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					uploadImageMetadata();
				}
			});
		}
		return uploadImageMDMenuItem;
	}

	/**
	 * This method initializes updateCachedImageMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUpdateCachedImageMDMenuItem() {
		if (updateCachedImageMDMenuItem == null) {
			updateCachedImageMDMenuItem = new JMenuItem();
			updateCachedImageMDMenuItem.setText("Update Cached Image Paths...");
			updateCachedImageMDMenuItem.setMnemonic(KeyEvent.VK_H);
			updateCachedImageMDMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							updateObjectImageCache();
						}
					});
		}
		return updateCachedImageMDMenuItem;
	}

	/**
	 * This method initializes uploadCatCardImageMDMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadCatCardImageMDMenuItem() {
		if (uploadCatCardImageMDMenuItem == null) {
			uploadCatCardImageMDMenuItem = new JMenuItem();
			uploadCatCardImageMDMenuItem.setText("Upload Catalog Card Image Metadata...");
			uploadCatCardImageMDMenuItem.setMnemonic(KeyEvent.VK_C);
			uploadCatCardImageMDMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							uploadCatalogCardMetadata();
						}
					});
		}
		return uploadCatCardImageMDMenuItem;
	}

	/**
	 * This method initializes updateCategoryCountsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUpdateCategoryCountsMenuItem() {
		if (updateCategoryCountsMenuItem == null) {
			updateCategoryCountsMenuItem = new JMenuItem();
			updateCategoryCountsMenuItem.setText("Update Category Counts");
			updateCategoryCountsMenuItem.setMnemonic(KeyEvent.VK_N);
			updateCategoryCountsMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							updateCategoryCounts();
						}
					});
		}
		return updateCategoryCountsMenuItem;
	}

	/**
	 * This method initializes setupWebAppDBInfoMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSetupWebAppDBInfoMenuItem() {
		if (setupWebAppDBInfoMenuItem == null) {
			setupWebAppDBInfoMenuItem = new JMenuItem();
			setupWebAppDBInfoMenuItem.setText("Setup Web App DB Info");
			setupWebAppDBInfoMenuItem.setMnemonic(KeyEvent.VK_S);
			setupWebAppDBInfoMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setupWebAppDBInfo();
						}
					});
		}
		return setupWebAppDBInfoMenuItem;
	}

	/**
	 * This method initializes enableWebAppLockoutMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getEnableWebAppLockoutMenuItem() {
		if (enableWebAppLockoutMenuItem == null) {
			enableWebAppLockoutMenuItem = new JMenuItem();
			enableWebAppLockoutMenuItem.setText("Enable Web App Lockout");
			enableWebAppLockoutMenuItem.setMnemonic(KeyEvent.VK_E);
			enableWebAppLockoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setWebAppLockout(true);
				}
			});
		}
		return enableWebAppLockoutMenuItem;
	}

	/**
	 * This method initializes releaseWebAppLockoutMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getReleaseWebAppLockoutMenuItem() {
		if (releaseWebAppLockoutMenuItem == null) {
			releaseWebAppLockoutMenuItem = new JMenuItem();
			releaseWebAppLockoutMenuItem.setText("Release Web App Lockout");
			releaseWebAppLockoutMenuItem.setMnemonic(KeyEvent.VK_E);
			releaseWebAppLockoutMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							setWebAppLockout(false);
						}
					});
		}
		return releaseWebAppLockoutMenuItem;
	}

	/**
	 * This method initializes webAppDBInfoDialog
	 *
	 * @return javax.swing.JDialog
	 */
	private JDialog getWebAppDBInfoDialog() {
		if (webAppDBInfoDialog == null) {
			webAppDBInfoDialog = new JDialog(getJFrame(), "Setup Web App DB Info", true);
			webAppDBInfoDialog.setBounds(new Rectangle(2, 0, 400, 276));
			webAppDBInfoDialog.setMinimumSize(new Dimension(400, 276));
			webAppDBInfoDialog.setResizable(false);
			webAppDBInfoDialog.setContentPane(getWebAppDBInfoContentPane());
		}
		return webAppDBInfoDialog;
	}

	/**
	 * This method initializes webAppDBInfoContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getWebAppDBInfoContentPane() {
		if (webAppDBInfoContentPane == null) {
			// Set up the banner title text
			webAppDBInfoTitle1 = new JLabel();
			webAppDBInfoTitle1.setBounds(new Rectangle(25, 18, 343, 19));
			webAppDBInfoTitle1.setHorizontalAlignment(SwingConstants.CENTER);
			webAppDBInfoTitle1.setText("Enter the connection information for the");
			webAppDBInfoTitle1.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			webAppDBInfoTitle2 = new JLabel();
			webAppDBInfoTitle2.setBounds(new Rectangle(25, 35, 343, 19));
			webAppDBInfoTitle2.setHorizontalAlignment(SwingConstants.CENTER);
			webAppDBInfoTitle2.setText("Delphi Web Application database");
			webAppDBInfoTitle2.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			// Set up host, db, user and password labels
			hostLabel = new JLabel();
			hostLabel.setText("Host:");
			hostLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			hostLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			hostLabel.setBounds(new Rectangle(25, 77, 84, 19));
			webAppDB_DBNameLabel = new JLabel();
			webAppDB_DBNameLabel.setBounds(new Rectangle(25, 107, 84, 19));
			webAppDB_DBNameLabel.setText("DB Name:");
			webAppDB_DBNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			webAppDB_DBNameLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			webAppDB_UserLabel = new JLabel();
			webAppDB_UserLabel.setBounds(new Rectangle(25, 137, 84, 19));
			webAppDB_UserLabel.setText("User:");
			webAppDB_UserLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			webAppDB_UserLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			webAppDB_PasswordLabel = new JLabel();
			webAppDB_PasswordLabel.setBounds(new Rectangle(25, 167, 84, 19));
			webAppDB_PasswordLabel.setText("Password:");
			webAppDB_PasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			webAppDB_PasswordLabel.setFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
			// Build content panel with labels and text entry widgets

			webAppDBInfoInnerPane = new JPanel();
			webAppDBInfoInnerPane.setLayout(null);
			webAppDBInfoInnerPane.setSize(new Dimension(400, 275));
			//webAppDBInfoInnerPane.setPreferredSize(new Dimension(400, 275));
			webAppDBInfoInnerPane.add(webAppDBInfoTitle1, null);
			webAppDBInfoInnerPane.add(webAppDBInfoTitle2, null);
			webAppDBInfoInnerPane.add(hostLabel, null);
			webAppDBInfoInnerPane.add(getWebAppDB_HostNameTextField(), null);
			webAppDBInfoInnerPane.add(webAppDB_DBNameLabel, null);
			webAppDBInfoInnerPane.add(getWebAppDB_DBNameTextField(), null);
			webAppDBInfoInnerPane.add(webAppDB_UserLabel, null);
			webAppDBInfoInnerPane.add(getWebAppDB_UserTextField(), null);
			webAppDBInfoInnerPane.add(webAppDB_PasswordLabel, null);
			webAppDBInfoInnerPane.add(getWebAppDB_PasswordTextField(), null);
			webAppDBInfoInnerPane.add(getWebAppDBInfoOKButton(), null);
			webAppDBInfoInnerPane.setMinimumSize(new Dimension(400, 275));

			webAppDBInfoContentPane = new JPanel();
			webAppDBInfoContentPane.setLayout(new BorderLayout());
			webAppDBInfoContentPane.setMinimumSize(new Dimension(400, 275));
			webAppDBInfoContentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			webAppDBInfoContentPane.add(webAppDBInfoInnerPane, BorderLayout.CENTER);
		}
		return webAppDBInfoContentPane;
	}

	/**
	 * This method initializes webAppDB_HostNameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWebAppDB_HostNameTextField() {
		if (webAppDB_HostNameTextField == null) {
			webAppDB_HostNameTextField = new JTextField();
			webAppDB_HostNameTextField.setBounds(new Rectangle(120, 77, 250, 21));
		}
		return webAppDB_HostNameTextField;
	}

	/**
	 * This method initializes webAppDB_DBNameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWebAppDB_DBNameTextField() {
		if (webAppDB_DBNameTextField == null) {
			webAppDB_DBNameTextField = new JTextField();
			webAppDB_DBNameTextField.setBounds(new Rectangle(120, 107, 250, 21));
		}
		return webAppDB_DBNameTextField;
	}

	/**
	 * This method initializes webAppDB_UserTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWebAppDB_UserTextField() {
		if (webAppDB_UserTextField == null) {
			webAppDB_UserTextField = new JTextField();
			webAppDB_UserTextField.setBounds(new Rectangle(120, 137, 250, 21));
		}
		return webAppDB_UserTextField;
	}

	/**
	 * This method initializes webAppDB_PasswordTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getWebAppDB_PasswordTextField() {
		if (webAppDB_PasswordTextField == null) {
			webAppDB_PasswordTextField = new JTextField();
			webAppDB_PasswordTextField.setBounds(new Rectangle(120, 167, 250, 21));
		}
		return webAppDB_PasswordTextField;
	}

	/**
	 * This method initializes webAppDBInfoOKButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getWebAppDBInfoOKButton() {
		if (webAppDBInfoOKButton == null) {
			webAppDBInfoOKButton = new JButton();
			webAppDBInfoOKButton.setBounds(new Rectangle(157, 210, 80, 25));
			webAppDBInfoOKButton.setText("OK");
			webAppDBInfoOKButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setupWebAppDBInfoCommitInfo();
					webAppDBInfoDialog.setVisible(false);
				}
			});
		}
		return webAppDBInfoOKButton;
	}

	/**
	 * This method initializes uploadTermStatsMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUploadTermStatsMenuItem() {
		if (uploadTermStatsMenuItem == null) {
			uploadTermStatsMenuItem = new JMenuItem();
			uploadTermStatsMenuItem.setText("Term Statistics");
			uploadTermStatsMenuItem.setMnemonic(KeyEvent.VK_T);
			uploadTermStatsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					uploadTermStats();
				}
			});
		}
		return uploadTermStatsMenuItem;
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
				application.xmlFilter = new FileNameExtensionFilter( "XML files", "xml" );
				//application.sqlFilter = new FileNameExtensionFilter( "SQL files", "sql" );
				application.txtFilter = new FileNameExtensionFilter( "Text files", "txt" );
				application.dpfFilter = new FileNameExtensionFilter( "Delphi Project files", "dpf" );
				application.bkgdTan = new Color(242, 235, 207);
				application.getJFrame().setVisible(true);
				// Could allow a passed in filename at some future date.
				application.appSettings = new AppSettings( settingsFilename );
				String path = application.appSettings.getLastUserProjectPath();
		    	UserProjectInfo upi = null;
				if( path != null ) {
			    	try {
				    	upi = UserProjectInfo.loadFromPath( path );
				    	if( upi != null )
				    		application.setUserProject( upi );
					} catch (RuntimeException e) {
						JOptionPane.showMessageDialog(application.getJFrame(), "Error encountered:\n" + e.toString(),
								"Loading Project File Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if( upi == null ) {
					application.createNewProjectFile(null);
				}
			}
		});
	}

	private void setUserProject( UserProjectInfo upi ) {
		userProjInfo = upi;
		loadResourcesFromUserProjectInfo();
		updateUIForUserProjectInfo();
	}

	private void updateUIForUserProjectInfo() {
		if( userProjInfo == null ) {
			// Clear all the menus that require state
			// File menu
			copyProjMenuItem.setEnabled(false);
			saveMenuItem.setEnabled(false);
			saveAsMenuItem.setEnabled(false);
			// Analyze Menu
			objInfoTermUsageMenuItem.setEnabled(false);
			objConceptAssocMenuItem.setEnabled(false);
			objConceptAssocByFacetMenuItem.setEnabled(false);
			// Images Menu
			computeImageOrientationsMenuItem.setEnabled(false);
			// Import Menu
			fromDBMenuItem.setEnabled(false);
			// Export Menu
			buildObjectSQLMenuItem.setEnabled(false);
			saveOntologyAsSQLMenuItem.setEnabled(false);
			saveVocabAsXMLMenuItem.setEnabled(false);
			saveHooksAsSQLMenuItem.setEnabled(false);
			saveExclusionsAsSQLMenuItem.setEnabled(false);
			saveSQLMediaLoadFileMenuItem.setEnabled(false);
			saveSQLMediaInsertFileMenuItem.setEnabled(false);
			// Clear all the text fields
			jTextField_ProjName.setText("");
			jTextField_ColConfigFile.setText("");
			jTextField_ObjInfoFile.setText("");
			jTextField_OntologyFile.setText("");
			jTextField_VocabFile.setText("");
			jTextField_ImgPathsFile.setText("");
		} else {
			// Set all the menus and related UI that require state
			// File menu
			copyProjMenuItem.setEnabled(true);  // Can copy any open project
			saveAsMenuItem.setEnabled(true);	// ditto
			boolean upiIsDirty = userProjInfo.isDirty();
			saveMenuItem.setEnabled(upiIsDirty);

			String text = userProjInfo.getName();
			if( text == null )
				text = "";
			jTextField_ProjName.setText(text);

			text = userProjInfo.getMetadataConfigPath();
			if( text == null )
				text = "";
			jTextField_ColConfigFile.setText(text);

			// Import Menu
			boolean haveDBsource = ( userProjInfo.dbMetaDataReader != null );
			fromDBMenuItem.setEnabled(haveDBsource);

			// Export Menu
			text = userProjInfo.getMetadataPath();
			boolean metadataReady = (text != null)
										&& (userProjInfo.metaDataReader != null );
			if( text == null )
				text = "";
			jTextField_ObjInfoFile.setText(text);
			text = userProjInfo.getOntologyPath();
			boolean ontoReady = (text != null)
									&& (userProjInfo.facetMapHashTree != null );
			if( text == null )
				text = "";
			jTextField_OntologyFile.setText(text);
			objInfoTermUsageMenuItem.setEnabled(metadataReady);
			objConceptAssocMenuItem.setEnabled(metadataReady&&ontoReady);
			objConceptAssocByFacetMenuItem.setEnabled(metadataReady&&ontoReady);
			// Images Menu
			text = userProjInfo.getImagePathsPath();
			boolean imagePathsReady = (userProjInfo.imagePathsReader != null);
			if( text == null )
				text = "";
			jTextField_ImgPathsFile.setText(text);
			computeImageOrientationsMenuItem.setEnabled(imagePathsReady);
			// Export Menu
			buildObjectSQLMenuItem.setEnabled(metadataReady);
			saveOntologyAsSQLMenuItem.setEnabled(ontoReady);
			text = userProjInfo.getVocabTermsPath();
			boolean vocabReady = (text != null)
									&& (userProjInfo.vocabHashTree != null );
			if( text == null )
				text = "";
			jTextField_VocabFile.setText(text);
			saveVocabAsXMLMenuItem.setEnabled(vocabReady);
			saveHooksAsSQLMenuItem.setEnabled(ontoReady);
			saveExclusionsAsSQLMenuItem.setEnabled(ontoReady);
			saveSQLMediaLoadFileMenuItem.setEnabled(imagePathsReady);
			saveSQLMediaInsertFileMenuItem.setEnabled(imagePathsReady);
		}
	}

	private void loadResourcesFromUserProjectInfo() {
		if( userProjInfo == null ) {
			userProjInfo.vocabTermsReader = null;
			userProjInfo.vocabHashTree = null;
			userProjInfo.vocabOutputDoc = null;
			userProjInfo.facetMapHashTree = null;
			userProjInfo.metaDataReader = null;
			userProjInfo.dbMetaDataReader = null;
			userProjInfo.imagePathsReader = null;
		} else {
			String filename = null;
			if(( filename = userProjInfo.getImagePathsPath()) != null )
				loadImagePaths(filename);
			if(( filename = userProjInfo.getVocabTermsPath()) != null )
				loadVocabFile(filename);
			if(( filename = userProjInfo.getOntologyPath()) != null )
				loadOntology(filename);
			if(( filename = userProjInfo.getMetadataConfigPath()) != null ) {
				MetaDataConfigManager mdcfgMgr = loadMDConfig( filename );
				if( mdcfgMgr.GetDumpColConfigInfo() ) {
			        DBMetaDataReader dbmdRdr = mdcfgMgr.GetDBSourceInfo();
			        if(dbmdRdr != null)
			        	userProjInfo.dbMetaDataReader = dbmdRdr;
				}
				// TODO This should move to a separate config source
		        ImagePathsReader dbImageRdr = mdcfgMgr.GetDBImageInfo();
		        if(dbImageRdr != null)
		        	userProjInfo.imagePathsReader = dbImageRdr;
			}
			if(( filename = userProjInfo.getMetadataPath()) != null )
				loadMetadata(filename);
		}
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
			jFrame.setSize(700, 450);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Delphi");
			jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					checkOnExit();
					//System.out.println("windowClosing()"); // TODO Auto-generated Event stub windowClosing()
				}
			});
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
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getAnalyzeMenu());
			jJMenuBar.add(getImagesMenu());
			jJMenuBar.add(getImportMenu());
			jJMenuBar.add(getExportMenu());
			//jJMenuBar.add(getVocabActionsMenu());
			//jJMenuBar.add(getDataActionsMenu());
			//jJMenuBar.add(getObjectActionsMenu());
			jJMenuBar.add(getUploadMenu());
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
			fileMenu.setText("File");
			fileMenu.setMnemonic('F');
			fileMenu.add(getNewMenuItem());
			fileMenu.add(getOpenMenuItem());
			fileMenu.add(getCopyProjMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getSaveAsMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes the File->New jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	 */
	private JMenuItem getNewMenuItem() {
		if (newMenuItem == null) {
			newMenuItem = new JMenuItem();
			newMenuItem.setText("New Project");
			newMenuItem.setMnemonic('N');
			newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					Event.CTRL_MASK, true));
			newMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					createNewProjectFile(null);
				}
			} );
		}
		return newMenuItem;
	}

	/**
	 * This method initializes the File->Open jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("Open Project...");
			openMenuItem.setMnemonic('O');
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					Event.CTRL_MASK, true));
			openMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openProjectFile();
				}
			} );
		}
		return openMenuItem;
	}

	/**
	 * This method initializes the File->Copy jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	 */
	private JMenuItem getCopyProjMenuItem() {
		if (copyProjMenuItem == null) {
			copyProjMenuItem = new JMenuItem();
			copyProjMenuItem.setText("Copy Current Project");
			copyProjMenuItem.setMnemonic('C');
			copyProjMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					Event.CTRL_MASK, true));
			copyProjMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					createNewProjectFile(userProjInfo);
				}
			} );
		}
		return copyProjMenuItem;
	}

	/**
	 * This method initializes the File->Save jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save Project");
			saveMenuItem.setMnemonic('S');
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
			saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if( appSettings.getLastUserProjectPath() == null )
						saveProjectFileAs();
					else
						saveProjectFile();
				}
			} );
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes the File->SaveAs jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 *
	 */
	private JMenuItem getSaveAsMenuItem() {
		if (saveAsMenuItem == null) {
			saveAsMenuItem = new JMenuItem();
			saveAsMenuItem.setText("Save Project As...");
			saveAsMenuItem.setMnemonic('A');
			saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					Event.CTRL_MASK, true));
			saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveProjectFileAs();
				}
			} );
		}
		return saveAsMenuItem;
	}


	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.setMnemonic('E');
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
			cutMenuItem.setText("Cut");
			cutMenuItem.setMnemonic('T');
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
			copyMenuItem.setText("Copy");
			copyMenuItem.setMnemonic('C');
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setMnemonic('P');
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes the Analyze jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getAnalyzeMenu() {
		if (analyzeMenu == null) {
			analyzeMenu = new JMenu();
			analyzeMenu.setText("Analyze");
			analyzeMenu.setMnemonic('A');
			analyzeMenu.add(getObjInfoTermUsageMenuItem());
			analyzeMenu.add(getObjConceptAssocMenuItem());
			analyzeMenu.add(getObjConceptAssocByFacetMenuItem());
		}
		return analyzeMenu;
	}

	//Object/Concept Association (requires ObjInfo file and Ontology. Later with load variants and an option

	/**
	 * This method initializes Analyze->Object Info Term Usage jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getObjInfoTermUsageMenuItem() {
		if (objInfoTermUsageMenuItem == null) {
			objInfoTermUsageMenuItem = new JMenuItem();
			objInfoTermUsageMenuItem.setText("Object Info Term Usage");
			objInfoTermUsageMenuItem.setMnemonic('T');
			objInfoTermUsageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JDialog burDialog = getBuildUsageReportDialog();
					burDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(50, 50);
					burDialog.setLocation(loc);
					burDialog.setVisible(true);
					//debug(1,"Generate Usage report...");
				}
			} );
		}
		return objInfoTermUsageMenuItem;
	}

	/**
	 * This method initializes Analyze->Object/Concept Association jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getObjConceptAssocMenuItem() {
		if (objConceptAssocMenuItem == null) {
			objConceptAssocMenuItem = new JMenuItem();
			objConceptAssocMenuItem.setText("Object-Concept Association");
			objConceptAssocMenuItem.setMnemonic('A');
			objConceptAssocMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					categorizeObjects( true );
					}
			} );
		}
		return objConceptAssocMenuItem;
	}

	/**
	 * This method initializes Analyze->Object/Concept Association jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getObjConceptAssocByFacetMenuItem() {
		if (objConceptAssocByFacetMenuItem == null) {
			objConceptAssocByFacetMenuItem = new JMenuItem();
			objConceptAssocByFacetMenuItem.setText("Object-Concept Association By Facet");
			objConceptAssocByFacetMenuItem.setMnemonic('F');
			objConceptAssocByFacetMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					categorizeObjects( false );
					}
			} );
		}
		return objConceptAssocByFacetMenuItem;
	}

	/**
	 * This method initializes jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getImagesMenu() {
		if (imagesMenu == null) {
			imagesMenu = new JMenu();
			imagesMenu.setText("Images");
			imagesMenu.setMnemonic('M');
			imagesMenu.add(getComputeImageOrientationsMenuItem());
			imagesMenu.add(getImportMenu());
		}
		return imagesMenu;
	}

	/**
	 * This method initializes the Images->Compute Orientations jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getComputeImageOrientationsMenuItem() {
		if (computeImageOrientationsMenuItem == null) {
			computeImageOrientationsMenuItem = new JMenuItem();
			computeImageOrientationsMenuItem.setText("Compute Image Orientations...");
			computeImageOrientationsMenuItem.setMnemonic('O');
			computeImageOrientationsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					computeImageOrientations();
					//saveImagePathSQL();
				}
			});
		}
		computeImageOrientationsMenuItem.setEnabled(false); // PROJ REWORK
		return computeImageOrientationsMenuItem;
	}

	/**
	 * This method initializes the Export jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getExportMenu() {
		if (exportMenu == null) {
			exportMenu = new JMenu();
			exportMenu.setText("Export");
			exportMenu.setMnemonic('X');
			exportMenu.add(getBuildObjectSQLMenuItem());
			exportMenu.add(getConceptOntologyMenu());
			exportMenu.add(getImagePathsMenu());
			/*
			exportMenu.add(getOpenVocabMenuItem());
			exportMenu.add(getSaveVocabAsXMLMenuItem());
			exportMenu.add(getLoadVocabFromXMLMenuItem());
			// exportMenu.add(getAddVocabToDBMenuItem());
			exportMenu.add(getSaveOntologyAsSQLMenuItem());
			exportMenu.add(getSaveHooksAsSQLMenuItem());
			exportMenu.add(getSaveExclusionsAsSQLMenuItem());
			*/
		}
		return exportMenu;
	}

	/**
	 * This method initializes Export->Object Info jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getBuildObjectSQLMenuItem() {
		if (buildObjectSQLMenuItem == null) {
			buildObjectSQLMenuItem = new JMenuItem();
			buildObjectSQLMenuItem.setText("Object Info...");
			buildObjectSQLMenuItem.setMnemonic('O');
			buildObjectSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveObjectSQL();
				}
			});
		}
		return buildObjectSQLMenuItem;
	}

	/**
	 * This method initializes the Export->Concept Ontology jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getConceptOntologyMenu() {
		if (conceptOntologyMenu == null) {
			conceptOntologyMenu = new JMenu();
			conceptOntologyMenu.setText("Concept Ontology");
			conceptOntologyMenu.setMnemonic('C');
			conceptOntologyMenu.add(getSaveOntologyAsSQLMenuItem());
			conceptOntologyMenu.add(getSaveHooksAsSQLMenuItem());
			conceptOntologyMenu.add(getSaveExclusionsAsSQLMenuItem());
			conceptOntologyMenu.add(getSaveVocabAsXMLMenuItem());
		}
		return conceptOntologyMenu;
	}

	/**
	 * This method initializes the Export->Image Paths jMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getImagePathsMenu() {
		if (imagePathsMenu == null) {
			imagePathsMenu = new JMenu();
			imagePathsMenu.setText("Image Paths");
			imagePathsMenu.setMnemonic('I');
			imagePathsMenu.add(getSaveSQLMediaLoadFileMenuItem());
			imagePathsMenu.add(getSaveSQLMediaInsertFileMenuItem());
		}
		return imagePathsMenu;
	}

	private JMenuItem getSaveSQLMediaInsertFileMenuItem() {
		if (saveSQLMediaInsertFileMenuItem == null) {
			saveSQLMediaInsertFileMenuItem = new JMenuItem();
			saveSQLMediaInsertFileMenuItem.setText("Save SQL Media Insert File...");
			saveSQLMediaInsertFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveMediaPathsSQL(true);
				}
			});
		}
		saveSQLMediaInsertFileMenuItem.setEnabled(false); // PROJ REWORK
		return saveSQLMediaInsertFileMenuItem;
	}

	private JMenuItem getSaveSQLMediaLoadFileMenuItem() {
		if (saveSQLMediaLoadFileMenuItem == null) {
			saveSQLMediaLoadFileMenuItem = new JMenuItem();
			saveSQLMediaLoadFileMenuItem.setText("Save SQL Media Load File...");
			saveSQLMediaLoadFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveMediaPathsSQL(false);
				}
			});
		}
		saveSQLMediaLoadFileMenuItem.setEnabled(false); // PROJ REWORK
		return saveSQLMediaLoadFileMenuItem;
	}

	private void saveMediaPathsSQL( boolean asSQLInsert ) {
		ImagePathsReader reader = userProjInfo.imagePathsReader;
		if( reader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if( !reader.ready() && !reader.prepareInfo()) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info could not be loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!reader.orientationsComputed()) {
			int confirm = JOptionPane.showConfirmDialog(getJFrame(),
					"Image Orientations have not yet been computed. Proceed anyway?"
					+"\nClick YES to proceed; click NO to cancel.");
			if( confirm != JOptionPane.YES_OPTION )
				return;
		}
		String variant = asSQLInsert?"Insert":"Load";
		debug(1,"Saving SQL Media "+variant+" File...");
		try {
			String outFileName = userProjInfo.getMediaSQLLoadfilePath();
			if(outFileName == null ) {
				outFileName = reader.getInFile();
				if(outFileName == null )
					outFileName = userProjInfo.getImagePathsPath();
				int lastSlash = (outFileName==null)?-1:outFileName.lastIndexOf(File.separatorChar);
				if( lastSlash >= 0 )
					outFileName = outFileName.substring(0, lastSlash+1) + "media"+variant+".txt";
				else
					outFileName = "media"+variant+".txt";
			}
    		String filename = getSafeOutfile("Save SQL Media "+variant+" file...", outFileName,txtFilter);
		    if(filename != null) {
		    	// This is where we should call prepare()
	    		int nImgs = reader.getNumObjs();
	    		int nWithDims = reader.getNImagesWithDims();
	    		boolean omitMediaWithNoDims = false;
	    		if( nWithDims < nImgs ) {
	    			int confirm = JOptionPane.showConfirmDialog(getJFrame(),
	    					"Omit Images with no dimension info (probably invalid paths)?"
	    					+"\nClick YES to omit; click NO to include all media references.");
	    			if( confirm == JOptionPane.YES_OPTION )
	    				omitMediaWithNoDims = true;
	    		}
		        setStatus("Saving SQL Media "+variant+" File to file: " + filename);
		        if(asSQLInsert)
		        	reader.writeSQLMediaTableInsertFile(filename, omitMediaWithNoDims);
		        else
			        reader.writeSQLMediaTableLoadFile(filename, omitMediaWithNoDims);
		        // Keep track of where we put this.
		        userProjInfo.setMediaSQLLoadfilePath(filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Saving SQL Media Insert File: ", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
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
			saveVocabAsXMLMenuItem.setMnemonic('V');
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
			saveOntologyAsSQLMenuItem.setText("Save Ontology as SQL Load...");
			saveOntologyAsSQLMenuItem.setMnemonic('O');
			saveOntologyAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveOntologyAsSQL();
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
			saveHooksAsSQLMenuItem.setMnemonic('H');
			saveHooksAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveHooksOrExclusionsAsSQL( true );
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
			saveExclusionsAsSQLMenuItem.setMnemonic('E');
			saveExclusionsAsSQLMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveHooksOrExclusionsAsSQL( false );
				}
			});
		}
		return saveExclusionsAsSQLMenuItem;
	}

	private void importMetadataFromDB() {
		int answer = JOptionPane.showConfirmDialog(getJFrame(),
							"Begin metadata import from database?",
							"Metadata import from database", JOptionPane.OK_CANCEL_OPTION);
		if( answer == JOptionPane.YES_OPTION ) {
			try{
				String colNames[] = DumpColumnConfigInfo.getColumnNames();
				userProjInfo.dbMetaDataReader.prepareSources(colNames);
				// Ask user to save info to a file.
				String outFileName;
				if(( outFileName = userProjInfo.getMetadataPath()) == null ) {
					outFileName = userProjInfo.getMetadataConfigPath();
					int iDot = outFileName.lastIndexOf('.');
					if( iDot > 0 )
						outFileName = outFileName.substring(0, iDot);
					outFileName += "_dump.txt";
				}
	    		String filename = getSafeOutfile("Save Object Metadata to file...",
															outFileName,txtFilter);
	    		if( filename != null ) {
	    			debug(1,"Saving metadata from DB to file: " + filename);
					setStatus( "Saving metadata from DB to file...");
					int nObjsWritten = 0;
			        try {
						BufferedWriter writer = new BufferedWriter(
												  new OutputStreamWriter(
													new FileOutputStream(filename),"UTF8"));
						String separator = ""+(char)DumpColumnConfigInfo.getColumnSeparator();
						// Write the col headings
						int nCols = colNames.length;
						for(int iCol=0; iCol<nCols; iCol++) {
							writer.write(colNames[iCol]);
							if( iCol < nCols-1)
								writer.write(separator);
							else
								writer.newLine();
						}
						Pair<Integer,ArrayList<String>> nextObjInfo;
						//int nStringCols = nCols-1;
						//int nSeps = nStringCols-1;
						int nSeps = nCols-1;
						// Write the data
						while((nextObjInfo = userProjInfo.dbMetaDataReader.getNextObjectAsColumns())
								!= null ) {
							writer.write(nextObjInfo.first+separator+'"');
							String sepWithQuotes = '"'+separator+'"';
							ArrayList<String> colStrings = nextObjInfo.second;
							// Note we skip the id column (0) in the returned strings
							//for(int iCol=1; iCol<nStringCols; iCol++) {
							for(int iCol=1; iCol<nCols; iCol++) {
								// Must escape quotes in output
								String curr = colStrings.get(iCol).trim();
								if(!curr.isEmpty()) {
									String replaced;
									if(curr.length()>0) {
										replaced = curr.replaceAll("[\\\\]", "\\\\\\\\");
										replaced = replaced.replaceAll("[\"]", "\\\\\"");
									} else {
										replaced = curr;
									}
									writer.write(replaced);
								}
								if( iCol < nSeps) {
									writer.write(sepWithQuotes);
								} else {
									writer.write('"');
									writer.newLine();
								}
							}
							nObjsWritten++;
						}
						writer.flush();
						writer.close();
					} catch( IOException e ) {
			            e.printStackTrace();
						throw new RuntimeException("Could not create output file: " + filename);
					}
					// If we successfully write the metadata, update the path.
					userProjInfo.setMetadataPath(filename);
					String msg = "Wrote DB import of "+nObjsWritten+" objects to file: " + filename;
					JOptionPane.showMessageDialog(getJFrame(), msg,
							"Metadata import from database: ", JOptionPane.INFORMATION_MESSAGE);
					setStatus(msg);
			    }
			} catch (RuntimeException re ) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + re.getMessage(),
						"Metadata import from database: ", JOptionPane.ERROR_MESSAGE);
				debugTrace(1, re);
			}
		}
	}

	/**
	 * Creates a new project file, and sets the it to be the current project.
	 */
	protected void createNewProjectFile(UserProjectInfo pattern) {
		UserProjectInfo upi = new UserProjectInfo( "New Project" );
		if( pattern != null )
			upi.copyFrom(pattern);
		//String lastPath =
		appSettings.setLastUserProjectPath(null);
		setUserProject( upi );
	}

	/**
	 * Opens an existing project file, and sets the it to be the current project.
	 */
	private void openProjectFile() {
		chooser.setFileFilter(dpfFilter);
		String startDir = appSettings.getLastUserProjectFolder()+"*.dpf";
    	if( startDir != null )
    		chooser.setSelectedFile(new File( startDir ));
	    chooser.setDialogTitle("Open Delphi Project file...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filename = chooser.getSelectedFile().getPath();
	    	try {
		    	UserProjectInfo upi = UserProjectInfo.loadFromPath( filename );
		    	if( upi != null ) {
		    		appSettings.setLastUserProjectPath(filename);
		    		setUserProject( upi );
		    	}
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
						"Loading Project File Error", JOptionPane.ERROR_MESSAGE);
			}
	    }
	}

	/**
	 * Saves the current project file.
	 */
	private void saveProjectFile() {
		try {
			UserProjectInfo.saveToPath(userProjInfo, appSettings.getLastUserProjectPath());
	        setStatus("Saved Project Info" );
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Project File Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Saves the current project file to a new file, and sets that to be the current project.
	 */
	private void saveProjectFileAs() {
		try {
    		String filename = getSafeOutfile("Save Delphi Project file...",
												appSettings.getLastUserProjectFolder()+"*.dpf",dpfFilter);
		    if(filename != null) {
				UserProjectInfo.saveToPath(userProjInfo, filename);
		    	appSettings.setLastUserProjectPath(filename);
		        setStatus("Saved Project Info to file: " + filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
					"Save Project File Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setChooserStartFor( String filename, String extPattern ) {
		String path = filename;
		if( path!=null )
			path = 	StringUtils.getBaseDirForPath(path);
		if( path!=null )
    		chooser.setSelectedFile(new File( path+extPattern ));
	}

	/**
	 * @return TRUE if successfully opened and parsed
	 */
	private boolean browseToImagePathsFile() {
		boolean opened = false;
		chooser.setFileFilter(txtFilter);
		String outFileName = userProjInfo.getImagePathsPath();
		if(outFileName == null)
			outFileName = userProjInfo.getMetadataConfigPath();
		setChooserStartFor( outFileName, txtWildcard );
	    chooser.setDialogTitle("Open Image Paths data file...");

	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filename = chooser.getSelectedFile().getPath();
	    	if( opened = setImagePathsFile(filename) ) {
	    		jTextField_ImgPathsFile.setText(filename);
	    	}
	    }
	    return opened;
	}

	/**
	 * @return TRUE if successfully opened and parsed
	 */
	private boolean setImagePathsFile( String filename ) {
		boolean opened = false;
        // TODO consider deferring this until we actually need it
		if( opened = loadImagePaths( filename )) {
	        userProjInfo.setImagePathsPath(filename);
	        updateUIForUserProjectInfo();
		}
	    return opened;
	}

	/**
	 * @return TRUE if successfully opened and parsed
	 */
	private boolean loadImagePaths( String filename ) {
		boolean opened = false;
		if(userProjInfo.imagePathsReader != null) {
			int confirm = JOptionPane.showConfirmDialog(getJFrame(),
					"Create a new ImagePaths reader, discarding the existing one?"
					+"\nClick YES to rebuild; click NO to cancel.");
			if( confirm != JOptionPane.YES_OPTION )
				return true;
		}
		debug(1,"Loading Image paths...");
		try {
	        debug(1,"Scanning image paths from file: " + filename);
	        setStatus("Scanning image paths from file: " + filename);
	        userProjInfo.imagePathsReader = new ImagePathsReader(filename);
			if( !userProjInfo.imagePathsReader.ready()
					&& !userProjInfo.imagePathsReader.prepareInfo()) {
				throw new RuntimeException("Image Path info could not be loaded.");
			}
	    	int nObjs = userProjInfo.imagePathsReader.getNumObjs();
	        setStatus("Found: " + nObjs + " objects with images.");
	        debug(2,"Found: " + nObjs + " objects with images.");
			opened = true;
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Loading Image paths File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	    return opened;
	}

	/**
	 * Assumes the base path is set, and that there are variants under the thumbs.
	 */
	private void computeImageOrientations() {
		if( userProjInfo.imagePathsReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if( !userProjInfo.imagePathsReader.ready()
				&& !userProjInfo.imagePathsReader.prepareInfo()) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Image Path info could not be loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Let the user point to the base path for the images.
		String outFileName = userProjInfo.getImagePathsPath();
		if(outFileName == null)
			outFileName = userProjInfo.getMetadataConfigPath();
		chooser.setSelectedFile(new File( outFileName ));
		int selMode = chooser.getFileSelectionMode();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose root folder where images are located...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
		String pathBase;
	    if(returnVal != JFileChooser.APPROVE_OPTION) {
			chooser.setFileSelectionMode(selMode);
	    	return;
	    }
    	pathBase = chooser.getSelectedFile().getPath();
		chooser.setFileSelectionMode(selMode);
		debug(1,"Computing Image Orientations...");
		try {
			userProjInfo.imagePathsReader.computeImageOrientations( pathBase );
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Computing Aspect ratios: ", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 *
	 */
	private void saveObjectSQL() {
		if( userProjInfo.metaDataReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
					"Object info has not yet been loaded.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			debug(1,"Build Objects SQL...");
			// Ask user to save info to a file.
			String outFileName;
			if((outFileName = userProjInfo.getObjectsSQLLoadfilePath()) == null ) {
				if(( outFileName = userProjInfo.getMetadataPath()) == null ) {
					outFileName = userProjInfo.getMetadataConfigPath();
				}
				int iDot = outFileName.lastIndexOf('.');
				if( iDot > 0 )
					outFileName = outFileName.substring(0, iDot);
				outFileName += "_SQL.txt";
			}
    		String filename = getSafeOutfile("Save Object Metadata to files", outFileName, txtFilter);
    		if( filename != null ) {
    			ImagePathsReader ipReader = userProjInfo.imagePathsReader;
    			if(ipReader != null) {
	    			int confirm = JOptionPane.showConfirmDialog(getJFrame(),
	    					"Include image paths in object SQL?"
	    					+"\n(This is not recommended)"
	    					+"\nClick YES to include image paths; click NO to omit (recommended).");
	    			if( confirm != JOptionPane.YES_OPTION )
	    				ipReader = null;
	    			else if( !ipReader.ready()
	    					&& !ipReader.prepareInfo()) {
	    				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n",
	    						"Image Path info could not be loaded.", JOptionPane.ERROR_MESSAGE);
		    			confirm = JOptionPane.showConfirmDialog(getJFrame(),
		    					"Image Path info could not be loaded."
		    					+"\n Proceed without image path info?"
		    					+"\nClick YES to proceed; click NO to CANCEL Object Metadata SQL save.");
		    			if( confirm != JOptionPane.YES_OPTION )
		    				return;
		    			else
		    				ipReader = null;
	    			}
    			}
				SQLUtils.writeObjectsSQL( filename, SQLUtils.WRITE_AS_LOADFILE,
											userProjInfo.metaDataReader, ipReader);
			}
    		userProjInfo.setObjectsSQLLoadfilePath(filename);
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Object Metadata SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	private void categorizeObjects( boolean fAllFacets ) {
		debug(1,"Categorize Objects and generate association SQL...");
		if( userProjInfo.facetMapHashTree == null ) {
			JOptionPane.showMessageDialog(getJFrame(),
					"You cannot categorize before the ontology has been loaded\n"
					+ "Please load the ontology and then run this command.",
					"Categorize error", JOptionPane.ERROR_MESSAGE);
		} else if( userProjInfo.metaDataReader == null ) {
			JOptionPane.showMessageDialog(getJFrame(),
					"You must specify a metadata source to categorize.\n",
					"Categorize error", JOptionPane.ERROR_MESSAGE);
		} else try {
			String filename = userProjInfo.getMetadataPath();
			// Find last slash or backslash (to allow for windows paths), to get basepath
	    	int iSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
	    	if( iSlash<=0 )
	    		throw new RuntimeException("categorizeForFacet: odd input filename:\n"+filename);
	    	String basefilename = filename.substring(0, iSlash+1)+"obj_cats_";
			userProjInfo.metaDataReader.resetToLine1();
			boolean compileUnmatchedUsage =
				(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getJFrame(),
						"Compile statistics for unmatched tokens and n-grams?",
						"Choose Yes or No",
						JOptionPane.YES_NO_OPTION));
			if( !fAllFacets ) {
				JOptionPane.showMessageDialog(getJFrame(),
						"Single facet concept association is net yet implemented.\n",
						"Categorize error", JOptionPane.ERROR_MESSAGE);
				/* TODO - choose the right facet
				String facetName = Need to let user choose this via UI;
		    	basefilename += facetName)+"_";
				userProjInfo.categorizer.categorizeForFacet(userProjInfo.metaDataReader,
												facetName, basefilename, false, dbName);
				 */
			} else {
		    	basefilename += "all_";
		    	userProjInfo.categorizer.categorizeForFacet(userProjInfo.metaDataReader,
												null /*Do all facets at once */,
												Categorizer.COMPLEX_INFER_5_STEPS,
												basefilename, false, dbName,
												compileUnmatchedUsage );
			}
		} catch( Exception e ) {
			JOptionPane.showMessageDialog(getJFrame(),
					"Problem encountered when in categorizing the object metadata.\n" + e.toString(),
					"Categorize error", JOptionPane.ERROR_MESSAGE);

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
			helpMenu.setMnemonic('H');
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
			exitMenuItem.setMnemonic('X');
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					checkOnExit();
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	private void checkOnExit() {
		if(userProjInfo.isDirty()) {
			int confirm = JOptionPane.showConfirmDialog(getJFrame(),
					"Save project settings before exiting?"
					+"\nClick YES to save; click NO to discard.");
			if( confirm == JOptionPane.YES_OPTION ) {
				if( appSettings.getLastUserProjectPath() == null )
					saveProjectFileAs();
				else
					saveProjectFile();
			}
		}
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
			aboutMenuItem.setMnemonic('A');
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
			aboutDialog = new JDialog(getJFrame(), "About Delphi", true);
			aboutDialog.setSize(new Dimension(526, 460));
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
			aboutVersionLabel.setText("Delphi Import Manager Version 0.2");
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
			String showColumnNames[] = userProjInfo.metaDataReader.getColumnNames().clone();
			showColumnNames[0] = "All";
			burColumnList = new JList(showColumnNames);
			burColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
			if( showColumnNames.length > 6 )
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
					int colIdx = burColumnList.getSelectedIndex();
					if(colIdx==0) {
						buildFullReport();
					} else { // build all of them
						buildReportForColumn(colIdx);
					}
					buildUsageReportDialog.setVisible(false);
					burColListScrollPane.setEnabled(true);
					buildReportButton.setEnabled(true);
				}
			});
		}
		return buildReportButton;
	}

	/**
	 * Opens an XML ontology file in Delphi format.
	 * Format is documented on the project site.
	 * @return TRUE if successfully opened and parsed
	 */
	protected boolean browseToOntologyFile() {
		boolean opened = false;
		chooser.setFileFilter(xmlFilter);
		setChooserStartFor( userProjInfo.getOntologyPath(), xmlWildcard );
		chooser.setDialogTitle("Open Ontology file...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	if( opened = setOntologyFile(chooser.getSelectedFile().getPath()))
	    		jTextField_OntologyFile.setText(userProjInfo.getOntologyPath());
	    }
		return opened;
	}

	private boolean setOntologyFile(String filename) {
		boolean opened = false;
		if( opened = loadOntology( filename )) {
	        userProjInfo.setOntologyPath(filename);
	        updateUIForUserProjectInfo();
		}
		return opened;
	}

	private boolean loadOntology(String filename) {
		boolean opened = false;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
	        debug(1,"Scanning vocabulary from file: " + filename);
	        setStatus("Scanning vocabulary from file: " + filename);
        // REWRITE - separate this and do it when open the userProjInfo
			Document facetMapDoc = builder.parse(filename);
			userProjInfo.facetMapHashTree = new DoubleHashTree();
			userProjInfo.categorizer = new Categorizer(userProjInfo.facetMapHashTree);
			userProjInfo.facetMapHashTree.PopulateFromFacetMap(facetMapDoc);
	        String s1 = "Found a total of " + userProjInfo.facetMapHashTree.totalTermCount()
    		+ " terms for "
    		+ userProjInfo.facetMapHashTree.totalConceptCount() + " concepts.";
	        setStatus(s1);
	        debug(1, s1);
	        opened = true;
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
	 * Lets the user choose a vocabulary file to open, and then calls
	 * setVocabFile to open the file and set up a VocabTermsReader.
	 * @see VocabTermsReader for details
	 * @return TRUE if successfully opened and parsed
	 */
	protected boolean browseToVocabFile() {
		boolean opened = false;
		chooser.setFileFilter(txtFilter);
		setChooserStartFor( userProjInfo.getVocabTermsPath(), txtWildcard );
		chooser.setDialogTitle("Open Vocabulary export file...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	if( opened = setVocabFile(chooser.getSelectedFile().getPath()))
	    		jTextField_VocabFile.setText(userProjInfo.getVocabTermsPath());
	    }
	    return opened;
	}

	private boolean setVocabFile(String filename) {
		boolean opened = false;
		if( opened = loadVocabFile( filename ) ) {
            userProjInfo.setVocabTermsPath(filename);
            updateUIForUserProjectInfo();
		}
		return opened;
	}
	/**
	 * Opens a special vocabulary dump file in a fairly funky format,
	 * using the path specified.
	 * Builds a table of the read terms and their relationships.
	 * Also creates an XML model of the vocabulary.
	 * On success, update the UserProjectInfo and related UI.
	 * @see VocabTermsReader for details
	 * @return TRUE if successfully opened and parsed
	 */
	private boolean loadVocabFile(String filename) {
		boolean opened = false;
    	try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			userProjInfo.vocabOutputDoc = builder.newDocument();
	    	debug(1,"Scanning vocabulary from file: " + filename);
	        setStatus("Scanning vocabulary from file: " + filename);
	        userProjInfo.vocabTermsReader = new VocabTermsReader(filename);
	        userProjInfo.vocabTermsReader.addSkipTerm("PAHMA Attributes");
	        userProjInfo.vocabTermsReader.addSkipTerm("Object Attributes");
	        userProjInfo.vocabHashTree =
	        	userProjInfo.vocabTermsReader.readTerms( Integer.MAX_VALUE, userProjInfo.vocabOutputDoc,
	        			"PAHMA AUT Collection Facets" );
	        String s1 = "Scanned a total of " + userProjInfo.vocabHashTree.totalTermCount()
	        		+ " terms for "
	        		+ userProjInfo.vocabHashTree.totalConceptCount() + " concepts.";
	        setStatus(s1);
	        debug(1, s1);
	        opened = true;
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

	protected boolean browseToMDConfigFile() {
		boolean opened = false;
		chooser.setFileFilter(xmlFilter);
		setChooserStartFor( userProjInfo.getMetadataConfigPath(), xmlWildcard );
	    chooser.setDialogTitle("Open Metadata Configuration file...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filename = chooser.getSelectedFile().getPath();
	        if( opened = setMDConfigFile(filename) )
		        jTextField_ColConfigFile.setText(filename);
	    }
		return opened;
	}

	private boolean setMDConfigFile(String filename) {
		MetaDataConfigManager mdcfgMgr = loadMDConfig( filename );
		if( mdcfgMgr.GetDumpColConfigInfo() ) {
	        userProjInfo.setMetadataConfigPath(filename);
	        DBMetaDataReader dbmdRdr = mdcfgMgr.GetDBSourceInfo();
	        if(dbmdRdr != null)
	        	userProjInfo.dbMetaDataReader = dbmdRdr;
	        // TODO This should not be here - move to separare config.
	        ImagePathsReader dbImageRdr = mdcfgMgr.GetDBImageInfo();
	        if(dbImageRdr != null)
	        	userProjInfo.imagePathsReader = dbImageRdr;
			updateUIForUserProjectInfo();
			return true;
		}
		return false;
	}

	protected MetaDataConfigManager loadMDConfig( String filename ) {
		MetaDataConfigManager mdcfgMgr = null;
		try {
	        debug(2,"Scanning column config info from file: " + filename);
	        setStatus("Scanning column config info from file: " + filename);
	        mdcfgMgr = new MetaDataConfigManager(filename);
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return mdcfgMgr;
	}

	protected boolean browseToMDFile() {
		boolean opened = false;
		chooser.setFileFilter(txtFilter);
		setChooserStartFor( userProjInfo.getMetadataPath(), txtWildcard );
	    chooser.setDialogTitle("Open Object Metadata file...");
	    int returnVal = chooser.showOpenDialog(getJFrame());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filename = chooser.getSelectedFile().getPath();
	        if( opened = setMDFile(filename) )
		        jTextField_ObjInfoFile.setText(filename);
	    }
		return opened;
	}

	private boolean setMDFile(String filename) {
		boolean opened = false;
		if( opened = loadMetadata( filename )) {
	        userProjInfo.setMetadataPath(filename);
	        updateUIForUserProjectInfo();
		}
		return opened;
	}

	protected boolean loadMetadata( String filename ) {
		boolean opened = false;
		try {
	        debug(2,"Scanning column names from file: " + filename);
	        setStatus("Scanning column names from file: " + filename);
	        int colSep = DumpColumnConfigInfo.getColumnSeparator();
	        int nColumns = DumpColumnConfigInfo.getNumColumns();
	        int objectIDColumnIndex = DumpColumnConfigInfo.getIdColumnIndex();
	        if( !Character.isDefined(colSep) )
				throw new RuntimeException( "No Column separator configured!" );
        // REWRITE - separate this and do it when open the userProjInfo
	        userProjInfo.metaDataReader = new MetaDataReader(filename, colSep,
	        									nColumns, objectIDColumnIndex );
	        String columnNames[] = userProjInfo.metaDataReader.getColumnNames();
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
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Open MetaData File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
		return opened;
	}

	protected void buildFullReport() {
		try {
			setStatus( "Building usage reports...");
			ArrayList<Pair<String,Counter<String>>> allVocabCounts =
				userProjInfo.metaDataReader.compileUsageForAllColumns(2, 10000, -1, this );
			String outFileName = userProjInfo.metaDataReader.getFileName();
			int iDot = outFileName.lastIndexOf('.');
			if( iDot >0 )
				outFileName = outFileName.substring(0, iDot);
			Counter<String> totalCounts = new Counter<String>();
			String filename;
			for(Pair<String,Counter<String>> pair : allVocabCounts) {
				filename = outFileName+"_"+(pair.first.replaceAll("\\W", "_"))+"_Usage.txt";
				debug(1,"Saving column usage info to file: " + filename);
		        setStatus("Saving usage report to file: " + filename);
		        try {
					BufferedWriter writer = new BufferedWriter(
												new OutputStreamWriter(
													new FileOutputStream(filename),"UTF8"));
					Counter<String> colCounts = pair.second;
					totalCounts.addCounts(colCounts);
					colCounts.write(writer, true, 0, null, false, false, true);
					writer.flush();
					writer.close();
				} catch( IOException e ) {
		            e.printStackTrace();
					throw new RuntimeException("Could not create output file: " + filename);
				}
		    }
			// Ask the user whether to save total Usage to a text file, or to a sql load file.
			Object[] options = { "Text file", "SQL loadfile", "CANCEL" };
			int response = JOptionPane.showOptionDialog(getJFrame(),
					            "Do you want to save the statistics as \n"
								+"a text listing, or as an SQL load file?",
								"Saving vocab statistics to file",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null,
								options, options[1] );
			String separator = null;
			boolean escapeStrings = false;
			boolean quoteStrings = false;
			if( response == 0 ) {	// Save to txt file
				filename = outFileName+"_Total_Usage.txt";
				debug(1,"Saving total usage info to file: " + filename);
			} else if( response == 1 ) {	// Save to SQL load file
				filename = outFileName+"_Total_UsageSQL.txt";
				separator = "|";
				quoteStrings = true;
				escapeStrings = true;
				debug(1,"Saving total usage info to SQL load file: " + filename);
			} else {
				filename = null;
			}
			if(filename!= null) {
		        try {
					BufferedWriter writer = new BufferedWriter(
												new OutputStreamWriter(
													new FileOutputStream(filename),"UTF8"));
					writer.append("To load this file, use a sql command:\n");
					writer.append("SET NAMES utf8;\n");
					writer.append("LOAD DATA LOCAL INFILE '{filename}' INTO TABLE termStats CHARACTER SET utf8\n");
					writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES\n");
					writer.append("(`count`, term);\n");
					totalCounts.write(writer, true, 0, separator, escapeStrings, quoteStrings, true);
					writer.flush();
					writer.close();
				} catch( IOException e ) {
		            e.printStackTrace();
					throw new RuntimeException("Could not create output file: " + filename);
				}
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Build Usage Report Error", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
		}
	}

	protected boolean buildReportForColumn( int colIndex ) {
		boolean built = false;
		try {
			setStatus( "Building usage report...");
			Counter<String> vocabCounts =
				userProjInfo.metaDataReader.compileUsageForColumn(colIndex, 2, 10000, -1, this );
			String outFileName = userProjInfo.metaDataReader.getFileName();
			int iDot = outFileName.lastIndexOf('.');
			if( iDot >0 )
				outFileName = outFileName.substring(0, iDot);
			outFileName += "_"+(userProjInfo.metaDataReader.getColumnName(colIndex).replaceAll("\\W", "_"))+"_Usage.txt";
    		String filename = getSafeOutfile("Save Usage report to file...", outFileName,null);
    		if( filename != null ) {
    			debug(1,"Saving column usage info to file: " + filename);
		        setStatus("Saving usage report to file: " + filename);
		        try {
					BufferedWriter writer = new BufferedWriter(
												new OutputStreamWriter(
													new FileOutputStream(filename),"UTF8"));
					vocabCounts.write(writer, true, 0, null, false, false, true);
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

	protected void saveOntologyAsSQL() {
		boolean fWithNewlines = true;	// Easier for debugging output.
		try {
			Object[] options = { "SQL loadfile", "SQL Insert", "CANCEL" };
			int response = JOptionPane.showOptionDialog(getJFrame(),
		            "Do you want to save the ontology as \n"
					+"an SQL load file (required for upload function),\n"
					+"or as an SQL file (for manual DB update)?",
					"Saving ontology to file",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,
					options, options[0] );
			if(response != 0 && response != 1)
				return;		// Cancel
			boolean asSQLInsert = (response==1);
			setStatus( "Building SQL Ontology load file...");
			String suggest = userProjInfo.getFacetsSQLLoadfilePath();
			if(suggest == null) {
				suggest = userProjInfo.getOntologyPath().replaceAll("\\.xml$", "_facets.txt");
			}
    		String filename = getSafeOutfile("Save Facets as SQL load file...",
											    suggest,txtFilter);
    		if( filename == null )
    			return;
	        setStatus("Saving SQL Ontology Facets as SQL: " + filename);
	        try {
				BufferedWriter writer = new BufferedWriter(
											new OutputStreamWriter(
												new FileOutputStream(filename),"UTF8"));
				SQLUtils.writeFacetsSQL( userProjInfo.facetMapHashTree.GetFacets(),
											dbName, writer, asSQLInsert, fWithNewlines );
				writer.flush();
				writer.close();
				userProjInfo.setFacetsSQLLoadfilePath(filename);
				debug(1, "Wrote Ontology facets to SQL file: "+filename);
				setStatus( "Wrote Ontology facets to SQL file: "+filename);
			} catch( IOException e ) {
	            e.printStackTrace();
				throw new RuntimeException(
						"saveOntologyAsSQL: Could not create (or write to) facet output file: "
						+ filename);
			}
			suggest = userProjInfo.getCategoriesSQLLoadfilePath();
			if(suggest == null) {
				suggest = filename.replaceAll("_facets\\.txt$", "_cats.txt");
			}
    		filename = getSafeOutfile("Save Categories as SQL...",
											    suggest,txtFilter);
    		if( filename == null )
    			return;
	        setStatus("Saving SQL Ontology Categories to SQL: " + filename);
	        try {
				BufferedWriter writer = new BufferedWriter(
											new OutputStreamWriter(
												new FileOutputStream(filename),"UTF8"));
				SQLUtils.writeCategoriesSQL( userProjInfo.facetMapHashTree.GetFacets(),
											dbName, writer, asSQLInsert, fWithNewlines );
				writer.flush();
				writer.close();
				userProjInfo.setCategoriesSQLLoadfilePath(filename);
				debug(1, "Wrote Ontology categories to SQL file: "+filename);
				setStatus( "Wrote Ontology categories to SQL file: "+filename);
			} catch( IOException e ) {
	            e.printStackTrace();
				throw new RuntimeException(
						"saveOntologyAsSQL: Could not create (or write to) categories output file: "
						+ filename);
			}
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
								"Save Ontology As SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

	protected String getSafeOutfile( String title, String suggest, FileNameExtensionFilter filter ) {
    	String filename;
    	if( suggest != null )
    		chooser.setSelectedFile(new File( suggest ));
		chooser.setFileFilter(filter);
		if( title == null )
			title = "Save to file...";
		while(true) {
	    chooser.setDialogTitle(title);
			int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal != JFileChooser.APPROVE_OPTION)
		    	return null;
		    else {
		    	filename = chooser.getSelectedFile().getPath();
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

	protected void saveHooksOrExclusionsAsSQL( boolean fSaveHooks ) {
		boolean fWithNewlines = true;	// Easier for debugging output.
		try {
			String variant = fSaveHooks ? "Hook":"Exclusion";
			Object[] options = { "SQL loadfile", "SQL Insert", "CANCEL" };
			int response = JOptionPane.showOptionDialog(getJFrame(),
		            "Do you want to save the "+variant+"s as \n"
					+"an SQL load file (required for upload function),\n"
					+"or as an SQL file (for manual DB update)?",
					"Saving "+variant+"s to file",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,
					options, options[0] );
			if(response != 0 && response != 1)
				return;		// Cancel
			boolean asSQLInsert = (response==1);
			String suggest = fSaveHooks?
						userProjInfo.getHooksSQLLoadfilePath():
						userProjInfo.getExclusionsSQLLoadfilePath();
			if(suggest == null ){
				suggest = userProjInfo.getOntologyPath().replaceAll("\\.xml$",
					variant+"s.txt");
			}
    		String filename = getSafeOutfile("Save "+variant
    							+" values as SQL Insert file...", suggest,txtFilter);
    		if( filename == null )
    			return;
			setStatus("Saving SQL for "+variant+" values to file: " + filename);
	        try {
	    		setStatus( "Building SQL "+variant+" Load file...");
				BufferedWriter writer = new BufferedWriter(
										  new OutputStreamWriter(
											new FileOutputStream(filename),"UTF8"));
				SQLUtils.writeHooksOrExclusionsSQL(userProjInfo.facetMapHashTree.GetFacets(),
									dbName, fSaveHooks, writer, asSQLInsert, fWithNewlines);
				writer.flush();
				writer.close();
				if(fSaveHooks)
					userProjInfo.setHooksSQLLoadfilePath(filename);
				else
					userProjInfo.setExclusionsSQLLoadfilePath(filename);
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

	protected void uploadObjectMetadata() {
		String myName = "Upload Object Metadata";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			String suggest = userProjInfo.getObjectsSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( suggest != null )
	    		chooser.setSelectedFile(new File( suggest ));
		    chooser.setDialogTitle("Select Object Metadata file...");
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getJFrame())) {
		    	String filename = chooser.getSelectedFile().getPath();
				try {
					waDB.uploadObjectsMetadata(filename);
				} catch (Exception e ) {
					JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
							myName, JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	protected void uploadOntoConceptsMetadata() {
		String myName = "Upload Ontology Metadata";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			// Get Facets filepath
			String facetFN = userProjInfo.getFacetsSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( facetFN != null )
	    		chooser.setSelectedFile(new File( facetFN ));
		    chooser.setDialogTitle("Select Facet metadata loadfile...");
		    if(JFileChooser.APPROVE_OPTION != chooser.showOpenDialog(getJFrame()))
		    	return;
		    facetFN = chooser.getSelectedFile().getPath();
			// Get Categories filepath
			String categoriesFN = userProjInfo.getCategoriesSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( categoriesFN != null )
	    		chooser.setSelectedFile(new File( categoriesFN ));
		    chooser.setDialogTitle("Select Categories metadata loadfile...");
		    if(JFileChooser.APPROVE_OPTION != chooser.showOpenDialog(getJFrame()))
			    	return;
		    categoriesFN = chooser.getSelectedFile().getPath();
			// Get Hooks filepath
			String hooksFN = userProjInfo.getHooksSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( hooksFN != null )
	    		chooser.setSelectedFile(new File( hooksFN ));
		    chooser.setDialogTitle("Select Hooks metadata loadfile...");
		    if(JFileChooser.APPROVE_OPTION != chooser.showOpenDialog(getJFrame()))
			    	return;
		    hooksFN = chooser.getSelectedFile().getPath();
			// Get Exclusions filepath
			String exclusionsFN = userProjInfo.getExclusionsSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( exclusionsFN != null )
	    		chooser.setSelectedFile(new File( exclusionsFN ));
		    chooser.setDialogTitle("Select Exclusions metadata loadfile...");
		    if(JFileChooser.APPROVE_OPTION != chooser.showOpenDialog(getJFrame()))
			    	return;
		    exclusionsFN = chooser.getSelectedFile().getPath();
			try {
				waDB.uploadOntologyMetadata(facetFN, categoriesFN, hooksFN, exclusionsFN);
			} catch (Exception e ) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
						myName, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void uploadObjectConceptAssociations() {
		String myName = "Upload Object Concept Associations";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			String suggest = userProjInfo.getObj_catsSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( suggest != null )
	    		chooser.setSelectedFile(new File( suggest ));
		    chooser.setDialogTitle("Select Object Concept Associations file...");
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getJFrame())) {
		    	String filename = chooser.getSelectedFile().getPath();
				try {
					waDB.uploadObjectCategoryAssociations(filename);
				} catch (Exception e ) {
					JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
							myName, JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	protected void uploadImageMetadata() {
		String myName = "Upload Image Media Metadata";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			String suggest = userProjInfo.getMediaSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( suggest != null )
	    		chooser.setSelectedFile(new File( suggest ));
		    chooser.setDialogTitle("Select Image Media Metadata file...");
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getJFrame())) {
		    	String filename = chooser.getSelectedFile().getPath();
				try {
					waDB.uploadImageMediaMetadata(filename);
				} catch (Exception e ) {
					JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
							myName, JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	protected void uploadTermStats() {
		String myName = "Upload Term Stats";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			String suggest = userProjInfo.getMetadataPath();
			chooser.setFileFilter(txtFilter);
	    	if( suggest != null )
	    		chooser.setSelectedFile(new File( suggest ));
		    chooser.setDialogTitle("Select Term Stats Metadata file...");
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getJFrame())) {
		    	String filename = chooser.getSelectedFile().getPath();
				try {
					waDB.uploadTermStatsMetadata(filename);
				} catch (Exception e ) {
					JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
							myName, JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	protected void updateObjectImageCache() {
		String myName = "Update Object Image Cache";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			try {
				waDB.updateObjectMediaCache();
			} catch (Exception e ) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
						myName, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void uploadCatalogCardMetadata() {
		String myName = "Upload Catalog Card Metadata";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			String suggest = userProjInfo.getCatCardsSQLLoadfilePath();
			chooser.setFileFilter(txtFilter);
	    	if( suggest != null )
	    		chooser.setSelectedFile(new File( suggest ));
		    chooser.setDialogTitle("Select Catalog Card Metadata file...");
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getJFrame())) {
		    	String filename = chooser.getSelectedFile().getPath();
				try {
					waDB.uploadCatalogCardMetadata(filename);
				} catch (Exception e ) {
					JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
							myName, JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	protected void updateCategoryCounts() {
		String myName = "Update Category Counts";
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			try {
				waDB.updateOntologyCategoryCounts();
			} catch (Exception e ) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
						myName, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void setupWebAppDBInfo() {
		JDialog webAppDialog = getWebAppDBInfoDialog();
		webAppDialog.pack();
		String host = userProjInfo.getWebAppDB_host();
		String dbName = userProjInfo.getWebAppDB_dbName();
		String user = userProjInfo.getWebAppDB_user();
		String password = userProjInfo.getWebAppDB_password();
		webAppDB_HostNameTextField.setText(((host==null)?"":host));
		webAppDB_DBNameTextField.setText(((dbName==null)?"":dbName));
		webAppDB_UserTextField.setText(((user==null)?"":user));
		webAppDB_PasswordTextField.setText(((password==null)?"":password));
		webAppDialog.pack();
		Point loc = getJFrame().getLocation();
		loc.translate(100, 100);
		webAppDialog.setLocation(loc);
		webAppDialog.setVisible(true);

	}

	protected void setupWebAppDBInfoCommitInfo() {
		userProjInfo.setWebAppDB_host(webAppDB_HostNameTextField.getText());
		userProjInfo.setWebAppDB_dbName(webAppDB_DBNameTextField.getText());
		userProjInfo.setWebAppDB_user(webAppDB_UserTextField.getText());
		userProjInfo.setWebAppDB_password(webAppDB_PasswordTextField.getText());
		userProjInfo.resetWebAppDB();
	}

	protected void setWebAppLockout( boolean lockoutActive ) {
		WebAppDB waDB = checkWebAppDBReady();
		if( waDB != null ) {
			try {
				waDB.setAppLockout(lockoutActive);
			} catch (Exception e ) {
				JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
						"Set Web App Lockout Error:", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private WebAppDB checkWebAppDBReady() {
		if(userProjInfo.webAppDB == null)
			JOptionPane.showMessageDialog(getJFrame(),
				"You must first set up the connection information for the Web App DB,\n"
					+ " before you attempt to upload data.",
				"Upload to Web App DB not ready", JOptionPane.ERROR_MESSAGE);
		return userProjInfo.webAppDB;
	}

	protected void saveVocabAsXML() {
		try {
    		String filename = getSafeOutfile("Save Ontology as XML to file...",
    											userProjInfo.getOntologyPath(),xmlFilter);
		    if(filename != null) {
		    	XMLUtils.writeXMLDocToFile(userProjInfo.vocabOutputDoc, filename);
		        setStatus("Saved Vocabulary as XML to file: " + filename);
		    }
		} catch( RuntimeException e ) {
			JOptionPane.showMessageDialog(getJFrame(), "Error encountered:\n" + e.toString(),
											"Save Vocabulary File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		}
	}

}
