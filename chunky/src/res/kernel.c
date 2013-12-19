//#pragma OPENCL EXTENSION cl_khr_fp64 : enable

#define PI (3.1415926535897932384626433832795f)
#define EPSILON (.000001f)
#define RAY_OFFSET (.0005f)
#define absf(f) (f > 0 ? f : -f)
#define OCTREE (0)
#define RAY_DEPTH (3)

#define SUN_INTENSITY (3)
#define AMBIENT_INTENSITY (.4f)

#define SUN_POLAR_ANGLE (PI / 2.5f)
#define SUN_THETA (PI / 3)
#define RADIUS_COS (cos(.03f))

#define WATER_ID (0x08)

float3 cross_fp3(float3 a, float3 b)
{
	return (float3) (
			a.y * b.z - a.z * b.y,
			a.z * b.x - a.x * b.z,
			a.x * b.y - a.y * b.x);
}

/**
 * Ray-octree intersection test
 */
uint intersect(__global int* octree, uint depth,
	float3* d, float3* o, float3* n)
{
	
	int node;
	int level;
	int type = -1;
	
	int3 x;
	int3 l;
	float tNear = INFINITY;
	float t;
	
	float3	rd = (float3) (1.f / (*d).x, 1.f / (*d).y, 1.f / (*d).z);
	
	while (1) {
		
		// add small offset past the intersection to avoid
		// recursion to the same octree node!
		x = convert_int3(floor((*o) + (*d) * RAY_OFFSET));
		
		node = 0;
		level = depth;
		l = x >> level;
		
		if (l.x != 0 || l.y != 0 || l.z != 0) {
			// outside octree!
			return 0;
		}
		
		type = octree[node];
		while (type == -1) {
			level -= 1;
			l = x >> level;
			node = octree[node + 1 + (((l.x&1)<<2) | ((l.y&1)<<1) | (l.z&1))];
			type = octree[node];
		}

		if (type != 0) {
			// hit a non-air node
			return type;
		}

		// exit current octree node:

		t = ((l.x<<level) - (*o).x) * rd.x;
		if (t > EPSILON) {
			tNear = t;
			(*n) = (float3) (1, 0, 0);
		} else {
			t += (1<<level) * rd.x;
			if (t < tNear && t > EPSILON) {
				tNear = t;
				(*n) = (float3) (-1, 0, 0);
			}
		}

		t = ((l.y<<level) - (*o).y) * rd.y;
		if (t < tNear && t > EPSILON) {
			tNear = t;
			(*n) = (float3) (0, 1, 0);
		} else {
			t += (1<<level) * rd.y;
			if (t < tNear && t > EPSILON) {
				tNear = t;
				(*n) = (float3) (0, -1, 0);
			}
		}

		t = ((l.z<<level) - (*o).z) * rd.z;
		if (t < tNear && t > EPSILON) {
			tNear = t;
			(*n) = (float3) (0, 0, 1);
		} else {
			t += (1<<level) * rd.z;
			if (t < tNear && t > EPSILON) {
				tNear = t;
				(*n) = (float3) (0, 0, -1);
			}
		}

		(*o) = (*d) * tNear + (*o);
		tNear = INFINITY;
	}
}

/**
 * The MWC64X function is Copyright (c) 2011, David Thomas
 * See MWC64X.txt for the license terms affecting this function.
 *
 * See also
 * http://cas.ee.ic.ac.uk/people/dt10/research/rngs-gpu-mwc64x.html
 */
uint MWC64X(uint2 *state)
{
    enum { A=4294883355U};
    uint x=(*state).x, c=(*state).y;
    uint res=x^c;
    uint hi=mul_hi(x,A);
    x=x*A+c;
    c=hi+(x<c);
    *state=(uint2)(x,c);
    return res;
}

float rand_float(uint2* state)
{
	return MWC64X(state) / (float)(0xFFFFFFFF);
}

void reflect(float3* o, float3* d, float3* n)
{
	float x = dot(*d, *n);
	(*d) = (*d) - 2 * x * (*n);
	(*o) = mad(RAY_OFFSET, *d, *o);
}

float3 get_sun_direction(float3* su, float3* sv, float3* sw, uint2* state)
{
	float x1 = rand_float(state);
	float x2 = rand_float(state);
	float cos_a = 1-x1 + x1*RADIUS_COS;
	float sin_a = sqrt(1 - cos_a*cos_a);
	float phi = 2 * PI * x2;

	float3 u = *su;
	float3 v = *sv;
	float3 w = *sw;

	u *= cos(phi) * sin_a;
	v *= sin(phi) * sin_a;
	w *= cos_a;

	return normalize(u + v + w);
}

void reflect_diffuse(float3* o, float3* d, float3* n, uint2* state)
{
	// random point on unit disk
	float x1 = rand_float(state);
	float x2 = rand_float(state);
	float r = sqrt(x1);
	float theta = 2 * PI * x2;

	// project point on hemisphere in tangent space
	float3 t = (float3) (r * cos(theta), r * sin(theta), sqrt(1 - x1));
	
	// transform from tangent space to world space
	float3 x;
	float3 u;
	float3 v;
	
	if (fabs((*n).x) > .1f) {
		x = (float3) (0, 1, 0);
	} else {
		x = (float3) (1, 0, 0);
	}
	
	u = normalize(cross_fp3(x, *n));
	v = cross_fp3(u, *n);
	
	(*d) = u * t.x + v * t.y + (*n) * t.z;

	(*o) = mad(RAY_OFFSET, *d, *o);
}

