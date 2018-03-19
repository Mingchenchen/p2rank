package cz.siret.prank.domain.labeling

import cz.siret.prank.domain.Protein
import cz.siret.prank.domain.Residue
import cz.siret.prank.domain.Residues
import cz.siret.prank.features.api.ProcessedItemContext
import cz.siret.prank.geom.Atoms
import cz.siret.prank.prediction.metrics.ClassifierStats
import cz.siret.prank.program.ml.Model
import cz.siret.prank.program.params.Parametrized
import cz.siret.prank.utils.Cutils
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 *
 */
@Slf4j
@CompileStatic
class ModelBasedResidueLabeler extends ResidueLabeler<Boolean> implements Parametrized {

    private Model model
    private Atoms sasPoints
    private ProcessedItemContext context

    private List<LabeledPoint> labeledPoints
    private List<LabeledPoint> observedPoints = null

    private ClassifierStats classifierStats

    private double SCORE_THRESHOLD = params.residue_score_threshold

    ModelBasedResidueLabeler(Model model, Atoms sasPoints, ProcessedItemContext context) {
        this.model = model
        this.sasPoints = sasPoints
        this.context = context
    }

    ModelBasedResidueLabeler withObserved(List<LabeledPoint> observedPoints) {
        this.observedPoints = observedPoints
        return this
    }

    List<LabeledPoint> getLabeledPoints() {
        return labeledPoints
    }

    ClassifierStats getClassifierStats() {
        return classifierStats
    }

    @Override
    ResidueLabeling<Boolean> labelResidues(Residues residues, Protein protein) {

        ModelBasedPointLabeler predictor = new ModelBasedPointLabeler(model, context).withObserved(observedPoints)
        labeledPoints = predictor.labelPoints(sasPoints, protein)
        classifierStats = predictor.classifierStats

        return calculateLabeling(residues, labeledPoints, protein)
    }

    private BinaryLabeling calculateLabeling(Residues residues, List<LabeledPoint> labeledPoints, Protein protein) {

        Atoms points = new Atoms(labeledPoints)
        Residues exposed = protein.getExposedResidues()
        double radius = params.solvent_radius + params.surface_additional_cutoff

        // calculate binary labels by sum and threshold

        ResidueLabeling<Double> resScores = new ResidueLabeling<>(residues.count)

        for (Residue res : residues) {
            List<Double> pscores = Collections.emptyList()
            if (exposed.contains(res)) { // calculate only for exposed
                pscores = points.cutoutShell(res.atoms, radius).collect { (it as LabeledPoint).score }.asList()
            }

            double score = aggregateScore(pscores)
            resScores.add(res, score)
        }

        BinaryLabeling resLabels = new BinaryLabeling(residues.count)

        for (LabeledResidue<Double> it : resScores.labeledResidues) {
            resLabels.add(it.residue, transformScore(it.label))
        }

        return resLabels
    }

    private boolean transformScore(double score) {
        score >= SCORE_THRESHOLD
    }

    private double aggregateScore(List<Double> scores) {
        Cutils.sum(scores)
    }

    @Override
    boolean isBinary() {
        return true
    }

}