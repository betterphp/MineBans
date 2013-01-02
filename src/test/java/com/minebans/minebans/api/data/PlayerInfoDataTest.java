package com.minebans.minebans.api.data;

import org.junit.Assert;
import org.junit.Test;

public class PlayerInfoDataTest {
	
	@Test
	public void testResponseProcessing(){
		/*	PHP script to produce the test JSON
			
			<?php
			
			$data = array(
				'status'		=> true,
				'player_info'	=> array(
					'known_compromised'	=> true,
					'should_unban'		=> true,
				),
			);
			
			echo json_encode($data, JSON_FORCE_OBJECT);
			
			?>
		 */
		
		PlayerInfoData data = PlayerInfoData.fromString("{\"status\":true,\"player_info\":{\"known_compromised\":true,\"should_unban\":true}}");
		
		Assert.assertTrue(data.isKnownCompromised());
		Assert.assertTrue(data.shouldUnban());
		
		data = PlayerInfoData.fromString("{\"status\":true,\"player_info\":{\"known_compromised\":false,\"should_unban\":false}}");
		
		Assert.assertFalse(data.isKnownCompromised());
		Assert.assertFalse(data.shouldUnban());
	}
	
}
