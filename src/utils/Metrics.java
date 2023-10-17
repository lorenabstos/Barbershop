package utils;

import App.Barbershop;

public class Metrics {
	private double officer;
	private double sergeant;
	private double corporal;
	private double empty;

	public Metrics(double officer, double sergeant, double corporal) {
		this.empty = Barbershop.CHAIRS_AMOUNT - officer - sergeant - corporal;
		this.officer = officer;
		this.sergeant = sergeant;
		this.corporal = corporal;
	}

	public Metrics() {
		this.empty = 0;
		this.officer = 0;
		this.sergeant = 0;
		this.corporal = 0;
	}

	public void add(Metrics other) {
		this.officer += other.officer;
		this.sergeant += other.sergeant;
		this.corporal += other.corporal;
		this.empty += other.empty;
	}

	public void divide(double num) {
		this.officer /= num;
		this.sergeant /= num;
		this.corporal /= num;
		this.empty /= num;
	}

	public Metrics divideBy(double num) {
		return new Metrics(
				this.officer / num,
				this.sergeant / num,
				this.corporal / num
		);
	}

	public void setCorporal(double corporal) {
		this.corporal = corporal;
	}

	public void setEmpty(double empty) {
		this.empty = empty;
	}

	public void setOfficer(double officer) {
		this.officer = officer;
	}

	public void setSergeant(double sergeant) {
		this.sergeant = sergeant;
	}

	public double getCorporal() {
		return corporal;
	}

	public double getEmpty() {
		return empty;
	}

	public double getOfficer() {
		return officer;
	}

	public double getSergeant() {
		return sergeant;
	}
}
