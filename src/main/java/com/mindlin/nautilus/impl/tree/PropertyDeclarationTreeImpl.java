package com.mindlin.nautilus.impl.tree;

import java.util.List;

import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.tree.DecoratorTree;
import com.mindlin.nautilus.tree.ExpressionTree;
import com.mindlin.nautilus.tree.Modifiers;
import com.mindlin.nautilus.tree.PropertyDeclarationTree;
import com.mindlin.nautilus.tree.PropertyName;
import com.mindlin.nautilus.tree.type.TypeTree;

public class PropertyDeclarationTreeImpl extends AbstractTypedPropertyTree implements PropertyDeclarationTree {
	protected final ExpressionTree initializer;
	
	public PropertyDeclarationTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name, TypeTree type, ExpressionTree initializer) {
		super(start, end, modifiers, name, type);
		this.initializer = initializer;
	}

	@Override
	public List<DecoratorTree> getDecorators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeTree getType() {
		return super.getType();
	}

	@Override
	public ExpressionTree getInitializer() {
		return this.initializer;
	}
	
}
