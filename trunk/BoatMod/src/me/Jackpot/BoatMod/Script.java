package me.Jackpot.BoatMod;


public class Script{
	String permission;
	String file;
	public Script(String setup_permission, String setup_file){
		this.permission = setup_permission;
		this.file = setup_file;
	}
	
	public boolean isValid(){
		return (
				this.permission != "" &&
				this.file != ""
				);
	}
}
