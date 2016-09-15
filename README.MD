# JS AST

This is a JavaScript/TypeScript parser written in Java.

## Scope
This parser should be able to process valid JavaScript and TypeScript. It should fully support ES6 features.

### Things that this DOES do
 - [Tokenizing JavaScript/TypeScript](https://github.com/Divisors/js_ast/tree/master/src/com/mindlin/jsast/impl/lexer)

### Things that this WILL DO
 - Parsing JavaScript/TypeScript
 - Desctructuring operations

### Things that this WILL NEVER do
 - Automatic semicolon insertion
 - Perfect Unicode support
 - Some non-strict things
 - Identifiers named certain keywords (e.g., `of` is reserved by me for for-of loops. However, you can do window['of']
 to access it)

 ### Things that it MIGHT do in the future
  - Interpert generated ASTs
  - Compile generated ASTs to Java bytecode
  - Transpile to another dialect of ECMAScript (e.g., TypeScript 1.8 -> ES5)
  - Optimize JS ASTs
  - Format JavaScript prettily
