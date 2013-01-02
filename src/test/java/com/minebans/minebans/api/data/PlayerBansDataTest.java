package com.minebans.minebans.api.data;

import org.junit.Assert;
import org.junit.Test;

import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanSeverity;

public class PlayerBansDataTest {
	
	@Test
	public void testResponseProcessing(){
		/*	PHP script to produce the test JSON
			
			<?php
			
			$data = array(
				'status'		=> true,
				'player_info'	=> array(
					'total_bans'	=> array(
						'0'	=> array(0 => 1, 1 => 6, 2 => 11, 3 => 16),
						'1'	=> array(0 => 2, 1 => 7, 2 => 12, 3 => 17),
						'2'	=> array(0 => 3, 1 => 8, 2 => 13, 3 => 18),
						'3'	=> array(0 => 4, 1 => 9, 2 => 14, 3 => 19),
						'4'	=> array(0 => 5, 1 => 10, 2 => 15, 3 => 20),
					),
					'ban_summary'	=> array(
						'total'			=> 100,
						'last_24'		=> 10,
						'removed'		=> 40,
						'group_bans'	=> 10,
					),
				),
			);
			
			echo json_encode($data, JSON_FORCE_OBJECT);
			
			?>
		 */
		
		PlayerBansData data = PlayerBansData.fromString("{\"status\":true,\"player_info\":{\"total_bans\":{\"0\":{\"0\":1,\"1\":6,\"2\":11,\"3\":16},\"1\":{\"0\":2,\"1\":7,\"2\":12,\"3\":17},\"2\":{\"0\":3,\"1\":8,\"2\":13,\"3\":18},\"3\":{\"0\":4,\"1\":9,\"2\":14,\"3\":19},\"4\":{\"0\":5,\"1\":10,\"2\":15,\"3\":20}},\"ban_summary\":{\"total\":100,\"last_24\":10,\"removed\":40,\"group_bans\":10}}}");
		
		Assert.assertTrue(data.get(BanReason.THEFT, BanSeverity.LOW) == 6);
		Assert.assertTrue(data.get(BanReason.THEFT, BanSeverity.MEDIUM) == 11);
		Assert.assertTrue(data.get(BanReason.THEFT, BanSeverity.HIGH) == 16);
		Assert.assertTrue(data.get(BanReason.THEFT, BanSeverity.CONFIRMED) == 33);
		Assert.assertTrue(data.get(BanReason.THEFT, BanSeverity.UNCONFIRMED) == 1);
		
		Assert.assertTrue(data.get(BanReason.GRIEF, BanSeverity.LOW) == 7);
		Assert.assertTrue(data.get(BanReason.GRIEF, BanSeverity.MEDIUM) == 12);
		Assert.assertTrue(data.get(BanReason.GRIEF, BanSeverity.HIGH) == 17);
		Assert.assertTrue(data.get(BanReason.GRIEF, BanSeverity.CONFIRMED) == 36);
		Assert.assertTrue(data.get(BanReason.GRIEF, BanSeverity.UNCONFIRMED) == 2);
		
		Assert.assertTrue(data.get(BanReason.ABUSE, BanSeverity.LOW) == 8);
		Assert.assertTrue(data.get(BanReason.ABUSE, BanSeverity.MEDIUM) == 13);
		Assert.assertTrue(data.get(BanReason.ABUSE, BanSeverity.HIGH) == 18);
		Assert.assertTrue(data.get(BanReason.ABUSE, BanSeverity.CONFIRMED) == 39);
		Assert.assertTrue(data.get(BanReason.ABUSE, BanSeverity.UNCONFIRMED) == 3);
		
		Assert.assertTrue(data.get(BanReason.ADVERTISING, BanSeverity.LOW) == 9);
		Assert.assertTrue(data.get(BanReason.ADVERTISING, BanSeverity.MEDIUM) == 14);
		Assert.assertTrue(data.get(BanReason.ADVERTISING, BanSeverity.HIGH) == 19);
		Assert.assertTrue(data.get(BanReason.ADVERTISING, BanSeverity.CONFIRMED) == 42);
		Assert.assertTrue(data.get(BanReason.ADVERTISING, BanSeverity.UNCONFIRMED) == 4);
		
		Assert.assertTrue(data.get(BanReason.XRAY, BanSeverity.LOW) == 10);
		Assert.assertTrue(data.get(BanReason.XRAY, BanSeverity.MEDIUM) == 15);
		Assert.assertTrue(data.get(BanReason.XRAY, BanSeverity.HIGH) == 20);
		Assert.assertTrue(data.get(BanReason.XRAY, BanSeverity.CONFIRMED) == 45);
		Assert.assertTrue(data.get(BanReason.XRAY, BanSeverity.UNCONFIRMED) == 5);
		
		for (int id = 5; id <= 12; ++id){
			Assert.assertTrue(data.get(BanReason.getFromID(id), BanSeverity.LOW) == 0);
			Assert.assertTrue(data.get(BanReason.getFromID(id), BanSeverity.MEDIUM) == 0);
			Assert.assertTrue(data.get(BanReason.getFromID(id), BanSeverity.HIGH) == 0);
			Assert.assertTrue(data.get(BanReason.getFromID(id), BanSeverity.CONFIRMED) == 0);
			Assert.assertTrue(data.get(BanReason.getFromID(id), BanSeverity.UNCONFIRMED) == 0);
		}
		
		Assert.assertTrue(data.getTotal() == 100);
		Assert.assertTrue(data.getLast24() == 10);
		Assert.assertTrue(data.getRemoved() == 40);
		Assert.assertTrue(data.getTotalGroupBans() == 10);
	}
	
}
