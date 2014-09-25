package ebiCRM.utils;


import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;



public class EBISearchTreeNode extends DefaultMutableTreeTableNode {
	
    
    
    private String IntNr;
    private String name;
    private String Street;
    private String Zip;
    private String Location;
    private String Country;
    private String Category;
    private String Cooperation;
    private String Qualification;
    private String isLock;
    private Integer numValue;
    private String companyID;
    
    
    
	       
	    public EBISearchTreeNode(String IntNr,String name,String Street,String Zip,
	    		String Location,String Country,String Category,String Cooperation, String Qualification, String isLock,String companyID) {
	                this.IntNr = IntNr;
	                this.name = name;
	                this.Street = Street;
	                this.Zip = Zip;
	                this.Location = Location;
	                this.Country = Country;
	                this.Category = Category;
	                this.Qualification = Qualification;
	                this.isLock = isLock;
	                this.companyID = companyID;
	                this.Cooperation = Cooperation;
	        }

	    public Integer getNumValue() {
	        return numValue;
	    }

	    public void setNumValue(Integer numValue) {
	        this.numValue = numValue;
	    }

	    public String toString() {
	         return this.getIntNr();
	    }

//		/**
//		 * @return the amILeaf
//		 */
//		public boolean isAmILeaf() {
//			return amILeaf;
//		}

		/**
		 * @return the category
		 */
		public String getCategory() {
			return Category;
		}

		/**
		 * @param category the category to set
		 */
		public void setCategory(String category) {
			Category = category;
		}

		/**
		 * @return the companyID
		 */
		public String getCompanyID() {
			return companyID;
		}

		/**
		 * @param companyID the companyID to set
		 */
		public void setCompanyID(String companyID) {
			this.companyID = companyID;
		}

		/**
		 * @return the country
		 */
		public String getCountry() {
			return Country;
		}

		/**
		 * @param country the country to set
		 */
		public void setCountry(String country) {
			Country = country;
		}

		/**
		 * @return the intNr
		 */
		public String getIntNr() {
			return IntNr;
		}

		/**
		 * @param intNr the intNr to set
		 */
		public void setIntNr(String intNr) {
			IntNr = intNr;
		}

		/**
		 * @return the isLock
		 */
		public String getIsLock() {
			return isLock;
		}

		/**
		 * @param isLock the isLock to set
		 */
		public void setIsLock(String isLock) {
			this.isLock = isLock;
		}

		/**
		 * @return the location
		 */
		public String getLocation() {
			return Location;
		}

		/**
		 * @param location the location to set
		 */
		public void setLocation(String location) {
			Location = location;
		}

		/**
		 * @return the qualification
		 */
		public String getQualification() {
			return Qualification;
		}

		/**
		 * @param qualification the qualification to set
		 */
		public void setQualification(String qualification) {
			Qualification = qualification;
		}

		/**
		 * @return the street
		 */
		public String getStreet() {
			return Street;
		}

		/**
		 * @param street the street to set
		 */
		public void setStreet(String street) {
			Street = street;
		}

		/**
		 * @return the zip
		 */
		public String getZip() {
			return Zip;
		}

		/**
		 * @param zip the zip to set
		 */
		public void setZip(String zip) {
			Zip = zip;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * @param zip the zip to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		public String getCooperation() {
			return Cooperation;
		}

		public void setCooperation(String cooperation) {
			Cooperation = cooperation;
		}

	}

