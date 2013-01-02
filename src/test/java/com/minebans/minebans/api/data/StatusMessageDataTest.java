package com.minebans.minebans.api.data;

import org.junit.Assert;
import org.junit.Test;

public class StatusMessageDataTest {
	
	@Test
	public void restResponseProcessing(){
		StatusMessageData data = new StatusMessageData("test");
		
		Assert.assertTrue(data.getMessage().equals("test"));
	}
	
}
