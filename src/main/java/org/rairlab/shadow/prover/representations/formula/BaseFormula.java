package org.rairlab.shadow.prover.representations.formula;

import org.rairlab.shadow.prover.representations.value.Value;

import java.util.Set;

/**
 * Created by naveensundarg on 12/5/16.
 */
public abstract class BaseFormula extends Formula{

   public abstract  Set<Value> allValues();
   public abstract String getName();

}
