package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.CommonUtils;
import org.rairlab.shadow.prover.utils.Sets;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * Created by naveensundarg on 5/4/16.
 */
public final class Intends extends BaseFormula implements UnaryModalFormula{

    private final Value agent;
    private final Value time;
    private final Formula formula;
    private final Set<Formula> subFormulae;
    private final Set<Variable> variables;
    private final Set<Value> values;

    private final Set<Variable> boundVariables;
    private final Set<Value> allValues;

    private final int weight;


    public Intends(Value agent, Value time, Formula formula) {


        this.agent = agent;
        this.time = time;
        this.formula = formula;
        this.subFormulae = CollectionUtils.setFrom(formula.subFormulae());
        this.subFormulae.add(this);
        this.allValues = Sets.newSet();
        this.allValues.add(agent);
        this.allValues.add(time);
        this.variables = CollectionUtils.setFrom(formula.variablesPresent());
        this.values = Sets.union(Sets.union(agent.subValues(), time.subValues()), formula.valuesPresent());
        this.boundVariables = CollectionUtils.setFrom(formula.boundVariablesPresent());

        if (agent instanceof Variable) {
            variables.add((Variable) agent);
        }

        if (time instanceof Variable) {
            variables.add((Variable) time);

        }


        this.weight = 1 + agent.getWeight() + time.getWeight() + formula.getWeight();
    }

    public Formula getFormula(){
        return formula;
    }


    public Value getAgent() {
        return agent;
    }

    public Value getTime() {
        return time;
    }

    public Set<Formula> getSubFormulae() {
        return subFormulae;
    }

    public Set<Variable> getVariables() {
        return variables;
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
        return new Intends(agent.apply(substitution), time.apply(substitution), formula.apply(substitution));

    }

    @Override
    public Formula shadow(int level) {
        return new Atom("|"+ CommonUtils.sanitizeShadowedString(toString())+"|");
    }

    @Override
    public Formula applyOperation(UnaryOperator<Formula> operator) {
        return null;
    }

    @Override
    public int getLevel() {
        return 2;
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


        return new Intends(agent, time, formula.replaceSubFormula(oldFormula, newFormula));
    }


    @Override
    public String toString() {
        return "(Intends! "
                + agent + " "
                + time + " "+
                formula + ")";
    }

    @Override
    public String toSnarkString() {
        return "(Intends! "
                + agent.toSnarkString() + " "
                + time.toSnarkString() + " "+
                formula.toSnarkString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Intends intends = (Intends) o;

        if (!agent.equals(intends.agent)) return false;
        if (!time.equals(intends.time)) return false;
        return formula.equals(intends.formula);

    }

    @Override
    public int hashCode() {
        int result = agent.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + formula.hashCode();
        return result;
    }

    @Override
    public Set<Value> allValues() {
        return allValues;
    }

    @Override
    public String getName() {
        return "Intends of " +  ((formula instanceof Predicate)? ((Predicate) formula).getName():formula.getClass() +"");
    }

    @Override
    public Set<Variable> boundVariablesPresent() {
        return boundVariables;
    }

    public List<Formula> getArgs() {
        return Arrays.asList(formula);
    }
}
