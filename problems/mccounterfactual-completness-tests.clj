;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Warmup 1"
 :description "kicking the tires"
 :assumptions {1 P}
 :goal        (or P Q)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Warmup 2"
 :description "socrate is mortal"
 :assumptions {1 (forall (x)
                         (if (Man x) (Mortal x)))
               2 (Man socrates)}
 :goal        (Mortal socrates)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Warmup 3"
 :description "Law of Excluded Middle"
 :assumptions {}
 :goal        (forall (x) (or (P x) (not (P x))))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 1"
 :description ""
 :assumptions {1 (exists (x) (and (Human x) (Flew-to-Space x)))
               2 (Flew-to-Space armstrong)
               3 (Man armstrong)
               4 (forall (x) (iff (Human x) (or (Man x) (Woman x))))}
 :goal        (exists (x) (and (Man x) (Flew-to-Space x)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 2"
 :description ""
 :assumptions {1 (forall (x y z) (if (and (R x y) (R y z)) (R x z)))
               2 (R a b)
               3 (R b c)}
 :goal        (R a c)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 3"
 :description ""
 :assumptions {1 (forall (x y z) (if (and (R x y) (R y z)) (R x z)))
               2 (forall (x y) (if (R x y) (R y x)))
               3 (forall (x) (R x))
               4 (R a b)
               5 (R b c)
               6 (R c d)}
 :goal        (R a d)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 4"
 :description ""
 :assumptions {1 (forall (x y z) (if (and (R x y) (R y z)) (R x z)))
               2 (forall (x y) (if (R x y) (R y x)))
               3 (forall (x) (R x))
               4 (R a b)
               5 (R b c)
               6 (R c d)}
 :goal        (R d a)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 5"
 :description "Universal implies Existential"
 :assumptions {}
 :goal        (if (forall (x) (P x)) (exists (y) (P y)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 6"
 :description "Bird Theorem"
 :assumptions {}
 :goal        (exists (x) (if
                            (Bird x)
                            (forall (y) (Bird y))))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 7"
 :description "Everyone likes anyone who likes someone."
 :assumptions {:a1 (forall (x) (if (exists (z) (likes x z))
                                 (forall (y) (likes y x))))
               :a2 (likes a b)}
 :goal        (forall (x y) (likes x y))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 8"
 :description "Demodulation Test 1"
 :assumptions {:p1 (= a b)
               :p2 (P a)}
 :goal        (P b)}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 9"
 :description "Demodulation Test 2"
 :assumptions {:p1 (and (= a b) (= c d))
               :p2 (Q d)
               :p3 (P a)
               :p4 (forall (x) (if (and (P b) (Q c)) (R x)))}
 :goal        (forall (x) (R x))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 10"
 :description "Demodulation Test 3"
 :assumptions {:p1 (= socrates (husband xanthippe))
               :p2 (forall (x) (if (Man x) (Mortal x)))
               :p3 (Man socrates)}
 :goal        (Mortal (husband xanthippe))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 11"
 :description "Demodulation Test 4"
 :assumptions {:p1 (Culprit john)}
 :goal        (if (= jack john) (Culprit jack))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 12"
 :description "Suppes Theorem"
 :assumptions {1 (forall (x) (= x x))
               2 (forall (y) (exists (x) (or (In x y) (= y EmptySet))))
               3 (forall (z) (exists (x) (forall (y) (iff (In y x) (and (In y z) (not (= y y)))))))}
 :goal        (forall (x) (not (In x EmptySet)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "fol true test 13"
 :description "distribution"
 :assumptions {}
 :goal        (if
                (and (forall (x) (P x)) (exists (y) (not (Q y))))
                (forall (x) (and (P x) (exists (y) (not (Q y))))))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "dt16"
 :description "dt16: fol is transparent with Knows!"
 :assumptions {1 (not (Knows a now (= morning_star evening_star)))
               2 (= morning_star evening_star)
               3 (Knows a now (= morning_star morning_star))}
 :goal        (and P (not P))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;





{:name        "traid zoo reflexivity"
 :description ""
 :assumptions {AX1  (forall (x) (or (Camel x) (Llama x) (Aard x)))
               AX2  (not (exists (x) (and (Camel x) (Aard x))))
               AX3  (not (exists (x) (and (Aard x) (Llama x))))
               AX4  (not (exists (x) (and (Llama x) (Camel x))))
               AX5  (forall (x y) (if (and (Camel x) (Camel y)) (SameSpecies x y)))
               AX6  (forall (x y) (if (and (Llama x) (Llama y)) (SameSpecies x y)))
               AX7  (forall (x y) (if (and (Aard x) (Aard y)) (SameSpecies x y)))
               AX8  (forall (x y) (if (and (SameSpecies x y) (Camel x)) (Camel y)))
               AX9  (forall (x y) (if (and (SameSpecies x y) (Llama x)) (Llama y)))
               AX10 (forall (x y) (if (and (SameSpecies x y) (Aard x)) (Aard y)))

               }
 :goal        (forall (x) (SameSpecies x x))}
;
;
;{:name        "traid zoo symmetry"
; :description ""
; :assumptions {AX1 (forall (x) (or (Camel x) (Llama x) (Aard x)))
;               AX2 (not (exists (x) (and (Camel x) (Aard x))))
;               AX3 (not (exists (x) (and (Aard x) (Llama x))))
;               AX4 (not (exists (x) (and (Llama x) (Camel x))))
;               AX5 (forall (x y) (if (and (Camel x) (Camel y)) (SameSpecies x y)))
;               AX6 (forall (x y) (if (and (Llama x) (Llama y)) (SameSpecies x y)))
;               AX7 (forall (x y) (if (and (Aard x) (Aard y)) (SameSpecies x y)))
;               AX8 (forall (x y) (if (and (SameSpecies x y) (Camel x)) (Camel y)))
;               AX9 (forall (x y) (if (and (SameSpecies x y) (Llama x)) (Llama y)))
;               AX10 (forall (x y) (if (and (SameSpecies x y) (Aard x)) (Aard y)))
;
;               }
; :goal        (forall [x y]   (iff (SameSpecies x y) (SameSpecies y x)) )}


{:name        "traid zoo transitiviy"
 :description ""
 :assumptions {AX1  (forall (x) (or (Camel x) (Llama x) (Aard x)))
               AX2  (not (exists (x) (and (Camel x) (Aard x))))
               AX3  (not (exists (x) (and (Aard x) (Llama x))))
               AX4  (not (exists (x) (and (Llama x) (Camel x))))
               AX5  (forall (x y) (if (and (Camel x) (Camel y)) (SameSpecies x y)))
               AX6  (forall (x y) (if (and (Llama x) (Llama y)) (SameSpecies x y)))
               AX7  (forall (x y) (if (and (Aard x) (Aard y)) (SameSpecies x y)))
               AX8  (forall (x y) (if (and (SameSpecies x y) (Camel x)) (Camel y)))
               AX9  (forall (x y) (if (and (SameSpecies x y) (Llama x)) (Llama y)))
               AX10 (forall (x y) (if (and (SameSpecies x y) (Aard x)) (Aard y)))

               }
 :goal        (forall (x y z) (if (and (SameSpecies x y) (SameSpecies y z)) (SameSpecies x z)))}



{:name        "axiom selection 1"
 :description ""
 :assumptions {S1 (forall (x y) (if (= (+ x 1) (+ y 1)) (= x y)))}
 :goal        (forall (x y) (if (not (= x y)) (not (= (+ x 1) (+ y 1)))))
 }


{:name        "axiom selection 1"
 :description ""
 :assumptions {S9 (forall (z) (= (* z 0) 0))
               S7 (forall (x y) (= (+ x y) (+ y x)))
               S4 (forall (x) (= (+ x 0) x))
               S5 (forall (z y) (= (* z (+ y 1)) (+ (* z y) z)))
               }

 :goal        (forall (z) (= (* z 1) z))
 }


{:name        "axiom selection 1"
 :description ""
 :assumptions {
               S6 (forall (x y) (or (>= x y) (>= y x)))
               S4 (forall (x y) (if (>= x y) (not (> y x))))
               }

 :goal        (forall (x) (not (> x x)))
 }

{:name        "axiom selection 1"
 :description ""
 :assumptions {
               S6 (forall (x y) (or (>= x y) (>= y x)))
               S4 (forall (x y) (if (>= x y) (not (> y x))))
               S5 (forall (x y) (if (not (> x y)) (>= y x)))
               S7 (forall (x y z) (if (and (>= x y) (>= y z)) (>= x z)))
               }

 :goal        (forall (x y z) (if (and (> x y) (> y z)) (> x z)))
 }


{:name        "FPT"
 :description ""
 :assumptions {
               S6 (forall (P)
                          (exists (B)
                                  (forall (x)
                                          (=
                                            (has-property (has-property x x) P) (has-property x B)))))

               }

 :goal        (forall (?P) (exists (?q) (= ?q (has-property ?q ?P))))}






{
 :name        "CounterFactual Telephone Problem 1"
 :description ""
 :assumptions telephone
 :goal        (HoldsAt (Connected phone1 phone2) 3)
 }



{
 :name        "CounterFactual Telephone Problem 3"
 :description ""
 :assumptions telephone
 :goal        (if (and (not (forall [?e ?t]
                                    (iff (Happens ?e ?t)
                                         (or (and (= ?e (PickUp agent1 phone1)) (= ?t 0))
                                             (and (= ?e (Dial agent1 phone1 phone2)) (= ?t 1))
                                             (and (= ?e (PickUp agent2 phone2)) (= ?t 2))))))
                       (forall [?e ?t]
                               (iff (Happens ?e ?t)
                                    (or (and (= ?e (PickUp agent2 phone2)) (= ?t 0))
                                        (and (= ?e (Dial agent2 phone2 phone1)) (= ?t 1))
                                        (and (= ?e (PickUp agent1 phone1)) (= ?t 2))))))

                (and P (not P)))
 }


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{
 :name        "Counterfactual 1"
 :description ""
 :assumptions {
               A1 (forall [?x]
                          (if (GoToDoctor ?x) (not (Sick ?x))))
               A2 (not (GoToDoctor john))}

 :goal        (=> (GoToDoctor john) (not (Sick john)))
 }

{
 :name        "Counterfactual 2"
 :description ""
 :assumptions {A1 (forall [?x]
                          (if (GoToDoctor ?x) (not (Sick ?x))))
               A2 (not (GoToDoctor john))}

 :goal        (if (GoToDoctor john) (and P (not P)))
 }


{:name        "Counterfactual 3"
 :description ""
 :assumptions {A1 (forall [?x]
                          (if (GoToDoctor ?x) (not (Sick ?x))))
               A2 (not (GoToDoctor john))}

 :goal        (=> (GoToDoctor john) (and P (not P)))}


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


{:name        "Counterfactual Mortality 1"
 :description ""
 :assumptions {A1 (forall [?x] (if (Human ?x) (Mortal ?x)))
               A2 (Human socrates)}

 :goal        (=> (not (Mortal socrates)) (not (Human socrates)))}


{:name        "Counterfactual Mortality 2"
 :description ""
 :assumptions {A1 (forall [?x] (if (Human ?x) (Mortal ?x)))
               A2 (Human socrates)}

 :goal        (if (not (Mortal socrates)) (and P (not P)))}




{:name        "Counterfactual Mortality 3"
 :description ""
 :assumptions {A1 (forall [?x] (if (Human ?x) (Mortal ?x)))
               A2 (Human socrates)}

 :goal        (=> (not (Mortal socrates)) (and P (not P)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Identity A  1"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))}

 :goal        (=> (= jack jim) (CanAffordLuxury jim))}



{:name        "Counterfactual Identity A 2"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))}

 :goal        (if (= jack jim) (and P (not P)))}



{:name        "Counterfactual Identity A 3"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))}

 :goal        (=> (= jack jim) (and P (not P)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Identity B  1"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))
               A4 (not (= jim jack))}

 :goal        (=> (= jack jim) (CanAffordLuxury jim))}



{:name        "Counterfactual Identity B 2"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))
               A4 (not (= jim jack))}

 :goal        (if (= jack jim) (and P (not P)))}



