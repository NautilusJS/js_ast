package com.mindlin.nautilus.transform;

import java.util.ArrayList;
import java.util.List;

import com.mindlin.nautilus.impl.tree.ObjectLiteralTreeImpl;
import com.mindlin.nautilus.tree.ComputedPropertyKeyTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.ObjectLiteralPropertyTree;
import com.mindlin.nautilus.tree.ObjectLiteralTree;
import com.mindlin.nautilus.tree.ObjectPropertyKeyTree;
import com.mindlin.nautilus.tree.Tree.Kind;

public class ObjectInitializerTf implements TreeTransformation<Void> {

	@Override
	public ExpressionTree visitObjectLiteral(ObjectLiteralTree node, Void d) {
		boolean modified = false;
		List<ObjectLiteralPropertyTree> props = new ArrayList<>();
		for (ObjectLiteralPropertyTree property : node.getProperties()) {
			ObjectPropertyKeyTree key = property.getKey();
			ExpressionTree value = property.getInitializer();
			if (key == value)
				continue;
			if (key.isComputed() && key.getKind() == Kind.OBJECT_LITERAL_PROPERTY) {
				ComputedPropertyKeyTree cp = (ComputedPropertyKeyTree) key;
				cp.getExpression();
			}
			
			props.add(property);
		}
		if (!modified)
			return node;
		return new ObjectLiteralTreeImpl(node.getStart(), node.getEnd(), props);
	}
	
	
}
