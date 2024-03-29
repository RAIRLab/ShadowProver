package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CommonUtils;
import org.rairlab.shadow.prover.utils.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 4/11/16.
 */
public class Universal extends Formula implements Quantifier{

    private final Formula argument;
    private final Variable[] vars;
    private Set<Formula> subFormulae;
    private final Set<Variable> variables;
    private final Set<Value> values;

    private final Set<Variable> boundVariables;
    private final int weight;

    public Universal(Variable[] vars, Formula argument) {

        if (!(vars.length > 0)) {
            throw new AssertionError("Universal should have at least one variable");
        }

        this.vars = vars;
        this.argument = argument;
        this.subFormulae = Sets.copy(argument.subFormulae());
        this.variables = argument.variablesPresent();
        this.values = Sets.union(Arrays.stream(vars).collect(Collectors.toSet()),  argument.valuesPresent());
        this.boundVariables = Sets.union(Arrays.stream(vars).collect(Collectors.toSet()), argument.boundVariablesPresent());
        this.subFormulae.add(this);
        Arrays.stream(vars).forEach(this.variables::add);

        this.weight = 1 + variables.stream().mapToInt(Value::getWeight).reduce(0, Integer::sum)  + argument.getWeight();

    }

    public Formula getArgument() {
        return argument;
    }

    @Override
    public Set<Formula> subFormulae() {
        return subFormulae;
    }

    @Override
    public Set<Variable> variablesPresent() {
        return variables;
    }

    @Override
    public Formula apply(Map<Variable, Value> substitution) {
        //TODO:
        return new Universal(vars, argument.apply(substitution));
    }

    @Override
    public Formula shadow(int level) {
        if (level == 0) {

            return new Atom("|"+ CommonUtils.sanitizeShadowedString(toString())+"|");

        } else if (level == 1) {

            return new Universal(vars, argument.shadow(level));
        }

        throw new AssertionError("Invalid shadow getLevel: " + level);
    }


    @Override
    public Formula applyOperation(UnaryOperator<Formula> operator) {
        return new Universal(vars, argument.applyOperation(operator));
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public Variable[] vars() {
        return vars;
    }

    @Override
    public Set<Variable> boundVariablesPresent() {
        return boundVariables;
    }

    @Override
    public Set<Value> valuesPresent() {
        return values;
    }

    @Override
    public String toString() {
        return "(forall " + "(" + StringUtils.trim(Arrays.stream(vars).map(Variable::toString).reduce("", (x, y) -> x  + y + " "))
                + ")" + " "
                + argument.toString() + ")";
    }

    @Override
    public String toSnarkString() {
        return "(forall " + "(" + StringUtils.trim(Arrays.stream(vars).map(Variable::toSnarkString).reduce("", (x, y) -> x  + y + " "))
                + ")" + " "
                + argument.toSnarkString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Universal universal = (Universal) o;

        if (!argument.equals(universal.argument)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(vars, universal.vars);

    }

    @Override
    public int hashCode() {
        int result = safeHashCode(argument);
        result = 31 * result + Arrays.hashCode(vars);
        return result;
    }

    @Override
    public Formula generalize(Map<Value, Variable> substitution) {
        return new Universal(vars, argument.generalize(substitution));

    }

    @Override
    public Formula replaceSubFormula(Formula oldFormula, Formula newFormula) {
        return new Universal(vars, argument.replaceSubFormula(oldFormula, newFormula));
    }

    public List<Formula> getArgs() {
        return Arrays.asList(argument);
    }
}
