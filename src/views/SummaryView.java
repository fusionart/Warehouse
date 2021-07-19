package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import controller.Base;
import controller.UpcaseFilter;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;

public class SummaryView extends JDialog implements ActionListener, DocumentListener {

	private JPanel contentPane;

	private final static int WIDTH = 800;
	private final static int HEIGHT = 250;
	private final static int SINGLE_ROW_HEIGHT = 21;
	private JTextField txtConfirm;
	private JButton btnOk;
	private Boolean isOkClicked = false;

	/**
	 * Create the frame.
	 */
	public SummaryView(String summary) {
		int finalHeight = HEIGHT;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.FRAME_CAPTION);
		setResizable(false);
		setModal(true);

		JLabel lblSummary = new JLabel(summary);
		lblSummary.setFont(Base.DEFAULT_FONT);
		lblSummary.setSize(lblSummary.getPreferredSize());

		if (lblSummary.getSize().height / SINGLE_ROW_HEIGHT > 8) {
			int rows = lblSummary.getSize().height / SINGLE_ROW_HEIGHT;

			finalHeight += (rows - 8) * 21;
		}
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(WIDTH, finalHeight));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();

		JPanel panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		panel.setBackground(new Color(255, 255, 255, 180));
		panel.setBounds(10, 10, WIDTH - 20, finalHeight - 20);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		contentPane.add(panel);

		JPanel pnlSouth = new JPanel();
		panel.add(pnlSouth, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlSouth = new GridBagLayout();
		gbl_pnlSouth.columnWidths = new int[] { 175, 175, 175, 0 };
		gbl_pnlSouth.rowHeights = new int[] { 30, 0 };
		gbl_pnlSouth.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlSouth.rowWeights = new double[] { 1.0, 0.0 };
		pnlSouth.setLayout(gbl_pnlSouth);

		btnOk = new JButton("Ok");
		btnOk.setFont(Base.DEFAULT_FONT);
		btnOk.addActionListener(this);
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.fill = GridBagConstraints.BOTH;
		gbc_btnOk.gridx = 1;
		gbc_btnOk.gridy = 0;
		pnlSouth.add(btnOk, gbc_btnOk);

		JButton btnCancel = new JButton("Отказ");
		btnCancel.setFont(Base.DEFAULT_FONT);
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.fill = GridBagConstraints.BOTH;
		gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 0;
		pnlSouth.add(btnCancel, gbc_btnCancel);

		txtConfirm = new JTextField();
		txtConfirm.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_txtConfirm = new GridBagConstraints();
		gbc_txtConfirm.fill = GridBagConstraints.BOTH;
		gbc_txtConfirm.insets = new Insets(0, 5, 5, 5);
		gbc_txtConfirm.gridx = 0;
		gbc_txtConfirm.gridy = 0;
		pnlSouth.add(txtConfirm, gbc_txtConfirm);
		txtConfirm.setColumns(10);

		txtConfirm.getDocument().addDocumentListener(this);

		DocumentFilter dfilter = new UpcaseFilter(Base.FieldLimitSize);

		((AbstractDocument) txtConfirm.getDocument()).setDocumentFilter(dfilter);

		panel.add(lblSummary, BorderLayout.CENTER);

		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		JLabel lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, WIDTH, finalHeight);
		contentPane.add(lblBackground);

		// setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		// setVisible(true);
	}

	public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
			throws BadLocationException {
		fb.insertString(offset, text.toUpperCase(), attr);
	}

	// no need to override remove(): inherited version allows all removals

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr)
			throws BadLocationException {
		fb.replace(offset, length, text.toUpperCase(), attr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOk) {
			isOkClicked = true;
		} else {
			isOkClicked = false;
		}
		dispose();
	}

	public Boolean run() {
		this.setVisible(true);
		// this.setLocationRelativeTo(null);
		return isOkClicked;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		CheckOk();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		CheckOk();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		CheckOk();
	}

	public void CheckOk() {
		if (txtConfirm.getText().equals("OK")) {
			btnOk.doClick();
		}
	}
}
