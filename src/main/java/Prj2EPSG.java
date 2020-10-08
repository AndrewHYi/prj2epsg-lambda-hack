import java.util.*;

import org.geotools.metadata.i18n.ErrorKeys;
import org.geotools.metadata.i18n.Errors;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.AbstractIdentifiedObject;
import org.geotools.referencing.CRS;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.geotools.referencing.factory.IdentifiedObjectFinder;
import org.geotools.referencing.factory.epsg.CartesianAuthorityFactory;
import org.geotools.referencing.factory.epsg.LongitudeFirstFactory;
import org.geotools.referencing.factory.wms.AutoCRSFactory;
import org.geotools.referencing.factory.wms.WebCRSFactory;
import org.geotools.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class Prj2EPSG {
    public static String lookupIdentifier(
            final Citation authority, final CoordinateReferenceSystem crs, final boolean fullScan)
            throws FactoryException {

        ReferenceIdentifier id = AbstractIdentifiedObject.getIdentifier(crs, authority);
        if (id != null) {
            return id.getCode();
        }

        Set<CRSAuthorityFactory> factories = new HashSet<>(Arrays.asList(
                new LongitudeFirstFactory(),
                new CartesianAuthorityFactory(),
                new AutoCRSFactory(),
                new WebCRSFactory()
        ));

        for (final CRSAuthorityFactory factory : factories) {
            if (!Citations.identifierMatches(factory.getAuthority(), authority)) {
                continue;
            }
            if (!(factory instanceof AbstractAuthorityFactory)) {
                continue;
            }
            final AbstractAuthorityFactory f = (AbstractAuthorityFactory) factory;
            final IdentifiedObjectFinder finder = f.getIdentifiedObjectFinder(crs.getClass());
            finder.setFullScanAllowed(fullScan);
            final String code = finder.findIdentifier(crs);
            if (code != null) {
                return code;
            }
        }
        return null;
    }

    public static Integer lookupEpsgCode(
            final CoordinateReferenceSystem crs, final boolean fullScan) throws FactoryException {
        final String identifier = lookupIdentifier(Citations.EPSG, crs, fullScan);
        if (identifier != null) {
            final int split = identifier.lastIndexOf(GenericName.DEFAULT_SEPARATOR);
            final String code = identifier.substring(split + 1);
            // The above code works even if the separator was not found, since in such case
            // split == -1, which implies a call to substring(0) which returns 'identifier'.
            try {
                return Integer.parseInt(code);
            } catch (NumberFormatException e) {
                throw new FactoryException(Errors.format(ErrorKeys.ILLEGAL_IDENTIFIER_$1, identifier), e);
            }
        }
        return null;
    }

    public Map<String, Object> lookupFromWkt(String terms) {
        Map<String, Object> response = new HashMap<>();
        response.put("exact", Boolean.FALSE);
        response.put("codes", Arrays.asList());

        try {
            CoordinateReferenceSystem crs = CRS.parseWKT(terms);
            final Integer code = lookupEpsgCode(crs, true);

            if (code != null) {
                response.put("exact", Boolean.TRUE);
                response.put("codes", Arrays.asList(asCRSMap(String.valueOf(code), crs)));
            }
        } catch (FactoryException e) {
            return response;
        }

        return response;
    }

    private Map<String, String> asCRSMap(String code, CoordinateReferenceSystem crs) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("name", crs.getName().getCode());
        return map;
    }

    public static void main(String[] args) {
        Prj2EPSG service = new Prj2EPSG();
        Map<String, Object> response = service.lookupFromWkt("123");
        System.out.println(response.toString());
    }
}
