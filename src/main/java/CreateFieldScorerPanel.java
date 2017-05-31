import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class CreateFieldScorerPanel {

	private JTextField inputFileName, outputFieldScorerFileName, siteName;

	CreateFieldScorerPanel setInputFileName() {
		this.inputFileName.setText(null);
		return this;
	}

	CreateFieldScorerPanel setOutputFieldScorerFileName() {
		this.outputFieldScorerFileName.setText(null);
		return this;
	}

	void setSiteName() {
		this.siteName.setText(null);
	}

	void setToFieldScorerPanel(JPanel toFieldscorer, final JFrame frame) {
		JLabel icrisatLogoLabel = new JLabel("");
		toFieldscorer.add(icrisatLogoLabel);
		Util.addIcrisatLogo(icrisatLogoLabel);

		JLabel ibpLogoLabel = new JLabel("");
		toFieldscorer.add(ibpLogoLabel);
		Util.addbmsLogo(ibpLogoLabel);

		JLabel lblFileConverter = new JLabel("Template to FieldScorer");
		lblFileConverter.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblFileConverter.setForeground(Color.BLUE);
		lblFileConverter.setBounds(135, 25, 313, 44);
		toFieldscorer.add(lblFileConverter);

		JLabel lblTemplateFile = new JLabel("Template File");
		lblTemplateFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblTemplateFile.setBounds(10, 184, 98, 20);
		toFieldscorer.add(lblTemplateFile);

		inputFileName = new JTextField();
		inputFileName.setBounds(109, 184, 366, 20);
		toFieldscorer.add(inputFileName);
		inputFileName.setColumns(10);

		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setFont(new Font("Arial", Font.BOLD, 15));
		lblOutputFile.setBounds(10, 217, 89, 16);
		toFieldscorer.add(lblOutputFile);

		outputFieldScorerFileName = new JTextField();
		outputFieldScorerFileName.setBounds(109, 214, 366, 22);
		toFieldscorer.add(outputFieldScorerFileName);
		outputFieldScorerFileName.setColumns(10);

		JLabel lblSiteYear = new JLabel("Site Year");
		lblSiteYear.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteYear.setBounds(10, 131, 72, 20);
		toFieldscorer.add(lblSiteYear);

		JLabel lblSiteName = new JLabel("Site Name");
		lblSiteName.setFont(new Font("Arial", Font.BOLD, 15));
		lblSiteName.setBounds(257, 135, 89, 14);
		toFieldscorer.add(lblSiteName);

		siteName = new JTextField();
		siteName.setBounds(374, 132, 200, 20);
		toFieldscorer.add(siteName);
		siteName.setColumns(10);

		final JComboBox<Integer> comboBox = new JComboBox<Integer>();
		int[] years = { 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015,
				2016, 2017, 2018, 2019, 2020 };
		for (int year : years) {
			comboBox.addItem(year);
		}
		comboBox.setBounds(100, 130, 72, 22);
		toFieldscorer.add(comboBox);
		comboBox.getSelectedIndex();

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// checkOutputFileName();
				String status = TemplateToFieldScorerConverter.convert(inputFileName.getText(), outputFieldScorerFileName.getText(),
						comboBox.getSelectedIndex(), siteName.getText());
				JOptionPane.showMessageDialog(frame, status);
			}
		});
		btnSubmit.setBounds(257, 269, 89, 23);
		toFieldscorer.add(btnSubmit);

		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Util.addFileChooser(arg0, inputFileName, frame);
				String name = JOptionPane.showInputDialog(frame, "What is output file name?",
						inputFileName.getText().substring(0, inputFileName.getText().lastIndexOf(".")) + "_processed"
								+ ".csv");
				outputFieldScorerFileName.setText(name);
			}
		});
		btnNewButton.setBounds(485, 181, 89, 23);
		toFieldscorer.add(btnNewButton);
	}

}
