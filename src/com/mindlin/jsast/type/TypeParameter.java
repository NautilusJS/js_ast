package com.mindlin.jsast.type;

public class TypeParameter implements TypeVariable {
	
	public static RebindableTypeParameter unbound() {
		return new RebindableTypeParameter(null, null);
	}
	
	public static RebindableTypeParameter unbound(Type constraint, Type defaultValue) {
		return new RebindableTypeParameter(constraint, defaultValue);
	}
	
	protected Type constraint;
	protected Type defaultValue;
	
	public TypeParameter(Type constraint, Type defaultValue) {
		this.constraint = constraint;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Get constraint for parameter
	 */
	@Override
	public Type getConstraint() {
		return this.constraint;
	}
	
	public Type getDefault() {
		return this.defaultValue;
	}
	
	public static class RebindableTypeParameter extends TypeParameter {
		protected RebindableTypeParameter(Type constraint, Type defaultValue) {
			super(constraint, defaultValue);
		}
		
		public void rebind(Type newConstraint) {
			this.constraint = newConstraint;
		}
		
		public void rebind(Type newConstraint, Type newDefault) {
			this.constraint = newConstraint;
			this.defaultValue = newDefault;
		}
	}
}
