//Stolen from http://www-users.mat.uni.torun.pl/~wrona/3d_tutor/tri_fillers.html
void horizline(int x, int x2, int y, int color){
	drawline(x, y, x2, y, color);
}

void filltriangle(Point a, Point b, Point c, int color){
	Point A, B, C;
	//C.y>B.y>A.y
	//Sorting is annoying
	if(c.y > b.y){
	    if(c.y > a.y){
		C = c;
		if(b.y > a.y){
		    B = b;
		    A = a;
		}else{
		    B = a;
		    A = b;
		}
	    }else{
		C = a;
		B = c;
		A = b;
	    }
	}else{
	    if(c.y > a.y){
		C = b;
		B = c;
		A = a;
	    }else{
		A = c;
		if(b.y > a.y){
		    C = b;
		    B = a;
		}else{
		    C = a;
		    B = b;
		}
	    }
	}
	
	double dx1;
	double dx2;
	double dx3;
	if (B.y-A.y > 0) dx1=(B.x-A.x)/(B.y-A.y);
	else dx1=0;
	if (C.y-A.y > 0) dx2=(C.x-A.x)/(C.y-A.y);
	else dx2=0;
	if (C.y-B.y > 0) dx3=(C.x-B.x)/(C.y-B.y);
	else dx3=0;

	Point S=A;
	Point E=A;
	if(dx1 > dx2) {
		for(;S.y<=B.y;S.y++){
			horizline(S.x,E.x,S.y,color);
			E.y++;
			S.x+=dx2;
			E.x+=dx1;
		}
		E=B;
		for(;S.y<=C.y;S.y++){
			horizline(S.x,E.x,S.y,color);
			E.y++;
			S.x+=dx2;
			E.x+=dx3;
		}
	} else {
		for(;S.y<=B.y;S.y++){
			horizline(S.x,E.x,S.y,color);
			E.y++;
			S.x+=dx1;
			E.x+=dx2;
		}
		S=B;
		for(;S.y<=C.y;S.y++){
			horizline(S.x,E.x,S.y,color);
			E.y++;
			S.x+=dx3;
			E.x+=dx2;
		}
	}
}

void drawtriangle(Point A, Point B, Point C, int color){
	drawline(A.x, A.y, B.x, B.y, color);
	drawline(A.x, A.y, C.x, C.y, color);
	drawline(B.x, B.y, C.x, C.y, color);
}

drawTriangle3D(double x, double y, double z,
	    double x2, double y2, double z2,
	    double x3, double y3, double z3,
	    int color, Camera c){
    Point A = convertPointCamera(x, y, z, c);
    	if(A.x < 0 | A.x >= screen.x | A.y < 0 | A.y >= screen.y) return;
    Point B = convertPointCamera(x2, y2, z2, c);
	if(B.x < 0 | B.x >= screen.x | B.y < 0 | B.y >= screen.y) return;
    Point C = convertPointCamera(x3, y3, z3, c);
	if(C.x < 0 | C.x >= screen.x | C.y < 0 | C.y >= screen.y) return;
    
    if(wireframe) drawtriangle(A, B, C, color);
    else filltriangle(A, B, C, color);
}

typedef struct{
    double ax;
    double ay;
    double az;
    double bx;
    double by;
    double bz;
    double cx;
    double cy;
    double cz;
    int color;
} Triangle;

drawTriangles3D(Triangle t[], int length, Camera c){
	int i;
	for(i=0; i<length; i++){
		drawTriangle3D(t[i].ax, t[i].ay, t[i].az,
			       t[i].bx, t[i].by, t[i].bz,
			       t[i].cx, t[i].cy, t[i].cz,
			       t[i].color, c);
	}
}
