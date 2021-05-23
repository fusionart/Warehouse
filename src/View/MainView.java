package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import Controller.Base;
import Controller.BaseMethods;
import Controller.ExcelFile;
import Model.PalletModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import net.miginfocom.swing.MigLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.RowFilter;

public class MainView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblMain;
	private static JLabel lblBackground;
	private ButtonGroup rdbtnGroup;
	private static JPanel pnlButtons;

	private static DefaultTableModel defaultTableModel;
	private TableRowSorter<DefaultTableModel> sorter;

	private static String header[] = { "Складово място", "Тип батерия", "Количество", "Количество по документ",
			"Дата на производство", "Статус", "Дата на приход", "Час на приход", "Резервация" };
	private JTextField txtSearchBatteryType;
	private JTextField txtSearchPallet;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public MainView() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.fullFrameCaption);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(Base.WIDTH, Base.HEIGHT));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				ExcelFile.DropTable();
			}
		});

		defaultTableModel = new DefaultTableModel(0, 0);

		defaultTableModel.setColumnIdentifiers(header);

		rdbtnGroup = new ButtonGroup();

		JPanel pnlRadioButtons = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlRadioButtons.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(100, 149, 237), new Color(160, 160, 160)),
				"Избор на склад", TitledBorder.LEADING, TitledBorder.TOP, Base.DEFAULT_FONT, null));
		pnlRadioButtons.setBounds(10, 10, 600, 60);
		pnlRadioButtons.setOpaque(false);
		pnlRadioButtons.setBackground(new Color(255, 255, 255, 200));
		pnlRadioButtons.setVisible(false);

		contentPane.add(pnlRadioButtons);
		GridBagLayout gbl_pnlRadioButtons = new GridBagLayout();
		gbl_pnlRadioButtons.columnWidths = new int[] { 92, 92, 92, 0 };
		gbl_pnlRadioButtons.rowHeights = new int[] { 31, 0 };
		gbl_pnlRadioButtons.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlRadioButtons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlRadioButtons.setLayout(gbl_pnlRadioButtons);

		JRadioButton rdbtnWarehouseA = new JRadioButton(Base.warehouseAName);
		GridBagConstraints gbc_rdbtnWarehouseA = new GridBagConstraints();
		gbc_rdbtnWarehouseA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnWarehouseA.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnWarehouseA.gridx = 0;
		gbc_rdbtnWarehouseA.gridy = 0;
		pnlRadioButtons.add(rdbtnWarehouseA, gbc_rdbtnWarehouseA);
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

		JRadioButton rdbtnWarehouseB = new JRadioButton(Base.warehouseBName);
		GridBagConstraints gbc_rdbtnWarehouseB = new GridBagConstraints();
		gbc_rdbtnWarehouseB.fill = GridBagConstraints.BOTH;
		gbc_rdbtnWarehouseB.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnWarehouseB.gridx = 1;
		gbc_rdbtnWarehouseB.gridy = 0;
		pnlRadioButtons.add(rdbtnWarehouseB, gbc_rdbtnWarehouseB);
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

		JRadioButton rdbtnWarehouseC = new JRadioButton(Base.warehouseCName);
		GridBagConstraints gbc_rdbtnWarehouseC = new GridBagConstraints();
		gbc_rdbtnWarehouseC.fill = GridBagConstraints.BOTH;
		gbc_rdbtnWarehouseC.gridx = 2;
		gbc_rdbtnWarehouseC.gridy = 0;
		pnlRadioButtons.add(rdbtnWarehouseC, gbc_rdbtnWarehouseC);
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

		JPanel pnlFilters = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlFilters.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(100, 149, 237), new Color(160, 160, 160)), "Филтри",
				TitledBorder.LEADING, TitledBorder.TOP, Base.DEFAULT_FONT, null));
		pnlFilters.setBounds(620, 10, 736, 60);
		pnlRadioButtons.setOpaque(false);
		pnlFilters.setBackground(new Color(255, 255, 255, 150));
		pnlFilters.setVisible(false);
		contentPane.add(pnlFilters);
		GridBagLayout gbl_pnlFilters = new GridBagLayout();
		gbl_pnlFilters.columnWidths = new int[] { 235, 314, 0 };
		gbl_pnlFilters.rowHeights = new int[] { 40, 0 };
		gbl_pnlFilters.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlFilters.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlFilters.setLayout(gbl_pnlFilters);

		JPanel pnlSearchPallet = new JPanel();
		GridBagConstraints gbc_pnlSearchPallet = new GridBagConstraints();
		gbc_pnlSearchPallet.insets = new Insets(0, 0, 3, 5);
		gbc_pnlSearchPallet.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlSearchPallet.gridx = 0;
		gbc_pnlSearchPallet.gridy = 0;
		pnlFilters.add(pnlSearchPallet, gbc_pnlSearchPallet);
		GridBagLayout gbl_pnlSearchPallet = new GridBagLayout();
		gbl_pnlSearchPallet.columnWidths = new int[] { 166, 166, 0 };
		gbl_pnlSearchPallet.rowHeights = new int[] { 37, 0 };
		gbl_pnlSearchPallet.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlSearchPallet.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		pnlSearchPallet.setLayout(gbl_pnlSearchPallet);

		JLabel lblNewLabel_1 = new JLabel("Складово място");
		lblNewLabel_1.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		pnlSearchPallet.add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtSearchPallet = new JTextField();
		txtSearchPallet.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_txtSearchPallet = new GridBagConstraints();
		gbc_txtSearchPallet.fill = GridBagConstraints.BOTH;
		gbc_txtSearchPallet.gridx = 1;
		gbc_txtSearchPallet.gridy = 0;
		pnlSearchPallet.add(txtSearchPallet, gbc_txtSearchPallet);
		txtSearchPallet.setColumns(10);

		txtSearchPallet.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				FilterPallet();
			}

			public void insertUpdate(DocumentEvent e) {
				FilterPallet();
			}

			public void removeUpdate(DocumentEvent e) {
				FilterPallet();
			}
		});

		JPanel pnlSearchBatteryType = new JPanel();
		GridBagConstraints gbc_pnlSearchBatteryType = new GridBagConstraints();
		gbc_pnlSearchBatteryType.insets = new Insets(0, 0, 3, 5);
		gbc_pnlSearchBatteryType.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlSearchBatteryType.gridx = 1;
		gbc_pnlSearchBatteryType.gridy = 0;
		pnlFilters.add(pnlSearchBatteryType, gbc_pnlSearchBatteryType);
		GridBagLayout gbl_pnlSearchBatteryType = new GridBagLayout();
		gbl_pnlSearchBatteryType.columnWidths = new int[] { 166, 166, 0 };
		gbl_pnlSearchBatteryType.rowHeights = new int[] { 37, 0 };
		gbl_pnlSearchBatteryType.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlSearchBatteryType.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlSearchBatteryType.setLayout(gbl_pnlSearchBatteryType);

		JLabel lblNewLabel = new JLabel("Тип Батерия");
		lblNewLabel.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		pnlSearchBatteryType.add(lblNewLabel, gbc_lblNewLabel);

		txtSearchBatteryType = new JTextField();
		txtSearchBatteryType.setFont(Base.DEFAULT_FONT);
		GridBagConstraints gbc_txtSearchBatteryType = new GridBagConstraints();
		gbc_txtSearchBatteryType.fill = GridBagConstraints.BOTH;
		gbc_txtSearchBatteryType.gridx = 1;
		gbc_txtSearchBatteryType.gridy = 0;
		pnlSearchBatteryType.add(txtSearchBatteryType, gbc_txtSearchBatteryType);
		txtSearchBatteryType.setColumns(10);

		txtSearchBatteryType.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				FilterBatteryType();
			}

			public void insertUpdate(DocumentEvent e) {
				FilterBatteryType();
			}

			public void removeUpdate(DocumentEvent e) {
				FilterBatteryType();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 1346, 706);
		contentPane.add(scrollPane);

		sorter = new TableRowSorter<DefaultTableModel>(defaultTableModel);

		tblMain = new JTable(defaultTableModel) {
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
		// tblMain.setModel(defaultTableModel);
		tblMain.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblMain.setRowSorter(sorter);
		BaseMethods.ResizeColumnWidth(tblMain);

		pnlButtons = new JPanel();
		pnlButtons.setBounds(886, 728, 470, 30);
		contentPane.add(pnlButtons);
		pnlButtons.setLayout(null);

		if (Base.showAllWarehouses) {
			pnlButtons.setVisible(false);
		}

		JButton btnIncome = new JButton("Приход");
		btnIncome.setBounds(0, 0, 150, 30);
		pnlButtons.add(btnIncome);
		btnIncome.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					IncomeView incomeView = new IncomeView();
					incomeView.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							super.windowClosed(e);
							FillTable();
						}
					});
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
					OutcomeView outcomeView = new OutcomeView();
					outcomeView.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							super.windowClosed(e);
							FillTable();
						}
					});
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnExpense.setFont(Base.DEFAULT_FONT);

		JButton btnReservation = new JButton("Резервация");
		btnReservation.setBounds(320, 0, 150, 30);
		pnlButtons.add(btnReservation);
		btnReservation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReservationView reservationView = new ReservationView();
					reservationView.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							super.windowClosed(e);
							FillTable();
						}
					});
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnReservation.setFont(Base.DEFAULT_FONT);

		if (Base.showAllWarehouses) {
			pnlRadioButtons.setVisible(true);
			pnlFilters.setVisible(true);
			scrollPane.setBounds(10, 80, 1346, 626);
			LoadSelectedtWarehouseData();
			// FillTable();
		}

		SetBackgroundPicture();
		setVisible(true);
		SetVisibleButtons(false);
		FillTable();
		// SetVisibleButtons(true);
	}

	private void FilterPallet() {
		RowFilter<DefaultTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter(txtSearchPallet.getText(), 0);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	private void FilterBatteryType() {
		RowFilter<DefaultTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter(txtSearchBatteryType.getText(), 1);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
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

	public static void UpdateGui() {
		FillTable();
	}

	public static void SetVisibleButtons(Boolean isVisible) {
		pnlButtons.setVisible(isVisible);

		if (Base.showAllWarehouses) {
			pnlButtons.setVisible(false);
		}
	}

	private static void FillTable() {
		HashMap<Integer, PalletModel> data = ExcelFile.GetAllRows();
		PalletModel pm;

		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(new Object[] { pm.getPalletName(), pm.getBatteryType(), pm.getQuantityReal(),
					pm.getQuantity(), BaseMethods.FormatDate(pm.getProductionDate()), pm.getStatus(),
					BaseMethods.FormatDate(pm.getIncomeDate()), pm.getIncomeTime(), pm.getIsReserved() });
		}
	}
}
