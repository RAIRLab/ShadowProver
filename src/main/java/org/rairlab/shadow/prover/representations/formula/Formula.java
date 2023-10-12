package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.Expression;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Constant;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/** Base class for representing logical formulae
 * Acts like a node from hyperslate with justifications and assumptions
 * @author naveensundarg 
 * @date 4/8/16.
 */
public abstract class Formula extends Expression implements Cloneable{
    
    //Representation ===================================================================================================
    
    private Justification justification;
    private Set<Formula>  assumptions;
    //private int           strengthFactor;   //DEADCODE, strength factors are never used

    //Constructors, accessors, modifiers ===============================================================================
    
    public Formula(){
        //this.strengthFactor = Integer.MAX_VALUE; //DEADCODE, strength factors are never used
    }

    /**
     * @return The justification for this formula
     */
    public Justification getJustification() {
        return justification;
    }

    //REWRITE accessors shouldn't return "this", inconsistent style with other accessors. 
    /** Justification modifier
     * @param justification the justification for this formula
     * @return this, The modified formula
     */
    public Formula setJustification(Justification justification) {
        this.justification = justification;
        return this;
    }

    /** Accessor for the assumptions underlying this formula
     * @return the assumptions for this formula
     */
    public Set<Formula> getAssumptions() {
        return assumptions;
    }

    /** Modifier for the assumptions underlying this formula
     * @param assumptions the assumptions for this formula
     */
    public void setAssumptions(Set<Formula> assumptions) {
        this.assumptions = assumptions;
    }

    //DEADCODE strength factors unused
    /*
    public int getStrengthFactor(){
        return strengthFactor;
    }

    public void setStrengthFactor(int strength){
        this.strengthFactor = strength;
    }
     */

    // Member functions ================================================================================================
    
    /**
     * @return A set of all free variables in the formula
     */
    public Set<Variable> freeVariablesPresent() {
        Set<Variable> variables = this.valuesPresent().stream().
                filter(value -> value instanceof Variable).
                map(value -> (Variable) value).
                collect(Collectors.toSet());
        return Sets.difference(variables, this.boundVariablesPresent());
    }

    /**
     * @return A set of all constants in the formula
     */
    public Set<Constant> constantsPresent() {
        return this.valuesPresent().stream().
                filter(value -> value instanceof Constant).
                map(c -> (Constant) c).
                collect(Collectors.toSet());
    }

    /**
     * @return the arity of the predicate with the highest arity in the formula
     */
    public int maxPredicateArity() {
        return this.subFormulae().stream().
                filter(f-> f instanceof Predicate).
                map(f -> (Predicate) f).
                mapToInt(p -> p.getArguments().length).
                max().orElse(0);
    }

    /**
     * @return the arity of the function with the highest arity in the formula
     */
    public int maxFunctionArity() {
        return this.valuesPresent().stream().
                filter(v-> v instanceof Compound).
                map(c -> (Compound) c).
                mapToInt(c -> c.getArguments().length).
                max().orElse(0);
    }

    /**
     * Replaces an object (constant, variable, function, etc) in a formula with a new objects string representation.
     * Note that this replaces purely based on the same string representation of the objects rather than 
     * underlying objects.
     * @param oldValue the old object we're replacing
     * @param newValue the new object we're replacing with
     * @return A formula with all occurrences of the new object replaced with the new object
     */
    public Formula replace(Value oldValue, Value newValue){
        try {
            return Reader.readFormulaFromString(this.toString().replace(oldValue.toString(), newValue.toString()));
        } catch (Reader.ParsingException e) {
            return this;
        }
    }

    //REWRITE, this should be an abstract method but breaks And.java when trying to fix.
    public Formula generalize(Map<Value, Variable> substitution){
        throw new NotImplementedException("generalize");
    }

    //Abstract methods =================================================================================================

    public abstract List<Formula> getArgs();
    public abstract Set<Formula> subFormulae();
    public abstract Set<Variable> variablesPresent();
    public abstract Formula apply(Map<Variable, Value> substitution);
    public abstract Formula shadow(int level);
    public abstract Formula applyOperation(UnaryOperator<Formula> operator);
    public abstract int getLevel();
    public abstract int getWeight();
    public abstract Formula replaceSubFormula(Formula oldFormula, Formula newFormula);

    /**
     * @return A set of all bound variables in the formula
     */
    public abstract Set<Variable> boundVariablesPresent();

    /**
     * @return A set of all objects (Values) in the formula
     */
    public abstract Set<Value> valuesPresent();

    /**
     * @return A string representing the formula
     */
    public abstract String toSnarkString();

    // Static methods ==================================================================================================
    
    public static String _getSlateString_(Formula formula){
        return formula.toString().replace("implies ", "if ");
    }
}
