Vector RotatePoint(Vector p, Vector o){
	Vector ret;
	ret.x = cos(o.y)*(sin(o.z)*p.y+cos(o.z)*p.x)-sin(o.y)*p.z;
	ret.y = -(sin(o.x)*(cos(o.y)*p.z+sin(o.y)*(sin(o.z)*p.y+cos(o.z)*p.x))+cos(o.x)*(cos(o.z)*p.y-sin(o.z)*p.x));
	ret.z = cos(o.x)*(cos(o.y)*p.z+sin(o.y)*(sin(o.z)*p.y+cos(o.z)*p.x))-sin(o.x)*(cos(o.z)*p.y-sin(o.z)*p.x);
	return ret;
}

Vector ConvertPoint(Vector p, Vector c, Vector o, screeninfo screen){
	Vector ret = RotatePoint(p, o);
	ret.z = screen.y/(ret.z-c.z);
	ret.x = (ret.x-c.x)*ret.z + screen.x/2.0;
	ret.y = (ret.y-c.y)*ret.z + screen.y/2.0;
	return ret;
}