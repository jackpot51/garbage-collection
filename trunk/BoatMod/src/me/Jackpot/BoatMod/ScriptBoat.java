package me.Jackpot.BoatMod;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ScriptBoat extends Boat implements Runnable{
	Script script;
	public ScriptBoat(Block controlblock, Player setup_captain, Script setup_script, BoatMod instance){
		super(controlblock, setup_captain, instance);
		this.script = setup_script;
	}
	public static boolean Sleep(int millis){
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
	@Override
	public void run() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
		try{
			jsEngine.put("boat", this);
			jsEngine.eval(new FileReader(this.script.file));
		}catch (ScriptException ex) {
			plugin.LogMessage(ex.getMessage());
			Message("The script " + this.script.file + " failed.");
		} catch (FileNotFoundException e) {
			plugin.LogMessage(e.getMessage());
			Message("The script " + this.script.file + " could not be found.");
		}
	}
}
