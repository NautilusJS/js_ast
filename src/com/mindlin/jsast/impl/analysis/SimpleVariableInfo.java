package com.mindlin.jsast.impl.analysis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindlin.jsast.type.LiteralType;
import com.mindlin.jsast.type.Type;
import com.mindlin.jsast.type.TypeMember;

public class SimpleVariableInfo extends AbstractVariableInfo {
	protected final VariableInfo parent;
	protected final DynamicContext context;
	protected Type currentType;
	
	protected Map<String, VariableInfo> propertyCache;
	
	public SimpleVariableInfo(DynamicContext context, VariableInfo parent, long id, boolean assignable, Type restrictionType) {
		super(id, assignable, restrictionType);
		this.parent = parent;
		this.context = context;
	}
	
	@Override
	public boolean isMetaVar() {
		return false;
	}
	
	@Override
	public List<Object> getReads() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Object> getWrites() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public VariableInfo getProperty(String name) {
		VariableInfo prop = this.propertyCache.get(name);
		if (prop != null)
			return prop;
		
		//TODO: make into computeIfAbsent call
		
		TypeMember apparentMember = TypeCalculator.apparentMember(this.context, this.restrictionType, LiteralType.of(name));
		
		if (apparentMember == null)
			return null;
		
		//TODO: fix for optional members
		prop = new SimpleVariableInfo(this.context, this, -1, !apparentMember.isReadonly(), apparentMember.getType());
		this.propertyCache.put(name, prop);
		
		return prop;
	}
	
	@Override
	public Set<VariableInfo> getControlEdges() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
