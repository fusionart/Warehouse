package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import ColorRenderers.IncomeReservedColorRenderer;
import controller.Base;
import controller.BaseMethods;
import controller.ExcelFile;
import controller.UpcaseFilter;
import model.PalletModel;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JRadioButton;

public class ReservationView extends JDialog implements TableModelListener {

	private JPanel contentPane;
	private JTextField txtBatteryType;
	private JTextField txtPallet;
	private JTextField txtQuantityReal;
	private JTable tblMain;
	private JLabel lblBackground;

	private static int selectedRow = -1;
	private static int rowToSave;

	private List<PalletModel> pmList = new ArrayList<>();

	private int selectedQuantity;
	private int rowsChecked;
	private Boolean isFiltered = false;
	JPanel pnlPalletPlace;
	JLabel lblPalletPlace;
	JRadioButton rdbtnIncome;
	JRadioButton rdbtnOutcome;

	private static int excelFileRow;

	private static DefaultTableModel defaultTableModel;
	private TableRowSorter<DefaultTableModel> sorter;
	private static String header[] = { "�����", "�������� �����", "��� �������", "����������", "���������� �� ��������",
			"���� �� ������������", "���� �� ������", "��� �� ������", "������", "����������" };

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public ReservationView() throws IOException {

		int selectedQuantity;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Image frameIcon = Toolkit.getDefaultToolkit().getImage(Base.icon);
		setIconImage(frameIcon);
		setTitle(Base.fullFrameCaption);
		setResizable(false);
		setModal(true);

		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(Base.WIDTH, Base.HEIGHT));
		getContentPane().add(contentPane);
		contentPane.setLayout(null);

		pack();
		setLocationRelativeTo(null);

		JPanel pnlButtons = new JPanel();
		pnlButtons.setBounds(1600, 909, 310, 30);
		pnlButtons.setBackground(new Color(255, 255, 255, 150));
		contentPane.add(pnlButtons);
		pnlButtons.setLayout(null);

