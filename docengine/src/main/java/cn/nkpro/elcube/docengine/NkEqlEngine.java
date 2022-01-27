package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHQL;
import cn.nkpro.elcube.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * SELECT * |
 *         <SpEL>  [as alias],
 *        `<SpEL>` [as alias],
 *        "<SpEL>" [as alias]
 *   FROM [doc|all|<docType>]
 *  WHERE <property> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND <property> BETWEEN <value1> AND <value2>
 *    AND <property> IN (...<value>)
 *    AND <property> IS NULL
 *    AND <property> IS NOT NULL
 *
 *    AND dynamic.<indexKey> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND dynamic.<indexKey> BETWEEN <value1> AND <value2>
 *    AND dynamic.<indexKey> IN (...<value>)
 *    AND dynamic.<indexKey> IS NULL
 *    AND dynamic.<indexKey> IS NOT NULL
 *
 *    AND d.<indexKey> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND d.<indexKey> BETWEEN <value1> AND <value2>
 *    AND d.<indexKey> IN (...<value>)
 *    AND d.<indexKey> IS NULL
 *    AND d.<indexKey> IS NOT NULL
 *
 *
 * UPDATE [doc|all|<docType>]
 *    SET  <property> = <property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>',
 *         <SpEL>     = <property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>',
 *        `<SpEL>`    = <property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>',
 *        "<SpEL>"    = <property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>'
 *  WHERE <property> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND <property> BETWEEN <value1> AND <value2>
 *    AND <property> IN (...<value>)
 *    AND <property> IS NULL
 *    AND <property> IS NOT NULL
 *
 *    AND dynamic.<indexKey> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND dynamic.<indexKey> BETWEEN <value1> AND <value2>
 *    AND dynamic.<indexKey> IN (...<value>)
 *    AND dynamic.<indexKey> IS NULL
 *    AND dynamic.<indexKey> IS NOT NULL
 *
 *    AND d.<indexKey> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND d.<indexKey> BETWEEN <value1> AND <value2>
 *    AND d.<indexKey> IN (...<value>)
 *    AND d.<indexKey> IS NULL
 *    AND d.<indexKey> IS NOT NULL
 */
public interface NkEqlEngine {

    /**
     *
     * @param eql ELCube Query Language
     * @return List
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    List<? extends DocH> executeEql(String eql);

    /**
     *
     * @param eql ELCube Query Language
     * @return List
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    List<DocHQL> findByEql(String eql);

    /**
     *
     * @param eql ELCube Query Language
     * @return List
     */
    List<DocHV> execUpdateEql(String eql);

    /**
     *
     * @param eql ELCube Query Language
     * @return List
     */
    @Transactional
    List<DocHV> doUpdateByEql(String eql, String optSource);
}
