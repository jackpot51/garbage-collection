particle UpQuark(){
	particle ret;
	ret.massl = 1.7E6;
	ret.massh = 3.3E6;
	ret.charge = 2.0/3.0;
	ret.spin = 1.0/2.0;
	ret.wilh = 1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = 4.0/3.0;
	return ret;
}


particle DownQuark(){
	particle ret;
	ret.massl = 4.1E6;
	ret.massh = 5.8E6;
	ret.charge = -1.0/3.0;
	ret.spin = 1.0/2.0;
	ret.wilh = -1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = -2.0/3.0;
	return ret;
}


particle CharmQuark(){
	particle ret;
	ret.massl = 1.18E9;
	ret.massh = 1.34E9;
	ret.charge = 2.0/3.0;
	ret.spin = 1.0/2.0;
	
	ret.wilh = 1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = 4.0/3.0;
	return ret;
}

particle StrangeQuark(){
	particle ret;
	ret.massl = 8.0E7;
	ret.massh = 1.3E8;
	ret.charge = -1.0/3.0;
	ret.spin = 1.0/2.0;
	
	ret.wilh = -1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = -2.0/3.0;
	return ret;
}

particle TopQuark(){
	particle ret;
	ret.massl = 1.698E11;
	ret.massh = 1.742E11;
	ret.charge = 2.0/3.0;
	ret.spin = 1.0/2.0;
	
	ret.wilh = 1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = 4.0/3.0;
	return ret;
}

particle BottomQuark(){
	particle ret;
	ret.massl = 4.13E9;
	ret.massh = 4.37E9;
	ret.charge = -1.0/3.0;
	ret.spin = 1.0/2.0;
	
	ret.wilh = -1.0/2.0;
	ret.wirh = 0;
	ret.whlh = 1.0/3.0;
	ret.whrh = -2.0/3.0;
	return ret;
} 
