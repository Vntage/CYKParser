# How to Run and Any Dependencies and Assumptions:

> Note: Determines whether a string belongs to the inputted grammar/language, does not show table

> Test langauges from the document are not named correctly for clarity, knowing which language is which, they must be renamed as below, to work properly.

Language must be in a text file specifically named: "CNF.txt", One Grammar at a time.

Empty strings (epsilon) are not supported. This means:

     Input strings cannot be empty.

     Grammar rules like A -> ε are not allowed.

Grammar given must be in Chomsky Normal Form(CNF)

String given should have appropriate terminals, i.e. terminals in the input string should have terminals in the alphabet of the language
	
     More generally this means no characters in the input outside of valid characters in the language's terminal alphabet

To Run: Compile and then run, take care to ensure the "CNF.txt" is in the correct directory
