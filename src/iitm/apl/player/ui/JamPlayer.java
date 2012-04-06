package iitm.apl.player.ui;

import iitm.apl.player.Song;
import iitm.apl.player.ThreadedPlayer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

/**
 * The JamPlayer Main Class Sets up the UI, and stores a reference to a threaded
 * player that actually plays a song.
 * 
 * TODO: a) Implement the search functionality b) Implement a play-list
 * generation feature
 */
public class JamPlayer {

	// UI Items
	private JFrame mainFrame;
	private JPanel sort; 
	private PlayerPanel pPanel;
	private TrieNode trie = new TrieNode();
	private JTable libraryTable;
	private LibraryTableModel libraryModel;
	private LibraryTableModel temp = new LibraryTableModel();
	private Thread playerThread = null;
	private ThreadedPlayer player = null;
	private String[] entered = new String[100];
	private ArrayList<Song>[] songs_search = new ArrayList[100];
	private ArrayList<Song> main_songs_search = new ArrayList();
	JTextField searchText;
	private int count = 0;

	public JamPlayer() {
		// Create the player
		for (int i = 0; i < entered.length; i++) {
			entered[i] = "";
		}
		player = new ThreadedPlayer();
		playerThread = new Thread(player);
		playerThread.start();
	}

	/**
	 * Create a file dialog to choose MP3 files to add
	 */
	private Vector<Song> addFileDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;

		File selectedFile = chooser.getSelectedFile();
		// Read files as songs
		Vector<Song> songs = new Vector<Song>();
		if (selectedFile.isFile()
				&& selectedFile.getName().toLowerCase().endsWith(".mp3")) {
			songs.add(new Song(selectedFile));
			return songs;
		} else if (selectedFile.isDirectory()) {
			for (File file : selectedFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".mp3");
				}
			}))
				songs.add(new Song(file));
		}

		return songs;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		mainFrame = new JFrame("JamPlayer");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(300, 400));

		// Create and set up the content pane.
		Container pane = mainFrame.getContentPane();
		pane.add(createMenuBar(), BorderLayout.NORTH);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.EAST);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.WEST);
		pane.add(Box.createVerticalStrut(30), BorderLayout.SOUTH);

		JPanel mainPanel = new JPanel();
		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);

		pPanel = new PlayerPanel(player);
		
		JLabel searchLabel = new JLabel("Search: ");
		JTextField searchText = new JTextField(200);
		// searchText.setEditable(true);
		searchText.setMaximumSize(new Dimension(200, 20));
		searchText.addKeyListener(new KeyListener() {
			// Takes the search string from user
			// searches for a song in the trie tree
			// if songs exists updates the table
			// if search text field is empty outputs all songs into the table
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (c == '\b' && entered[count].length() == 0 && count == 0)
					return;

				else {
					if (c == '\b' && entered[count].length() == 0) {
						count--;
					} else if (c == ' ') {
						count++;
					} else if (c == '\b') {
						entered[count] = entered[count].substring(0,
								entered[count].length() - 1);
					} else {
						entered[count] += c;
					}
					entered[count] = entered[count].toLowerCase();
					songs_search[count] = trie.retSongs(entered[count]);
					main_songs_search = songs_search[0];
					if (main_songs_search != null) {
						for (int k = 1; k <= count; k++) {
							if(songs_search[k]==null && entered[k].length()!=0){
								main_songs_search=null;
								return;
							}
							for (int l = main_songs_search.size() - 1; l >= 0; l--) {
								if (songs_search[k] != null
										&& !songs_search[k]
												.contains(main_songs_search
														.get(l))) {
									main_songs_search.remove(l);
								}
							}
						}
					}
					/*
					 * if (songs_search != null) { for (int i = 0; i <
					 * songs_search.size(); i++) {
					 * System.out.println(songs_search.get(i).getTitle()
					 * .toLowerCase());
					 * System.out.println(songs_search.get(i).getAlbum()
					 * .toLowerCase());
					 * System.out.println(songs_search.get(i).getArtist()
					 * .toLowerCase());
					 * 
					 * } }
					 */

					libraryModel.deleteAll();
					for (int i = 0; main_songs_search != null
							&& i < main_songs_search.size(); i++) {
						libraryModel.add(main_songs_search.get(i));
					}
					if (entered[count].length() == 0 && count == 0) {
						ArrayList<Song> array = new ArrayList();
						trie.auto_complete_Songs(trie, array);
						libraryModel.add(array);
					}
					libraryModel.Sort_Title();
					//libraryModel.fireTableDataChanged();
					libraryModel.resetIdx();
					libraryModel.fireTableDataChanged();
				}

				// TODO: Handle the case when the text field has been
				// modified.
				// Optional: Can you update the search "incrementally" i.e.
				// as
				// and when the user changes the search text?

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});

		libraryModel = new LibraryTableModel();
		libraryTable = new JTable(libraryModel);
		libraryTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1) {
					Song song = libraryModel.get(libraryTable.getSelectedRow());
					if (song != null) {
						player.setSong(song);
						pPanel.setSong(song);
					}
				}
			}
		});

		libraryTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane libraryPane = new JScrollPane(libraryTable);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(pPanel).addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addComponent(searchLabel).addComponent(
										searchText).addContainerGap())
				.addComponent(libraryPane));

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(
				pPanel).addContainerGap().addGroup(
				layout.createParallelGroup(Alignment.CENTER).addComponent(
						searchLabel).addComponent(searchText)).addComponent(
				libraryPane));

		pane.add(mainPanel, BorderLayout.CENTER);

		// Display the window.
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar mbar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem addSongs = new JMenuItem("Add new files to library");
		addSongs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Vector<Song> songs = addFileDialog();
				if (songs != null) {
					// libraryModel.add(songs);
					// trie.add(libraryModel.getSong());

					trie.add(songs);
					ArrayList<Song> arr = new ArrayList();
					trie.auto_complete_Songs(trie, arr);

					for (int i = 0; i < arr.size(); i++) {
						libraryModel.add(arr.get(i));
					}
					libraryModel.Sort_Title();
					libraryModel.resetIdx();
					libraryModel.fireTableDataChanged();

				}
			}
		});
		file.add(addSongs);
		
		JPanel sort = new JPanel();
		JButton album = new JButton(); 
		JButton artist = new JButton(); 
		sort.add(album);
		sort.add(artist);
		
		JMenuItem createPlaylist = new JMenuItem("Create playlist");
		createPlaylist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createPlayListHandler();
			}
		});
		file.add(createPlaylist);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
			}
		});
		file.add(quitItem);

		mbar.add(file);

		return mbar;
	}

	protected void createPlayListHandler() {
		// TODO: Create a dialog window allowing the user to choose length of
		// play list, and a play list you create that best fits the time
		// specified
		PlayListMakerDialog dialog = new PlayListMakerDialog(this);
		dialog.setVisible(true);
	}

	public Vector<Song> getSongList() {
		Vector<Song> songs = new Vector<Song>();
		for (int i = 0; i < libraryModel.getRowCount(); i++)
			songs.add(libraryModel.get(i));
		return songs;
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JamPlayer player = new JamPlayer();
				player.createAndShowGUI();
			}
		});
	}

}
