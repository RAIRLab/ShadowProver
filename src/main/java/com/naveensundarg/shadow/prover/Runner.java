package com.naveensundarg.shadow.prover;

import com.naveensundarg.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import com.naveensundarg.shadow.prover.core.proof.Justification;
import com.naveensundarg.shadow.prover.utils.Problem;
import com.naveensundarg.shadow.prover.utils.ProblemReader;
import com.naveensundarg.shadow.prover.utils.Reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Optional;

public final class Runner {

    public static void main(String[] args) {

        System.out.println("--------------- Starting ShadowProver --------------- ");

        // Grab filename from argument list
        if (args.length < 1) {
            System.out.println("Need to include filename containing problem.");
            return;
        }
        String fileName = args[0];

        // Read File
        FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        List<Problem> problems;
        try {
            problems = ProblemReader.readFrom(fileStream);
        } catch (Reader.ParsingException e) {
            e.printStackTrace();
            return;
        }

        CognitiveCalculusProver cognitiveCalculusProver = new CognitiveCalculusProver();

        for (Problem problem : problems) {
            Optional<Justification> optionalJustification = cognitiveCalculusProver.prove(
                problem.getAssumptions(),
                problem.getGoal()
            );

            if(optionalJustification.isPresent()) {
                System.out.println(optionalJustification.get().toString());
            }
            else {
                System.out.println("FAILED");
            }
        }
    }
}
