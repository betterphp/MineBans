package com.minebans.util;

import java.util.Collection;
import java.util.List;

public class ListUtils {
	
	public static Long sumLongs(Collection<Long> numbers){
		Long sum = 0L;
		
		for (Long value : numbers){
			sum += value;
		}
		
		return sum;
	}
	
	public static Long sumLongs(List<Long> numbers){
		Long sum = 0L;
		
		for (Long value : numbers){
			sum += value;
		}
		
		return sum;
	}
	
	public static Integer sumIntegers(Collection<Integer> numbers){
		Integer sum = 0;
		
		for (Integer value : numbers){
			sum += value;
		}
		
		return sum;
	}
	
	
	public static Integer sumIntegers(List<Integer> numbers){
		Integer sum = 0;
		
		for (Integer value : numbers){
			sum += value;
		}
		
		return sum;
	}
	
	public static String implode(String sep, List<?> values){
		StringBuilder builder = new StringBuilder();
		
		if (values.size() == 0){
			return "";
		}
		
		builder.append(values.get(0).toString());
		
		for (int i = 1; i < values.size(); ++i){
			builder.append(sep);
			builder.append(values.get(i).toString());
		}
		
		return builder.toString();
	}
	
}