uint sample_to_rgb(float* c)
{
	c[0] = max(1.f, c[0]);
	c[1] = max(1.f, c[1]);
	c[2] = max(1.f, c[2]);
	return (uint) (0xFF<<24) |
		((0xFF*(uint)c[0])<<16) |
		((0xFF*(uint)c[1])<<8) |
		(0xFF*(uint)c[2]);
}

/**
 * Russian Roulette kill function
 */
bool kill(uint ray_depth, uint2* state)
{
	return (ray_depth >= RAY_DEPTH) && (MWC64X(state) % 2);
}

__kernel void path_trace(
		__global float* output,
		__global float3* origin,
		__global int* octree,
		uint depth,
		__global float* transform,
		float fov,
		__global uint2* seeds,
		uint num_samples,
		__global float* block_color)
{

	uint ix = get_global_id(0);
	uint iy = get_global_id(1);
	uint width = get_global_size(0);
	uint height = get_global_size(1);
	uint index = ix + iy * width;
	uint2 seed = seeds[index];
	
	float3 d;
	float3 o;
	float3 n;

	float3 t0 = (float3) (transform[0], transform[1], transform[2]);
	float3 t1 = (float3) (transform[3], transform[4], transform[5]);
	float3 t2 = (float3) (transform[6], transform[7], transform[8]);
	
	float fov_tan = tanpi(fov / 360.f);

	// set sun vector
	float3 su, sv, sw;
	float theta = SUN_POLAR_ANGLE;
	float phi = SUN_THETA;

	sw.x = cos(theta);
	sw.y = sin(phi);
	sw.z = sin(theta);

	float r = sqrt(sw.x*sw.x + sw.z*sw.z);
	r = fabs(cos(phi) / r);

	sw.x *= r;
	sw.z *= r;

	if (fabs(sw.x) > .1f)
		su = (float3) (0, 1, 0);
	else
		su = (float3) (1, 0, 0);

	sv = normalize(cross_fp3(sw, su));
	su = cross_fp3(sv, sw);

	
	float sinv = 1.f / (num_samples + 1);

#ifdef RNGTEST
	output[index*3] = rand_float(&seed);
	output[index*3+1] = rand_float(&seed);
	output[index*3+2] = rand_float(&seed);
#else
	float ox = 2 * rand_float(&seed);
	float oy = 2 * rand_float(&seed);
	ox = ox<1 ? sqrt(ox)-1 : 1-sqrt(2-ox);
	oy = oy<1 ? sqrt(oy)-1 : 1-sqrt(2-oy);
	d.x = fov_tan * (-.5f + (iy + oy) / height);
	d.y = -1;
	d.z = fov_tan * (.5f - (ix + ox) / width);

	d = normalize(d);
	
	d = (float3) (dot(d, t0), dot(d, t1), dot(d, t2));
	o = *origin;
	
	bool hit = false;
	uint ray_depth = 0;
	float3 light = 0;
	float3 attenuation = 1;
#ifdef AMBIENT_OCCLUSION
	while (
	
		uint material = intersect(octree, depth, &d, &o, &n);
		
		if (material == 0) break;
	
		uint block = 0xFF & material;
		attenuation.x *= block_color[block*3];
		attenuation.y *= block_color[block*3+1];
		attenuation.z *= block_color[block*3+2];
		
		if (kill(ray_depth, &seed)) {
			hit = false;
			break;
		}

		ray_depth += 1;
		hit = true;

		reflect_diffuse(&o, &d, &n, &seed);
	}
	if (hit) light = attenuation;
#else
	while (1) {
		float3 prev_o = o;
		float3 prev_n = n;

		uint material = intersect(octree, depth, &d, &o, &n);

		float3 rd = get_sun_direction(&su, &sv, &sw, &seed);
		float3 ro = prev_o + rd * RAY_OFFSET;
		float3 rn = prev_n;

		if (!material) {
			if (hit && !intersect(octree, depth, &rd, &ro, &rn)) {
				float direct_light = dot(prev_n, rd);
				if (direct_light > 0) {
					light = (direct_light * SUN_INTENSITY +
							AMBIENT_INTENSITY) * attenuation;
				}
			}
			break;
		}
		
		if (kill(ray_depth, &seed)) {
			hit = false;
			break;
		}

		ray_depth += 1;
		hit = true;

		uint block = 0xFF & material;
		attenuation.x *= block_color[block*3];
		attenuation.y *= block_color[block*3+1];
		attenuation.z *= block_color[block*3+2];

		// find diffuse reflected ray
		reflect_diffuse(&o, &d, &n, &seed);
	}
#endif

	output[index*3] = sinv * (output[index*3] * num_samples + light.x);
	output[index*3+1] = sinv * (output[index*3+1] * num_samples + light.y);
	output[index*3+2] = sinv * (output[index*3+2] * num_samples + light.z);
#endif
	
	seeds[index] = seed;
}
