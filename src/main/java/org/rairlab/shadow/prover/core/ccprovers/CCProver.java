package org.rairlab.shadow.prover.core.ccprovers;

import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;

import java.util.Set;

public interface CCProver  extends Prover {
    Set<Formula> getProhibited();
}
