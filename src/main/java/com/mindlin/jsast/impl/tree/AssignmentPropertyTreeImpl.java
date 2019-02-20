package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.tree.AssignmentPropertyTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.PropertyName;
import com.mindlin.jsast.tree.ShorthandAssignmentPropertyTree;

public class AssignmentPropertyTreeImpl extends AbstractTree implements AssignmentPropertyTree {
	protected final Modifiers modifiers;
	protected final PropertyName name;
	protected final ExpressionTree initializer;
	
	public AssignmentPropertyTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers, PropertyName name, ExpressionTree initializer) {
		super(start, end);
		this.modifiers = modifiers;
		this.name = name;
		this.initializer = initializer;
	}

	@Override
	public Modifiers getModifiers() {
		return this.modifiers;
	}

	@Override
	public PropertyName getName() {
		return this.name;
	}

	@Override
	public ExpressionTree getInitializer() {
		return this.initializer;
	}
	
	public static class ShorthandAssignmentPropertyTreeImpl extends AssignmentPropertyTreeImpl implements ShorthandAssignmentPropertyTree {

		public ShorthandAssignmentPropertyTreeImpl(SourcePosition start, SourcePosition end, Modifiers modifiers,
				PropertyName name, ExpressionTree initializer) {
			super(start, end, modifiers, name, initializer);
		}
		
		@Override
		public IdentifierTree getName() {
			return (IdentifierTree) this.name;
		}
	}
}
