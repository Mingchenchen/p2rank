package cz.siret.prank.score.transformation

import com.google.gson.Gson
import com.google.gson.JsonElement
import cz.siret.prank.program.routines.results.Evaluation
import cz.siret.prank.utils.StatSample
import cz.siret.prank.utils.Writable
import groovy.transform.CompileStatic

/**
 *
 */
@CompileStatic
class ProbabilityScoreTransformer extends ScoreTransformer implements Writable {

    private static int NBINS = 1000

    double min
    double max
    int nbins

    int[] tp_cumul_hist
    int[] fp_cumul_hist

    @Override
    double transformScore(double rawScore) {

        double tpx = getInterpoletedVal(tp_cumul_hist, rawScore) // true pockets with score lower or equal to rawScore
        double fpx = getInterpoletedVal(fp_cumul_hist, rawScore)  // false pockets with score higher or equal to rawScore

        double res =  tpx / (tpx + fpx)

        return res
    }

    /**
     * linear interpolation between 2 histogram values
     */
    private double getInterpoletedVal(int[] hist, double x) {
        double step = (max-min) / nbins
        int idx = (int)((x-min) / step)

        if (idx < 0)
            idx = 0
        if (idx >= nbins)
            idx = nbins - 1

        if (idx == nbins-1)
            return hist[idx]

        double mod = x - min - (double)step*idx

        double add = (hist[idx+1] - hist[idx]) * (mod/step)

        //write "Val: ${hist[idx]} Val+1: ${hist[idx+1]}   Add: $add"

        return hist[idx] + add
    }

    @Override
    void train(Evaluation evaluation) {
        nbins = NBINS

        List<Double> scores   = evaluation.pocketRows.collect { it.score }.toList()
        List<Double> tpScores = evaluation.pocketRows.findAll { it.isTruePocket()  }.collect { it.score }.toList()
        List<Double> fpScores = evaluation.pocketRows.findAll { !it.isTruePocket() }.collect { it.score }.toList()

//        write("Scores: " + scores)
//        write("TScores: " + tpScores)
//        write("FScores: " + fpScores)

        StatSample scoresSample = new StatSample(scores)
        min = scoresSample.min
        max = scoresSample.max

        int[] tp_hist = calcHist(nbins, min, max, tpScores)
        int[] fp_hist = calcHist(nbins, min, max, fpScores)

        for (int i=1; i<nbins; i++) {
            tp_hist[i] += tp_hist[i-1]
        }
        for (int i=nbins-2; i>-1; i--) { // reverse order
            fp_hist[i] += fp_hist[i+1]
        }

        tp_cumul_hist = tp_hist
        fp_cumul_hist = fp_hist
    }

    /**
     * create histogram
     */
    private int[] calcHist(int nbins, double min, double max, List<Double> scores) {
        int[] hist = new int[nbins]

        double step = (max-min) / nbins

        for (double x : scores) {
            int idx = (int)((x-min) / step)
            if (idx > nbins-1)
                idx = nbins - 1
            hist[idx]++
        }

        return hist
    }

    @Override
    JsonElement toJson() {
        new Gson().toJsonTree(this)
    }

    @Override
    ScoreTransformer loadFromJson(JsonElement json) {
        new Gson().fromJson(json, ProbabilityScoreTransformer.class)
    }


    @Override
    public String toString() {
        return "ProbabilityScoreTransformer{" +
                "min=" + min +
                ", max=" + max +
                ", nbins=" + nbins +
                ", tp_cumul_hist=" + Arrays.toString(tp_cumul_hist) +
                ", fp_cumul_hist=" + Arrays.toString(fp_cumul_hist) +
                '}';
    }
}
