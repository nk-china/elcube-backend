package cn.nkpro.groovy.cards

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import org.springframework.stereotype.Component

@SuppressWarnings("unused")
@NkNote("NkCardAssets")
@Component("NkCardAssets")
class NkCardAssets extends NkAbstractCard {

    @Override
    String getDataComponentName() {
        return "NkCardAssets"
    }

    @Override
    protected String[] getDefComponentNames() {
        return ["NkCardAssetsDef"];
    }
}
