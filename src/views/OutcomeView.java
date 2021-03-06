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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import org.apache.derby.vti.IFastPath;

import controller.Base;
import controller.BaseMethods;
import controller.ExcelFile;
import controller.PrintOutcome;
import controller.UpcaseFilter;
import model.PalletModel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class OutcomeView extends JDialog implements TableModelListener {

	private JPanel contentPane;
	private JTextField txtBatteryType;
	private JTextField txtPallet;
	private JTextField txtQuantity;
	private static JTable tblMain;
	private JLabel lblBackground;
	private JButton btnSwap;

	private static int selectedRow = -1;
	private static int rowToSave;

	private ArrayList<PalletModel> pmList = new ArrayList<>();
	private ArrayList<PalletModel> repritntPmList = new ArrayList<>();
	private List<Integer> rowsCheckedList = new ArrayList<>();

	private static DefaultTableModel defaultTableModel;
	private static DefaultTableModel footerTableModel;
	private TableRowSorter<DefaultTableModel> sorter;

	private static int selectedQuantity;
	private Boolean isFiltered = false;

	private static int excelFileRow;

	private static int realQuantityTotal;
	private static int quantityTotal;

	private static String header[] = { "?????", "???????? ?????", "??? ???????", "??????????", "?????????? ?? ????????",
			"???? ?? ????????????", "???? ?? ??????", "??? ?? ??????", "??????", "??????????" };
	private JTable tblFooter;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public OutcomeView() throws IOException {

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
		pnlButtons.setBounds(1491, 909, 419, 30);
		pnlButtons.setBackground(new Color(255, 255, 255, 150));
		contentPane.add(pnlButtons);

		Action saveAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pmList.isEmpty()) {
					SummaryView summaryView = new SummaryView(CreateSummaryString());
					Boolean isOkClicked = summaryView.run();
					if (isOkClicked) {
						SaveData();
						FillTable();

						PrintOutcome(pmList);

						ResetForm();
					}
//						summaryView.addWindowListener(new WindowAdapter() {
//							@Override
//							public void windowClosed(WindowEvent e) {
//								super.windowClosed(e);
//								dispose();
//							}
//						});

				} else {
					JOptionPane.showMessageDialog(null, "???? ???????? ???????? ?????", "??????",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		Action reprintAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!repritntPmList.isEmpty()) {
					PrintOutcome(repritntPmList);
				}
			}
		};

		GridBagLayout gbl_pnlButtons = new GridBagLayout();
		gbl_pnlButtons.columnWidths = new int[] { 0, 150, 150, 0 };
		gbl_pnlButtons.rowHeights = new int[] { 30, 0 };
		gbl_pnlButtons.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlButtons.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		pnlButtons.setLayout(gbl_pnlButtons);

		btnSwap = new JButton("???????");
		btnSwap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				SwapData();
				SummaryView summaryView = new SummaryView(CreateSwapSummaryString());
				Boolean isOkClicked = summaryView.run();
				if (isOkClicked) {
					SaveSwapData();
					FillTable();
					ResetForm();
				} else {
					FillTable();
					ResetForm();
				}

