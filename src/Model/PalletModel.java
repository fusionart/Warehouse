package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import controller.Base;

public class PalletModel {

	private int row;
	private String palletName;
	private String batteryType;
	private int quantityReal;
	private int quantity;
	private String destination;
	private Boolean isFree;
	private LocalDate productionDate;
	private LocalDate incomeDate;
	private LocalTime incomeTime;
	private Boolean isReserved;

	public PalletModel() {

	}

	public PalletModel(ArrayList<String> temp) {
		super();
		this.row = Integer.parseInt(temp.get(0));
		this.palletName = temp.get(1);
		this.batteryType = temp.get(2);

		this.quantityReal = TryParseInt(temp.get(3), 0);

		this.quantity = TryParseInt(temp.get(4), 0);

		if (TryParseDate(temp.get(5))) {
			this.productionDate = LocalDate.parse(temp.get(5), Base.dateFormat);
		} else {
			this.productionDate = null;
		}
		
		this.isFree = Boolean.parseBoolean(temp.get(6));

		if (TryParseDate(temp.get(7))) {
			this.incomeDate = LocalDate.parse(temp.get(7), Base.dateFormat);
		} else {
			this.incomeDate = null;
		}

		if (TryParseTime(temp.get(8))) {
			this.incomeTime = LocalTime.parse(temp.get(8), Base.timeFormat);
		} else {
			this.incomeTime = null;
		}

		this.isReserved = Boolean.parseBoolean(temp.get(9));
	}

	private int TryParseInt(String value, int defaultVal) {
		try {
			double d = Double.parseDouble(value);
			return (int) d;
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	private boolean TryParseDate(String value) {
		try {
			LocalDate.parse(value, Base.dateFormat);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	private boolean TryParseTime(String value) {
		try {
			LocalTime.parse(value, Base.timeFormat);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getPalletName() {
		return palletName;
	}

	public void setPalletName(String palletName) {
		this.palletName = palletName;
	}

	public String getBatteryType() {
		return batteryType;
	}

	public void setBatteryType(String batteryType) {
		this.batteryType = batteryType;
	}

	public int getQuantityReal() {
		return quantityReal;
	}

	public void setQuantityReal(int quantityReal) {
		this.quantityReal = quantityReal;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Boolean getStatus() {
		return isFree;
	}

	public void setStatus(Boolean status) {
		this.isFree = status;
	}

	public LocalDate getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(LocalDate productionDate) {
		this.productionDate = productionDate;
	}

	public LocalDate getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(LocalDate incomeDate) {
		this.incomeDate = incomeDate;
	}

	public LocalTime getIncomeTime() {
		return incomeTime;
	}

	public void setIncomeTime(LocalTime incomeTime) {
		this.incomeTime = incomeTime;
	}

	public Boolean getIsReserved() {
		return isReserved;
	}

	public void setIsReserved(Boolean isReserved) {
		this.isReserved = isReserved;
	}

}
