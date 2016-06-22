package util;

import java.util.List;

public class Util {
	public static int selectIndexWeighted(List<? extends Weighted> list){
		double totalWeight=0;
		for(Weighted e: list){
			totalWeight+=e.getWeight();
		}
		double selected=Math.random()*totalWeight;
		int i=0;
		for(Weighted e: list){
			selected -= e.getWeight();
			if(selected<0)
				return i;
			i++;
		}
		return 0;
	}
}
