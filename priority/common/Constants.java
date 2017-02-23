package priority.common;

public interface Constants {//TODO
	static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";
	static final String PREAMBLE = "set_bndstk_size 100000;load_package \"redlog\";rlset ibalp;";
	static final String WORD_BOUNDARY = "\\b";
	static final String FORMULA_NAME = "qaz";
	static final String FORMULA_NAME_EQUAL = "qaz :=";
	static final String SHUT = "shut";
	static final String TILDE = "tilde";
	static final String CIRC = "circ";
	static final String BULLET = "bullet";
	static final String CURRENT_MEMORY = "ring";
	static final String NEXT_MEMORY = "xring";
	static final String IMPLIES = " impl ";
	static final String RIGHTLEFTARROW = " equiv ";
	static final String NOT = " not ";
	static final String OR = " or ";
	static final String AND = " and ";
	static final String TRUE = " true ";
	static final String FALSE = " false ";
	static final char SEPARATOR = ';';
	static final String SPACE =  " ";
	static final String STATE_DELIMINATOR = ":";
	static final String CLOSE_TAG_BRACKET = "]";
	static final String OPEN_TAG_BRACKET = "[";
	static final String CLOSE_TAG_PARANTHESIS = ")";
	static final String OPEN_TAG_PARANTHESIS = "(";
	static final String STRING_COMMA = ",";
	static final String PREFIX_NOT = "!";
	static final String STRING_SPACE = " ";
	static final String STRING_EMPTY = "";
	static final String SOURCE_END_SIGN = "------";
	static final String TARGET_START_SIGN = "------->";
}