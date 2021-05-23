package views;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.Base;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.SwingConstants;

public class LoadingScreen extends JDialog {
	
	private final static int WIDTH = 350;
	private final static int HEIGHT = 150;

	private static JProgressBar progressBar;
	private static JLabel lblPercentage;
	private static JLabel lblBackground;

	private JPanel contentPane;
	private JPanel panel;

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
		contentPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();

		panel = new JPanel();
		panel.setBounds(10, 10, WIDTH-20, HEIGHT-20);
		panel.setBackground(new Color(255, 255, 255, 200));
		contentPane.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 146, 0 };
		gbl_panel.rowHeights = new int[] { 17, 17, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lblPercentage = new JLabel("Зареждане. Моля изчакайте.");
		lblPercentage.setHorizontalAlignment(SwingConstants.CENTER);
		lblPercentage.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_lblPercentage = new GridBagConstraints();
		gbc_lblPercentage.insets = new Insets(0, 0, 5, 0);
		gbc_lblPercentage.fill = GridBagConstraints.BOTH;
		gbc_lblPercentage.gridx = 0;
		gbc_lblPercentage.gridy = 0;
		panel.add(lblPercentage, gbc_lblPercentage);

		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		panel.add(progressBar, gbc_progressBar);
		progressBar.setFont(Base.DEFAULT_FONT);
		progressBar.setStringPainted(true);

		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, WIDTH, HEIGHT);
		contentPane.add(lblBackground);

		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void setProgress(double percentage) {
		progressBar.setValue((int) percentage);
	}
}
