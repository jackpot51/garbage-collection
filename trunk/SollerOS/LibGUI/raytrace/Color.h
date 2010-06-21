typedef struct{
	double r;
	double g;
	double b;
} Color;

int ColorIntR(Color c){
	int ir = (int)(255*c.r);
	ir = ir > 255 ? 255 : ir;
	return ir < 0 ? 0 : ir;
}

int ColorIntG(Color c){
	int ir = (int)(255*c.g);
	ir = ir > 255 ? 255 : ir;
	return ir < 0 ? 0 : ir;
}

int ColorIntB(Color c){
	int ir = (int)(255*c.b);
	ir = ir > 255 ? 255 : ir;
	return ir < 0 ? 0 : ir;
}

int ColorTo24(Color c){
	int cr = ColorIntB(c);
	cr += ColorIntG(c)<<8;
	cr += ColorIntR(c)<<16;
	return cr;
}