		JButton btnSave = new JButton("������");
		btnSave.setBounds(0, 0, 150, 30);
		pnlButtons.add(btnSave);
		btnSave.setFont(Base.DEFAULT_FONT);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnOutcome.isSelected()) {
					if (!pmList.isEmpty()) {
						SummaryView summaryView = new SummaryView(CreateSummaryStringOutcome());
						Boolean isOkClicked = summaryView.run();
						if (isOkClicked) {
							SaveDataOutcome();

							summaryView.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosed(WindowEvent e) {
									super.windowClosed(e);
									dispose();
								}
							});
						}
					} else {
						JOptionPane.showMessageDialog(null, "���� �������� �������� �����", "������",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}

				if (rdbtnIncome.isSelected()) {

					PalletModel pm = CreateModelToSave();

					if (pm != null) {
						SummaryView summaryView = new SummaryView(CreateSummaryStringIncome(pm));
						Boolean isOkClicked = summaryView.run();
						if (isOkClicked) {
							SaveDataIncome(pm);
							selectedRow = -1;

							summaryView.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosed(WindowEvent e) {
									super.windowClosed(e);
									dispose();
								}
							});
						}
					}
				}
			}
		});

		JButton btnCancel = new JButton("�����");
		btnCancel.setBounds(160, 0, 150, 30);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setFont(Base.DEFAULT_FONT);

		JPanel pnlInput = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlInput.setBounds(10, 11, 290, 887);
		pnlInput.setOpaque(false);
		pnlInput.setBackground(new Color(255, 255, 255, 200));
		contentPane.add(pnlInput);
		pnlInput.setLayout(null);

		JPanel pnlBatteryType = new JPanel();
		pnlBatteryType.setBounds(20, 117, 250, 74);
		pnlInput.add(pnlBatteryType);
		pnlBatteryType.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlBatteryType = new GridBagLayout();
		gbl_pnlBatteryType.columnWidths = new int[] { 89, 0 };
		gbl_pnlBatteryType.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlBatteryType.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlBatteryType.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlBatteryType.setLayout(gbl_pnlBatteryType);

		JLabel lblBatteryType = new JLabel("��������");
		GridBagConstraints gbc_lblBatteryType = new GridBagConstraints();
		gbc_lblBatteryType.fill = GridBagConstraints.BOTH;
		gbc_lblBatteryType.insets = new Insets(0, 0, 5, 0);
		gbc_lblBatteryType.gridx = 0;
		gbc_lblBatteryType.gridy = 0;
		pnlBatteryType.add(lblBatteryType, gbc_lblBatteryType);
		lblBatteryType.setFont(Base.DEFAULT_FONT);

		txtBatteryType = new JTextField();
		txtBatteryType.getDocument().addDocumentListener(new DocumentListener() {
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
//		txtBatteryType.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (rdbtnOutcome.isSelected()) {
//					if (!txtBatteryType.getText().trim().isEmpty()) {
//						FillTable(ExcelFile.FilterBatteryType(txtBatteryType.getText()));
//						isFiltered = true;
//					} else {
//						if (txtPallet.getText().trim().isEmpty() && isFiltered) {
//							FillTable();
//						}
//					}
//				}
//			}
//		});
		GridBagConstraints gbc_txtBatteryType = new GridBagConstraints();
		gbc_txtBatteryType.fill = GridBagConstraints.BOTH;
		gbc_txtBatteryType.gridx = 0;
		gbc_txtBatteryType.gridy = 1;
		pnlBatteryType.add(txtBatteryType, gbc_txtBatteryType);
		txtBatteryType.setFont(Base.DEFAULT_FONT);
		txtBatteryType.setColumns(10);

		DocumentFilter dfilter = new UpcaseFilter(Base.FieldLimitSize);

		((AbstractDocument) txtBatteryType.getDocument()).setDocumentFilter(dfilter);
		
		JPanel pnlPallet = new JPanel();
		pnlPallet.setBounds(20, 32, 250, 74);
		pnlInput.add(pnlPallet);
		pnlPallet.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlPallet = new GridBagLayout();
		gbl_pnlPallet.columnWidths = new int[] { 89, 0 };
		gbl_pnlPallet.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlPallet.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlPallet.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlPallet.setLayout(gbl_pnlPallet);

		JLabel lblPallet = new JLabel("�������� �����");
		GridBagConstraints gbc_lblPallet = new GridBagConstraints();
		gbc_lblPallet.fill = GridBagConstraints.BOTH;
		gbc_lblPallet.insets = new Insets(0, 0, 5, 0);
		gbc_lblPallet.gridx = 0;
		gbc_lblPallet.gridy = 0;
		pnlPallet.add(lblPallet, gbc_lblPallet);
		lblPallet.setFont(Base.DEFAULT_FONT);

		txtPallet = new JTextField();
		txtPallet.getDocument().addDocumentListener(new DocumentListener() {
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
//		txtPallet.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (rdbtnOutcome.isSelected()) {
//					if (!txtPallet.getText().trim().isEmpty()) {
//						FillTable(ExcelFile.FilterPallet(txtPallet.getText().toUpperCase()));
//						isFiltered = true;
//					} else {
//						if (txtBatteryType.getText().trim().isEmpty() && isFiltered) {
//							FillTable();
//						}
//					}
//				}
//			}
//		});
		GridBagConstraints gbc_txtPallet = new GridBagConstraints();
		gbc_txtPallet.fill = GridBagConstraints.BOTH;
		gbc_txtPallet.gridx = 0;
		gbc_txtPallet.gridy = 1;
		pnlPallet.add(txtPallet, gbc_txtPallet);
		txtPallet.setFont(Base.DEFAULT_FONT);
		txtPallet.setColumns(10);

		((AbstractDocument) txtPallet.getDocument()).setDocumentFilter(dfilter);

		JPanel pnlQuantity = new JPanel();
		pnlQuantity.setBounds(20, 202, 250, 74);
		pnlInput.add(pnlQuantity);
		pnlQuantity.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlQuantity = new GridBagLayout();
		gbl_pnlQuantity.columnWidths = new int[] { 108, 0 };
		gbl_pnlQuantity.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlQuantity.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlQuantity.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlQuantity.setLayout(gbl_pnlQuantity);

		JLabel lblQuantity = new JLabel("����������");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.fill = GridBagConstraints.BOTH;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 0);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 0;
		pnlQuantity.add(lblQuantity, gbc_lblQuantity);
		lblQuantity.setFont(Base.DEFAULT_FONT);

		txtQuantityReal = new JTextField();
		GridBagConstraints gbc_txtQuantityReal = new GridBagConstraints();
		gbc_txtQuantityReal.fill = GridBagConstraints.BOTH;
		gbc_txtQuantityReal.gridx = 0;
		gbc_txtQuantityReal.gridy = 1;
		pnlQuantity.add(txtQuantityReal, gbc_txtQuantityReal);
		txtQuantityReal.setFont(Base.DEFAULT_FONT);
		txtQuantityReal.setColumns(10);
		txtQuantityReal.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!txtQuantityReal.getText().equals("")) {
					Boolean isNumber = BaseMethods.CheckIsNumber(txtQuantityReal.getText());
					if (!isNumber) {
						txtQuantityReal.setText("");
						txtQuantityReal.requestFocus();
					} else if (BaseMethods.CheckIfNegative(txtQuantityReal.getText())) {
						txtQuantityReal.setText("");
						txtQuantityReal.requestFocus();
					}
				}
			}
		});

		ButtonGroup rdbtnGroup = new ButtonGroup();

		rdbtnIncome = new JRadioButton("������");
		rdbtnIncome.setBounds(20, 303, 109, 23);
		rdbtnIncome.setActionCommand("Income");
		rdbtnIncome.setFont(Base.DEFAULT_FONT);
		rdbtnGroup.add(rdbtnIncome);
		pnlInput.add(rdbtnIncome);

		rdbtnOutcome = new JRadioButton("������");
		rdbtnOutcome.setBounds(141, 303, 109, 23);
		rdbtnOutcome.setActionCommand("Outcome");
		rdbtnOutcome.setFont(Base.DEFAULT_FONT);
		rdbtnGroup.add(rdbtnOutcome);
		pnlInput.add(rdbtnOutcome);

		pnlPalletPlace = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlPalletPlace.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(100, 149, 237), new Color(160, 160, 160)),
				"�������� �����", TitledBorder.LEADING, TitledBorder.TOP, Base.DEFAULT_FONT, null));
		pnlPalletPlace.setBounds(20, 345, 250, 100);
		pnlPalletPlace.setOpaque(false);
		pnlPalletPlace.setBackground(new Color(255, 255, 255, 0));
		pnlPalletPlace.setVisible(false);
		pnlInput.add(pnlPalletPlace);
		pnlPalletPlace.setLayout(new BorderLayout(0, 0));

		lblPalletPlace = new JLabel("");
		pnlPalletPlace.add(lblPalletPlace);
		lblPalletPlace.setFont(Base.DEFAULT_FONT);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(310, 11, 1600, 887);
		contentPane.add(scrollPane);

		defaultTableModel = new DefaultTableModel(0, 0);

		defaultTableModel.setColumnIdentifiers(header);
		sorter = new TableRowSorter<DefaultTableModel>(defaultTableModel);

		tblMain = new JTable(defaultTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Boolean isFree = Boolean.parseBoolean(tblMain.getModel().getValueAt(row,
				// 8).toString());
				Boolean isReserved = Boolean.parseBoolean(tblMain.getModel().getValueAt(row, 9).toString());

				if (isReserved && rdbtnOutcome.isSelected()) {
					return column == 0;
				} else {
					return false;
				}
			};

			@Override
			public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
				super.changeSelection(rowIndex, columnIndex, true, false);
			}

			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Boolean.class;
				default:
					return String.class;
				}
			}
		};
		tblMain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedRow = tblMain.getSelectedRow();

				if (selectedRow != -1) {
					int reservedColumn = 9;
					int statusColumn = 8;

					Boolean isFree = Boolean
							.parseBoolean(tblMain.getModel().getValueAt(selectedRow, statusColumn).toString());

					if (!isFree) {
						selectedRow = -1;
					}
				}

				if (selectedRow != -1) {
					if (rdbtnIncome.isSelected()) {
						int column = 1;

						String palletName = tblMain.getModel().getValueAt(selectedRow, column).toString();

						rowToSave = ExcelFile.FindPalletRow(palletName);

						SetTextForManualSelected();
					}
				} else {
					lblPalletPlace.setText("");
					pnlPalletPlace.setVisible(false);
					txtPallet.setText("");
				}
			}
		});
		tblMain.setBounds(0, 0, 0, 0);
		tblMain.setFont(Base.DEFAULT_FONT);
		tblMain.setRowHeight(26);
		tblMain.getTableHeader().setFont(Base.DEFAULT_FONT);
		tblMain.getTableHeader().setResizingAllowed(true);
		scrollPane.setViewportView(tblMain);
		//tblMain.setModel(defaultTableModel);
		tblMain.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMain.getModel().addTableModelListener(this);
		tblMain.setRowSorter(sorter);
		FillTable();
		BaseMethods.ResizeColumnWidth(tblMain);
		// IncomeReservedColorRenderer ircr = new IncomeReservedColorRenderer();
		// tblMain.setDefaultRenderer(Object.class, ircr);

		SetBackgroundPicture();
		setVisible(true);

	}

	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		if (column == 0) {
			TableModel model = (TableModel) e.getSource();
			String columnName = model.getColumnName(column);
			Boolean checked = (Boolean) model.getValueAt(row, column);
			if (checked) {
				AddToListForSave(row);
			} else {
				RemoveFromListForSave(row);
			}
		}
	}

	private void AddToListForSave(int row) {
		PalletModel pm = new PalletModel();

		pm.setRow(ExcelFile.FindPalletRow(tblMain.getModel().getValueAt(row, 1).toString()));
		pm.setPalletName(tblMain.getModel().getValueAt(row, 1).toString());
		pm.setBatteryType(tblMain.getModel().getValueAt(row, 2).toString());
		pm.setQuantityReal(Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString()));
		pm.setQuantity(0);
		pm.setProductionDate(LocalDate.now());
		pm.setIncomeDate(LocalDate.now());
		pm.setIncomeTime(LocalTime.now());
		pm.setStatus(true);
		pm.setIsReserved(false);

		selectedQuantity += Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString());
		rowsChecked++;
		QuantityTextboxEdit();

		pmList.add(pm);
	}

	private void RemoveFromListForSave(int row) {

		String palletName = tblMain.getModel().getValueAt(row, 1).toString();

		selectedQuantity -= Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString());
		rowsChecked--;
		QuantityTextboxEdit();

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();
			if (pm.getPalletName().equals(palletName)) {
				itr.remove();
				break;
			}
		}
	}

	private void QuantityTextboxEdit() {
		txtQuantityReal.setText(String.valueOf(selectedQuantity));
		if (rowsChecked > 1) {
			txtQuantityReal.setEditable(false);
		} else {
			txtQuantityReal.setEditable(true);
		}
	}

	private void SaveDataOutcome() {
		Boolean readyToSave = true;
		int quantityLeft = 0;
		// Save the left quantity in the working Excel warehouse db file
		if (rowsChecked == 1) {
			PalletModel pm = pmList.get(0);
			readyToSave = ValidateQuantity(pm);
			if (readyToSave && (pm.getQuantityReal() - Integer.parseInt(txtQuantityReal.getText()) != 0)) {
				quantityLeft = pm.getQuantityReal() - Integer.parseInt(txtQuantityReal.getText());
				pm.setQuantityReal(Integer.parseInt(txtQuantityReal.getText()));
				pm.setQuantity(Integer.parseInt(txtQuantityReal.getText()));
				pm.setStatus(false);

				pmList.remove(0);
				pmList.add(pm);
			}
		}

		if (readyToSave) {
			ExcelFile.SaveDataAtOutcome(pmList, quantityLeft);
			ExcelFile.UpdateTable(pmList, quantityLeft);
			ExcelFile.FillOutcomeReport(pmList);
			// ShowNotify(pmList);
		}
	}

	private void SaveDataIncome(PalletModel pm) {

		ExcelFile.SaveData(pm);
	}

	private PalletModel CreateModelToSave() {
		if (selectedRow != -1) {
			PalletModel pm = new PalletModel();
			pm.setRow(rowToSave);
			pm.setPalletName(tblMain.getModel().getValueAt(selectedRow, 1).toString());
			pm.setBatteryType(txtBatteryType.getText());
			pm.setQuantityReal(Integer.parseInt(txtQuantityReal.getText()));
			pm.setQuantity(0);
			pm.setProductionDate(LocalDate.now());
			pm.setIncomeDate(LocalDate.now());
			pm.setIncomeTime(LocalTime.now());
			pm.setStatus(true);
			pm.setIsReserved(true);
			pm.setDestination(null);

			return pm;
		} else {
			JOptionPane.showMessageDialog(null, "���� �������� �������� �����", "������",
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
	}

	private String CreateSummaryStringIncome(PalletModel pm) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>����������� ����������: <br>");
		sb.append("�������� ����� ");
		sb.append(pm.getPalletName());
		sb.append(", ��������: ");
		sb.append(pm.getBatteryType());
		sb.append(", ����������: ");
		sb.append(pm.getQuantityReal());
		sb.append("</html>");

		return sb.toString();
	}

	private String CreateSummaryStringOutcome() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>�������� ����������: <br>");

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pmTemp = (PalletModel) itr.next();
			sb.append("�������� �����: ");
			sb.append(pmTemp.getPalletName());
			sb.append(", ��������: ");
			sb.append(pmTemp.getBatteryType());
			sb.append(", ����������: ");
			sb.append(pmTemp.getQuantityReal());
			sb.append("<br>");
		}

		sb.append("</html>");
		return sb.toString();
	}

	private Boolean ValidateQuantity(PalletModel pm) {
		if (txtQuantityReal.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "�������� ����������", "������", JOptionPane.INFORMATION_MESSAGE);
			txtQuantityReal.requestFocus();
			return false;
		} else {
			if (Integer.parseInt(txtQuantityReal.getText()) > pm.getQuantityReal()) {
				JOptionPane.showMessageDialog(null, "���������� ���������� � ��-������ �� ������������ � ������.",
						"������", JOptionPane.INFORMATION_MESSAGE);
				txtQuantityReal.requestFocus();
				return false;
			}
		}
		return true;
	}

	private void SetTextForManualSelected() {
		pnlPalletPlace.setVisible(true);
		lblPalletPlace.setText("<html>����� ������� �������� �����: "
				+ tblMain.getModel().getValueAt(selectedRow, 1).toString() + "</html>");
		txtPallet.setText(tblMain.getModel().getValueAt(selectedRow, 1).toString());
	}

	private void SetBackgroundPicture() {
		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, Base.WIDTH, Base.HEIGHT);
		contentPane.add(lblBackground);

		// frmMain.setComponentZOrder(lblBackground, 0);
	}

	private void FillTable(HashMap<Integer, PalletModel> data) {
		PalletModel pm;
		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(new Object[] { false, pm.getPalletName(), pm.getBatteryType(),
					pm.getQuantityReal(), pm.getQuantity(), BaseMethods.FormatDate(pm.getProductionDate()),
					BaseMethods.FormatDate(pm.getIncomeDate()), pm.getIncomeTime(), pm.getStatus(),
					pm.getIsReserved() });
		}
	}

	private void FillTable() {

		HashMap<Integer, PalletModel> data = ExcelFile.GetAllRows();
		PalletModel pm;

		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(new Object[] { false, pm.getPalletName(), pm.getBatteryType(),
					pm.getQuantityReal(), pm.getQuantity(), BaseMethods.FormatDate(pm.getProductionDate()),
					BaseMethods.FormatDate(pm.getIncomeDate()), pm.getIncomeTime(), pm.getStatus(),
					pm.getIsReserved() });
		}
	}
	
	private void FilterPallet() {
		RowFilter<DefaultTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter("(?i)" + txtPallet.getText(), 1);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	private void FilterBatteryType() {
		RowFilter<DefaultTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter("(?i)" + txtBatteryType.getText(), 2);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}
}
