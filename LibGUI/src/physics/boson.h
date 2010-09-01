particle Photon(){
	particle ret;
	ret.massl = 0;
	ret.massh = 0;
	ret.charge = 0;
	ret.spin = 1;
	return ret;
}

particle WBoson(){
	particle ret;
	ret.massl = 8.0375E10;
	ret.massh = 8.0421E10;
	ret.charge = -1;
	ret.spin = 1;
	return ret;
}

particle ZBoson(){
	particle ret;
	ret.massl = 9.1855E10;
	ret.massh = 9.1897E10;
	ret.charge = 0;
	ret.spin = 1;
	return ret;
}

particle Gluon(){
	particle ret;
	ret.massl = 0;
	ret.massh = 0;
	ret.charge = 0;
	ret.spin = 1;
	return ret;
}

particle HiggsBoson(){ 
	particle ret;
	ret.massl = 1.15E11;
	ret.massh = 1.85E11;
	ret.charge = 0;
	ret.spin = 0;
	return ret;
}

particle Graviton(){
	particle ret;
	ret.massl = 0;
	ret.massh = 0;
	ret.charge = 0;
	ret.spin = 2;
	return ret;
}
