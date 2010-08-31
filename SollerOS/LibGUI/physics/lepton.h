particle Electron(){
	particle ret;
	ret.massl = 5.109989E5;
	ret.massh = 5.109989E5;
	ret.charge = -1;
	ret.spin = 1.0/2.0;
	return ret;
}

particle ElectronNeutrino(){
	particle ret;
	ret.massl = 0.2;
	ret.massh = 2.0;
	ret.charge = 0;
	ret.spin = 1.0/2.0;
	return ret;
}

particle Muon(){
	particle ret;
	ret.massl = 1.0565837E8;
	ret.massh = 1.0565837E8;
	ret.charge = -1;
	ret.spin = 1.0/2.0;
	return ret;
}


particle MuonNeutrino(){
	particle ret;
	ret.massl = 0;
	ret.massh = 2.2;
	ret.charge = 0;
	ret.spin = 1.0/2.0;
	return ret;
}

particle Tau(){
	particle ret;
	ret.massl = 1.77667E3;
	ret.massh = 1.77701E3;
	ret.charge = -1;
	ret.spin = 1.0/2.0;
	return ret;
}


particle TauNeutrino(){
	particle ret;
	ret.massl = 0;
	ret.massh = 15.5;
	ret.charge = 0;
	ret.spin = 1.0/2.0;
	return ret;
}