//				summaryView.addWindowListener(new WindowAdapter() {
//					@Override
//					public void windowClosed(WindowEvent e) {
//						super.windowClosed(e);
//						dispose();
//					}
//				});
			}
		});
		GridBagConstraints gbc_btnSwap = new GridBagConstraints();
		gbc_btnSwap.fill = GridBagConstraints.BOTH;
		gbc_btnSwap.insets = new Insets(0, 0, 0, 5);
		gbc_btnSwap.gridx = 0;
		gbc_btnSwap.gridy = 0;
		pnlButtons.add(btnSwap, gbc_btnSwap);
		btnSwap.setFont(Base.DEFAULT_FONT);
		btnSwap.setEnabled(false);

		JButton btnSave = new JButton("??????");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.BOTH;
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 0;
		pnlButtons.add(btnSave, gbc_btnSave);
		btnSave.setFont(Base.DEFAULT_FONT);
		// Add F10 shortcut to save
		InputMap inputMap = btnSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = btnSave.getActionMap();

		btnSave.addActionListener(saveAction);

		btnSave.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!pmList.isEmpty()) {

						SummaryView summaryView = new SummaryView(CreateSummaryString());
						Boolean isOkClicked = summaryView.run();
						if (isOkClicked) {
							SaveData();
						}
//						summaryView.addWindowListener(new WindowAdapter() {
//							@Override
//							public void windowClosed(WindowEvent e) {
//								super.windowClosed(e);
//								dispose();
//							}
//						});
					} else {
						JOptionPane.showMessageDialog(null, "???? ???????? ???????? ?????", "??????",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

		JButton btnCancel = new JButton("?????");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.fill = GridBagConstraints.BOTH;
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 0;
		pnlButtons.add(btnCancel, gbc_btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ResetForm();
				dispose();
			}
		});
		btnCancel.setFont(Base.DEFAULT_FONT);

		inputMap.put(KeyStroke.getKeyStroke("F10"), "saveAction");
		actionMap.put("saveAction", saveAction);

		inputMap.put(KeyStroke.getKeyStroke("F11"), "reprintAction");
		actionMap.put("reprintAction", reprintAction);

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
		pnlBatteryType.setBounds(20, 11, 250, 74);
		pnlInput.add(pnlBatteryType);
		pnlBatteryType.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlBatteryType = new GridBagLayout();
		gbl_pnlBatteryType.columnWidths = new int[] { 89, 0 };
		gbl_pnlBatteryType.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlBatteryType.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlBatteryType.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlBatteryType.setLayout(gbl_pnlBatteryType);

		JLabel lblBatteryType = new JLabel("??? ???????");
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
				ShowQuantityTotal();
				FillFooter();
			}

			public void insertUpdate(DocumentEvent e) {
				FilterBatteryType();
				ShowQuantityTotal();
				FillFooter();
			}

			public void removeUpdate(DocumentEvent e) {
				FilterBatteryType();
				ShowQuantityTotal();
				FillFooter();
			}
		});
//		txtBatteryType.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (!txtBatteryType.getText().trim().isEmpty()) {
//					FillTable(ExcelFile.FilterBatteryType(txtBatteryType.getText()));
//					isFiltered = true;
//				} else {
//					if (txtPallet.getText().trim().isEmpty() && isFiltered) {
//						FillTable();
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
		pnlPallet.setBounds(20, 96, 250, 74);
		pnlInput.add(pnlPallet);
		pnlPallet.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlPallet = new GridBagLayout();
		gbl_pnlPallet.columnWidths = new int[] { 89, 0 };
		gbl_pnlPallet.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlPallet.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlPallet.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlPallet.setLayout(gbl_pnlPallet);

		JLabel lblPallet = new JLabel("???????? ?????");
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
				ShowQuantityTotal();
				FillFooter();
			}

			public void insertUpdate(DocumentEvent e) {
				FilterPallet();
				ShowQuantityTotal();
				FillFooter();
			}

			public void removeUpdate(DocumentEvent e) {
				FilterPallet();
				ShowQuantityTotal();
				FillFooter();
			}
		});
