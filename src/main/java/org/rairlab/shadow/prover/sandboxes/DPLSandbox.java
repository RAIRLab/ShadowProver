package org.rairlab.shadow.prover.sandboxes;

import org.rairlab.shadow.prover.dpl.Interpreter;
import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.representations.deduction.MethodApplication;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.method.Claim;
import org.rairlab.shadow.prover.representations.method.ModusPonens;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Reader;
import org.rairlab.shadow.prover.utils.Sets;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by naveensundarg on 8/28/17.
 */
public class DPLSandbox {

    public static void main(String[] args) throws Reader.ParsingException {

        Formula P = Reader.readFormulaFromString("P");
        Formula Q = Reader.readFormulaFromString("Q");
        Formula implication = (Formula) Reader.readPhraseFromString("(if P Q)");



        Phrase p =   Reader.readPhraseFromString("(assume* P Q :in (!both P Q))");

        Set<Formula> assumptionBase = Sets.newSet();


        System.out.println(assumptionBase +  "==>" + Interpreter.interpret(assumptionBase, p));

    }


}
