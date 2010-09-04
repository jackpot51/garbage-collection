typedef struct {
	FPN m;
	Vector p;
	Vector v;
	FPN r;
	int c;
	Vector pix;
	int zbuf;
} object;

enum {
	ObjectSphere = 0,
} ObjectTypes;