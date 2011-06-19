boat.Message("Autoship started.");
boat.setSpeed(1);
var vec = boat.getCaptain().getLocation().getDirection();
var running = true;
while(running){
	while(true){
		var times = boat.getCaptain().getWorld().getTime();
		boat.Sleep(5000)
		var timee = boat.getCaptain().getWorld().getTime();
		if((times < 12000 && timee > 12000) || (times > 12000 && timee < 12000)){
			break;
		}
	}
	boat.Message("Autoship moving.");
	while(true){
		if(!boat.Move(vec)){
			vec = vec.multiply(-1);
			break;
		}
		if(!boat.Sleep(500)){
			running = false;
			break;
		}
	}
}
boat.Message("Autoship stopped.");