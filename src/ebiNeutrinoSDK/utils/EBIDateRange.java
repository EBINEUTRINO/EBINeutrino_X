package ebiNeutrinoSDK.utils;

public class EBIDateRange{
  
    private long end;
    private long start;

    public EBIDateRange(long aStartDate, long anEndDate){
       start = aStartDate;
       end   = anEndDate;
    }
    
    public int compareTo(long astart, long aend){ 	
    	if(astart >= start &&  aend <= end){
    		return 1;  
    	}else if(astart <= start &&  aend >= end){
    		return 1;  
    	}else if(astart >= end &&  aend <= start){
    		return -1;
    	}
    	
    	return 0;
    }

}

