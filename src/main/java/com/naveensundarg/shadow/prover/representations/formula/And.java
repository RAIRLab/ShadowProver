package com.naveensundarg.shadow.prover.representations.formula;

import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CommonUtils;
import com.naveensundarg.shadow.prover.utils.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 4/8/16.
 */
public final class And extends Formula {

    private final Formula[] arguments;
    private final Set<Formula> subFormulae;
    private final Set<Variable> variables;
    private final Set<Value> values;

    private final Set<Variable> boundVariables;
    private final int level;
    private final int weight;

    public And(Formula ... arguments){
        this.arguments = arguments;
        this.subFormulae = Arrays.stream(arguments).map(Formula::subFormulae).
                reduce(Sets.newSet(), Sets::union);
        this.subFormulae.add(this);
        this.variables = Arrays.stream(arguments).map(Formula::variablesPresent).reduce(Sets.newSet(), Sets::union);
        this.values = Arrays.stream(arguments).map(Formula::valuesPresent).reduce(Sets.newSet(), Sets::union);
        this.boundVariables = Arrays.stream(arguments).map(Formula::boundVariablesPresent).reduce(Sets.newSet(), Sets::union);

        this.level = CommonUtils.maxLevel(arguments);
        this.weight = 1 + Arrays.stream(arguments).mapToInt(Formula::getWeight).reduce(0, Integer::sum);


    }

    public And(List<Formula> arguments){
        this.arguments = new Formula[arguments.size()];

        for(int i = 0; i< arguments.size(); i++){
            this.arguments[i] = arguments.get(i);
        }

        this.subFormulae = Arrays.stream(this.arguments).map(Formula::subFormulae).
                reduce(Sets.newSet(), Sets::union);

        this.subFormulae.add(this);

        this.variables = arguments.stream().map(Formula::variablesPresent).reduce(Sets.newSet(), Sets::union);
        this.values = arguments.stream().map(Formula::valuesPresent).reduce(Sets.newSet(), Sets::union);
        this.boundVariables = arguments.stream().map(Formula::boundVariablesPresent).reduce(Sets.newSet(), Sets::union);

        this.level = CommonUtils.maxLevel(this.arguments);
        this.weight = 1 + arguments.stream().mapToInt(Formula::getWeight).reduce(0, Integer::sum);



    }
    public Formula[] getArguments() {
        return arguments.clone();
    }

    @Override
    public String toString() {
        return "(and "  + CommonUtils.toString(arguments) + ")";
    }

    @Override
    public String toSnarkString() {
        return "(and "  + CommonUtils.toSnarkString(arguments) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        And and = (And) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(arguments, and.arguments);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(arguments);
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
    public Set<Value> valuesPresent() {
        return values;
    }


    @Override
    public Formula apply(Map<Variable, Value> substitution) {
        return new And(Arrays.stream(arguments).map(x->x.apply(substitution)).collect(Collectors.toList()));

    }

    @Override
    public Formula generalize(Map<Value, Variable> substitution) {
        return new And(Arrays.stream(arguments).map(x->x.generalize(substitution)).collect(Collectors.toList()));

    }

    @Override
    public Formula shadow(int level) {
        return new And(Arrays.stream(arguments).map(f->f.shadow(level)).collect(Collectors.toList()));
    }

    @Override
    public Formula applyOperation(UnaryOperator<Formula> operator) {
        return new And(Arrays.stream(arguments).map(x->x.applyOperation(operator)).collect(Collectors.toList()));

    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public Formula replaceSubFormula(Formula oldFormula, Formula newFormula) {

        if(oldFormula.equals(this)){

            return newFormula;
        }

        if(!subFormulae().contains(oldFormula)){

            return this;
        }

        Formula[] newArgs = new Formula[arguments.length];

        for(int i = 0; i < arguments.length; i++){

            newArgs[i] = arguments[i].replaceSubFormula(oldFormula, newFormula);
        }

        return new And(newArgs);
    }

    @Override
    public Set<Variable> boundVariablesPresent() {
        return boundVariables;
    }



}
