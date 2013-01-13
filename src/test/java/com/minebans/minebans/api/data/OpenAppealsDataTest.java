package com.minebans.minebans.api.data;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.minebans.minebans.api.data.OpenAppealsData.AppealData;
import com.minebans.minebans.bans.BanReason;

public class OpenAppealsDataTest {
	
	@Test
	public void testResponseProcessing(){
		/*	PHP script to produce the test JSON
			
			<?php
			
			$data = array(
				'status'	=> true,
				'disputes'	=> array(
					'0'	=> array('player_name' => 'test_1', 'ban_reason_id' => 1),
					'1'	=> array('player_name' => 'test_2', 'ban_reason_id' => 2),
					'2'	=> array('player_name' => 'test_3', 'ban_reason_id' => 3),
				),
			);
			
			echo json_encode($data, JSON_FORCE_OBJECT);
			
			?>
		*/
		
		OpenAppealsData data = OpenAppealsData.fromString("{\"status\":true,\"disputes\":{\"0\":{\"player_name\":\"test_1\",\"ban_reason_id\":\"1\"},1:{\"player_name\":\"test_2\",\"ban_reason_id\":2},\"2\":{\"player_name\":\"test_3\",\"ban_reason_id\":3}}}");
		
		List<AppealData> appeals = data.getAppeals();
		
		Assert.assertTrue(appeals.size() == 3);
		
		Assert.assertTrue(appeals.get(0).getPlayerName().equals("test_1"));
		Assert.assertTrue(appeals.get(0).getBanReason() == BanReason.GRIEF);
		
		Assert.assertTrue(appeals.get(1).getPlayerName().equals("test_2"));
		Assert.assertTrue(appeals.get(1).getBanReason() == BanReason.ABUSE);
		
		Assert.assertTrue(appeals.get(2).getPlayerName().equals("test_3"));
		Assert.assertTrue(appeals.get(2).getBanReason() == BanReason.ADVERTISING);
	}
	
}
