import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.event.ActionEvent;

public class BMConverterMain extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel toFieldscorer,toTemplate;

	/**
	 * * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BMConverterMain window = new BMConverterMain();
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
	public BMConverterMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel mainPanel = new JPanel();
		frame.getContentPane().add(mainPanel, "mainPanel");
		mainPanel.setLayout(null);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setBounds(0, 0, 186, 355);
		mainPanel.add(sidePanel);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setBounds(187, 0, 595, 355);
		mainPanel.add(contentPanel);
		contentPanel.setLayout(new CardLayout(0, 0));
		
		toFieldscorer = new JPanel();
		toFieldscorer.setLayout(null);
		toTemplate = new JPanel();
		toTemplate.setLayout(null);
		contentPanel.add(toFieldscorer);
		contentPanel.add(toTemplate);
		
		JButton btnToFieldscorer = new JButton("Template To FieldScorer");
		btnToFieldscorer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				toTemplate.setVisible(false);
				toFieldscorer.setVisible(true);
			}
		});
		sidePanel.add(btnToFieldscorer);
		
		JButton btnToTemplate = new JButton("FieldScorer To Template");
		btnToTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				toFieldscorer.setVisible(false);
				toTemplate.setVisible(true);
			}
		});
		sidePanel.add(btnToTemplate);
		
		setToFieldScorerPanel(toFieldscorer);
		setToTemplatePanel(toTemplate);
	}

	private void setToTemplatePanel(JPanel toTemplate) {
		JLabel icrisatLogoLabel = new JLabel("");
		toTemplate.add(icrisatLogoLabel);
		addIcrisatLogo(icrisatLogoLabel);
		
		JLabel ibpLogoLabel = new JLabel("");
		toTemplate.add(ibpLogoLabel);
		addbmsLogo(ibpLogoLabel);
		
		JLabel lblFileConverter = new JLabel("FieldScorer to Template");
		lblFileConverter.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblFileConverter.setForeground(Color.BLUE);
		lblFileConverter.setBounds(135, 25, 313, 44);
		toTemplate.add(lblFileConverter);
	}

	private void setToFieldScorerPanel(JPanel toFieldscorer) {
		JLabel icrisatLogoLabel = new JLabel("");
		toFieldscorer.add(icrisatLogoLabel);
		addIcrisatLogo(icrisatLogoLabel);
		
		JLabel ibpLogoLabel = new JLabel("");
		toFieldscorer.add(ibpLogoLabel);
		addbmsLogo(ibpLogoLabel);
		
		JLabel lblFileConverter = new JLabel("Template to FieldScorer");
		lblFileConverter.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblFileConverter.setForeground(Color.BLUE);
		lblFileConverter.setBounds(135, 25, 313, 44);
		toFieldscorer.add(lblFileConverter);

		
		JLabel lblFile = new JLabel("Input File");
		lblFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblFile.setBounds(10, 184, 98, 20);
		toFieldscorer.add(lblFile);

		final JTextField inputFileName = new JTextField();
		inputFileName.setBounds(109, 184, 366, 20);
		toFieldscorer.add(inputFileName);
		inputFileName.setColumns(10);

		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblOutputFile.setBounds(10, 217, 89, 16);
		toFieldscorer.add(lblOutputFile);

		final JTextField outputFileName = new JTextField();
		outputFileName.setBounds(109, 214, 366, 22);
		toFieldscorer.add(outputFileName);
		outputFileName.setColumns(10);

	
		JLabel lblSiteYear = new JLabel("Site Year");
		lblSiteYear.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteYear.setBounds(10, 131, 72, 20);
		toFieldscorer.add(lblSiteYear);

		JLabel lblSiteName = new JLabel("Site Name");
		lblSiteName.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteName.setBounds(257, 135, 89, 14);
		toFieldscorer.add(lblSiteName);

		final JTextField siteName = new JTextField();
		siteName.setBounds(374, 132, 200, 20);
		toFieldscorer.add(siteName);
		siteName.setColumns(10);

		final JComboBox<Integer> comboBox = new JComboBox<Integer>();
		int[] years = { 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015,
				2016, 2017, 2018,2019,2020};
		for (int i = 0; i < years.length; i++) {
			comboBox.addItem(years[i]);
		}
		comboBox.setBounds(100, 130, 72, 22);
		toFieldscorer.add(comboBox);
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
		toFieldscorer.add(btnSubmit);

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
		toFieldscorer.add(btnNewButton);
	}
	
	private void buttonActionPerformed(ActionEvent evt, JTextField inputFileName, JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			inputFileName.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	
	public void addIcrisatLogo(JLabel label) {
		URL url = Application.class.getResource("/icrisat.png");
		ImageIcon icon = new ImageIcon(
				new ImageIcon(url).getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT));
		label.setIcon(icon);
		label.setBounds(433, 292, 150, 50);
	}

	public void addbmsLogo(JLabel label) {
		URL url = Application.class.getResource("/ibp_logo.jpg");
		ImageIcon icon = new ImageIcon(
				new ImageIcon(url).getImage().getScaledInstance(80, 50, Image.SCALE_DEFAULT));
		label.setIcon(icon);	
		label.setBounds(51, 13, 72, 61);
	}
}
