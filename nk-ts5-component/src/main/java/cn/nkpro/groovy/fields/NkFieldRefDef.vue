<template>
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="对话框选项" :col="2">
                {{value.modelConfig}}
                <nk-sp-el-template-editor slot="edit" v-model="value.options"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="数据映射模版">
                {{value.dataMappings}}
                <nk-sp-el-template-editor slot="edit" v-model="value.dataMappings"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="选项映射模版">
                {{value.optionMappings}}
                <nk-sp-el-template-editor slot="edit" v-model="value.optionMappings"></nk-sp-el-template-editor>
            </nk-form-item>
            <nk-form-item title="单据TP">
                {{value.isPartner?'是':'否'}}
                <a-switch slot="edit" size="small" v-model="value.isPartner"></a-switch>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object
    }
}
</script>

<style scoped>

</style>