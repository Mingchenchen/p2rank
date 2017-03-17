package cz.siret.prank.program.routines

import cz.siret.prank.program.routines.results.EvalResults
import cz.siret.prank.utils.Futils
import groovy.util.logging.Slf4j

import static cz.siret.prank.utils.ATimer.startTimer

/**
 * Routine that iterates through different values of random seed param
 */
@Slf4j
class SeedLoop extends EvalRoutine {

    EvalRoutine innerRoutine  // routine to iterate on

    SeedLoop(EvalRoutine routine, String outdir) {
        super(outdir)
        this.innerRoutine = routine
    }

    @Override
    EvalResults execute() {
        def timer = startTimer()

        EvalResults results = new EvalResults(0)

        int origSeed = params.seed
        int n = params.loop
        for (int seedi in 1..n) {
            write "random seed iteration: $seedi/$n"

            String label = "seed.${params.seed}"
            innerRoutine.outdir = "$outdir/$label"

            results.addAll(innerRoutine.execute())

            params.seed += 1
        }

        results.logAndStore(outdir, params.classifier)
        if (innerRoutine instanceof CrossValidation) {
            CrossValidation cv = (CrossValidation) innerRoutine
            logSummaryResults(cv.dataset.label, "crossvalidation", results)
        } else {
            logSummaryResults("--", "evaluation", results)
        }
        params.seed = origSeed // set seed back for other experiments

        logTime "random seed iteration finished in $timer.formatted"
        write "results saved to directory [${Futils.absPath(outdir)}]"


        return results
    }

}
