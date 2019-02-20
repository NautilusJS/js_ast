package com.mindlin.jsast.type;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.mindlin.jsast.impl.analysis.TypeCalculator;
import com.mindlin.jsast.impl.tree.IdentifierTreeImpl;

public class FunctionOverloadTest {
	
	public Set<Signature> getSimpleOverrideList() {
		//function f(x: number): number
		ParameterInfo x = new ParameterInfo(null, new IdentifierTreeImpl(null, null, "x"), false, IntrinsicType.NUMBER, null);
		Signature f1 = new SignatureImpl(Collections.emptyList(), Arrays.asList(x), IntrinsicType.NUMBER);
		
		//function f(y: string): string
		ParameterInfo y = new ParameterInfo(null, new IdentifierTreeImpl(null, null, "y"), false, IntrinsicType.STRING, null);
		Signature f2 = new SignatureImpl(Collections.emptyList(), Arrays.asList(y), IntrinsicType.STRING);
		
		ParameterInfo z = new ParameterInfo(null, new IdentifierTreeImpl(null, null, "z"), false, CompositeType.union(IntrinsicType.STRING, IntrinsicType.NUMBER), null);
		Signature f3 = new SignatureImpl(Collections.emptyList(), Arrays.asList(z), IntrinsicType.BOOLEAN);
		
		Set<Signature> f = new HashSet<>();
		f.add(f1);
		f.add(f2);
		f.add(f3);
		
		return f;		
	}
	
	@Test
	public void testTrivialOverload() {
		Set<Signature> f = getSimpleOverrideList();
		
		Set<Signature> restr = TypeCalculator.restrictSignatures(null, f, Collections.emptyList(), Arrays.asList(IntrinsicType.STRING));
		System.out.println(restr);
	}
	
}
