package org.rairlab.shadow.prover.representations.value;

import org.rairlab.shadow.prover.core.proof.Unifier;
import org.rairlab.shadow.prover.representations.Expression;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Pair;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Abstract Base class for "objects" in the universe of discourse such as constants as 
 * {@link org.rairlab.shadow.prover.representations.value.Constant}, 
 * functions (and sets?) as {@link org.rairlab.shadow.prover.representations.value.Compound},
 * and variables over objects as {@link org.rairlab.shadow.prover.representations.value.Variable}
 * @author naveensundarg
 * @date 4/11/16
 */
@SuppressWarnings("rawtypes")
public abstract class Value extends Expression implements Comparable{

    /** Name of the object */
    protected String name;

    //Constructors Accessors Modifiers =================================================================================

    /** object name accessor
     * @return the name of the object
     */
    public String getName(){
        return name;
    }

    //DEADCODE used in 2 places, but doesn't do anything
    public boolean occurs(Variable x){
        return false;
    }

    public String toSnarkString() {
        return this.toString();
    }

    // Abstract Methods ================================================================================================

    //REWRITE This is a dumb non-extensible way of doing this
    public abstract boolean isVariable();
    public abstract boolean isConstant();
    public abstract boolean isCompound();

    public abstract int arity();
    public abstract Value[] getArguments();
    public abstract Set<Variable> variablesPresent();
    public abstract Value apply(Map<Variable, Value> substitution);
    public abstract Value replace(Value value1, Value value2);
    public abstract Value generalize(Map<Value, Variable> substitution);
    public abstract Set<Value> subValues();
    public abstract int getWeight();
    public abstract Optional<Pair<Variable, Value>> subsumes(Value other);

    // Static Methods ==================================================================================================

    public static Optional<Map<Variable,Value>> subsumes(Value[] values1, Value[] values2){
        if(values1.length!=values2.length){
            return Optional.empty();
        } 

        Map<Variable, Value> possibleAnswer = CollectionUtils.newMap();
        for(int i  = 0; i < values1.length; i++){
            Value v1 = values1[i];
            Value v2 = values2[i];
            Optional<Pair<Variable, Value>> thisSubsumed = v1.subsumes(v2);
            if(!thisSubsumed.isPresent()){
                return Optional.empty();
            } 
            Variable variable = thisSubsumed.get().first();
            Value value = thisSubsumed.get().second();
            Optional<Map<Variable, Value>> augmentedOpt = Unifier.addTo(possibleAnswer, variable, value);
            if(!augmentedOpt.isPresent()){
                return augmentedOpt;
            } 
            possibleAnswer.putAll(augmentedOpt.get());
        }
        return Optional.of(possibleAnswer);
    }
 }
