package org.rairlab.shadow.prover.representations.method;

import org.rairlab.shadow.prover.representations.ErrorPhrase;
import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by naveensundarg on 8/28/17.
 */
public class Both extends PrimitiveMethod {

    private static final Both INSTANCE;

    static {
        INSTANCE = new Both();
    }

    private Both() {

    }

    public static Both getInstance() {

        return INSTANCE;
    }

    @Override
    public Phrase apply(Set<Formula> assumptionBase, List<Phrase> args) {

        if(args.size()>=2){

            List<Formula> conjuncts = CollectionUtils.newEmptyList();
            for(Phrase arg: args){


                if(arg instanceof Formula){

                    if(assumptionBase.contains(arg)){

                        conjuncts.add((Formula) arg);

                    } else {

                        return new ErrorPhrase("both: "+arg+ " not in the assumption base");


                    }
                } else{


                    return new ErrorPhrase("both: all arguments must be a formula, but got: "+ arg + " of class " + arg.getClass());
                }


            }

            return new And(conjuncts);


        } else {


            return new ErrorPhrase("both requires at least two arguments");
        }


    }

    @Override
    public String toString() {
        return "both";
    }
}
