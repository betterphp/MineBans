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
	
}
