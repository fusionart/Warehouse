package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import ColorRenderers.IncomeReservedColorRenderer;
import controller.Base;
import controller.BaseMethods;
import controller.ExcelFile;
import controller.PrintOutcome;
import model.PalletModel;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.*;
import javax.swing.border.*;

public class IncomeView extends JDialog {

	private JPanel contentPane;
	private JTextField txtBatteryType;
	private JTextField txtDate;
	private JTextField txtQuantity;
	private JTextField txtQuantityReal;
	private JTable tblMain;
	private JLabel lblBackground;
	private JLabel lblPalletPlace;
	private JPanel pnlPalletPlace;

	private static int selectedRow = -1;
	private static int rowToSave;

	private static int excelFileRow;

	private static DefaultTableModel defaultTableModel;
	private static String header[] = { "Складово място", "Тип батерия", "Количество", "Количество по документ",
			"Дата на производство", "Дата на приход", "Час на приход", "Статус", "Резервация" };

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public IncomeView() throws IOException {
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
		pnlButtons.setBounds(1046, 727, 310, 30);
		pnlButtons.setBackground(new Color(255, 255, 255, 150));
		contentPane.add(pnlButtons);

		Action saveAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ValidateForm()) {
					PalletModel pm = CreateModelToSave();

					SummaryView summaryView = new SummaryView(CreateSummaryString(pm));
					Boolean isOkClicked = summaryView.run();
					if (isOkClicked) {

						ExcelFile.SaveData(pm);
						selectedRow = -1;

						ResetForm();
						ExcelFile.CheckIfFileIsEdited();

					}

//					summaryView.addWindowListener(new WindowAdapter() {
//						@Override
//						public void windowClosed(WindowEvent e) {
//							super.windowClosed(e);
//							dispose();
//						}
//					});
				}
			}
		};
		GridBagLayout gbl_pnlButtons = new GridBagLayout();
		gbl_pnlButtons.columnWidths = new int[] { 0, 150, 150, 0 };
		gbl_pnlButtons.rowHeights = new int[] { 30, 0 };
		gbl_pnlButtons.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlButtons.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		pnlButtons.setLayout(gbl_pnlButtons);

		JButton btnSave = new JButton("Запази");
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
		inputMap.put(KeyStroke.getKeyStroke("F10"), "saveAction");
		actionMap.put("saveAction", saveAction);

		JButton btnCancel = new JButton("Отказ");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.fill = GridBagConstraints.BOTH;
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 0;
		pnlButtons.add(btnCancel, gbc_btnCancel);
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
		pnlInput.setBounds(10, 11, 290, 705);
		pnlInput.setOpaque(false);
		pnlInput.setBackground(new Color(255, 255, 255, 200));
		contentPane.add(pnlInput);
		pnlInput.setLayout(null);

		JPanel pnlBatteryType = new JPanel();
		pnlBatteryType.setBounds(20, 0, 250, 74);
		pnlInput.add(pnlBatteryType);
		pnlBatteryType.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlBatteryType = new GridBagLayout();
		gbl_pnlBatteryType.columnWidths = new int[] { 89, 0 };
		gbl_pnlBatteryType.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlBatteryType.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlBatteryType.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlBatteryType.setLayout(gbl_pnlBatteryType);

		JLabel lblBatteryType = new JLabel("Тип Батерия");
		GridBagConstraints gbc_lblBatteryType = new GridBagConstraints();
		gbc_lblBatteryType.fill = GridBagConstraints.BOTH;
		gbc_lblBatteryType.insets = new Insets(0, 0, 5, 0);
		gbc_lblBatteryType.gridx = 0;
		gbc_lblBatteryType.gridy = 0;
		pnlBatteryType.add(lblBatteryType, gbc_lblBatteryType);
		lblBatteryType.setFont(Base.DEFAULT_FONT);

		txtBatteryType = new JTextField();
		txtBatteryType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtDate.requestFocus();
			}
		});
		txtBatteryType.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!txtBatteryType.getText().isEmpty()) {
					SelectRowToSave();
					if (selectedRow > 0) {
						SetTextForManualSelected();
					} else {
						SetTextForAutomaticSelected();
					}
				}
			}
		});
		GridBagConstraints gbc_txtBatteryType = new GridBagConstraints();
		gbc_txtBatteryType.fill = GridBagConstraints.BOTH;
		gbc_txtBatteryType.gridx = 0;
		gbc_txtBatteryType.gridy = 1;
		pnlBatteryType.add(txtBatteryType, gbc_txtBatteryType);
		txtBatteryType.setFont(Base.DEFAULT_FONT);
		txtBatteryType.setColumns(10);

		JPanel pnlDate = new JPanel();
		pnlDate.setBounds(20, 85, 250, 74);
		pnlInput.add(pnlDate);
		pnlDate.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlDate = new GridBagLayout();
		gbl_pnlDate.columnWidths = new int[] { 115, 0 };
		gbl_pnlDate.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlDate.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlDate.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlDate.setLayout(gbl_pnlDate);

		JLabel lblDate = new JLabel("Дата на производство (dd.mm.yyyy)");
		GridBagConstraints gbc_lblDate = new GridBagConstraints();
		gbc_lblDate.fill = GridBagConstraints.BOTH;
		gbc_lblDate.insets = new Insets(0, 0, 5, 0);
		gbc_lblDate.gridx = 0;
		gbc_lblDate.gridy = 0;
		pnlDate.add(lblDate, gbc_lblDate);
		lblDate.setFont(Base.DEFAULT_FONT);

		txtDate = new JTextField();
		txtDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtQuantityReal.requestFocus();
			}
		});
		GridBagConstraints gbc_txtDate = new GridBagConstraints();
		gbc_txtDate.fill = GridBagConstraints.BOTH;
		gbc_txtDate.gridx = 0;
		gbc_txtDate.gridy = 1;
		pnlDate.add(txtDate, gbc_txtDate);
		txtDate.setFont(Base.DEFAULT_FONT);
		txtDate.setColumns(10);

		JPanel pnlQuantity = new JPanel();
		pnlQuantity.setBounds(20, 255, 250, 74);
		pnlInput.add(pnlQuantity);
		pnlQuantity.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlQuantity = new GridBagLayout();
		gbl_pnlQuantity.columnWidths = new int[] { 89, 0 };
		gbl_pnlQuantity.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlQuantity.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlQuantity.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlQuantity.setLayout(gbl_pnlQuantity);

		JLabel lblQuantity = new JLabel("Количество по документ");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.fill = GridBagConstraints.BOTH;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 0);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 0;
		pnlQuantity.add(lblQuantity, gbc_lblQuantity);
		lblQuantity.setFont(Base.DEFAULT_FONT);

		txtQuantity = new JTextField();
		txtQuantity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSave.requestFocus();
			}
		});
		GridBagConstraints gbc_txtQuantity = new GridBagConstraints();
		gbc_txtQuantity.fill = GridBagConstraints.BOTH;
		gbc_txtQuantity.gridx = 0;
		gbc_txtQuantity.gridy = 1;
		pnlQuantity.add(txtQuantity, gbc_txtQuantity);
		txtQuantity.setFont(Base.DEFAULT_FONT);
		txtQuantity.setColumns(10);
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

		JPanel pnlQuantityReal = new JPanel();
		pnlQuantityReal.setBounds(20, 170, 250, 74);
		pnlInput.add(pnlQuantityReal);
		pnlQuantityReal.setBackground(new Color(255, 255, 255, 0));
		GridBagLayout gbl_pnlQuantityReal = new GridBagLayout();
		gbl_pnlQuantityReal.columnWidths = new int[] { 108, 0 };
		gbl_pnlQuantityReal.rowHeights = new int[] { 37, 37, 0 };
		gbl_pnlQuantityReal.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlQuantityReal.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pnlQuantityReal.setLayout(gbl_pnlQuantityReal);

		JLabel lblQuantityReal = new JLabel("Реално количество");
		GridBagConstraints gbc_lblQuantityReal = new GridBagConstraints();
		gbc_lblQuantityReal.fill = GridBagConstraints.BOTH;
		gbc_lblQuantityReal.insets = new Insets(0, 0, 5, 0);
		gbc_lblQuantityReal.gridx = 0;
		gbc_lblQuantityReal.gridy = 0;
		pnlQuantityReal.add(lblQuantityReal, gbc_lblQuantityReal);
		lblQuantityReal.setFont(Base.DEFAULT_FONT);

		txtQuantityReal = new JTextField();
		txtQuantityReal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtQuantity.requestFocus();
			}
		});
		GridBagConstraints gbc_txtQuantityReal = new GridBagConstraints();
		gbc_txtQuantityReal.fill = GridBagConstraints.BOTH;
		gbc_txtQuantityReal.gridx = 0;
		gbc_txtQuantityReal.gridy = 1;
		pnlQuantityReal.add(txtQuantityReal, gbc_txtQuantityReal);
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
					} else {
						txtQuantity.setText(txtQuantityReal.getText());
					}
				}
			}
		});

		pnlPalletPlace = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		pnlPalletPlace.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(100, 149, 237), new Color(160, 160, 160)),
				"Складово място", TitledBorder.LEADING, TitledBorder.TOP, Base.DEFAULT_FONT, null));
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
		scrollPane.setBounds(310, 11, 1046, 705);
		contentPane.add(scrollPane);

		defaultTableModel = new DefaultTableModel(0, 0);

		defaultTableModel.setColumnIdentifiers(header);

		tblMain = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			};

			@Override
			public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
				super.changeSelection(rowIndex, columnIndex, true, false);
			}
		};
		tblMain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedRow = tblMain.getSelectedRow();

				if (selectedRow != -1) {
					int reservedColumn = 8;
					int statusColumn = 7;

					Boolean isReserved = Boolean
							.parseBoolean(tblMain.getModel().getValueAt(selectedRow, reservedColumn).toString());
					Boolean isFree = Boolean
							.parseBoolean(tblMain.getModel().getValueAt(selectedRow, statusColumn).toString());

					if (isReserved || !isFree) {
						selectedRow = -1;
					}
				}

				SelectRowToSave();

				if (selectedRow != -1) {
					SetTextForManualSelected();
				} else {
					if (!txtBatteryType.getText().isEmpty()) {
						SetTextForAutomaticSelected();
					} else {
						lblPalletPlace.setText("");
						pnlPalletPlace.setVisible(false);
					}
				}
			}
		});
		tblMain.setBounds(0, 0, 0, 0);
		tblMain.setFont(Base.DEFAULT_FONT);
		tblMain.setRowHeight(26);
		tblMain.getTableHeader().setFont(Base.DEFAULT_FONT);
		tblMain.getTableHeader().setResizingAllowed(true);
		scrollPane.setViewportView(tblMain);
		tblMain.setModel(defaultTableModel);
		tblMain.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		FillTable();
		BaseMethods.ResizeColumnWidth(tblMain);
		IncomeReservedColorRenderer ircr = new IncomeReservedColorRenderer();
		tblMain.setDefaultRenderer(Object.class, ircr);

		SetBackgroundPicture();
		setVisible(true);
	}

	private void SetTextForManualSelected() {
		pnlPalletPlace.setVisible(true);
		lblPalletPlace.setText("<html>Ръчно избрано складово място: "
				+ tblMain.getModel().getValueAt(selectedRow, 0).toString() + "</html>");
	}

	private void SetTextForAutomaticSelected() {
		pnlPalletPlace.setVisible(true);
		lblPalletPlace.setText("<html>Автоматично избрано складово място: "
				+ tblMain.getModel().getValueAt(rowToSave, 0).toString() + "</html>");
		// .setText("<html>Автоматично избрано складово място: " +
		// ExcelFile.GetPalletName(rowToSave) + "</html>");

	}

	private PalletModel CreateModelToSave() {
		PalletModel pm = new PalletModel();
		pm.setRow(rowToSave);
		pm.setPalletName(tblMain.getModel().getValueAt(rowToSave, 0).toString());
		pm.setBatteryType(txtBatteryType.getText());
		pm.setQuantityReal(Integer.parseInt(txtQuantityReal.getText()));
		pm.setQuantity(Integer.parseInt(txtQuantity.getText()));
		pm.setProductionDate(LocalDate.parse(txtDate.getText(), Base.dateFormat));
		pm.setIncomeDate(LocalDate.now());
		pm.setIncomeTime(LocalTime.now());
		pm.setStatus(false);
		pm.setIsReserved(false);
		pm.setDestination(null);
		return pm;
	}

	private String CreateSummaryString(PalletModel pm) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>Заприходено количество: <br>");
		sb.append("Складово място ");
		sb.append(pm.getPalletName());
		sb.append(", Тип Батерия: ");
		sb.append(pm.getBatteryType());
		sb.append(", Количество: ");
		sb.append(pm.getQuantityReal());
		sb.append("</html>");

		return sb.toString();
	}

	private void ResetForm() {
		txtBatteryType.setText("");
		txtDate.setText("");
		txtQuantity.setText("");
		txtQuantityReal.setText("");
		pnlPalletPlace.setVisible(false);

		txtBatteryType.requestFocus();
	}

	private void SelectRowToSave() {
		if (selectedRow != -1) {
			int column = 0;

			String palletName = tblMain.getModel().getValueAt(selectedRow, column).toString();

			rowToSave = ExcelFile.FindPalletRow(palletName);

		} else {
			if (!txtBatteryType.getText().isEmpty()) {
				rowToSave = ExcelFile.GetClosestFreePlace(txtBatteryType.getText());
				if (rowToSave == -1) {
					rowToSave = ExcelFile.GetFirstFreeRow();
				}
			}
		}
	}

	private Boolean ValidateForm() {
		if (txtBatteryType.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Въведете Тип Батерия", "Грешка", JOptionPane.INFORMATION_MESSAGE);
			txtBatteryType.requestFocus();
			return false;
		}

		if (txtDate.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Въведете Дата", "Грешка", JOptionPane.INFORMATION_MESSAGE);
			txtDate.requestFocus();
			return false;
		}

		if (txtQuantityReal.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Въведете Реално количество", "Грешка",
					JOptionPane.INFORMATION_MESSAGE);
			txtQuantityReal.requestFocus();
			return false;
		}

		if (txtQuantity.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Въведете Kоличество по документ", "Грешка",
					JOptionPane.INFORMATION_MESSAGE);
			txtQuantity.requestFocus();
			return false;
		}

		return true;
	}

	private void SetBackgroundPicture() {
		ImageIcon imageIcon = new ImageIcon(Base.backgroundPic);
		lblBackground = new JLabel(imageIcon);
		lblBackground.setBounds(0, 0, Base.WIDTH, Base.HEIGHT);
		contentPane.add(lblBackground);

		// frmMain.setComponentZOrder(lblBackground, 0);
	}
	
	public static void UpdateTable() {
		FillTable();
	}

	private static void FillTable() {
		
		ExcelFile.setCallingFrame(1);

		HashMap<Integer, PalletModel> data = ExcelFile.GetAllRows();
		PalletModel pm;

		defaultTableModel.setRowCount(0);

		for (Map.Entry<Integer, PalletModel> entry : data.entrySet()) {
			pm = entry.getValue();

			defaultTableModel.addRow(
					new Object[] { pm.getPalletName(), pm.getBatteryType(), pm.getQuantityReal(), pm.getQuantity(),
							BaseMethods.FormatDate(pm.getProductionDate()), BaseMethods.FormatDate(pm.getIncomeDate()),
							pm.getIncomeTime(), pm.getStatus(), pm.getIsReserved() });
		}
	}
}
