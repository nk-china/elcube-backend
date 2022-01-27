package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHQL;
import cn.nkpro.elcube.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * ELCube Query Language
 *
 * 注意：包含特殊字符的复杂的SpEL，需要用 " 或 ` 符号包裹
 *
 * SELECT * |
 *         <SpEL>  [[AS] <alias>],
 *        `<SpEL>` [[AS] <alias>],
 *        "<SpEL>" [[AS] <alias>]
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
 *    SET  <property> = [<property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>'],
 *         <SpEL>     = [<property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>'],
 *        `<SpEL>`    = [<property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>'],
 *        "<SpEL>"    = [<property> | `<SpEL>` | "<SpEL>" | <Number> | '<String>']
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
 *    // d 和 dy 是 dynamic 的别名，作用是一样的
 *    AND d.<indexKey> [=|>|>=|<|<=|!=|<>|LIKE] <value>
 *    AND d.<indexKey> BETWEEN <value1> AND <value2>
 *    AND d.<indexKey> IN (...<value>)
 *    AND d.<indexKey> IS NULL
 *    AND d.<indexKey> IS NOT NULL
 *
 * etc.
 *
 *  select docName,
 *         data.customer.name,
 *         "data.customer.name" as name1,
 *         `data.customer.name` as name2,
 *         'data.customer.name' as name3,
 *         `data.plans?.^[true]?.name` as plan_name,
 *         //el('data.plans?.^[true]?.name') // 这种函数语法不支持，可使用SpEL来执行函数
 *    from doc
 *   where d.abc is null
 *     and docId = '11bd25ac-c303-484b-b7c0-c66816e18cf2'
 *
 *  update doc
 *     set docName = '123',
 *         "data.payment.^[true].remark" = '123',
 *         createdTime = 0,
 *         `partnerName` = null,
 *         docName = `data.payment.^[true].expireDate`
 *   where docId = '13b24c8a-66aa-4753-8444-b790d8602c7f'
 *
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
