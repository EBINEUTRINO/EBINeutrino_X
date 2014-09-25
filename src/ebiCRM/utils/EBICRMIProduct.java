package ebiCRM.utils;

public class EBICRMIProduct {
	
	private String name="";
	private long quantity;
	private String calculatedSPriceGross="";
	private String calculatedSPriceNet = "";
	private double calDpriceGross=0.0;
	private double calDpriceNet=0.0;
	private String taxType="";
	private String calculatedSDeduction="";
	
	
	public EBICRMIProduct(){
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getQuantity() {
		return quantity;
	}


	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}


	public String getCalculatedSPriceGross() {
		return calculatedSPriceGross;
	}


	public void setCalculatedSPriceGross(String calculatedSPriceGross) {
		this.calculatedSPriceGross = calculatedSPriceGross;
	}


	public String getCalculatedSPriceNet() {
		return calculatedSPriceNet;
	}


	public void setCalculatedSPriceNet(String calculatedSPriceNet) {
		this.calculatedSPriceNet = calculatedSPriceNet;
	}


	public double getCalDpriceGross() {
		return calDpriceGross;
	}


	public void setCalDpriceGross(double calDpriceGross) {
		this.calDpriceGross = calDpriceGross;
	}


	public double getCalDpriceNet() {
		return calDpriceNet;
	}


	public void setCalDpriceNet(double calDpriceNet) {
		this.calDpriceNet = calDpriceNet;
	}


	public String getTaxType() {
		return taxType;
	}


	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}


	public String getCalculatedSDeduction() {
		return calculatedSDeduction;
	}


	public void setCalculatedSDeduction(String calculatedSDeduction) {
		this.calculatedSDeduction = calculatedSDeduction;
	}
	

}
