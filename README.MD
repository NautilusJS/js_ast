# JS AST

This is a Java parser for JavaScript.

## Scope
This parser should be able to process valid JavaScript, possibly up to ES6. This parser
should also be able to handle TypeScript style type annotations.

### Things that this DOES NOT do
 - Automatic semicolon insertion
 - Perfect Unicode support
 - Some non-strict things
 - Identifiers named 'of' (of is reserved by me for for-of loops). However, you can do window['of']
 to access it
 - Dead code analasys

 ### Things that it does not do, but might in the future
  - Run JavaScript
  - Compress JavaScript
  - Format JavaScript