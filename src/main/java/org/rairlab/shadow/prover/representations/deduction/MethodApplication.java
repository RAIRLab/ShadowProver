package org.rairlab.shadow.prover.representations.deduction;

import org.rairlab.shadow.prover.dpl.Interpreter;
import org.rairlab.shadow.prover.representations.Phrase;
import org.rairlab.shadow.prover.utils.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by naveensundarg on 8/27/17.
 */
public final class MethodApplication extends Deduction {

    private final Phrase E;
    private final List<Phrase> args;

    public MethodApplication(Phrase E, List<Phrase> args){

        this.E = E;
        this.args = Collections.unmodifiableList(args);

    }

    public Phrase getE() {
        return E;
    }

    public List<Phrase> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "(" + E + " " + args.stream().map(Object::toString).reduce("", (x,y) -> x + " " + y) + ")";
    }
}
