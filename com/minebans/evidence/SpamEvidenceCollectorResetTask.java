package com.minebans.evidence;

public class SpamEvidenceCollectorResetTask implements Runnable {
	
	private SpamEvidenceCollector collector;
	
	public SpamEvidenceCollectorResetTask(SpamEvidenceCollector collector){
		this.collector = collector;
	}

	public void run(){
		this.collector.resetCounter();
	}
	
}
