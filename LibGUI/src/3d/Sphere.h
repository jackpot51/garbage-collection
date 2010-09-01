drawSphere(double x, double y, double z, double r, int color, Camera cam){
    Point p = convertPointCamera(x, y, z, cam);
    if(p.x < 0 | p.y < 0 | p.x > screen.x | p.y > screen.x) return;
    if(wireframe) drawcircle(p.x, p.y, r*p.zs, color);
    else fillcircle(p.x, p.y, r*p.zs, color);
}
