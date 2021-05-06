package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Controller.Base;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SummaryView extends JDialog {

	private JPanel contentPane;

	private final static int WIDTH = 550;
	private final static int HEIGHT = 250;
	private JTextField txtConfirm;

	/**
	 * Create the frame.
	 */
	public SummaryView(String summary) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.FRAME_CAPTION);
		setResizable(false);
		setModal(true);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();

		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 255, 255, 220));
		panel.setBounds(10, 10, WIDTH - 20, HEIGHT - 20);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		contentPane.add(panel);

		JPanel pnlSouth = new JPanel();
		panel.add(pnlSouth, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlSouth = new GridBagLayout();
		gbl_pnlSouth.columnWidths = new int[] { 265, 265, 0 };
		gbl_pnlSouth.rowHeights = new int[] { 29, 0 };
		gbl_pnlSouth.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_pnlSouth.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlSouth.setLayout(gbl_pnlSouth);

		JButton bntOk = new JButton("Ok");
		bntOk.setFont(Base.DEFAULT_FONT);
		bntOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		txtConfirm = new JTextField();
		txtConfirm.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_txtConfirm = new GridBagConstraints();
		gbc_txtConfirm.fill = GridBagConstraints.BOTH;
		gbc_txtConfirm.insets = new Insets(0, 5, 5, 5);
		gbc_txtConfirm.gridx = 0;
		gbc_txtConfirm.gridy = 0;
		pnlSouth.add(txtConfirm, gbc_txtConfirm);
		txtConfirm.setColumns(10);
		GridBagConstraints gbc_bntOk = new GridBagConstraints();
		gbc_bntOk.insets = new Insets(0, 0, 5, 5);
		gbc_bntOk.fill = GridBagConstraints.BOTH;
		gbc_bntOk.gridx = 1;
		gbc_bntOk.gridy = 0;
		pnlSouth.add(bntOk, gbc_bntOk);

		txtConfirm.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				CheckOk();
			}

			public void removeUpdate(DocumentEvent e) {
				CheckOk();
			}

			public void insertUpdate(DocumentEvent e) {
				CheckOk();
			}

			public void CheckOk() {
				if (txtConfirm.getText().equals("OK")) {
					dispose();
				}
			}
		});

		JLabel lblSummary = new JLabel(summary);
		lblSummary.setFont(Base.DEFAULT_FONT);
		panel.add(lblSummary, BorderLayout.CENTER);

		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		JLabel lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, WIDTH, HEIGHT);
		contentPane.add(lblBackground);

		// setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
