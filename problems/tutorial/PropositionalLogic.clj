
;;This file contains easy PC examples, including some PC infrence rules

{:name        "And Intro"
 :description ""
 :assumptions {1 P 2 Q}
 :goal        (and P Q)}

{:name       "And Elim Left"
 :description ""
 :assumptions {1 (and P Q)}
 :goal        P}

{:name      "And Elim Right"
 :description ""
 :assumptions {1 (and P Q)}
 :goal        Q}

{:name        "Or Intro Right"
 :description ""
 :assumptions {1 P}
 :goal        (or P Q)}

{:name        "Or Intro Left"
 :description ""
 :assumptions {1 P}
 :goal        (or Q P)}

{:name       "Or Elim"
 :description ""
 :assumptions {1 (or P Q) 2 (if P R) 3 (if Q R)}
 :goal        R}

{:name       "Green Cheese Moon"
 :description "(if P (if Q P) is a tauto, its negation is a contradiction we can derive G from"
 :assumptions {1 (not (if P (if Q P)))}
 :goal        G}