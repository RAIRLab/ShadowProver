package org.rairlab.shadow.prover.representations.method;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.representations.ErrorPhrase;
import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.Reader;

import java.util.List;
import java.util.Set;

/**
 * Created by naveensundarg on 8/28/17.
 */
public class TrueIntro extends PrimitiveMethod {

    private static final TrueIntro INSTANCE;

    static {
        INSTANCE = new TrueIntro();
    }

    private TrueIntro() {

    }

    public static TrueIntro getInstance() {

        return INSTANCE;
    }
    @Override
    public Phrase apply(Set<Formula> assumptionBase, List<Phrase> args) {

        if(args.size()==0){

            return Logic.getTrueFormula();


        } else {

            return new ErrorPhrase("true-intro takes no arguments but got: "+ args);
        }
    }

    @Override
    public String toString() {
        return "true-intro";
    }
}
