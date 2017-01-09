package cz.siret.prank.features.api

import cz.siret.prank.domain.Protein
import groovy.transform.CompileStatic
import org.biojava.nbio.structure.Atom

/**
 * Calculator for a single composite feature. Composite feature consists of the vector of doubles with the header.
 *
 * Implementations should be stateless.
 */
@CompileStatic
interface FeatureCalculator {

    /**
     * ATOM ... feature is calculated for protein solvent exposed atoms and then projected to SAS points by P2RANK
     * SAS_POINT ... feature is calculated directly for SAS points
     */
    enum Type { ATOM, SAS_POINT } // TODO: BOTH... feature calculator that can set values to atoms and then project them to SAS point (and thus not rely on default P2RANK projection)

    Type getType()

    /**
     * Name serves as a unique key that is then used in Params. Add this key to Params.extra_features to enable this feature.
     */
    String getName()

    /**
     * Return the header according to current parametrization.
     * Feature lenght can be parametrized change between runs.
     */
    List<String> getHeader()

    /**
     * (Optionally) perform preliminary calculations on the whole protein.
     * Store the calculated data into protein.secondaryData map.
     *
     * @param protein
     */
    void preProcessProtein(Protein protein)

    /**
     *
     * @param sasPoint one of the points on the Protein's SAS (Solvent Accessible Surface)
     * @param context local context for feature calcuation that corresponds to the given SAS point
     * @return array of values, lenght must be the same as length of the header
     */
    double[] calculateForSasPoint(Atom sasPoint, SasFeatureCalculationContext context)

    /**
     *
     * @param proteinSurfaceAtom
     * @param protein
     * @return
     */
    double[] calculateForAtom(Atom proteinSurfaceAtom, AtomFeatureCalculationContext protein)

    /**
     * (Optionally) perform post processing on the whole protein.
     * @param protein
     */
    void postProcessProtein(Protein protein)

}