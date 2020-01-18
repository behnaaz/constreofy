package org.behnaz.rcsp.model.util;

import org.apache.commons.lang3.StringUtils;
import org.behnaz.rcsp.ConstraintConnector;
import org.behnaz.rcsp.Starter;
import org.behnaz.rcsp.StateValue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.behnaz.rcsp.AbstractConnector.AND;
import static org.behnaz.rcsp.AbstractConnector.NOT;
import static org.behnaz.rcsp.AbstractConnector.SPACE;
import static org.behnaz.rcsp.ConstraintConnector.KEY_WORDS_REGEX;
import static org.behnaz.rcsp.ConstraintConnector.WORD_BOUNDARY;
import static org.behnaz.rcsp.Solver.FORMULA_NAME;
import static org.behnaz.rcsp.Solver.SHUT;
import static priority.Variable.CURRENT_MEMORY;
import static priority.Variable.NEXT_MEMORY;

public class SolverHelper {
    static final String PREAMBLE = "set_bndstk_size 100000;load_package \"redlog\";rlset ibalp;";

    public static Set<String> extractVariables(final String newConstraint) {
        final Set<String> result = new HashSet<>();

        if (StringUtils.isNotBlank(newConstraint)) {
            // Replace all keywords with empty string
            final String onlyVariables = newConstraint.replaceAll(KEY_WORDS_REGEX, "");
            // Constraint
            for (final String term : onlyVariables.split(SPACE)) {
                if (!term.trim().isEmpty()) {
                    result.add(term.toUpperCase(Locale.US));
                }
            }
        }
        return result;
    }

    public static String capitalizeVars(final String constraint) {
        String result = constraint;
        for (final String term : constraint.replaceAll(KEY_WORDS_REGEX, "").split(SPACE)) {
            result = result.replaceAll(WORD_BOUNDARY + term + WORD_BOUNDARY, term.toUpperCase(Locale.US));
        }
        return result;
    }


    public static String declarationSection(final String formulae) {
        final StringBuilder builder = new StringBuilder();
        final Set<String> vars = SolverHelper.extractVariables(formulae).stream().filter(item -> !item.isEmpty())//TODO orElse??
                .map(String::toUpperCase).collect(Collectors.toSet());

        builder.append("rlpcvar ");
        final String variable = vars.toString();
        builder.append(variable.substring(1, variable.length() - 1)).append(';');
        return builder.toString();
    }

    public static Set<String> getAllFIFOs(final String mainConstraint) {
        final Set<String> result = new TreeSet<>();
        final Pattern pattern = Pattern.compile("\\w+XRING", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(mainConstraint);

        while(matcher.find())
            result.add(mainConstraint.substring(matcher.start(), matcher.end()));

        return result;
    }

    public static String applyFIFOStates(final String mainConstraint, final StateValue stateValue) {
        final StringBuilder builder = new StringBuilder();
        builder.append(mainConstraint);

        for (final String capitalFIFO : SolverHelper.getAllFIFOs(mainConstraint)) {
            final String fifo = capitalFIFO.toLowerCase(Locale.ENGLISH).replaceAll(NEXT_MEMORY, CURRENT_MEMORY);
            if (stateValue != null && stateValue.getValue(fifo).isPresent() && stateValue.getValue(fifo).get()) {//TODO what to do with optional???
                if (stateValue.getValue(fifo).get()) {
                    builder.append(AND);
                    builder.append(fifo.toUpperCase(Locale.ENGLISH));
                } else {
                    assert false;
                }
            } else {
                builder.append(AND);
                builder.append(" (");
                builder.append(NOT);
                builder.append(fifo.toUpperCase(Locale.ENGLISH));
                builder.append(") ");
            }
        }
        return builder.toString();
    }

    /**
     * Wrap the constraints with required by REDUCE
     * @param stateValue
     * @return
     */
    public static String constraintSection(final StateValue stateValue, final String constraint) {
        final StringBuilder builder = new StringBuilder();
        builder.append(PREAMBLE);
        builder.append(SolverHelper.declarationSection(constraint));
        builder.append(FORMULA_NAME + " := " + SolverHelper.applyFIFOStates(constraint, stateValue) + ";;");
        builder.append(dnf(FORMULA_NAME));
        builder.append(SHUT);
        builder.append("; end;");
        String temp = builder.toString();
        Starter.log("Built constraints " + temp);
        return temp;
    }

    private static String dnf(final String formulae) {
        return new StringBuilder().append("rldnf ").append(formulae).append(";").toString();
    }

}
