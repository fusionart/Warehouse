package View;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controller.Base;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;

public class LoadingScreen extends JDialog {

	private static JProgressBar progressBar;
	private static JLabel lblPercentage;
	private static JLabel lblBackground;

	private JPanel contentPane;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					LoadingScreen frame = new LoadingScreen();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//	
	/**
	 * Create the frame.
	 */
	public LoadingScreen() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.FRAME_CAPTION);
		setResizable(false);
		//setModal(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(250, 250));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();
		
		panel = new JPanel();
		panel.setBounds(34, 66, 193, 87);
		contentPane.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 146, 0 };
		gbl_panel.rowHeights = new int[] { 17, 17, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 0;
		panel.add(progressBar, gbc_progressBar);
		progressBar.setStringPainted(true);

		lblPercentage = new JLabel("New label");
		GridBagConstraints gbc_lblPercentage = new GridBagConstraints();
		gbc_lblPercentage.fill = GridBagConstraints.BOTH;
		gbc_lblPercentage.gridx = 0;
		gbc_lblPercentage.gridy = 1;
		panel.add(lblPercentage, gbc_lblPercentage);
		
		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, 250, 250);
		contentPane.add(lblBackground);

		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void setProgress(double percentage) {
		progressBar.setValue((int) percentage);
		lblPercentage.setText(String.valueOf(percentage));
	}
}
