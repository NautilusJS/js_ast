# NautilusJS Core

This is a JavaScript/TypeScript parser written in Java.

I wrote this project to satisfy the problem of parsing JS/TS code in Java (even Nashorn doesn't do this well, or for modern dialects). 

## Scope
This parser should be able to process valid JavaScript and TypeScript. It should fully support ES6 features.

### Things that this DOES do
 - [Tokenizing JavaScript/TypeScript](github.com/NautilusJS/js_ast/tree/master/src/main/java/com/mindlin/nautilus/impl/lexer)
 - [Parsing JavaScript/TypeScript](github.com/NautilusJS/js_ast/tree/master/src/main/java/com/mindlin/nautilus/impl/parser)

### Things that this WILL DO (but not right now)
 - Desctructuring operations
 - Optimize JS ASTs

### Things that it MIGHT do in the future
  - Interpert generated ASTs
  - Compile generated ASTs to Java bytecode
  - Transpile to another dialect of ECMAScript (e.g., TypeScript 1.8 -> ES5)
  - Format JavaScript prettily
 
### Thisngs that this PROBABLY WON'T DO
but could, if people express interest:
 - Automatic semicolon insertion
 - Identifiers named certain keywords (e.g., `of` is reserved by me for for-of loops. However, you can do `window['of']` to access it)

### Things that this WILL NEVER do
(before the next release):
 - Perfect Unicode support
 - Some non-strict things
