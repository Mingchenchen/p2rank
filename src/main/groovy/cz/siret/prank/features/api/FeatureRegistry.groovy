package cz.siret.prank.features.api

import cz.siret.prank.features.implementation.AAIndexFeature
import cz.siret.prank.features.implementation.Asa2Feature
import cz.siret.prank.features.implementation.AsaFeature
import cz.siret.prank.features.implementation.AsaResiduesFeature
import cz.siret.prank.features.implementation.AtomicResidueFeature
import cz.siret.prank.features.implementation.BfactorFeature
import cz.siret.prank.features.implementation.ContactResidue1Feature
import cz.siret.prank.features.implementation.ContactResidue1PositionFeature
import cz.siret.prank.features.implementation.ContactResiduesPositionFeature
import cz.siret.prank.features.implementation.ProteinMassFeature
import cz.siret.prank.features.implementation.ProtrusionFeature
import cz.siret.prank.features.implementation.ProtrusionHistogramFeature
import cz.siret.prank.features.implementation.PyramidFeature
import cz.siret.prank.features.implementation.sequence.DupletsPropensityAtomicFeature
import cz.siret.prank.features.implementation.sequence.DupletsPropensityFeature
import cz.siret.prank.features.implementation.SurfaceProtrusionFeature
import cz.siret.prank.features.implementation.XyzDummyFeature
import cz.siret.prank.features.implementation.chem.ChemFeature
import cz.siret.prank.features.implementation.conservation.ConservationFeature
import cz.siret.prank.features.implementation.conservation.ConservationCloudFeature
import cz.siret.prank.features.implementation.conservation.ConservationCloudScaledFeature
import cz.siret.prank.features.implementation.histogram.PairHistogramFeature
import cz.siret.prank.features.implementation.sequence.TripletsPropensityAtomicFeature
import cz.siret.prank.features.implementation.sequence.TripletsPropensityFeature
import cz.siret.prank.features.implementation.volsite.VolsiteFeature
import cz.siret.prank.program.PrankException
import groovy.transform.CompileStatic

/**
 * Registry of feature implementations
 */
@CompileStatic
class FeatureRegistry {

    private static Map<String, FeatureCalculator> features = new HashMap<>()

    /**
     *
     * @param key unique feature key. Add this key to Params.extra_features to enable this feature.
     * @param featureCalculator
     */
    static void register(FeatureCalculator featureCalculator) {
        if (features.containsKey(featureCalculator.name)) {
            throw new PrankException("Trying to register 2 Features with the same name " + featureCalculator.name)
        }

        features.put(featureCalculator.name, featureCalculator)
    }

    static Map<String, FeatureCalculator> getFeatureImplementations() {
        return features
    }

    static {
        // TODO register with names

        register new ChemFeature()
        register new VolsiteFeature()
        register new ProtrusionFeature()
        register new SurfaceProtrusionFeature()
        register new BfactorFeature()
        register new ProtrusionHistogramFeature()
        register new ConservationFeature()
        register new ConservationCloudFeature()
        register new ConservationCloudScaledFeature()
        register new AtomicResidueFeature()
        register new ContactResidue1Feature()
        register new ContactResiduesPositionFeature()
        register new ContactResidue1PositionFeature()
        register new AsaFeature()
        register new Asa2Feature()
        register new AsaResiduesFeature()
        register new XyzDummyFeature()
        register new PairHistogramFeature()
        register new PyramidFeature()
        register new ProteinMassFeature()
        register new AAIndexFeature()
        register new DupletsPropensityFeature()
        register new DupletsPropensityAtomicFeature()
        register new TripletsPropensityFeature()
        register new TripletsPropensityAtomicFeature()

        // Register new feature implementations here

    }

}
