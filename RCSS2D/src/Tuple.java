//change
import java.util.Iterator;
import java.util.List;

/**
 * Taken from integrated AI and modified briefly as basic storage data structure
 */
public class Tuple {
    String label; // The “label” from (label, params)
    String sParams[]; // The “parameters” from (label,params)
    public double iParams[]; // Int parameters from (label, params)

    public Tuple(String label,String[] sParams,double[] iParams){
        this.label=label;
        this.sParams=sParams;
        this.iParams=iParams;

    }
    public void setLabel(String label){
        this.label=label;
    }
    public void setSParams(String[] sParams){
        this.sParams=sParams;
    }
    public void setIParams(double[] IParams){
        this.iParams=IParams;
    }
    //public abstract void setTuple();
    public String getLabel(){
        return this.label;
    }
    public String[] getSParams(){
        return this.sParams;
    }
    public double[] getIParams(){
        return this.iParams;
    }

    public String toString() {
    	String out = label;
    	if (iParams.length != sParams.length) {
    		System.out.println("not needed");
    	} else {
    		for (int i = 0; i < iParams.length; i++) {
				out += sParams[i];
				out += ": ";
				out += iParams[i];
				out += "|";
			}
    	}
    	
    	return out;
    }
    

    public static <T> T[] toArray(List<T> list){
        Iterator<T> iterator = list.iterator();
        int len = list.size();
        System.out.println(len);
        T[] arr = (T[])new Object[len];
        for (int i =0; i < list.size(); i++){
            arr[i] = list.get(i);
        }
        return arr;

    }





}