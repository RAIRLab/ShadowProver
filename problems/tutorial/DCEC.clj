{:name        "Belief follows from knowledge"
 :description ""
 :assumptions {1 (Knows! a P)}
 :goal        (Believes! a P)}

 {:name        "Conjunction under Belief"
 :description ""
 :assumptions {1 (Believes! a P)
               2 (Believes! a Q)}
 :goal        (Believes! a (and P Q))}

 {:name       "MP under Belief"
 :description ""
 :assumptions {1 (Believes! a1 t0 P)
               2 (Believes! a1 t0 (if P Q))}
 :goal        (Believes! a1 t0 Q)}

{:name        "Common Knowledge"
 :description ""
 :assumptions {1 (Common! t0 P)}
 :goal        (and (Knows! a1 t1 P) (Knows! a2 t1 P))}


 {:name       "Bird Theorem"
 :description "Belief Conjunction and Truth from Knowledge"
 :assumptions {1 (Believes! a P)
               2 (Believes! a Q)
               3 (if (Believes! a (and P Q)) (Knows! a R))}
 :goal        R}