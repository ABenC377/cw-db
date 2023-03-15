# cw-db
A database server for a coursework in my "OOP with Java" module.  This database can be queried with a language that is 
a condensed version of SQL (full gramm in grammar.txt)

### Interpretter
The first part of this is to set up an interpreter for the language. I am implementing this with a recursive descent 
parser, which outputs an abstract syntax tree.  When preparing the abstract syntax tree, a recursvie set of "tryX()" 
methods are called.  Each of these prepares a holder tree node before calling the next tryX() method.  If the tryX() 
method fails, then the temporary node is deleted and a boolean value of false is returned.  
This abstract syntax tree is then provided as input to the interpretter

### Error detection
every time you get a false from the recursive-descent-parser, add an error and the index of the failure - at the end the failure with the
greatest index will be the correct error

### Database server
The server itself stores a permanent copy of the information in the database in tab-separated files in the local files
system.  When a query is provided to the server, it reads the tab-separated files to make a temporary data-structure 
reflecting the information in the database.  The query then updates this temporary data-structure.  When the query is
complete, then it resaves the information in the temporary datastructure into tab-separated files locally.  This ensures 
that the information persists even if the server is shut down etc.