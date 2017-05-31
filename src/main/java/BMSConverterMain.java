import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class BMSConverterMain extends Frame {
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
					BMSConverterMain window = new BMSConverterMain();
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
	private BMSConverterMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		
		final JPanel toFieldscorer = new JPanel();
		toFieldscorer.setLayout(null);
		final JPanel toTemplate = new JPanel();
		toTemplate.setLayout(null);
		contentPanel.add(toFieldscorer);
		contentPanel.add(toTemplate);

		final CreateFieldScorerPanel fieldScorerPanel= new CreateFieldScorerPanel();
		fieldScorerPanel.setToFieldScorerPanel(toFieldscorer, frame);

		final CreateTemplatePanel templatePanel = new CreateTemplatePanel();
		templatePanel.setToTemplatePanel(toTemplate, frame);

		
		JButton btnToFieldscorer = new JButton("Template To FieldScorer");
		btnToFieldscorer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Util.clearAll(templatePanel, fieldScorerPanel);
				toTemplate.setVisible(false);
				toFieldscorer.setVisible(true);
			}
		});
		sidePanel.add(btnToFieldscorer);
		
		JButton btnToTemplate = new JButton("FieldScorer To Template");
		btnToTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Util.clearAll(templatePanel, fieldScorerPanel);
				toFieldscorer.setVisible(false);
				toTemplate.setVisible(true);
			}
		});
		sidePanel.add(btnToTemplate);
		
	}


	
}
