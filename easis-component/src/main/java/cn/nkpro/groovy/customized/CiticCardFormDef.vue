<template>
    <nk-dynamic-form-def :edit-mode="editMode" :doc-def="docDef" :card-key="cardKey">
        <template slot="header">
            <nk-form-item title="TABLE">
                {{def.table}}
                <a-input v-model="def.table" slot="edit" size="small"></a-input>
            </nk-form-item>
            <nk-form-item title="KEY">
                {{def.keys}}
                <a-select v-model="def.keys" slot="edit" size="small" mode="tags"></a-select>
            </nk-form-item>
            <nk-form-item title="KEY映射">
                {{def.keysMapping}}
                <nk-sp-el-editor v-model="def.keysMapping" slot="edit"></nk-sp-el-editor>
            </nk-form-item>
        </template>
    </nk-dynamic-form-def>
</template>

<script>
    import {MixinDef, NkDynamicFormDef} from 'nk';
    export default {
        mixins:[new MixinDef({})],
        components:{
            NkDynamicFormDef
        }
    }
</script>

<style scoped>

</style>