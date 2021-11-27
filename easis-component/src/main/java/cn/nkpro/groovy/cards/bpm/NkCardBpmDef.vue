<template>
    <nk-card :title="`${card.cardName}配置`">
        <nk-form ref="formBPM" size="small" :col="1" :edit="editMode" style="width:500px;">

            <nk-form-item term="流程定义" :validateFor="def.processKey">
                {{def.processKey}}
                <a-input size="small" v-model="def.processKey" slot="edit" allowClear></a-input>
            </nk-form-item>
            <nk-form-item v-if="def.processKey" term="启动状态" :validateFor="def.startBy" message="请输入启动状态" required len :max="20" lenMessage="启动状态1-20个字">
                {{def.startBy}}
                <a-input size="small" v-model="def.startBy" slot="edit" allowClear></a-input>
            </nk-form-item>
            <nk-form-item term="终止时回退状态" :validateFor="def.rollbackTo" message="请输入终止回退状态" lenMessage="终止回退状态1-20个字">
                {{def.rollbackTo}}
                <a-input size="small" v-model="def.rollbackTo" slot="edit" allowClear></a-input>
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import {MixinDef} from "nk";

    export default {
    mixins:[new MixinDef({})],
    created() {
    },
    mounted() {
    },
    methods:{
    }
}
</script>

<style scoped>

</style>