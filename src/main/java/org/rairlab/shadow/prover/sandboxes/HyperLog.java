package org.rairlab.shadow.prover.sandboxes;

import org.rairlab.shadow.prover.hyperlog.Evaluator;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.NamedLambda;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

public class HyperLog {

    public static void main(String[] args) throws Reader.ParsingException {

        NamedLambda def = (NamedLambda) Reader.readFormulaFromString("(defn parent [x]  (inc x))");
        Formula f1 = Reader.readFormulaFromString("(exists [x] (O x))");
        Formula f2 = Reader.readFormulaFromString("(forall [x] (O x))");

        Object answer = (Evaluator.evaluate(Sets.with(def), Reader.readLogicValueFromString("(clojure.string/join (reverse \"naveen\"))")));

        System.out.println(answer);
    }
}
