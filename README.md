# ndl Github Repository

**BIG BOLD WARNING: THIS LANGUAGE IS NOT YET FUNCTIONAL OR USEFUL**

**PLEASE WAIT FOR ME TO DEVELOP IT FAR ENOUGH THAT IT CAN ACTUALLY BE USED**

# Misison Statement

ndl is a small scripting language which I plan to work on intermitently. It's an experiment of mine to see how expressive I can make the syntax while not making abritrary constraints. Library vendors would in theory be able to overload any part of the syntax for their own purposes. Just how much of this is actually feasible is what this project aims to determine. The ultimate goal is to make it so that one never needs to repeat themself for the sake of syntactic restrictions.

# Consistent Declaration Form

Every declarative statement in ndl is of the form [name] [type] [other...] where the type is usually a keyword for most statements, but is a type name for variables instead. At least, this is the aim, but right now the var keyword is still required for variables. This is because the parser cannot yet determine with 100% certainty what is a type name, variable name or function name. This will be fixed with a stack based parsing technique in the future.

# Indentation as Scope

Yes, I've falled in the same trap as Python :P

More seriously, the main problem people have with this way of doing this is that mixing tabs and spaces leads to disaster. To fix this, one would have the ability to specify the length of a tab in spaces somewhere in the file's header, that way every editor would be able to display that file correctly. Again, no idea if this is actually a good idea, but this whole thing is an experiment after all.

# Universal Function Call Syntax

A little syntax trick nabbed from D and expanded upon. The idea in D is that any function can be called as a member function with dot notation, and the variable before the dot is used as the first argument. Furthermore, functions that would normally required empty parentheses can simply ommit them so that those look like you're accessing a member variable. The way I'm boosting this idea is that you can also call functions that have one or two arguments as unary or binary operators. In fact, a function of any nonzero number of arguments can be called this way by chaining the function name between that number of arguments. Will this create problems with how the language parses expressions? Probably. Hopefully! :D

# Conclusion

In case you couldn't tell, this isn't very serious, but I still think I have a few novel ideas that I want to test out. And hey, if this works, maybe we'll all be programming in Non-Descript Language 20 years from now! ;)