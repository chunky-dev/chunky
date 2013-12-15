package se.llbit.chunky.main;

import se.llbit.chunky.world.ChunkPosition;

public class Test {

	public static void main(String[] args) {
		ChunkPosition cp;
		cp = ChunkPosition.get(17179869197L);
		System.out.println(cp.toString());
		cp = ChunkPosition.get(8589934606L);
		System.out.println(cp.toString());
		cp = ChunkPosition.get(8589934605L);
		System.out.println(cp.toString());
		cp = ChunkPosition.get(17179869198L);
		System.out.println(cp.toString());
		cp = ChunkPosition.get(12884901902L);
		System.out.println(cp.toString());
		cp = ChunkPosition.get(12884901901L);
		System.out.println(cp.toString());
	}
}
