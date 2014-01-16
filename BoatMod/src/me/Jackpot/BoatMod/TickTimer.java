package me.Jackpot.BoatMod;

public class TickTimer {
	long time_start;
	long time_end;
	
	public void start(){
		this.time_start = System.nanoTime();
	}
	
	public void stop(){
		this.time_end = System.nanoTime();
	}
	
	public long nanoseconds(){
		return this.time_end - this.time_start;
	}
	
	public double microseconds(){
		return (this.time_end - this.time_start)/1000.0;
	}
	
	public double milliseconds(){
		return (this.time_end - this.time_start)/1000000.0;
	}

	public double seconds(){
		return (this.time_end - this.time_start)/1000000000.0;
	}
}
