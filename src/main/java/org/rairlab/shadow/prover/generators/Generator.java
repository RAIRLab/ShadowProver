package org.rairlab.shadow.prover.generators;


import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.Pair;
import org.rairlab.shadow.prover.utils.Problem;

import java.util.List;
import java.util.Set;

public interface Generator {

    List<Pair<List<Formula>, Boolean>> generate(int total);

}
