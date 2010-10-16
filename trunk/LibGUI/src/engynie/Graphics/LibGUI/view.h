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
	Drawable **zbuf;
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
	Vector ret = VectorSub(p, c);
	ret = TransformPoint(ret, t);
	ret.z = -(FPN)s.y/ret.z;
	ret.x = ret.x*ret.z + (FPN)s.x/2.0;
	ret.y = ret.y*ret.z + (FPN)s.y/2.0;
	return ret;
}

View makeView(Object objs[], int len, Vector c, Transform t, screeninfo s){
	View ret;
	ret.screen = s;
	ret.cam = c;
	ret.trans = t;
	ret.draws = (Drawable *)malloc(len*sizeof(Drawable));
	ret.zbuf = (Drawable **)malloc(len*sizeof(Drawable *));
	ret.drawslen = 0;
	int i;
	for(i=0; i<len; i++){
		Drawable d;
		d.type = DrawCircle;
		d.pos = ConvertPoint(objs[i].p, c, t, s);
		d.size.x = ceil(objs[i].r*d.pos.z);
		d.color = objs[i].c;
		int j;
		for(j=0; j<i; j++){
			if(d.pos.z < ret.zbuf[j]->pos.z){
				memmove(&ret.zbuf[j+1], &ret.zbuf[j], (i-j)*sizeof(Drawable *));
				ret.zbuf[j] = &ret.draws[ret.drawslen];
				goto swapped;
			}
		}
		ret.zbuf[i] = &ret.draws[ret.drawslen];
	swapped:
		ret.draws[ret.drawslen++] = d;
	}
	return ret;
}

void drawView(View *v){
	int i;
	for(i=0;i<v->drawslen;i++){
		Drawable d = *v->zbuf[i];
		switch(d.type){
			case DrawCircle:
				if(d.pos.x < v->screen.x && d.pos.x >= 0 &&
					d.pos.y < v->screen.y && d.pos.y >= 0 &&
					d.size.x > 0){
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
