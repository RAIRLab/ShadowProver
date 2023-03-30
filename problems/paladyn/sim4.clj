
{:description "The adjudicator reasoning about information from the fixed ldrone."
 :assumptions
 {;;; It is common knowledge at the start what is needed to satisfy clause 2.
   :common (Common! t0
                    (iff clause2
                         (and
                          (exists p
                                  (and (Inside p Building)
                                       (Planning p)))
                          (forall p (if (Inside p Building) (not (Civilian p)))))))

   ;;; Report from the the fixed ldrone.
   :report (Believes! adj t3
                      (Believes! ldrone t3
                                 (exists p (and (Inside p Building) (Civilian p)))))}

 :goal        (Believes! adj t4 (Believes! ldrone t3 (not clause2)))}




