package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controller.Base;
import Controller.BaseMethods;
import Controller.ExcelFile;
import Model.PalletModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblMain;
	private static JLabel lblBackground;

	private static DefaultTableModel defaultTableModel;
	private static String header[] = { "Складово място", "Тип батерия", "Количество", "Количество по документ",
			"Направление", "Статус", "Дата на приход", "Час на приход", "Резервация" };

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public MainView() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.FRAME_CAPTION);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(Base.WIDTH, Base.HEIGHT));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();
		setLocationRelativeTo(null);

		defaultTableModel = new DefaultTableModel(0, 0);

		defaultTableModel.setColumnIdentifiers(header);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 1004, 706);
		contentPane.add(scrollPane);

		tblMain = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		tblMain.setBounds(0, 0, 0, 0);
		tblMain.setFont(Base.DEFAULT_FONT);
		tblMain.setRowHeight(26);
		tblMain.getTableHeader().setFont(Base.DEFAULT_FONT);
		tblMain.getTableHeader().setResizingAllowed(true);
		scrollPane.setViewportView(tblMain);
		tblMain.setModel(defaultTableModel);
		tblMain.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		FillTable();
		BaseMethods.ResizeColumnWidth(tblMain);

		JPanel pnlButtons = new JPanel();
		pnlButtons.setBounds(544, 727, 470, 30);
		contentPane.add(pnlButtons);
		pnlButtons.setLayout(null);

		JButton btnIncome = new JButton("Приход");
		btnIncome.setBounds(0, 0, 150, 30);
		pnlButtons.add(btnIncome);
		btnIncome.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new IncomeView();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnIncome.setFont(Base.DEFAULT_FONT);

		JButton btnExpense = new JButton("Разход");
		btnExpense.setBounds(160, 0, 150, 30);
		pnlButtons.add(btnExpense);
		btnExpense.setFont(Base.DEFAULT_FONT);

		JButton btnReference = new JButton("Справка");
		btnReference.setBounds(320, 0, 150, 30);
		pnlButtons.add(btnReference);
		btnReference.setFont(Base.DEFAULT_FONT);

		SetBackgroundPicture();
		setVisible(true);
	}

	private void SetBackgroundPicture() {
		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, Base.WIDTH, Base.HEIGHT);
		contentPane.add(lblBackground);

		// frmMain.setComponentZOrder(lblBackground, 0);
	}

	private void FillTable() throws IOException {
		HashMap<Integer, PalletModel> data = ExcelFile.GetAllRows();
		PalletModel pm;

		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(new Object[] { pm.getPalletName(), pm.getBatteryType(), pm.getQuantityReal(), pm.getQuantity(),
					pm.getDestination(), pm.getStatus(), pm.getIncomeDate(), pm.getIncomeTime(),
					pm.getIsReserved() });
		}
	}
}
