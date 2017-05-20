package excelReader.excelReader;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.Color;

import javax.swing.JTextField;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class Application extends Frame{

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
		frame.getContentPane().setLayout(null);
		addIcrisatLogo();

		JLabel lblFile = new JLabel("File Name");
		lblFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblFile.setBounds(10, 181, 121, 20);
		frame.getContentPane().add(lblFile);

		final JTextField fileName = new JTextField();
		fileName.setBounds(109, 184, 366, 20);
		frame.getContentPane().add(fileName);
		fileName.setColumns(10);

		JLabel lblFileConverter = new JLabel("File Converter");
		lblFileConverter.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblFileConverter.setForeground(Color.BLUE);
		lblFileConverter.setBounds(192, 11, 210, 44);
		frame.getContentPane().add(lblFileConverter);
		
		JLabel lblhornysaze = new JLabel("@hornySaze");
		lblhornysaze.setBounds(10, 336, 101, 14);
		frame.getContentPane().add(lblhornysaze);
		
		JLabel lblSiteYear = new JLabel("Site Year");
		lblSiteYear.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteYear.setBounds(10, 131, 72, 20);
		frame.getContentPane().add(lblSiteYear);
		
		final JTextField siteYear = new JTextField();
		siteYear.setBounds(109, 132, 86, 20);
		frame.getContentPane().add(siteYear);
		siteYear.setColumns(10);
		
		JLabel lblSiteName = new JLabel("Site Name");
		lblSiteName.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteName.setBounds(257, 135, 89, 14);
		frame.getContentPane().add(lblSiteName);
		
		final JTextField siteName = new JTextField();
		siteName.setBounds(374, 132, 200, 20);
		frame.getContentPane().add(siteName);
		siteName.setColumns(10);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Converter.convert(fileName.getText(), siteYear.getText(), siteName.getText());
			}
		});
		btnSubmit.setBounds(257, 215, 89, 23);
		frame.getContentPane().add(btnSubmit);
		
		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				buttonActionPerformed(arg0, fileName,fileChooser);      	
			}
		});
		btnNewButton.setBounds(485, 181, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
	
	}


	private void buttonActionPerformed(ActionEvent evt, JTextField fileName, JFileChooser fileChooser) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileName.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
    }
	public void addIcrisatLogo() {

		File resourceDest = new File("src/main/resources");
		String iconLocation = resourceDest.getAbsolutePath() + "\\icrisat.png";
		JLabel icrisatLogoLabel = new JLabel("");
		ImageIcon icon = new ImageIcon(
				new ImageIcon(iconLocation).getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT));
		icrisatLogoLabel.setIcon(icon);
		icrisatLogoLabel.setBounds(407, 286, 167, 50);
		frame.getContentPane().add(icrisatLogoLabel);
	}
}
