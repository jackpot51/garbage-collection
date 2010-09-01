typedef struct{
    double cx; //location
    double cy;
    double cz;
    
    double ox; //orientation
    double oy;
    double oz;
    
    double ex; //viewer
    double ey;
    double ez;
} Camera;
/*
double cosox = 0;
double sinox = 0;
double cosoy = 0;
double sinoy = 0;
double cosoz = 0;
double sinoz = 0;

updateCamera(Camera c){
    cosox = cos(c.ox);
    sinox = sin(c.ox);
    cosoy = cos(c.oy);
    cosoy = sin(c.oy);
    cosoz = cos(c.oz);
    cosoz = sin(c.oz);
}
*/
