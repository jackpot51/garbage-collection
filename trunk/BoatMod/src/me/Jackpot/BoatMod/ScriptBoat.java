package me.Jackpot.BoatMod;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ScriptBoat extends Boat implements Runnable{
	Script _script;
	public ScriptBoat(Block controlblock, Player captain, Script script, BoatMod instance){
		super(controlblock, captain, instance);
		_script = script;
	}
	public boolean Sleep(int millis){
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
			jsEngine.eval(new FileReader(_script._file));
		}catch (ScriptException ex) {
			plugin.LogMessage(ex.getMessage());
			Message("The script " + _script._file + " failed.");
		} catch (FileNotFoundException e) {
			plugin.LogMessage(e.getMessage());
			Message("The script " + _script._file + " could not be found.");
		}
	}
}
