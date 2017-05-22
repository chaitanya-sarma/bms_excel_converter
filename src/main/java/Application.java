
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JTextField;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

public class Application extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;

	/**
	 * * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = frame.getContentPane();
		container.setLayout(null);
		addIcrisatLogo();

		JLabel lblFile = new JLabel("Input File");
		lblFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblFile.setBounds(10, 184, 98, 20);
		frame.getContentPane().add(lblFile);

		final JTextField inputFileName = new JTextField();
		inputFileName.setBounds(109, 184, 366, 20);
		frame.add(inputFileName);
		inputFileName.setColumns(10);

		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblOutputFile.setBounds(10, 217, 89, 16);
		frame.add(lblOutputFile);

		final JTextField outputFileName = new JTextField();
		outputFileName.setBounds(109, 214, 366, 22);
		frame.add(outputFileName);
		outputFileName.setColumns(10);

		JLabel lblFileConverter = new JLabel("File Converter");
		lblFileConverter.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblFileConverter.setForeground(Color.BLUE);
		lblFileConverter.setBounds(192, 11, 210, 44);
		frame.getContentPane().add(lblFileConverter);

		JLabel lblSiteYear = new JLabel("Site Year");
		lblSiteYear.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteYear.setBounds(10, 131, 72, 20);
		frame.getContentPane().add(lblSiteYear);

		JLabel lblSiteName = new JLabel("Site Name");
		lblSiteName.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteName.setBounds(257, 135, 89, 14);
		frame.getContentPane().add(lblSiteName);

		final JTextField siteName = new JTextField();
		siteName.setBounds(374, 132, 200, 20);
		frame.getContentPane().add(siteName);
		siteName.setColumns(10);

		final JComboBox<Integer> comboBox = new JComboBox<Integer>();
		int[] years = { 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015,
				2016, 2017 };
		for (int i = 0; i < years.length; i++) {
			comboBox.addItem(years[i]);
		}
		comboBox.setBounds(100, 130, 72, 22);
		frame.getContentPane().add(comboBox);
		comboBox.getSelectedIndex();

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// checkOutputFileName();
				String status = Converter.convert(inputFileName.getText(),outputFileName.getText(), comboBox.getSelectedIndex(),
						siteName.getText());
				JOptionPane.showMessageDialog(frame, status);
			}
		});
		btnSubmit.setBounds(257, 269, 89, 23);
		frame.getContentPane().add(btnSubmit);

		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				buttonActionPerformed(arg0, inputFileName, fileChooser);
				String name = JOptionPane.showInputDialog(frame, "What is output file name?",
						inputFileName.getText().substring(0, inputFileName.getText().lastIndexOf(".")) + "_processed"
								+ ".csv");
				outputFileName.setText(name);
			}
		});
		btnNewButton.setBounds(485, 181, 89, 23);
		frame.getContentPane().add(btnNewButton);

	}

	private void buttonActionPerformed(ActionEvent evt, JTextField inputFileName, JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			inputFileName.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void addIcrisatLogo() {
		URL url = Application.class.getResource("/icrisat.png");
		JLabel icrisatLogoLabel = new JLabel("");
		ImageIcon icon = new ImageIcon(
				new ImageIcon(url).getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT));
		icrisatLogoLabel.setIcon(icon);
		icrisatLogoLabel.setBounds(407, 286, 167, 50);
		frame.add(icrisatLogoLabel);
	}
}
