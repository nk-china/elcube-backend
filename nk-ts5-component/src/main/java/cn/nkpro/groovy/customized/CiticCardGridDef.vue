<template>
    <nk-dynamic-grid-def :edit-mode="editMode" :doc-def="docDef" :card-key="cardKey">
        <template slot="header">
            <nk-form-item title="TABLE">
                {{def.table}}
                <a-input v-model="def.table" slot="edit" size="small"></a-input>
            </nk-form-item>
            <nk-form-item title="KEY">
                {{def.keys}}
                <a-select v-model="def.keys" slot="edit" size="small" mode="tags"></a-select>
            </nk-form-item>
            <nk-form-item title="WHERE映射">
                {{def.whereMapping}}
                <nk-sp-el-editor v-model="def.whereMapping" slot="edit"></nk-sp-el-editor>
            </nk-form-item>
        </template>
    </nk-dynamic-grid-def>
</template>

<script>
    import {MixinDef, NkDynamicGridDef} from 'nk';
    export default {
        mixins:[new MixinDef({})],
        components:{
            NkDynamicGridDef
        }
    }
</script>

<style scoped>

</style>