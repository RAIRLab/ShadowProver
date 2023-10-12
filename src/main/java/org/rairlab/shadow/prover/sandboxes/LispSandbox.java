package org.rairlab.shadow.prover.sandboxes;

import org.rairlab.shadow.prover.core.internals.UniversalInstantiation;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Universal;
import org.rairlab.shadow.prover.utils.CommonUtils;
import org.rairlab.shadow.prover.utils.Sets;
import org.armedbear.lisp.*;

import java.util.Set;

/**
 * Created by naveensundarg on 4/8/16.
 */
public class LispSandbox {

    public static void main(String[] args) throws Exception {

        Interpreter interpreter = Interpreter.createInstance();


        LispObject result  = interpreter.eval("(load \"./snark/snark-system.lisp\")");

        System.out.println(result);

        result = interpreter.eval("(make-snark-system)");


        System.out.println(result);

        result = interpreter.eval("(load \"./snark/snark-interface.lisp\")");
        System.out.println(result);

        String s = "(prove-from-axioms-and-get-answer '((man socrates) (forall ?x (implies (man ?x) (mortal ?x))))" +
                " '(mortal ?x)  '?x)";
        System.out.println(s);
        result = interpreter.eval(s);

        System.out.println(result);



    }

}
