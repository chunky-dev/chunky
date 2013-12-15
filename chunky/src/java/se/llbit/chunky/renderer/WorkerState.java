package se.llbit.chunky.renderer;

import java.util.Random;

import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;
import se.llbit.math.Vector4d;
import se.llbit.util.VectorPool;

public class WorkerState {
	public VectorPool vectorPool;
	public RayPool rayPool;
	public Ray ray;
	public Vector4d attenuation = new Vector4d();
	public Random random;
}
