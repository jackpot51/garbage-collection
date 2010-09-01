Vector RotatePoint(Vector p, Vector o){
	Vector ret;
	ret.x = cosl(o.y)*(sinl(o.z)*p.y+cosl(o.z)*p.x)-sinl(o.y)*p.z;
	ret.y = -(sinl(o.x)*(cosl(o.y)*p.z+sinl(o.y)*(sinl(o.z)*p.y+cosl(o.z)*p.x))+cosl(o.x)*(cosl(o.z)*p.y-sinl(o.z)*p.x));
	ret.z = cosl(o.x)*(cosl(o.y)*p.z+sinl(o.y)*(sinl(o.z)*p.y+cosl(o.z)*p.x))-sinl(o.x)*(cosl(o.z)*p.y-sinl(o.z)*p.x);
	return ret;
}

Vector ConvertPoint(Vector p, Vector c, Vector o, screeninfo screen){
	Vector ret = RotatePoint(p, o);
	ret.z = screen.y/(ret.z-c.z);
	ret.x = (ret.x-c.x)*ret.z + screen.x/2.0;
	ret.y = (ret.y-c.y)*ret.z + screen.y/2.0;
	return ret;
}