{:name        "Counterfactual Identity B 3"
 :description ""
 :assumptions {A1 (forall [?x] (if (Rich ?x) (CanAffordLuxury ?x)))
               A2 (Rich jack)
               A3 (not (Rich jim))
               A4 (not (= jim jack))}

 :goal        (=> (= jack jim) (and P (not P)))}


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Disjunction 1"
 :description ""
 :assumptions {A1 (forall [?x] (or (Big ?x) (Small ?x)))
               A2 (Big tree)}

 :goal        (=> (not (Big tree)) (Small tree))}


{:name        "Counterfactual Disjunction 2"
 :description ""
 :assumptions {A1 (forall [?x] (or (Big ?x) (Small ?x)))
               A2 (Big tree)}

 :goal        (if (not (Big tree)) (and P (not P)))}

{:name        "Counterfactual Disjunction 3"
 :description ""
 :assumptions {A1 (forall [?x] (or (Big ?x) (Small ?x)))
               A2 (Big tree)}

 :goal        (=> (not (Big tree)) (and P (not P)))}


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Disjunction A 1"
 :description ""
 :assumptions {A1 (forall [?x] (or (Human ?x) (Animal ?x)))
               A2 (Human j)
               A3 (forall [?x] (if (Human ?x) (Thinks ?x)))}

 :goal        (=> (not (Thinks j)) (or (Animal j)
                                       (exists [?x] (and (Human ?x) (not (Thinks ?x))))))}