//		txtPallet.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (!txtPallet.getText().trim().isEmpty()) {
//					FillTable(ExcelFile.FilterPallet(txtPallet.getText().toUpperCase()));
//					isFiltered = true;
//				} else {
//					if (txtBatteryType.getText().trim().isEmpty() && isFiltered) {
//						FillTable();
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
		pnlQuantity.setBounds(20, 181, 250, 74);
		pnlInput.add(pnlQuantity);
		pnlQuantity.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlQuantity = new GridBagLayout();
		gbl_pnlQuantity.columnWidths = new int[] { 108, 0 };
		gbl_pnlQuantity.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlQuantity.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlQuantity.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlQuantity.setLayout(gbl_pnlQuantity);

		JLabel lblQuantity = new JLabel("??????????");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.fill = GridBagConstraints.BOTH;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 0);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 0;
		pnlQuantity.add(lblQuantity, gbc_lblQuantity);
		lblQuantity.setFont(Base.DEFAULT_FONT);

		txtQuantity = new JTextField();
		GridBagConstraints gbc_txtQuantity = new GridBagConstraints();
		gbc_txtQuantity.fill = GridBagConstraints.BOTH;
		gbc_txtQuantity.gridx = 0;
		gbc_txtQuantity.gridy = 1;
		pnlQuantity.add(txtQuantity, gbc_txtQuantity);
		txtQuantity.setFont(Base.DEFAULT_FONT);
		txtQuantity.setColumns(10);
		txtQuantity.setEditable(false);
		txtQuantity.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!txtQuantity.getText().equals("")) {
					Boolean isNumber = BaseMethods.CheckIsNumber(txtQuantity.getText());
					if (!isNumber) {
						txtQuantity.setText("");
						txtQuantity.requestFocus();
					} else if (BaseMethods.CheckIfNegative(txtQuantity.getText())) {
						txtQuantity.setText("");
						txtQuantity.requestFocus();
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(310, 11, 1600, 846);
		contentPane.add(scrollPane);

		defaultTableModel = new DefaultTableModel(0, 0);

		defaultTableModel.setColumnIdentifiers(header);

		sorter = new TableRowSorter<DefaultTableModel>(defaultTableModel);

		tblMain = new JTable(defaultTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
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

			}
		});

		tblMain.setBounds(0, 0, 0, 0);
		tblMain.setFont(Base.DEFAULT_FONT);
		tblMain.setRowHeight(26);
		tblMain.getTableHeader().setFont(Base.DEFAULT_FONT);
		tblMain.getTableHeader().setResizingAllowed(true);
		scrollPane.setViewportView(tblMain);
		// tblMain.setModel(defaultTableModel);
		tblMain.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMain.getModel().addTableModelListener(this);
		tblMain.setRowSorter(sorter);
		FillTable();
		BaseMethods.ResizeColumnWidth(tblMain);

		JScrollPane scrollPaneFooter = new JScrollPane();
		scrollPaneFooter.setBounds(310, 868, 1600, 28);
		scrollPaneFooter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPaneFooter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPaneFooter);

		footerTableModel = new DefaultTableModel(0, 0);
		footerTableModel.setColumnIdentifiers(header);

		tblFooter = new JTable(footerTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		tblFooter.setBounds(0, 0, 0, 0);
		tblFooter.setFont(Base.DEFAULT_FONT);
		tblFooter.setRowHeight(26);
		tblFooter.getTableHeader().setFont(Base.DEFAULT_FONT);
		tblFooter.getTableHeader().setResizingAllowed(true);
		tblFooter.getTableHeader().setUI(null);
		tblFooter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPaneFooter.setViewportView(tblFooter);
		FillFooter();

		ResizeTables();
		// IncomeReservedColorRenderer ircr = new IncomeReservedColorRenderer();
		// tblMain.setDefaultRenderer(Object.class, ircr);

		SetBackgroundPicture();
		setVisible(true);

	}

	private void ResizeTables() {
		tblMain.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				final TableColumnModel tableColumnModel = tblMain.getColumnModel();
				TableColumnModel footerColumnModel = tblFooter.getColumnModel();
				for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
					int w = tableColumnModel.getColumn(i).getWidth();
					footerColumnModel.getColumn(i).setMinWidth(w);
					footerColumnModel.getColumn(i).setMaxWidth(w);
					// footerColumnModel.getColumn(i).setPreferredWidth(w);
				}
				tblFooter.doLayout();
				tblFooter.repaint();
				repaint();
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
			}
		});
	}

	private void ResetForm() {
		repritntPmList.clear();
		repritntPmList = (ArrayList<PalletModel>) pmList.clone();
		pmList.clear();
		rowsCheckedList.clear();
		selectedQuantity = 0;
		txtQuantity.setText("");
		txtBatteryType.setText("");
		txtBatteryType.requestFocus();
		btnSwap.setEnabled(false);
	}

	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		if (column == 0) {
			TableModel model = (TableModel) e.getSource();
			// String columnName = model.getColumnName(column);
			Boolean checked = (Boolean) model.getValueAt(row, column);
			if (checked) {
				if (IsRowChecked(row)) {
					AddToListForSave(row);
				}
			} else {
				RemoveFromListForSave(row);
			}
			AllowSwap();
		}
	}

	private void AllowSwap() {
		if (rowsCheckedList.size() == 2) {
			btnSwap.setEnabled(true);
		} else {
			btnSwap.setEnabled(false);
		}
	}

	private Boolean IsRowChecked(int row) {
		if (rowsCheckedList.contains(row)) {
			return false;
		}
		return true;
	}

	private void AddToListForSave(int row) {
		PalletModel pm = new PalletModel();

		pm.setRow(ExcelFile.FindPalletRow(tblMain.getModel().getValueAt(row, 1).toString()));
		pm.setPalletName(tblMain.getModel().getValueAt(row, 1).toString());
		pm.setBatteryType(tblMain.getModel().getValueAt(row, 2).toString());
		pm.setQuantityReal(Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString()));
		pm.setQuantity(Integer.parseInt(tblMain.getModel().getValueAt(row, 4).toString()));
		pm.setProductionDate(LocalDate.parse(tblMain.getModel().getValueAt(row, 5).toString(), Base.dateFormat));
		pm.setIncomeDate(LocalDate.now());
		pm.setIncomeTime(LocalTime.now());
		pm.setStatus(true);
		pm.setIsReserved(false);

		selectedQuantity += Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString());

		rowsCheckedList.add(row);

		QuantityTextboxEdit();

		pmList.add(pm);
	}

	private void RemoveFromListForSave(int row) {

		int isRow = rowsCheckedList.lastIndexOf(row);
		if (isRow != -1) {
			String palletName = tblMain.getModel().getValueAt(row, 1).toString();

			selectedQuantity -= Integer.parseInt(tblMain.getModel().getValueAt(row, 3).toString());

			rowsCheckedList.remove(isRow);

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
	}

	private void QuantityTextboxEdit() {
		txtQuantity.setText(String.valueOf(selectedQuantity));
		if (rowsCheckedList.size() == 1) {
			txtQuantity.setEditable(true);
		} else {
			txtQuantity.setEditable(false);
		}
	}

	private void SaveData() {

		Boolean readyToSave = true;
		int quantityLeft = 0;
		// Save the left quantity in the working Excel warehouse db file
		if (rowsCheckedList.size() == 1) {
			PalletModel pm = pmList.get(0);
			readyToSave = ValidateQuantity(pm);
			if (readyToSave && (pm.getQuantityReal() - Integer.parseInt(txtQuantity.getText()) != 0)) {
				quantityLeft = pm.getQuantityReal() - Integer.parseInt(txtQuantity.getText());
				pm.setQuantityReal(Integer.parseInt(txtQuantity.getText()));
				pm.setQuantity(Integer.parseInt(txtQuantity.getText()));
				pm.setStatus(false);

				pmList.remove(0);
				pmList.add(pm);
			}
		}

		if (readyToSave) {
			ExcelFile.SaveDataAtOutcome(pmList, quantityLeft);
			// ExcelFile.UpdateTable(pmList, quantityLeft);
			ExcelFile.FillOutcomeReport(pmList);
			// ShowNotify(pmList);
		}

		// Set the quantity for the Outcome report Excel file
		// pm.setQuantityReal(Integer.parseInt(txtQuantity.getText()));
		// pm.setQuantity(Integer.parseInt(txtPallet.getText()));

	}

	private void SwapData() {
		int tempRow = 0;
		String tempPalletName;
		PalletModel pm = pmList.get(0);
		PalletModel pm1 = pmList.get(1);

		tempRow = pm.getRow();
		pm.setRow(pm1.getRow());
		pm1.setRow(tempRow);

		tempPalletName = pm.getPalletName();
		pm.setPalletName(pm1.getPalletName());
		pm1.setPalletName(tempPalletName);

		pm.setStatus(false);
		pm1.setStatus(false);

		pmList.clear();
		pmList.add(pm);
		pmList.add(pm1);
	}

	private void SaveSwapData() {
		ExcelFile.SaveSwapData(pmList);
	}

	private String CreateSummaryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>&nbsp;&nbsp;???????? ??????????: <br>");

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pmTemp = (PalletModel) itr.next();
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;???????? ?????: ");
			sb.append(pmTemp.getPalletName());
			sb.append(", ??? ???????: ");
			sb.append(pmTemp.getBatteryType());
			sb.append(", ??????????: ");
			if (rowsCheckedList.size() == 1) {
				sb.append(Integer.parseInt(txtQuantity.getText()));
			} else {
				sb.append(pmTemp.getQuantityReal());
			}

			sb.append("<br>");
		}

		sb.append("</html>");
		return sb.toString();
	}

	private String CreateSwapSummaryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>????????? ?????? ?????: <br>");

		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pmTemp = (PalletModel) itr.next();
			sb.append("???????? ?????: ");
			sb.append(pmTemp.getPalletName());
			sb.append(", ??? ???????: ");
			sb.append(pmTemp.getBatteryType());
			sb.append(", ??????????: ");
			sb.append(pmTemp.getQuantityReal());
			sb.append("<br>");
		}

		sb.append("<br>");
		sb.append("????????? ?????? ????? ?? ???? ???????!");
		sb.append("<br>");

		sb.append("</html>");
		return sb.toString();
	}

	private Boolean ValidateQuantity(PalletModel pm) {
		if (txtQuantity.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "???????? ??????????", "??????", JOptionPane.INFORMATION_MESSAGE);
			txtQuantity.requestFocus();
			return false;
		} else {
			if (Integer.parseInt(txtQuantity.getText()) > pm.getQuantityReal()) {
				JOptionPane.showMessageDialog(null, "?????????? ?????????? ? ??-?????? ?? ???????????? ? ??????.",
						"??????", JOptionPane.INFORMATION_MESSAGE);
				txtQuantity.requestFocus();
				return false;
			}
		}
		return true;
	}

	private void PrintOutcome(ArrayList<PalletModel> pm) {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new PrintOutcome(pm));
		if (job.printDialog()) {
			try {

				// print the label
				job.print();

			} catch (PrinterException pe) {
				// System.out.println(pe);
			}
		}
	}

	private void SetBackgroundPicture() {
		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, Base.WIDTH, Base.HEIGHT);
		contentPane.add(lblBackground);

		// frmMain.setComponentZOrder(lblBackground, 0);
	}

	// Fill filtered table
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

	public static void UpdateTable() {
		FillTable();
	}

	// Fill table with all records Status == false
	private static void FillTable() {

		ExcelFile.setCallingFrame(2);

		HashMap<Integer, PalletModel> data = ExcelFile.GetAllNotFreePlaces();
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

	private static void FillFooter() {
		footerTableModel.setRowCount(0);
		footerTableModel.addRow(new Object[] { null, tblMain.getRowCount(), null, realQuantityTotal, quantityTotal,
				null, null, null, null, null });
	}

	private void ShowQuantityTotal() {
		realQuantityTotal = 0;
		quantityTotal = 0;
		for (int i = 0; i < tblMain.getRowCount(); i++) {
			realQuantityTotal += Integer
					.parseInt(tblMain.getModel().getValueAt(tblMain.convertRowIndexToModel(i), 3).toString());
			quantityTotal += Integer
					.parseInt(tblMain.getModel().getValueAt(tblMain.convertRowIndexToModel(i), 4).toString());
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
