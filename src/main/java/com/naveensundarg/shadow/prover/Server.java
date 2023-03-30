
//DEADCODE this class and its methods are never used anywhere in the project

package com.naveensundarg.shadow.prover;

import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.utils.Problem;
import com.naveensundarg.shadow.prover.utils.ProblemReader;
import com.naveensundarg.shadow.prover.utils.Reader;

import java.util.List;


public final class Server {
    static {
        Prover prover = SnarkWrapper.getInstance();
        try {
            List<Problem> problems = ProblemReader.readFrom(Server.class.getResourceAsStream("firstorder-completness-tests.clj"));
            problems.forEach(problem -> {
                prover.prove(problem.getAssumptions(), problem.getGoal());
            });
        }
        catch (Reader.ParsingException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public class RequestPayload {
        public String method;
        public String variable;
        public String[] variables;
        public String[] assumptions;
        public String goal;
    }
}