{:name        "Counterfactual Disjunction A 2"
 :description ""
 :assumptions {A1 (forall [?x] (or (Human ?x) (Animal ?x)))
               A2 (Human j)
               A3 (forall [?x] (if (Human ?x) (Thinks ?x)))}

 :goal        (if (not (Thinks j)) (and P (not P)))}


{:name        "Counterfactual Disjunction A 3"
 :description ""
 :assumptions {A1 (forall [?x] (or (Human ?x) (Animal ?x)))
               A2 (Human j)
               A3 (forall [?x] (if (Human ?x) (Thinks ?x)))}

 :goal        (=> (not (Thinks j)) (and P (not P)))}

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Linking A 1"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 (not P)
               }

 :goal        (=> P R)}


{:name        "Counterfactual Linking A 2"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 (not P)
               }

 :goal        (if P (and P (not P)))}

{:name        "Counterfactual Linking A 2"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 (not P)
               }

 :goal        (=> P (and P (not P)))}


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Reverse Linking A 1"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S
               }

 :goal        (=> (not S) (not P))}


{:name        "Counterfactual Reverse Linking A 2"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S
               }

 :goal        (if (not S) (and P (not P)))}

{:name        "Counterfactual Reverse Linking A 2"
 :description ""
 :assumptions {A1 (if P Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S
               }

 :goal        (=> (not S) (and P (not P)))}



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

{:name        "Counterfactual Reverse Linking B 1"
 :description ""
 :assumptions {A1 (if (forall [?x] (P ?x)) Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S}

 :goal        (=> (not S) (exists [?x] (not (P ?x))))}


{:name        "Counterfactual Reverse Linking B 2"
 :description ""
 :assumptions {A1 (if (forall [?x] (P ?x)) Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S
               }

 :goal        (if (not S) (and P (not P)))}

{:name        "Counterfactual Reverse Linking B 2"
 :description ""
 :assumptions {A1 (if (forall [?x] (P ?x)) Q)
               A2 (if Q R)
               A3 (if R S)
               A4 S}

 :goal        (=> (not S) (and P (not P)))}

