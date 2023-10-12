package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.representations.value.Variable;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.List;

/**
 * Created by naveensundarg on 4/13/16.
 */
public interface Quantifier {

    Variable[] vars();

    public abstract Formula getArgument();

    public abstract Set<Variable> variablesPresent(); 


    default Formula renamed(Map<Variable, Variable> variableMap){

        Variable[] vars = vars();


        Variable[] newVars = new Variable[vars.length];

        for(int i = 0; i<vars.length; i++){

            newVars[i] = variableMap.get(vars[i]);
        }


        if(this instanceof Existential){

            return  new Existential(newVars, getArgument());
        }
        else{

            return  new Universal(newVars, getArgument());
        }

    }

    default List<Formula> getArgs() {
        return Arrays.asList(getArgument());
    }


}
