<template>
    <nk-def-card>
        <nk-form :edit="editMode" :col="1">
            <nk-form-item title="计算公式">
                {{def.mtSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.mtSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="计算选项">
                {{def.mtOptions}}
                <nk-sp-el-template-editor slot="edit" v-model="def.mtOptions"></nk-sp-el-template-editor>
            </nk-form-item>
        </nk-form>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";
    export default {
        mixins:[new MixinDef({})],
    }
</script>

<style scoped>

</style>