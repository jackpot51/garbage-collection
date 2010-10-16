#define PRECISION 64

#if PRECISION == 32
#warning "Floats are inaccurate!"
#define FPN float
#define M(function, args...) function ## l(args)
#endif

#if PRECISION == 64
#define FPN double
#define M(function, args...) function ## l(args)
#endif

#if PRECISION == 128
#define FPN long double
#define M(function, args...) function ## l(args)
#endif