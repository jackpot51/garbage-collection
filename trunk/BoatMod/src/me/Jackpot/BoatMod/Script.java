package me.Jackpot.BoatMod;


public class Script{
	String _permission;
	String _file;
	public Script(String permission, String file){
		_permission = permission;
		_file = file;
	}
	
	public boolean isValid(){
		return (
				_permission != "" &&
				_file != ""
				);
	}
}
