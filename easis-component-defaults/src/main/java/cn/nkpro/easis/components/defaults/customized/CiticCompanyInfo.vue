<template>
    <nk-card>
        <nk-form :col="2" :edit="editMode">
            <nk-form-item title="基本每股收益(元)">
                {{data.map1['S_FA_ARTURN']}}
                ({{data.map2['PROBS_SCORE']}})
                <template slot="edit">
                    <a-input v-model="data.map1['S_FA_ARTURN']"></a-input>
                    <a-input v-model="data.map2['PROBS_SCORE']"></a-input>
                </template>
            </nk-form-item>
            <nk-form-item title="稀释每股收益(元)">
                {{data.map1['S_FA_ASSETSTOEQUITY']}}
                ({{data.map2['NEWS_CODE']}})
                <template slot="edit">
                    <a-input v-model="data.map1['S_FA_ASSETSTOEQUITY']"></a-input>
                    <a-input v-model="data.map2['NEWS_CODE']"></a-input>
                </template>
            </nk-form-item>
            <nk-form-item title="每股净资产(元) ">
                {{data.map1['S_FA_CFPS']}}
                ({{data.map2['COMPANY_CODE']}})
                <template slot="edit">
                    <a-input v-model="data.map1['S_FA_CFPS']"></a-input>
                    <a-input v-model="data.map2['COMPANY_CODE']"></a-input>
                </template>
            </nk-form-item>
            <nk-form-item title="每股未分配利润(元)">
                {{data.map1['S_FA_DEBTTOASSETS']}}
                <a-input v-model="data.map1['S_FA_DEBTTOASSETS']" slot="edit"></a-input>
            </nk-form-item>
            <nk-form-item title="每股经营现金流(元) ">
                {{data.map1['S_FA_DEDUCTEDPROFIT']}}
                <a-input v-model="data.map1['S_FA_DEDUCTEDPROFIT']" slot="edit"></a-input>
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import {Mixin} from 'nk';
    export default {
        mixins:[new Mixin()],
        components:{
        }
    }
</script>

<style scoped>

</style>