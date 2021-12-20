package cn.nkpro.elcube.components.defaults.cards

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.exception.NkOperateNotAllowedCaution
import cn.nkpro.elcube.security.SecurityUtilz
import cn.nkpro.elcube.security.UserAccountService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@NkNote("账号绑定")
@Component("NkCardAccount")
class NkCardAccount extends NkAbstractCard<Data,Map> {

    @Autowired
    private UserAccountService userAccountService

    @Override
    Data beforeUpdate(DocHV doc, Data data, Data original, DocDefIV defIV, Map d) {

        if(SecurityUtilz.hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:AUTH')){

            if(original != null && (data.account != original.account)){

                if(original.account){
                    def account = userAccountService.getAccount(original.account, false)
                    account.setObjectId(StringUtils.EMPTY)
                    account.setPassword(null)
                    userAccountService.update(account)
                }

                if(data.account){
                    def account = userAccountService.getAccount(data.account, false)

                    Assert.notNull(account, "账号不存在")

                    if(account.getObjectId()!=doc.getDocId()){

                        if(account.getObjectId()){
                            throw new NkOperateNotAllowedCaution("账单已被其他交易伙伴绑定")
                        }else{
                            account.setObjectId(doc.getDocId())
                            account.setPassword(null)
                            userAccountService.update(account)
                        }
                    }
                }
            }
        }else{
            data.account = original.account
        }

        return super.beforeUpdate(doc, data, original, defIV, d) as Data
    }

    static class Data{
        String account
    }
}
