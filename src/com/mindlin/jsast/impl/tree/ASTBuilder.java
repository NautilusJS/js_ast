package com.mindlin.jsast.impl.tree;

public class ASTBuilder {
        public static final short MAJOR_VERSION = 51;//Java 7
        public static final short MINOR_VERSION = 0;
        public <T extends Tree> Class<? extends Tree> implement(Class<T> clazz, Tree.Kind kind) {
                ClassPool cp = ClassPool.getDefault();
                cp.insertClassPath(new ClassClassPath(clazz));
                cp.insertClassPath(new ClassClassPath(AbstractTree.class));
                CtClass ifaceClass = cp.get(clazz.getName());
                CtClass implClass = cp.makeClass(mapName(clazz));
                //Override the getKind method
                if (kind == null)
                        kind = lookupKindFor(clazz);
                
                //Get methods to implement
                List<Method> methods = getMethodsToImplement(clazz);
                for (Method method : methods) {
                        Class<?> returnType = method.getReturnType();
                        cp.insertClassPath(new ClassClassPath(returnType));
                        CtClass fieldType = cp.get(returnType.getName());
                        CtField field = new CtField(fieldType, mapGetterToField(method), implClass);
                        field.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
                        implClass.addField(field);
                        CtMethod getterMethod = new CtMethod(fieldType, method.getName(), new CtClass[0], implClass);
                        getterMethod.setModifiers(Modifier.FINAL | Modifier.PUBLIC);
                        implClass.addMethod(getterMethod);
                }
        }
        protected Tree.Kind lookupKindFor(Class<? extends Tree> clazz) {
                for (Tree.Kind kind : Tree.Kind.values())
                        if (kind.asInterface().equals(clazz))
                                return kind;
                return null;
        }
        protected Strign mapGetterToField(Method getter) {
                String getterName = getter.getName();
                if (!getterName.startsWith("get"))
                        throw new IllegalArgumentException("Method " + getter + " is not a getter");
                return getterName.substring(3,1).toLowerCase() + getterName.substring(4);
        }
        protected List<Method> findGetters(Class<? extends Tree> clazz) {
                List<Method> methods = Arrays.asList(clazz.getMethods());
                //remove getStart and getEnd
                methods.removeIf(method->{
                        switch(method.getName()) {
                                case "getStart":
                                case "getEnd":
                                case "accept":
                                        return true;
                        }
                        return false;
                });
        }
        protected String mapName(Class<? extends Tree> clazz) {
                return new StringBuffer()
                        .append("com.mindlin.jsast.impl.tree.")
                        .append(clazz.getSimpleName())
                        .append("Impl")
                        .toString();
        }
        protected class ASTBuilderClassLoader extends ClassLoader {
                
        }
}