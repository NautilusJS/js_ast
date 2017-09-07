package com.mindlin.jsast.transform;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.mindlin.jsast.tree.Tree;

public class AsyncTransformer {
	Queue<Tree> queue = new LinkedBlockingQueue<>();
	//MultiValuedMap<? extends Tree, TreeReplacement> observers = new MultiValuedMap<>();
	
	interface TreeReplacement {
		Tree base();
		List<? extends Tree> from();
		Tree apply(List<? extends Tree> values);
	}
	
	public void replace(Tree original, Tree replacement) {
		
	}
	
	public void advance(Tree current) {
		
	}
}
