Vector RotatePoint(Vector p, Vector o){
	Vector ret;
	
	ret.x = COS(o.y)*(SIN(o.z)*p.y+COS(o.z)*p.x)-SIN(o.y)*p.z;
	ret.y = -(SIN(o.x)*(COS(o.y)*p.z+SIN(o.y)*(SIN(o.z)*p.y+COS(o.z)*p.x))+COS(o.x)*(COS(o.z)*p.y-SIN(o.z)*p.x));
	ret.z = COS(o.x)*(COS(o.y)*p.z+SIN(o.y)*(SIN(o.z)*p.y+COS(o.z)*p.x))-SIN(o.x)*(COS(o.z)*p.y-SIN(o.z)*p.x);

	return ret;
}

Vector ConvertPoint(Vector p, Vector c, Vector o, screeninfo screen){
	Vector ret = RotatePoint(p, o);
	ret.z = screen.y/(ret.z-c.z);
	ret.x = (ret.x-c.x)*ret.z + screen.x/2.0;
	ret.y = (ret.y-c.y)*ret.z + screen.y/2.0;
	return ret;
}
