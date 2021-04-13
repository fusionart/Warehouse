package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import Controller.Base;
import Controller.BaseMethods;
import Controller.ExcelFile;
import Model.PalletModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JRadioButton;
import javax.swing.BoxLayout;

public class MainView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblMain;
	private static JLabel lblBackground;
	private ButtonGroup rdbtnGroup;

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
		
		rdbtnGroup = new ButtonGroup();
		
		JPanel pnlRadioButtons = new JPanel(){
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlRadioButtons.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(100, 149, 237), new Color(160, 160, 160)),
				"Избор на склад", TitledBorder.LEADING, TitledBorder.TOP, Base.DEFAULT_FONT, null));
		pnlRadioButtons.setBounds(10, 10, 370, 60);
		pnlRadioButtons.setOpaque(false);
		pnlRadioButtons.setBackground(new Color(255, 255, 255, 200));
		pnlRadioButtons.setVisible(false);
		contentPane.add(pnlRadioButtons);
		pnlRadioButtons.setLayout(new BoxLayout(pnlRadioButtons, BoxLayout.X_AXIS));
		
		JRadioButton rdbtnWarehouseA = new JRadioButton("Склад А");
		pnlRadioButtons.add(rdbtnWarehouseA);
		rdbtnWarehouseA.setActionCommand("A");
		rdbtnWarehouseA.setFont(Base.DEFAULT_FONT);
		rdbtnWarehouseA.setSelected(true);
		rdbtnGroup.add(rdbtnWarehouseA);
		rdbtnWarehouseA.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadSelectedtWarehouseData();
					FillTable();
				}
			}
		});
		
		JRadioButton rdbtnWarehouseB = new JRadioButton("Склад В");
		pnlRadioButtons.add(rdbtnWarehouseB);
		rdbtnWarehouseB.setActionCommand("B");
		rdbtnWarehouseB.setFont(Base.DEFAULT_FONT);
		rdbtnGroup.add(rdbtnWarehouseB);
		rdbtnWarehouseB.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadSelectedtWarehouseData();
					FillTable();
				}
			}
		});
		
		JRadioButton rdbtnWarehouseC = new JRadioButton("Склад С");
		pnlRadioButtons.add(rdbtnWarehouseC);
		rdbtnWarehouseC.setActionCommand("C");
		rdbtnWarehouseC.setFont(Base.DEFAULT_FONT);
		rdbtnGroup.add(rdbtnWarehouseC);
		rdbtnWarehouseC.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadSelectedtWarehouseData();
					FillTable();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 1346, 706);
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
		pnlButtons.setBounds(886, 728, 470, 30);
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
		btnExpense.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ExcelFile.GetMemoryDb();
					new OutcomeView();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnExpense.setFont(Base.DEFAULT_FONT);

		JButton btnReference = new JButton("Справка");
		btnReference.setBounds(320, 0, 150, 30);
		pnlButtons.add(btnReference);
		btnReference.setFont(Base.DEFAULT_FONT);
		
		if (Base.showAllWarehouses) {
			pnlRadioButtons.setVisible(true);
			scrollPane.setBounds(10, 80, 1346, 626);
			LoadSelectedtWarehouseData();
			FillTable();
		}

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

	private void LoadSelectedtWarehouseData() {
		switch (rdbtnGroup.getSelection().getActionCommand()) {
		case "A":
			Base.AssignMainDbFile(rdbtnGroup.getSelection().getActionCommand());
			break;
		case "B":
			Base.AssignMainDbFile(rdbtnGroup.getSelection().getActionCommand());
			break;
		case "C":
			Base.AssignMainDbFile(rdbtnGroup.getSelection().getActionCommand());
			break;
		default:
			break;
		}
	}
	private void FillTable() {
		HashMap<Integer, PalletModel> data = ExcelFile.GetAllRows();
		PalletModel pm;

		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(new Object[] { pm.getPalletName(), pm.getBatteryType(), pm.getQuantityReal(), pm.getQuantity(),
					pm.getDestination(), pm.getStatus(), BaseMethods.FormatDate(pm.getIncomeDate()), pm.getIncomeTime(),
					pm.getIsReserved() });
		}
	}
}
