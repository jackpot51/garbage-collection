typedef struct {
	FPN cx;
	FPN cy;
	FPN sx;
	FPN sy;
} Transform;

typedef struct {
	char type;
	Vector pos;
	Vector size;
	int color;
} Drawable;

enum{
	DrawNone,
	DrawCircle,
};

typedef struct {
	screeninfo screen;
	Vector cam;
	Transform trans;
	Drawable *draws;
	int drawslen;
} View;

Transform GetTransform(Vector o){
	Transform ret;
	ret.cx = M(cos, o.x);
	ret.cy = M(cos, o.y);
	ret.sx = M(sin, o.x);
	ret.sy = M(sin, o.y);
	return ret;
}

Vector TransformPoint(Vector p, Transform t){
	Vector ret;
	ret.x = t.cy*p.x-t.sy*p.z;
	ret.y = -(t.sx*(t.cy*p.z+t.sy*p.x)+t.cx*p.y);
	ret.z = t.cx*(t.cy*p.z+t.sy*p.x)-t.sx*p.y;
	return ret;
}

Vector ConvertPoint(Vector p, Vector c, Transform t, screeninfo s){
	Vector ret = TransformPoint(p, t);
	ret.z = (FPN)s.y/(ret.z-c.z);
	ret.x = (ret.x-c.x)*ret.z + (FPN)s.x/2.0;
	ret.y = (ret.y-c.y)*ret.z + (FPN)s.y/2.0;
	return ret;
}

View makeView(Object objs[], int len, Vector c, Transform t, screeninfo s){
	View ret;
	ret.screen = s;
	ret.cam = c;
	ret.trans = t;
	ret.draws = (Drawable *)malloc(len*sizeof(Drawable));
	ret.drawslen = 0;
	int i;
	for(i=0; i<len; i++){
		Drawable d;
		d.type = DrawCircle;
		d.pos = ConvertPoint(objs[i].p, c, t, s);
		d.size.x = ceil(objs[i].r*d.pos.z);
		d.color = objs[i].c;
		int j = ret.drawslen++;
		//ret.draws = realloc(ret.draws, ret.drawslen*sizeof(Drawable));
		ret.draws[j] = d;
	}
	return ret;
}

void drawView(View *v){
	int i;
	for(i=0;i<v->drawslen;i++){
		Drawable d = v->draws[i];
		switch(d.type){
			case DrawCircle:
				if(d.pos.x < v->screen.x && d.pos.x >= 0 &&
					d.pos.y < v->screen.y && d.pos.y >= 0){
						fillcircle(d.pos.x, d.pos.y, d.size.x, d.color);
				}
				break;
		}
	}
}

void freeView(View *v){
	v->drawslen = 0;
	free(v->draws);
	v->draws = NULL;
}
