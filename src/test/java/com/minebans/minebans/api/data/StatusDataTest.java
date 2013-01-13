package com.minebans.minebans.api.data;

import org.junit.Assert;
import org.junit.Test;

public class StatusDataTest {
	
	@Test
	public void testResponseProcessing(){
		/*	PHP script to produce the test JSON
			
			<?php
			
			$data = array(
				'status'	=> true,
				'load_avg'	=> array(
					'0'	=> 0.12,
					'1'	=> 1.45,
					'2'	=> 3.44,
				),
			);
			
			echo json_encode($data, JSON_FORCE_OBJECT);
			
			?>
		 */
		
		StatusData data = StatusData.fromString("{\"status\":true,\"load_avg\":{\"0\":0.12,\"1\":1.45,\"2\":3.44}}");
		
		Double[] loadAvg = data.getLoadAverage();
		
		Assert.assertTrue(loadAvg[0].equals(0.12));
		Assert.assertTrue(loadAvg[1].equals(1.45));
		Assert.assertTrue(loadAvg[2].equals(3.44));
		
		Assert.assertNotNull(data.getResponceTime());
		Assert.assertFalse(data.getResponceTime() == 0L);
	}
	
}
