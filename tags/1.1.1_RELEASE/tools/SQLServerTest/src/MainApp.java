
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Event;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Rectangle;
//import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.sql.*;

public class MainApp {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="70,54"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem cutMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="6,684"
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JLabel label1 = null;
	private JTextField server_ip_tf = null;
	private JLabel label2 = null;
	private JButton ConnectBtn = null;
	private JTextArea queryTextArea = null;
	private JButton runQBtn = null;
	private JScrollPane jScrollPane = null;
	private JLabel resultsLabel = null;
	private JTextArea jTextArea = null;

	// Declare the JDBC objects.
	Connection jdbcCon = null;
	Statement jdbcStmt = null;
	ResultSet jdbcResultSet = null;

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
			jFrame.setSize(663, 576);
			jFrame.setContentPane( getJContentPane() );
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
			label1 = new JLabel("Connection String:", SwingConstants.LEFT);
			label1.setBounds(new Rectangle(15, 10, 109, 25));
			label2 = new JLabel("Query String:", SwingConstants.LEFT);
			label2.setBounds(new Rectangle(15, 66, 109, 25));
			resultsLabel = new JLabel("Query Results:", SwingConstants.LEFT);
			resultsLabel.setBounds(new Rectangle(15, 249, 109, 25));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(label1, null);
			jContentPane.add(getServer_ip_tf(), null);
			jContentPane.add(getConnectBtn(), null);
			jContentPane.add(label2, null);
			jContentPane.add(getQueryTextArea(), null);
			jContentPane.add(getRunQBtn(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(resultsLabel, null);
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
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
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
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
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
				public void actionPerformed(ActionEvent evt) {
					if (jdbcResultSet != null) try { jdbcResultSet.close(); } catch(Exception e) {}
					if (jdbcStmt != null) try { jdbcStmt.close(); } catch(Exception e) {}
					if (jdbcCon != null) try { jdbcCon.close(); } catch(Exception e) {}
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
			aboutDialog.setSize(new Dimension(407, 84));
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
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(5);
			borderLayout.setVgap(5);
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(borderLayout);
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
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
			aboutVersionLabel.setText("This is a simple tool to experiment with SQLServer connectivity.");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
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
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
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
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes server_ip_tf
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getServer_ip_tf() {
		if (server_ip_tf == null) {
			server_ip_tf = new JTextField("jdbc:sqlserver://169.229.141.204;databaseName=TMS;user=pschmitz;password=Delphi'sDad");
			server_ip_tf.setBounds(new Rectangle(128, 10, 511, 26));
		}
		return server_ip_tf;
	}

	/**
	 * This method initializes ConnectBtn
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getConnectBtn() {
		if (ConnectBtn == null) {
			ConnectBtn = new JButton();
			ConnectBtn.setText("Connect");
			ConnectBtn.setBounds(new Rectangle(527, 40, 111, 27));
			ConnectBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					try {
					if (jdbcCon != null) try { jdbcCon.close(); } catch(Exception e) {}
					String connectionUrl = server_ip_tf.getText();
					jTextArea.setText("Connecting to:["+connectionUrl+"]...\n");
					// System.out.println("Connecting to:["+connectionUrl+"]");
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					jdbcCon = DriverManager.getConnection(connectionUrl);
					jTextArea.append("Established connection to the sqlserver DB.");
					//System.out.println( "Established connection to the sqlserver DB." );
				}
				// Handle any errors that may have occurred.
				catch (Exception e) {
					jTextArea.append("Connection failed:\n"+e.getMessage());
					e.printStackTrace();
				}
			}
		});
		}
		return ConnectBtn;
	}

	/**
	 * This method initializes queryTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getQueryTextArea() {
		if (queryTextArea == null) {
			queryTextArea = new JTextArea( "{call dbo.sp_help}" );
			queryTextArea.setLineWrap(true);
			queryTextArea.setWrapStyleWord(true);
			queryTextArea.setBounds(new Rectangle(15, 97, 622, 118));
		}
		return queryTextArea;
	}

	/**
	 * This method initializes runQBtn
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getRunQBtn() {
		if (runQBtn == null) {
			runQBtn = new JButton();
			runQBtn.setBounds(new Rectangle(527, 221, 111, 27));
			runQBtn.setText("Run Query");
			runQBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					try {
						// Create and execute an SQL statement that returns some data.
						//String SQL = "sp_help";
						jdbcStmt = jdbcCon.createStatement();
						jTextArea.setText("Executing statement...");
						jdbcResultSet = jdbcStmt.executeQuery(queryTextArea.getText());
						jTextArea.append( "Done. Result Columns: \n" );
						ResultSetMetaData rsmd = jdbcResultSet.getMetaData();
						int nCols = rsmd.getColumnCount();
						for( int iCol=1; iCol<=nCols; iCol++ ) {
							jTextArea.append(rsmd.getColumnName(iCol));
							if( iCol < nCols )
								jTextArea.append( ", " );
						}
						jTextArea.append("\n");
						// Iterate through the data in the result set and display it.
						while (jdbcResultSet.next()) {
							for( int iCol=1; iCol<=nCols; iCol++ )
								jTextArea.append(jdbcResultSet.getString(iCol) + " | " );
							jTextArea.append("\n");
						}
					}
					// Handle any errors that may have occurred.
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (jdbcResultSet != null) try { jdbcResultSet.close(); } catch(Exception e) {}
						if (jdbcStmt != null) try { jdbcStmt.close(); } catch(Exception e) {}
					}
				}
			});
		}
		return runQBtn;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(15, 280, 622, 230));
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainApp application = new MainApp();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
