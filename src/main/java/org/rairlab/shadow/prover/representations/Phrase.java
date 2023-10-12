package org.rairlab.shadow.prover.representations;

import java.io.Serializable;

/** abstract Base Class of the entire representation hierarchy
 * @author naveensundarg 
 * @date 7/24/16.
 */
public abstract class Phrase implements Serializable {

    //REWRITE should be static or a util, has no bearing on instances
    /** A null safe version of {@link java.lang.Object#hashCode}
     *  @param o the object to hash
     *  @return 0 if the object is null, the result from {@link java.lang.Object#hashCode} otherwise.
     */
    protected int safeHashCode(Object o) {
        return (o != null) ? o.hashCode() : 0;
    }
}
